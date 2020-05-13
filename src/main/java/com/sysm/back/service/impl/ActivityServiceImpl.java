package com.sysm.back.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sysm.back.Enums.ActivityStatusEnum;
import com.sysm.back.VO.ResultVO;
import com.sysm.back.dao.ActivityMapper;
import com.sysm.back.model.Activity;
import com.sysm.back.model.ActivityExample;
import com.sysm.back.model.SysUser;
import com.sysm.back.service.ActivityService;
import com.sysm.back.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author shidun
 */
@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    ActivityMapper activityMapper;

    @Override
    public Map<String, Object> addActivity(Integer productId, String productName, Integer period, String activityName, String originalPrice, String purchasePrice, String discountPrice, String token) {
        SysUser sysUser = JwtUtil.getSysUser(token);
        Activity activity = new Activity();
        activity.setCreateTime(new Date());
        activity.setIsEffective(0);
        activity.setProductId(productId);
        activity.setProductName(productName);
        activity.setPeriod(period);
        activity.setActivityName(activityName);
        Integer fenToYuan = getFenToYuan(originalPrice);
        activity.setOriginalPrice(fenToYuan);
        Integer purchasePr = getFenToYuan(purchasePrice);
        activity.setPurchasePrice(purchasePr);
        Integer discountPr = getFenToYuan(discountPrice);
        activity.setDiscountPrice(discountPr);
        activity.setDifferencePrice(discountPr - purchasePr);
        activity.setCreateId(sysUser.getId());
        activity.setSpreadUrl("http://www.taohuohuo.cn/ttviews");
        activityMapper.insertSelective(activity);
        return ResultVO.success();
    }

    @Override
    public Map<String, Object> listActivity(String productName, ActivityStatusEnum status, Integer pageSize, Integer pageNum, String token) {
        PageHelper.startPage(pageNum, pageSize);
        SysUser sysUser = JwtUtil.getSysUser(token);
        ActivityExample activityExample = new ActivityExample();
        ActivityExample.Criteria criteria = activityExample.createCriteria();
        criteria.andCreateIdEqualTo(sysUser.getId());
        if (!StringUtils.isEmpty(productName)) {
            criteria.andProductNameLike(productName);
        }
        if (status.getCode() != 3) {
            criteria.andIsEffectiveEqualTo(status.getCode());
        }
        List<Activity> activities = activityMapper.selectByExample(activityExample);
        PageInfo<Activity> pageInfo = new PageInfo<>(activities);
        List<Activity> list = pageInfo.getList();
        list.forEach(e->{
            e.setPurchasePriceStr(new BigDecimal(e.getPurchasePrice().toString()).divide(new BigDecimal("100")).setScale(2).toString());
            e.setDifferencePriceStr(new BigDecimal(e.getDifferencePrice().toString()).divide(new BigDecimal("100")).setScale(2).toString());
            e.setOriginalPriceStr(new BigDecimal(e.getOriginalPrice().toString()).divide(new BigDecimal("100")).setScale(2).toString());
            e.setDiscountPriceStr(new BigDecimal(e.getDiscountPrice().toString()).divide(new BigDecimal("100")).setScale(2).toString());
        });
        return ResultVO.success(pageInfo);
    }

    @Override
    public Map<String, Object> modifyActivity(Integer id) {
        Activity activity = activityMapper.selectByPrimaryKey(id);
        activity.setIsEffective(1);
        activity.setUpdateTime(new Date());
        activityMapper.updateByPrimaryKey(activity);
        return ResultVO.success();
    }

    @Override
    public Map<String, Object> qurryActivityById(Integer id) {
        Activity activity = activityMapper.selectByPrimaryKey(id);
        activity.setPurchasePriceStr(new BigDecimal(activity.getPurchasePrice().toString()).divide(new BigDecimal("100")).setScale(2).toString());
        activity.setDiscountPriceStr(new BigDecimal(activity.getDiscountPrice().toString()).divide(new BigDecimal("100")).setScale(2).toString());
        activity.setDifferencePriceStr(new BigDecimal(activity.getDifferencePrice().toString()).divide(new BigDecimal("100")).setScale(2).toString());
        return ResultVO.success(activity);
    }


    private Integer getFenToYuan(String fen) {
        String integer = new BigDecimal(fen).multiply(new BigDecimal("100")).setScale(0).toString();
        return Integer.valueOf(integer);
    }
}
