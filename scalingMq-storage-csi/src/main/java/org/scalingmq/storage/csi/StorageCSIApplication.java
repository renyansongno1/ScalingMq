package org.scalingmq.storage.csi;

import lombok.extern.slf4j.Slf4j;
import org.scalingmq.common.config.ConfigParseUtil;
import org.scalingmq.common.lifecycle.Lifecycle;
import org.scalingmq.common.utils.PropertiesUtil;
import org.scalingmq.storage.csi.config.StorageCsiConfig;
import org.scalingmq.storage.csi.csiserver.CsiGrpcServer;

/**
 * csi插件启动类
 * @author renyansong
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Slf4j
public class StorageCSIApplication {

    private static volatile boolean STOP = false;

    public static void main(String[] args) {
        // 加载配置文件
        PropertiesUtil.generateProperties(StorageCSIApplication.class, "application.properties", StorageCsiConfig.CONF_NAME);
        ConfigParseUtil.getInstance().parse(StorageCsiConfig.getInstance());

        // 启动unix socket 监听
        CsiGrpcServer csiGrpcServer = new CsiGrpcServer();
        try {
            csiGrpcServer.start(StorageCsiConfig.UNIX_SOCKET_PATH);
        } catch (Exception e) {
            log.error("csi启动异常", e);
            System.exit(1);
        }

        // shutdown 钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            csiGrpcServer.stop();
            STOP = true;
        }));

        // 挂起主线程
        while (!STOP) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

}
