package com.sysm.back.contorller;

import com.sysm.back.Enums.UserRoleEnum;
import com.sysm.back.Enums.UserStatusEnum;
import com.sysm.back.VO.ResultVO;
import com.sysm.back.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@Api(tags = "用户")
public class UserContorller {
    @Autowired
    SysUserService sysUserService;


    @ApiOperation("创建用户接口")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "账号", required = true),
            @ApiImplicitParam(name = "password", value = "密码", required = true)
    })
    public Map<String, Object> register(String userName, String password, @RequestParam("userRole") UserRoleEnum userRoleEnum, @RequestHeader("token") String token) {
        if (StringUtils.isEmpty(userName.trim())) {
            return ResultVO.failure(401, "用户账号不能为空");
        }
        if (StringUtils.isEmpty(password.trim())) {
            return ResultVO.failure(401, "用户密码不能为空");
        }
        if (null == userRoleEnum) {
            return ResultVO.failure(401, "用户角色不能为空");
        }
        return sysUserService.userregister(userName, password, token,userRoleEnum);
    }

    @ApiOperation("修改密码接口")
    @RequestMapping(value = "/modifyPassword", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "账号", required = true),
            @ApiImplicitParam(name = "originalPassword", value = "原始密码", required = true),
            @ApiImplicitParam(name = "newPasswordOne", value = "新密码密码1", required = true),
            @ApiImplicitParam(name = "newPasswordTow", value = "新密码密码2", required = true)
    })
    public Map<String, Object> modifyPassword(String userName, String originalPassword, String newPasswordOne, String newPasswordTow, @RequestHeader("token") String token) {
        if (StringUtils.isEmpty(userName)) {
            return ResultVO.failure(401, "账号不能为空");
        }
        if (StringUtils.isEmpty(originalPassword.trim())) {
            return ResultVO.failure(401, "原始密码不能为空");
        }
        if (StringUtils.isEmpty(newPasswordOne.trim()) || StringUtils.isEmpty(newPasswordTow.trim())) {
            return ResultVO.failure(401, "新密码不能为空");
        }
        if ((originalPassword.equals(newPasswordOne)) || (originalPassword.equals(newPasswordTow))) {
            return ResultVO.failure(401, "新密码不能与原始密码一样");
        }
        if (!newPasswordOne.equals(newPasswordTow)) {
            return ResultVO.failure(401, "新密码不一致");
        }
        return sysUserService.modifyPassword(userName, originalPassword, newPasswordOne, newPasswordTow, token);
    }

    @ApiOperation("账号启用/禁用")
    @RequestMapping(value = "/userOperate", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "账号ID", required = true)
    })
    public Map<String, Object> userOperate(Integer userId, @RequestParam("userStatus") UserStatusEnum userStatusEnum, @RequestHeader("token") String token) {
        if (null == userId) {
            return ResultVO.failure(401, "账号ID不能为空");
        }
        if (null == userStatusEnum) {
            return ResultVO.failure(401, "用户状态不能为空");
        }
        return sysUserService.userOperate(userId, userStatusEnum, token);
    }

    @ApiOperation("账号列表")
    @RequestMapping(value = "/listUser", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "账号名称", required = false),
            @ApiImplicitParam(name = "userStatus", value = "账号状态", required = false),
            @ApiImplicitParam(name = "pageNum", value = "当前页", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", required = true)
    })
    public Map<String, Object> listUser(@RequestHeader("token") String token, String userName, String userStatus, Integer pageNum, Integer pageSize) {

        return sysUserService.listUser(token, userName, userStatus, pageNum, pageSize);
    }
}
