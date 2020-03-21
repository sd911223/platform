package com.sysm.back.contorller;

import com.sysm.back.VO.ResultVO;
import com.sysm.back.bean.dto.Product;
import com.sysm.back.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

/**
 * @author shidun
 */
@RestController
@RequestMapping("/product")
@Api(tags = "产品")
public class ProductController {
    @Autowired
    ProductService productService;

    @ApiOperation("添加商品")
    @PostMapping("/addProduct")
    public Map<String, Object> addProduct(@RequestBody Product product) {
        if (Objects.isNull(product)) {
            return ResultVO.failure(401, "产品不能为空");
        }
        return productService.addProduct(product);
    }

    @ApiOperation("产品列表")
    @PostMapping("/listProduct")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "当前页", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", required = true)})
    public Map<String, Object> listProduct(Integer pageNum, Integer pageSize) {

        return productService.listProduct(pageNum, pageSize);
    }

    @ApiOperation("删除产品")
    @PostMapping("/modifyProduct")
    @ApiImplicitParams({@ApiImplicitParam(name = "productId", value = "产品ID", required = true)
    })
    public Map<String, Object> modifyProduct(Integer productId) {
        if (null == productId) {
            return ResultVO.failure("产品ID不能为空");
        }
        return productService.modifyProduct(productId);
    }



}
