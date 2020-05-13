package com.sysm.back.contorller;

import com.sysm.back.service.GoodsOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author shi
 */
@RestController
@Slf4j
@RequestMapping("/goodsOrder")
@Api(tags = "商品订单")
public class GoodsOrderController {
    @Autowired
    GoodsOrderService goodsOrderService;

    @PostMapping("/list")
    @ApiOperation("查询销售活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNum", value = "订单号", required = false),
            @ApiImplicitParam(name = "activityName", value = "活动名称", required = false),
            @ApiImplicitParam(name = "pageNum", value = "当前页", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", required = true)
    })
    public Map<String, Object> goodsOrderList(String orderNum, String activityName, Integer pageNum, Integer pageSize, @RequestHeader String token) {

        return goodsOrderService.goodsOrderList(orderNum, activityName, pageNum, pageSize, token);
    }

    @PostMapping("/profit")
    @ApiOperation("利润总额")
    public Map<String, Object> goodsOrderProfit(@RequestHeader String token) {

        return goodsOrderService.goodsOrderProfit(token);
    }

    @PostMapping("/applyWithdraw")
    @ApiOperation("申请提现")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNum", value = "订单号", required = true)
    })
    public Map<String, Object> applyWithdraw(String orderNum, @RequestHeader String token) {

        return goodsOrderService.applyWithdraw(orderNum, token);
    }

    @PostMapping("/updateWithdraw")
    @ApiOperation("提现审核 ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNum", value = "订单号", required = true),
            @ApiImplicitParam(name = "withdrawStatus", value = "提现状态", required = true)
    })
    public Map<String, Object> updateWithdraw(String orderNum, String withdrawStatus) {

        return goodsOrderService.updateWithdraw(orderNum, withdrawStatus);
    }

    @PostMapping("/withdrawList")
    @ApiOperation("提现列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNum", value = "订单号", required = false),
            @ApiImplicitParam(name = "activityName", value = "活动名称", required = false),
            @ApiImplicitParam(name = "pageNum", value = "当前页", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", required = true)
    })
    public Map<String, Object> withdrawList(String orderNum, String activityName, Integer pageNum, Integer pageSize) {

        return goodsOrderService.withdrawList(orderNum, activityName, pageNum, pageSize);
    }
}
