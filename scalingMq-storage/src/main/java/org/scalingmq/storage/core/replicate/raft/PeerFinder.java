package org.scalingmq.storage.core.replicate.raft;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.lifecycle.Lifecycle;
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
        log.info("查询SRV:{} 下所有的域名服务", SRV_SERVICE);

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
                                            log.info("Host " + mx.getTarget() + " has preference " + mx.getPriority());
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
        log.info("当前所有的peer域名:{}",Arrays.toString(PEER_HOST_SET.toArray()));
    }

    /**
     * 获取所有的peer
     * @return peer un modify set
     */
    public Set<String> getPeers() {
        return Collections.unmodifiableSet(PEER_HOST_SET);
    }

    @Override
    public void componentStart() {
        find();
        TIMER.scheduleWithFixedDelay(this::find, 0L, 10_000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void componentStop() {

    }
}
