package org.scalingmq.storage.csi.config;

import org.scalingmq.common.utils.PropertiesUtil;

import java.util.Properties;

/**
 * 存储配置类
 * @author renyansong
 */
public class StorageCsiConfig {

    /**
     * 对应的配置namespace
     */
    public static final String CONF_NAME = "storage";

    private static final String CLI_VERSION_KEY = "k8s.plugin.version";

    public static final String CSI_PLUGIN_NAME = "org.scalingmq.csi";

    public static final String UNIX_SOCKET_PATH = "/var/lib/kubelet/plugins/" + CSI_PLUGIN_NAME + "/csi.sock";

    /*public static final String UNIX_SOCKET_PATH = "/Users/renyansong/.docker/run/docker.sock";*/


    private static final Properties PROPERTIES = PropertiesUtil.getProperties(CONF_NAME);

    /**
     * 获取存储插件版本
     */
    public static String getCliVersion() {
        return String.valueOf(PROPERTIES.get(CLI_VERSION_KEY));
    }

}
