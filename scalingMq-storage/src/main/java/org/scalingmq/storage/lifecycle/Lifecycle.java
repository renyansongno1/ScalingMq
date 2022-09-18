package org.scalingmq.storage.lifecycle;

/**
 * 组件生命周期接口
 * @author renyansong
 */
public interface Lifecycle {

    /**
     * 组件启动
     */
    void componentStart();

    /**
     * 组件停止
     */
    void componentStop();

}
