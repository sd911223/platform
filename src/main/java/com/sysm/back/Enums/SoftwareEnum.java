package com.sysm.back.Enums;

import lombok.Getter;

/**
 * @author shidun
 */

@Getter
public enum SoftwareEnum {
    LAN_HAI_CI("lan_hai_ci", "蓝海词"),;
    private String code;

    private String message;

    SoftwareEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static SoftwareEnum parse(String code) {
        SoftwareEnum[] values = values();
        for (SoftwareEnum value : values) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new RuntimeException("Unknown code of ResultEnum");
    }
}
