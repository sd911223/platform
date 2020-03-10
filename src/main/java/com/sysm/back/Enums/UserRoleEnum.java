package com.sysm.back.Enums;

import lombok.Getter;

@Getter
public enum UserRoleEnum {
    ROLE_ADMIN("ROLE_ADMIN", "超级管理员"),
    ROLE_USER("ROLE_USER", "普通用户");
    private String code;

    private String message;

    UserRoleEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static UserRoleEnum parse(String code) {
        UserRoleEnum[] values = values();
        for (UserRoleEnum value : values) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new RuntimeException("Unknown code of ResultEnum");
    }
}
