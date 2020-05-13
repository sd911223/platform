package com.sysm.back.contorller;

import com.sysm.back.VO.ResultVO;
import com.sysm.back.service.WeChatPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

/**
 * @author shidun
 */
@RestController
@Slf4j
@RequestMapping("/weChat")
@Api(tags = "微信支付")
public class WeChatPayController {
    @Autowired
    WeChatPayService weChatPayService;

    @ApiOperation("下单")
    @PostMapping("/orderForm")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "活动ID", required = true),
            @ApiImplicitParam(name = "remark", value = "备注", required = false)
    })
    public Map<String, Object> orderForm(Integer id, String remark, HttpServletRequest request) throws Exception {
        if (Objects.isNull(id)) {
            return ResultVO.failure(401, "活动ID不能为空");
        }
        return weChatPayService.orderForm(id, request,remark);
    }

    @ApiOperation("微信支付回调")
    @PostMapping("/notify")
    public String weChatNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("进入微信回调");
        return weChatPayService.weChatNotify(request, response);
    }

}
