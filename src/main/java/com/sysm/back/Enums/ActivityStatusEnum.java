package com.sysm.back.Enums;

import lombok.Getter;

/**
 * @author shidun
 */
@Getter
public enum ActivityStatusEnum {

    use(0, "正使用"),
    Disable(1, "已停用"),
    ALL(3, "全部"),
    ;

    private Integer code;

    private String message;

    ActivityStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * @deprecation:通过code返回枚举
     */
    public static ActivityStatusEnum parse(int code) {
        ActivityStatusEnum[] values = values();
        for (ActivityStatusEnum value : values) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new RuntimeException("Unknown code of ResultEnum");
    }
}
