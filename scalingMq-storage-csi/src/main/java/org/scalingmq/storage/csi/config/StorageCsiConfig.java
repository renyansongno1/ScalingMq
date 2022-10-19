package org.scalingmq.storage.csi.config;

import org.scalingmq.common.config.EnvironmentVariable;
import org.scalingmq.common.utils.PropertiesUtil;

import java.util.Properties;

/**
 * 存储配置类
 * @author renyansong
 */
public class StorageCsiConfig {

    private static final StorageCsiConfig INSTANCE = new StorageCsiConfig();

    private StorageCsiConfig() {}

    public static StorageCsiConfig getInstance() {
        return INSTANCE;
    }

    /**
     * 对应的配置namespace
     */
    public static final String CONF_NAME = "storage";

    private static final String CLI_VERSION_KEY = "k8s.plugin.version";

    public static final String CSI_PLUGIN_NAME = "org.scalingmq.csi";

    public static final String UNIX_SOCKET_PATH = "/var/lib/kubelet/plugins/" + CSI_PLUGIN_NAME + "/csi.sock";

    private static final Properties PROPERTIES = PropertiesUtil.getProperties(CONF_NAME);

    /**
     * 协调者节点编号
     */
    @EnvironmentVariable("CLOUD_ENV")
    private String cloudEnv;

    /**
     * 获取存储插件版本
     */
    public static String getCliVersion() {
        return String.valueOf(PROPERTIES.get(CLI_VERSION_KEY));
    }

    public String getCloudEnv() {
        return cloudEnv;
    }

    public void setCloudEnv(String cloudEnv) {
        this.cloudEnv = cloudEnv;
    }

    /**
     * 容器环境
     */
    public enum CloudType {
        /**
         * 容器环境枚举
         */
        ALI_YUN,
        TENCENT_YUN,
        HUAWEI_YUN,
        LOCAL,
        ;

        /**
         * 通过str名称获取枚举
         */
        public static CloudType get(String env) {
            return CloudType.valueOf(env);
        }
    }

}
