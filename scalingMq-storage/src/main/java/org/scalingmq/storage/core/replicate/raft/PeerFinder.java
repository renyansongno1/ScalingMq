package org.scalingmq.storage.core.replicate.raft;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.storage.conf.StorageConfig;
import org.scalingmq.storage.lifecycle.Lifecycle;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
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
        Hashtable<String, String> env = new Hashtable<>(2);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");

        try {
            DirContext ctx = new InitialDirContext(env);
            Attributes attributes = ctx.getAttributes("_" + StorageConfig.getInstance().getServiceName() + "._tcp." + SRV_NAME_SUFFIX,
                    new String [] { "SRV" });

            for (Enumeration<? extends Attribute> e = attributes.getAll(); e.hasMoreElements();) {
                Attribute a = e.nextElement();
                int size = a.size();
                for (int i = 0; i < size; i++) {
                    PEER_HOST_SET.add((String) a.get(i));
                }
            }
        } catch (NamingException e) {
            log.error("naming srv error..", e);
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
