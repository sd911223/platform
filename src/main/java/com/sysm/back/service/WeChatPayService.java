package com.sysm.back.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface WeChatPayService {
    Map<String, Object> orderForm(Integer id, HttpServletRequest request,String remark) throws Exception;

    String weChatNotify(HttpServletRequest request, HttpServletResponse response);
}
