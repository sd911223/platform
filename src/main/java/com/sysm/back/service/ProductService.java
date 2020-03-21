package com.sysm.back.service;

import com.sysm.back.bean.dto.Product;

import java.util.Map;

/**
 * @author shidun
 */
public interface ProductService {
    /**
     * 添加产品
     *
     * @param product
     * @return
     */
    Map<String, Object> addProduct(Product product);

    /**
     * 产品列表
     *
     * @return
     */
    Map<String, Object> listProduct(Integer pageNum, Integer pageSize);

    /**
     * 删除产品
     *
     * @param productId
     * @return
     */
    Map<String, Object> modifyProduct(Integer productId);
}
