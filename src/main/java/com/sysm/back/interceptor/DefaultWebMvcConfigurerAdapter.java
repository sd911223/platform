package com.sysm.back.interceptor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

/**
 * Spring MVC 的辅助配置, 用来注册拦截器.
 *
 * @author hugy
 */
@Configuration
public class DefaultWebMvcConfigurerAdapter extends WebMvcConfigurerAdapter {

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 访问权限控制
        AppInterceptor companyAPPInterceptor = applicationContext.getBean(AppInterceptor.class);
        registry.addInterceptor(companyAPPInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login/**", "other/user/**", "/swagger-resources/**");
    }


}
