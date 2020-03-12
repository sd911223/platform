package com.sysm.back.contorller;

import com.sysm.back.VO.ResultVO;
import com.sysm.back.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 对外接口
 */
@RestController
@RequestMapping("other/user")
@Api(tags = "对外接口")
@Slf4j
public class ForeignContorller {
    @Autowired
    SysUserService sysUserService;

    @ApiOperation("注册")
    @RequestMapping(value = "/registered", method = RequestMethod.POST)
    public Map<String, Object> registrationUser(@RequestParam(name = "sign", required = true) String sign) {
        log.info("进入第三方注册接口------>加密串:{}", sign);
        if ("".equals(sign)) {
            return ResultVO.failure(401, "加密串不能为空");
        }
        return sysUserService.registrationUser(sign);
    }

    @ApiOperation("修改密码")
    @RequestMapping(value = "/modifyPassword", method = RequestMethod.POST)
    public Map<String, Object> modifyPassword(@RequestParam(name = "sign", required = true) String sign) {
        log.info("进入第三方修改密码接口------>加密串:{}", sign);
        if ("".equals(sign)) {
            return ResultVO.failure(401, "加密串不能为空");
        }
        return sysUserService.otherModifyPassword(sign);
    }
}
