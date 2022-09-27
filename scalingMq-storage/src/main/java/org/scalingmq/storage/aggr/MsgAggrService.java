package org.scalingmq.storage.aggr;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.ioc.IocContainer;
import org.scalingmq.common.net.NetworkClient;
import org.scalingmq.storage.api.StorageApiReqWrapper;
import org.scalingmq.storage.api.StorageApiResWrapper;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.core.PartitionMsgStorage;
import org.scalingmq.storage.core.replicate.raft.RaftCore;
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
     * @return 追加响应
     */
    public StorageApiResWrapper.PutMsgRes putMsg(StorageApiReqWrapper.StorageApiReq.PutMsgReq req) {
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
            return res.getPutMsgRes();
        }
        // 2. 本地追加
        List<StorageApiReqWrapper.StorageApiReq.PutMsgReq.MsgItem> msgItemsList = req.getMsgItemsList();
        long offset = 0L;
        for (StorageApiReqWrapper.StorageApiReq.PutMsgReq.MsgItem msgItem : msgItemsList) {
            offset = IocContainer.getInstance().getObj(PartitionMsgStorage.class).append(msgItem.getContent().toByteArray());
        }
        if (offset == 0L) {
            throw new StorageBaseException(ExceptionCodeEnum.UNKNOWN, "未知错误 append null");
        }

        return StorageApiResWrapper.PutMsgRes.newBuilder()
                // TODO: 2022/9/27 MSG ID  生成策略
                .setMsgId("null")
                .setOffset(offset)
                .build();
    }

}
