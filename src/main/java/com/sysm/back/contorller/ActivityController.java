package com.sysm.back.contorller;

import com.sysm.back.Enums.ActivityStatusEnum;
import com.sysm.back.VO.ResultVO;
import com.sysm.back.service.ActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author shi
 */
@RestController
@Slf4j
@RequestMapping("/activity")
@Api(tags = "活动管理")
public class ActivityController {
    @Autowired
    ActivityService activityService;

    @PostMapping("/addActivity")
    @ApiOperation("添加活动")
    @ApiImplicitParams({@ApiImplicitParam(name = "productId", value = "产品Id", required = true),
            @ApiImplicitParam(name = "productName", value = "产品名称", required = true),
            @ApiImplicitParam(name = "period", value = "期数", required = true),
            @ApiImplicitParam(name = "activityName", value = "活动名称", required = true),
            @ApiImplicitParam(name = "originalPrice", value = "原价", required = true),
            @ApiImplicitParam(name = "purchasePrice", value = "采购价", required = true),
            @ApiImplicitParam(name = "discountPrice", value = "优惠价格", required = true)})
    public Map<String, Object> addActivity(Integer productId, String productName, Integer period, String activityName, String originalPrice, String purchasePrice, String discountPrice, @RequestHeader("token") String token) {
        log.info("进入添加活动接口,产品ID{},产品名称{},期数{},活动名称{},原价{},采购价{},优惠价格{}", productId, productName, period, activityName, originalPrice, purchasePrice, discountPrice);
        if (null == productId) {
            return ResultVO.failure("产品Id不能为空");
        }
        if (StringUtils.isEmpty(productName)) {
            return ResultVO.failure("产品名称不能为空");
        }
        if (null == period) {
            return ResultVO.failure("期数不能为空");
        }
        if (StringUtils.isEmpty(activityName)) {
            return ResultVO.failure("活动名称不能为空");
        }
        if (StringUtils.isEmpty(originalPrice)) {
            return ResultVO.failure("原价不能为空");
        }
        if (StringUtils.isEmpty(purchasePrice)) {
            return ResultVO.failure("采购价不能为空");
        }
        if (StringUtils.isEmpty(discountPrice)) {
            return ResultVO.failure("优惠价格不能为空");
        }
        return activityService.addActivity(productId, productName, period, activityName, originalPrice, purchasePrice, discountPrice, token);
    }

    @PostMapping("/listActivity")
    @ApiOperation("活动列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productName", value = "产品名称", required = true),
            @ApiImplicitParam(name = "status", value = "状态", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", required = true),
            @ApiImplicitParam(name = "pageNum", value = "当前页", required = true)})
    public Map<String, Object> listActivity(String productName, ActivityStatusEnum status, Integer pageSize, Integer pageNum, @RequestHeader("token") String token) {
        log.info("进入添加活动接口,产品名称{},状态{},每页条数{},当前页{}", productName, status, pageSize, pageNum);


        if (null == status) {
            return ResultVO.failure("状态不能为空");
        }
        if (null == pageSize) {
            return ResultVO.failure("条数不能为空");
        }
        if (null == pageNum) {
            return ResultVO.failure("页数不能为空");
        }
        return activityService.listActivity(productName, status, pageSize, pageNum, token);
    }

    @PostMapping("/modifyActivity")
    @ApiOperation("修改活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "活动ID", required = true)})
    public Map<String, Object> modifyActivity(Integer id) {
        if (null == id) {
            return ResultVO.failure("活动ID不能为空");
        }
        return activityService.modifyActivity(id);
    }

    @GetMapping("/byId")
    @ApiOperation("查询活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "活动ID", required = true)})
    public Map<String, Object> qurryActivityById(Integer id) {
        if (null == id) {
            return ResultVO.failure("活动ID不能为空");
        }
        return activityService.qurryActivityById(id);
    }

}
