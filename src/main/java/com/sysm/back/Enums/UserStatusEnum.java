package com.sysm.back.Enums;

import lombok.Getter;

@Getter
public enum UserStatusEnum {
    SWITCH_ON("0", "启用"),
    PROHIBIT("1", "禁用");
    private String code;

    private String message;

    UserStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static UserStatusEnum parse(String code) {
        UserStatusEnum[] values = values();
        for (UserStatusEnum value : values) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new RuntimeException("Unknown code of ResultEnum");
    }
}
