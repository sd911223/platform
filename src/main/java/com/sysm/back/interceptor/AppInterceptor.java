package com.sysm.back.interceptor;

import com.alibaba.fastjson.JSON;
import com.sysm.back.Enums.ResultEnum;
import com.sysm.back.VO.ResultVO;
import com.sysm.back.model.SysUser;
import com.sysm.back.utils.JwtUtil;
import com.sysm.back.utils.RedisUtil;
import com.sysm.back.utils.SpringUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 用户拦截器
 *
 * @author hz
 */
@Component
public class AppInterceptor implements HandlerInterceptor {

    /**
     * redis服务
     **/
    private static RedisUtil redisUtil;

    /**
     * redis服务
     */
    private static RedisUtil getRedisService() {
        if (redisUtil == null) {
            redisUtil = (RedisUtil) SpringUtil.getBean("redisUtils");
        }
        return redisUtil;
    }

//    private static final List<String> noLoginResources = new ArrayList<String>() {
//        private static final long serialVersionUID = 1L;
//
//        {
//            add("/user/findPassword");
//            add("/user/login");
//            add("/error");
//            add("/sendCode/sendPhoneCode");
//            add("/app/company/versions");
//            add("/open/validateCompany");
//            add("/user/forget");
//            add("/company/account/income/add");
//            add("/company/byCompanyId");
//            add("/company/companyNotice");
//            add("/company/identity/queryUserIdentity");
//            add("/company/identity/getCompanyLegalPerson");
//            add("/open/queryCompanyByUrl");
//            add("/open/querySubCompanyList");
//            add("/user/checkoutPassword");
//            add("/user/checkoutPhone");
//            add("/user/getPhone");
//            add("/user/resetPassword");
//            add("/user/validatePhoneForforget");
//            add("/user/listUserByPhone");
//            add("/swagger");
//        }
//    };


    /**
     * 在请求到达运行的方法之前，用于拦截非法请求
     * 在controlller之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object controlller) throws Exception {
        // 不需要进行访问控制的资源过滤

//        String uri = request.getRequestURI();
//        for (String resource : noLoginResources) {
//            if (uri.startsWith(resource)) {
//                return true;
//            }
//        }
        String token = request.getHeader("token");
        //如果不是映射到方法直接通过
        if (!(controlller instanceof HandlerMethod)) {
            return true;
        }
        if (token == null || token.equals("")) {
            response.getWriter().write(JSON.toJSONString(ResultVO.result(ResultEnum.LOGIN_IS_OVERDUE, false)));
            return false;
        }
        Object redisToken = getRedisService().get(token);
        if (Objects.isNull(redisToken)) {
            response.getWriter().write(JSON.toJSONString(ResultVO.result(ResultEnum.LOGIN_IS_OVERDUE, false)));
            return false;
        }
        SysUser sysUser = JwtUtil.getSysUser(token);
        if (null == sysUser) {
            response.getWriter().write(JSON.toJSONString(ResultVO.result(ResultEnum.LOGIN_IS_OVERDUE, false)));
            return false;
        }
        return true;

    }


    /**
     * 用于重定向方法，这个方法可以重新返回一个新的页面，进行新的数据展示
     * 在controller之后
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    /**
     * 所有程序完成之后最终会执行的方法，一般用于销毁对象IO等操作
     * 在postHandle之后
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }

}
