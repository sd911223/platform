package com.sysm.back.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sysm.back.VO.ResultVO;
import com.sysm.back.dao.GoodsOrderMapper;
import com.sysm.back.dao.SysUserMapper;
import com.sysm.back.model.GoodsOrder;
import com.sysm.back.model.GoodsOrderExample;
import com.sysm.back.model.SysUser;
import com.sysm.back.service.GoodsOrderService;
import com.sysm.back.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class GoodsOrderServiceImpl implements GoodsOrderService {
    @Autowired
    GoodsOrderMapper goodsOrderMapper;
    @Autowired
    SysUserMapper sysUserMapper;

    @Override
    public Map<String, Object> goodsOrderList(String orderNum, String activityName, Integer pageNum, Integer pageSize, String token) {
        PageHelper.startPage(pageNum, pageSize);
        SysUser sysUser = JwtUtil.getSysUser(token);
        GoodsOrderExample example = new GoodsOrderExample();
        GoodsOrderExample.Criteria criteria = example.createCriteria();
        criteria.andBelongPeopleEqualTo(sysUser.getId());
        if (!StringUtils.isEmpty(orderNum)) {
            criteria.andOrderIdEqualTo(orderNum);
        }
        if (!StringUtils.isEmpty(activityName)) {
            criteria.andActivityNameLike("%" + activityName + "%");
        }
        List<GoodsOrder> goodsOrderList = goodsOrderMapper.selectByExample(example);
        PageInfo<GoodsOrder> pageInfo = new PageInfo<>(goodsOrderList);
        List<GoodsOrder> list = pageInfo.getList();
        list.forEach(e -> {
            e.setPayAmountStr(new BigDecimal(e.getPayAmount().toString()).divide(new BigDecimal("100")).setScale(2).toString());
            e.setDifferenceAmountStr(new BigDecimal(e.getDifferenceAmount().toString()).divide(new BigDecimal("100")).setScale(2).toString());
        });
        return ResultVO.success(pageInfo);
    }

    @Override
    public Map<String, Object> goodsOrderProfit(String token) {
        SysUser sysUser = JwtUtil.getSysUser(token);
        GoodsOrderExample example = new GoodsOrderExample();
        example.createCriteria().andBelongPeopleEqualTo(sysUser.getId()).andPayStatusEqualTo("2");
        List<GoodsOrder> goodsOrders = goodsOrderMapper.selectByExample(example);
        int profitTotal = 0;
        if (goodsOrders.isEmpty()) {
            return ResultVO.success(new BigDecimal(profitTotal).divide(new BigDecimal("100")).setScale(2).toString());
        }
        for (GoodsOrder e : goodsOrders) {
            profitTotal += e.getDifferenceAmount();
        }
        return ResultVO.success(new BigDecimal(profitTotal).divide(new BigDecimal("100")).setScale(2).toString());
    }

    @Override
    public Map<String, Object> withdrawList(String orderNum, String activityName, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        GoodsOrderExample example = new GoodsOrderExample();
        GoodsOrderExample.Criteria criteria = example.createCriteria();
        criteria.andWithdrawStatusNotEqualTo("0");
        if (StringUtils.isEmpty(orderNum)) {
            criteria.andOrderIdLike("%" + orderNum + "%");
        }
        if (StringUtils.isEmpty(activityName)) {
            criteria.andOrderIdLike("%" + activityName + "%");
        }
        List<GoodsOrder> goodsOrderList = goodsOrderMapper.selectByExample(example);
        PageInfo<GoodsOrder> pageInfo = new PageInfo<>(goodsOrderList);
        List<GoodsOrder> list = pageInfo.getList();
        list.forEach(e -> {
            e.setPayAmountStr(new BigDecimal(e.getPayAmount().toString()).divide(new BigDecimal("100")).setScale(2).toString());
            e.setDifferenceAmountStr(new BigDecimal(e.getDifferenceAmount().toString()).divide(new BigDecimal("100")).setScale(2).toString());
            SysUser sysUser = sysUserMapper.selectByPrimaryKey(e.getBelongPeople());
            e.setBelongPeopleStr(sysUser.getUserName());
        });
        return ResultVO.success(pageInfo);
    }

    @Override
    public Map<String, Object> applyWithdraw(String orderNum, String token) {
        GoodsOrder goodsOrder = goodsOrderMapper.selectByPrimaryKey(orderNum);
        if (null == goodsOrder) {
            return ResultVO.failure("订单不存在!");
        }
        goodsOrder.setWithdrawStatus("1");
        goodsOrderMapper.updateByPrimaryKey(goodsOrder);
        return ResultVO.success();
    }

    @Override
    public Map<String, Object> updateWithdraw(String orderNum, String withdrawStatus) {
        GoodsOrder goodsOrder = goodsOrderMapper.selectByPrimaryKey(orderNum);
        if (null == goodsOrder) {
            return ResultVO.failure("订单不存在!");
        }
        goodsOrder.setWithdrawStatus(withdrawStatus);
        goodsOrderMapper.updateByPrimaryKey(goodsOrder);
        return ResultVO.success();
    }
}
