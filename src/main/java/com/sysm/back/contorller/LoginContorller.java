package com.sysm.back.contorller;

import com.sysm.back.VO.ResultVO;
import com.sysm.back.service.SysUserService;
import com.sysm.back.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
@Api(tags = "登录")
public class LoginContorller {

    @Autowired
    SysUserService sysUserService;

    @Autowired
    RedisUtil redisUtil;

    @ApiOperation("用户登录接口")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "账号", required = true),
            @ApiImplicitParam(name = "password", value = "密码", required = true)
    })
    public Map<String, Object> login(String userName, String password) {
        if (StringUtils.isEmpty(userName.trim())) {
            return ResultVO.failure(401, "用户账号不能为空");
        }
        if (StringUtils.isEmpty(password.trim())) {
            return ResultVO.failure(401, "用户密码不能为空");
        }
        return sysUserService.userLogin(userName, password);
    }

    @ApiOperation("用户登出接口")
    @RequestMapping(value = "/loginOut", method = RequestMethod.POST)
    public Map<String, Object> loginOut(@RequestHeader("token") String token) {
        redisUtil.delete(token);
        return ResultVO.success();
    }
}
