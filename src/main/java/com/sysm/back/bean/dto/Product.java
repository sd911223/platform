package com.sysm.back.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author shidun
 */
@ApiModel("产品")
@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Product {
    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("产品信息")
    private List<ProductInfoDTO> productInfoDTOS;
}
