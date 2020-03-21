package com.sysm.back.service;

import com.sysm.back.Enums.ActivityStatusEnum;

import java.util.Map;

public interface ActivityService {
    Map<String, Object> addActivity(Integer productId, String productName, Integer period, String activityName, String originalPrice, String purchasePrice, String discountPrice,String token);

    Map<String, Object> listActivity(String productName, ActivityStatusEnum status, Integer pageSize, Integer pageNum,String token);
}
