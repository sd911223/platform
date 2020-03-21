package com.sysm.back.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shidun
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("产品信息")
public class ProductInfoDTO {
    @ApiModelProperty("月份")
    private Integer monthNum;

    @ApiModelProperty("产品价格(分)")
    private Integer productPrice;

    @ApiModelProperty("优惠价格(分)")
    private Integer discountPrice;

    @ApiModelProperty("产品价格(元)")
    private String productPriceStr;

    @ApiModelProperty("优惠价格(元)")
    private String discountPriceStr;
}
