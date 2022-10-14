package org.scalingmq.storage.exception;

import lombok.Getter;

/**
 * 错误码枚举
 * @author renyansong
 */
@Getter
public enum ExceptionCodeEnum {
    /**
     * 枚举
     */
    UNKNOWN("unknown"),

    // 没有找到对应的数据
    FETCH_MISS("fetch miss"),

    // 发送消息等待复制超时
    PRODUCE_TIMEOUT("produce timeout")
    ;

    private final String code;

    ExceptionCodeEnum(String code) {
        this.code = code;
    }

    /**
     * 通过str获取code
     * @param codeStr str
     * @return code
     */
    public static ExceptionCodeEnum getCodeByStr(String codeStr) {
        for (ExceptionCodeEnum value : values()) {
            if (value.getCode().equals(codeStr)) {
                return value;
            }
        }
        return null;
    }

}
