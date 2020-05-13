package com.sysm.back.service;

import java.util.Map;

public interface GoodsOrderService {
    Map<String, Object> goodsOrderList(String orderNum, String activityName,Integer pageNum,Integer pageSize,String token);

    Map<String, Object> goodsOrderProfit(String token);

    Map<String, Object> withdrawList(String orderNum, String activityName, Integer pageNum, Integer pageSize);

    Map<String, Object> applyWithdraw(String orderNum, String token);

    Map<String, Object> updateWithdraw(String orderNum, String withdrawStatus);
}
