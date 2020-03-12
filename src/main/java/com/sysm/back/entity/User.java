package com.sysm.back.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @ApiModelProperty("手机号")
    private String phoneNum;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("平台标识")
    private String platformSign;

    @ApiModelProperty("IP")
    private String ip;
}
