package org.scalingmq.client.sidecar.iptable;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 关于nat的iptable转换规则管理
 * @author renyansong
 */
@Slf4j
public class NatIptableManager {

    private static final String BROKER_PORT = "BROKER_PORT";

    private static final NatIptableManager INSTANCE = new NatIptableManager();

    private static volatile boolean INIT = false;

    private NatIptableManager() {
        if (INSTANCE != null) {
            throw new RuntimeException("not support reflect invoke");
        }
    }

    public static NatIptableManager getInstance() {
        return INSTANCE;
    }

    public void init() {
        if (!INIT) {
            synchronized (this) {
                if (INIT) {
                    return;
                }
                // 初始化操作
                // iptables -t nat -A PREROUTING -p tcp --dport ${目标端口} -j DNAT --to-destination ${本地IP}:${本地端口}
                try {
                    // 获取Broker的端口
                    String brokerPort = System.getProperty(BROKER_PORT);

                    Process process = Runtime.getRuntime().exec(new String[]{"iptables",
                            "-t", "nat", "-A", "PREROUTING", "-p", "tcp", "--dport", brokerPort, "-j", "--to-destination",
                    "127.0.0.1:9999"});
                    InputStream inputStream = process.getInputStream();
                    BufferedReader read = new BufferedReader(new InputStreamReader(inputStream));
                    String result = read.readLine();
                    if (log.isDebugEnabled()) {
                        log.debug("iptable执行结果:{}", result);
                    }
                } catch (IOException e) {
                    log.error("执行iptables命令异常:", e);
                }
                INIT = true;
            }
        }
    }

}
