package org.scalingmq.storage.core.storage.impl;

import org.scalingmq.storage.conf.StorageConfig;

/**
 * ssd的实现
 * @author renyansong
 */
public class SsdStorage extends DiskStorage {

    public SsdStorage() {
    }

    @Override
    public int storagePriority() {
        return 98;
    }

    @Override
    public void componentStart() {
        super.init(StorageConfig.getInstance().getSsdMountPath());
    }
}
