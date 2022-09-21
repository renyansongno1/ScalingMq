package org.scalingmq.storage.core.replicate.raft;

/**
 * raft的状态枚举
 * @author renyansong
 */
public enum RaftStateEnum {

    /**
     * raft 流程过程中的身份
     */
    CANDIDATE,

    LEADER,

    FOLLOWER,

    COORDINATOR,

    /**
     *
     */
    ;

}
