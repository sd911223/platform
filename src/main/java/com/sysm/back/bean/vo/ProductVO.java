package com.sysm.back.bean.vo;

import com.sysm.back.bean.dto.ProductInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author shidun
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVO {
    private Integer id;

    private String productName;

    private List<ProductInfoDTO> list;
}
