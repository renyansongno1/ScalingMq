package org.scalingmq.storage.core.replicate.raft;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.common.lifecycle.Lifecycle;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;
import org.xbill.DNS.lookup.LookupSession;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 找到所有的pods
 * @author renyansong
 */
@Slf4j
public class PeerFinder implements Lifecycle {

    private static final PeerFinder INSTANCE = new PeerFinder();

    /**
     * SRV的后缀
     * <service name>.<namespace>.svc.cluster.local
     */
    private static final String SRV_NAME_SUFFIX = ".svc.cluster.local";

    private final String SRV_SERVICE =
            StorageConfig.getInstance().getServiceName()
            + "."
            + StorageConfig.getInstance().getNamespace()
            + SRV_NAME_SUFFIX;

    /**
     * 存储所有的peer的域名set
     */
    private static final CopyOnWriteArraySet<String> PEER_HOST_SET = new CopyOnWriteArraySet<>();

    /**
     * 监听节点变动的listener集合
     */
    private static final CopyOnWriteArraySet<PeerChangeListener> PEER_CHANGE_LISTENERS = new CopyOnWriteArraySet<>();

    private static final ScheduledThreadPoolExecutor TIMER = new ScheduledThreadPoolExecutor(1,
            r -> new Thread(r, "peer-find-timer"));

    public PeerFinder() {
        /*if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }*/
    }

    public static PeerFinder getInstance() {
        return INSTANCE;
    }

    /**
     * 开始查找
     */
    private void find() {
        log.debug("查询SRV:{} 下所有的域名服务", SRV_SERVICE);
        int startSize = PEER_HOST_SET.size();
        LookupSession s = LookupSession.defaultBuilder().build();
        try {
            Name mxLookup = Name.fromString(SRV_SERVICE);
            s.lookupAsync(mxLookup, Type.SRV)
                    .whenComplete(
                            (answers, ex) -> {
                                if (ex == null) {
                                    if (!answers.getRecords().isEmpty()) {
                                        for (Record rec : answers.getRecords()) {
                                            SRVRecord mx = ((SRVRecord) rec);
                                            log.debug("Host " + mx.getTarget() + " has preference " + mx.getPriority());
                                            PEER_HOST_SET.add(mx.getTarget().toString());
                                        }
                                    }
                                } else {
                                    log.error("dns naming error...", ex);
                                }
                            })
                    .toCompletableFuture()
                    .get();
        } catch (TextParseException | InterruptedException | ExecutionException e) {
            log.error("dns java error", e);
        }
        log.debug("当前所有的peer域名:{}",Arrays.toString(PEER_HOST_SET.toArray()));
        if (PEER_HOST_SET.size() != startSize) {
            // 通知所有的listener
            for (PeerChangeListener peerChangeListener : PEER_CHANGE_LISTENERS) {
                peerChangeListener.allPeer(getPeers());
            }
        }
    }

    /**
     * 获取所有的peer
     * @return peer un modify set
     */
    public Set<String> getPeers() {
        return Collections.unmodifiableSet(PEER_HOST_SET);
    }

    /**
     * 订阅节点变化
     * @param peerChangeListener 节点变动监听者
     */
    public void listenPeer(PeerChangeListener peerChangeListener) {
        PEER_CHANGE_LISTENERS.add(peerChangeListener);
    }

    /**
     * 验证传入的peer字符串是不是本节点
     *
     * 验证的节点可能是最后带一个. 所以需要用全包含来验证
     *
     * @param validPeer 需要验证的节点
     * @return 是否本节点
     */
    public boolean isSelf(String validPeer) {
        return validPeer.contains(StorageConfig.getInstance().getHostname() + "." + SRV_SERVICE);
    }



    @Override
    public void componentStart() {
        find();
        TIMER.scheduleWithFixedDelay(this::find, 0L, 10_000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void componentStop() {

    }

    /**
     * 节点变动的监听接口
     */
    public interface PeerChangeListener {

        /**
         * 返回节点信息
         * @param peerSet 当前节点set
         */
        void allPeer(Set<String> peerSet);

    }

}
