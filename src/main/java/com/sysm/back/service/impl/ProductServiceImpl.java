package com.sysm.back.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sysm.back.VO.ResultVO;
import com.sysm.back.bean.dto.Product;
import com.sysm.back.bean.dto.ProductInfoDTO;
import com.sysm.back.bean.vo.ProductVO;
import com.sysm.back.dao.ProductInfoMapper;
import com.sysm.back.dao.ProductKindMapper;
import com.sysm.back.model.ProductInfo;
import com.sysm.back.model.ProductInfoExample;
import com.sysm.back.model.ProductKind;
import com.sysm.back.model.ProductKindExample;
import com.sysm.back.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author shidun
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductInfoMapper productInfoMapper;
    @Autowired
    ProductKindMapper productKindMapper;

    @Override
    @Transactional
    public Map<String, Object> addProduct(Product product) {
        ProductInfo productInfo = new ProductInfo();
        ProductKind productKind = new ProductKind();
        ProductKindExample kindExample = new ProductKindExample();
        kindExample.createCriteria().andKindNameLike(product.getProductName()).andIsEffectiveEqualTo(Byte.valueOf("0"));
        List<ProductKind> kindList = productKindMapper.selectByExample(kindExample);
        if (kindList.isEmpty()) {
            productKind.setCreateTime(new Date());
            productKind.setIsEffective(Byte.valueOf("0"));
            productKind.setKindName(product.getProductName());
            productKindMapper.insertSelective(productKind);
            List<ProductInfoDTO> infoList = product.getProductInfoDTOS();
            infoList.forEach(e -> {
                productInfo.setProductId(productKind.getId());
                productInfo.setProductNum(e.getMonthNum());
                productInfo.setProductPrice(Integer.valueOf(new BigDecimal(e.getProductPrice().toString()).multiply(new BigDecimal("100")).toString()));
                productInfo.setDiscountPrice(Integer.valueOf(new BigDecimal(e.getDiscountPrice().toString()).multiply(new BigDecimal("100")).toString()));
                productInfoMapper.insert(productInfo);
            });
            return ResultVO.success();
        }
        return ResultVO.failure(401, "产品名称重复");
    }

    @Override
    public Map<String, Object> listProduct(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ProductVO> list = new ArrayList<ProductVO>();
        ProductKindExample kindExample = new ProductKindExample();
        kindExample.createCriteria().andIsEffectiveEqualTo(Byte.valueOf("0"));
        List<ProductKind> kindList = productKindMapper.selectByExample(kindExample);
        PageInfo<ProductKind> pageInfo = new PageInfo<>(kindList);
        List<ProductKind> pageInfoList = pageInfo.getList();
        if (!pageInfoList.isEmpty()) {
            pageInfoList.forEach(e -> {
                ProductVO productVO = new ProductVO();
                List<ProductInfoDTO> voList = new ArrayList<ProductInfoDTO>();
                productVO.setId(e.getId());
                productVO.setProductName(e.getKindName());
                ProductInfoExample infoExample = new ProductInfoExample();
                infoExample.createCriteria().andProductIdEqualTo(e.getId());
                List<ProductInfo> infoList = productInfoMapper.selectByExample(infoExample);
                infoList.forEach(info -> {
                    ProductInfoDTO productInfoDTO = new ProductInfoDTO();
                    productInfoDTO.setMonthNum(info.getProductNum());
                    productInfoDTO.setProductPriceStr(new BigDecimal(info.getProductPrice().toString()).divide(new BigDecimal(100)).setScale(2).toString());
                    productInfoDTO.setDiscountPriceStr(new BigDecimal(info.getDiscountPrice().toString()).divide(new BigDecimal(100)).setScale(2).toString());
                    voList.add(productInfoDTO);
                });
                productVO.setList(voList);
                list.add(productVO);
            });
            PageInfo<ProductVO> voPageInfo = new PageInfo<>();
            voPageInfo.setTotal(pageInfo.getTotal());
            voPageInfo.setPageNum(pageInfo.getPageNum());
            voPageInfo.setPages(pageInfo.getPages());
            voPageInfo.setPageSize(pageInfo.getPageSize());
            voPageInfo.setList(list);
            return ResultVO.success(voPageInfo);
        } else {
            PageInfo<ProductVO> voPageInfo = new PageInfo<>();
            return ResultVO.success(voPageInfo);
        }
    }

    @Override
    public Map<String, Object> modifyProduct(Integer productId) {
        ProductKind kind = productKindMapper.selectByPrimaryKey(productId);
        kind.setIsEffective(Byte.valueOf("1"));
        productKindMapper.updateByPrimaryKey(kind);
        return ResultVO.success();
    }
}
