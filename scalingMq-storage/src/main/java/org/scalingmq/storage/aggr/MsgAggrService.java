package org.scalingmq.storage.aggr;

import com.google.protobuf.ByteString;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.ioc.IocContainer;
import org.scalingmq.common.net.NetworkClient;
import org.scalingmq.common.timer.Timer;
import org.scalingmq.common.timer.TimerTask;
import org.scalingmq.storage.api.StorageApiReqWrapper;
import org.scalingmq.storage.api.StorageApiResWrapper;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.storage.PartitionMsgStorage;
import org.scalingmq.storage.core.replicate.ReplicateController;
import org.scalingmq.storage.core.replicate.entity.FollowerOffsetProgressReport;
import org.scalingmq.storage.core.replicate.raft.RaftCore;
import org.scalingmq.storage.core.storage.entity.FetchResult;
import org.scalingmq.storage.exception.ExceptionCodeEnum;
import org.scalingmq.storage.exception.StorageBaseException;

import java.util.List;

/**
 * 消息聚合业务层
 * @author renyansong
 */
@Slf4j
public class MsgAggrService {

    private static final MsgAggrService INSTANCE = new MsgAggrService();

    private static final Timer TIMER = new Timer(20, 1024);

    private MsgAggrService() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    public static MsgAggrService getInstance() {
        return INSTANCE;
    }

    /**
     * 追加消息
     * @param req 追加消息请求
     */
    public void putMsg(StorageApiReqWrapper.StorageApiReq.PutMsgReq req, Channel channel) {
        // 1. 判断当前节点是不是leader
        RaftCore raftCore = IocContainer.getInstance().getObj(RaftCore.class);
        if (!raftCore.isLeader()) {
            // 1.1 转发请求给Leader处理
            String leaderAddr = raftCore.getLeaderAddr();
            if (log.isDebugEnabled()) {
                log.debug("转发追加消息请求给Leader...{}", leaderAddr);
            }
            // 构造完整的请求
            StorageApiReqWrapper.StorageApiReq fullReq =
                    StorageApiReqWrapper.StorageApiReq.newBuilder()
                            .setApiTypeValue(StorageApiReqWrapper.StorageApiReq.ApiType.PRODUCT_VALUE)
                            .setPutMsgReq(req)
                            .build();
            StorageApiResWrapper.StorageApiRes res = (StorageApiResWrapper.StorageApiRes) NetworkClient.getInstance().sendReq(
                    fullReq,
                    leaderAddr,
                    StorageConfig.MSG_PORT,
                    StorageApiResWrapper.StorageApiRes.getDefaultInstance()
            );
            if (log.isDebugEnabled()) {
                log.debug("转发给leader的响应数据:{}", res);
            }
            if (!"".equals(res.getErrorCode())) {
                // 有异常
                ExceptionCodeEnum exceptionCodeEnum = ExceptionCodeEnum.getCodeByStr(res.getErrorCode());
                throw new StorageBaseException(exceptionCodeEnum, res.getErrorMsg());
            }
            channel.writeAndFlush(res.getPutMsgRes());
            return;
        }
        // 2. 本地追加
        List<StorageApiReqWrapper.StorageApiReq.PutMsgReq.MsgItem> msgItemsList = req.getMsgItemsList();
        long offset = 0L;
        for (StorageApiReqWrapper.StorageApiReq.PutMsgReq.MsgItem msgItem : msgItemsList) {
            offset = IocContainer.getInstance().getObj(PartitionMsgStorage.class).append(msgItem.getContent().toByteArray(), null);
        }
        if (offset == 0L) {
            throw new StorageBaseException(ExceptionCodeEnum.UNKNOWN, "未知错误 append null");
        }
        // 挂起响应 等待消息复制
        long finalOffset = offset;
        TIMER.addTask(new TimerTask(req.getMaxWaitSec(), () -> {
            // 检查复制的位点
            boolean result = ReplicateController.LeaderController.checkIsrOffset(finalOffset);
            if (!result) {
                // 返回超时异常
                throw new StorageBaseException(ExceptionCodeEnum.PRODUCE_TIMEOUT, "wait follower fetch timeout");
            }

            StorageApiResWrapper.PutMsgRes msgRes = StorageApiResWrapper.PutMsgRes.newBuilder()
                    // TODO: 2022/9/27 MSG ID  生成策略
                    .setMsgId("null")
                    .setOffset(finalOffset)
                    .build();
            channel.writeAndFlush(msgRes);
        }));

    }

    /**
     * 拉取消息
     * @param req 拉取消息请求
     */
    public void fetchMsg(StorageApiReqWrapper.StorageApiReq.FetchMsgReq req, Channel channel) {
        if (log.isDebugEnabled()) {
            log.debug("收到拉取消息请求:{}", req);
        }
        // 拉取数据
        PartitionMsgStorage partitionMsgStorage = IocContainer.getInstance().getObj(PartitionMsgStorage.class);
        FetchResult fetchResult = partitionMsgStorage.fetchMsg(req.getOffset());

        // 如果是follower的拉取 上报偏移量
        if (!"".equals(req.getFollowerHostname())) {
            ReplicateController.LeaderController.updateFollowerOffset(
                    FollowerOffsetProgressReport.builder()
                            .followerHostname(req.getFollowerHostname())
                            // 对拉取完的offset复制 可能是最新的offset
                            .lastFetchOffset(fetchResult.getFetchLastOffset())
                            .build()
            );
        }
        StorageApiResWrapper.FetchMsgRes.Builder builder = StorageApiResWrapper.FetchMsgRes.newBuilder()
                .setAlreadyLastOffset(fetchResult.getNoResult())
                .setFetchLastOffset(fetchResult.getFetchLastOffset());

        if (!fetchResult.getNoResult()) {
            for (int i = 0; i < fetchResult.getFetchDataList().size(); i++) {
                byte[] data = fetchResult.getFetchDataList().get(i);
                builder.setData(i, ByteString.copyFrom(data));
            }
        }
        channel.writeAndFlush(builder.build());
    }

}
