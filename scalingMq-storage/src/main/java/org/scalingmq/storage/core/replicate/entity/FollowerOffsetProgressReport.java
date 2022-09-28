package org.scalingmq.storage.core.replicate.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 从节点的复制进度上报
 * @author renyansong
 */
@Getter
@Setter
@Builder
public class FollowerOffsetProgressReport {

    /**
     * 从节点的hostname
     */
    private String followerHostname;

    /**
     * 最后一次拉取时间
     */
    private Long lastFetchOffset;

}
