package com.sysm.back.service.impl;

import com.sysm.back.VO.ResultVO;
import com.sysm.back.dao.ActivityMapper;
import com.sysm.back.dao.GoodsOrderMapper;
import com.sysm.back.model.Activity;
import com.sysm.back.model.GoodsOrder;
import com.sysm.back.service.WeChatPayService;
import com.sysm.back.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.jdom.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Service
@Slf4j
public class WeChatPayServiceImpl implements WeChatPayService {
    @Autowired
    ActivityMapper activityMapper;
    @Autowired
    GoodsOrderMapper goodsOrderMapper;
    @Autowired
    HttpServletRequest request;
    @Value("${WeChat.appid}")
    private String appid;
    @Value("${WeChat.mch_id}")
    private String mch_id;
    @Value("${WeChat.url}")
    private String url;
    @Value("${WeChat.api_key}")
    private String api_key;

    @Override
    public Map<String, Object> orderForm(Integer id, HttpServletRequest request, String remark) throws Exception {
        synchronized (this) {
            Activity activity = activityMapper.selectByPrimaryKey(id);
            if (null == activity) {
                return ResultVO.failure("无效活动");
            }
            SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
            String ipAddress = AccessAddressUtil.getIpAddress(request);
            String currTime = PayToolUtil.getCurrTime();
            String strTime = currTime.substring(8, currTime.length());
            String strRandom = String.valueOf(PayToolUtil.buildRandom(4));
            String generateOrder = GenerateNum.getInstance().GenerateOrder();
            //公众账号ID
            packageParams.put("appid", appid);
            //商户号
            packageParams.put("mch_id", mch_id);
            //随机字符串
            packageParams.put("nonce_str", strTime + strRandom);
            //商品描述
            packageParams.put("body", "淘火火充值中心-会员充值");
            //商户订单号
            packageParams.put("out_trade_no", generateOrder);
            //标价金额 订单总金额，单位为分
            packageParams.put("total_fee", activity.getDiscountPrice().toString());
            //终端IP APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP
            packageParams.put("spbill_create_ip", ipAddress);
            //通知地址 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数
            packageParams.put("notify_url", "http://118.126.108.210:8081//weChat/notify");
            //交易类型 NATIVE 扫码支付
            packageParams.put("trade_type", "NATIVE");
            // 签名
            String sign = PayToolUtil.createSign("UTF-8", packageParams, api_key);
            packageParams.put("sign", sign);
            // 将请求参数转换为xml格式的string
            String requestXML = PayToolUtil.getRequestXml(packageParams);
            // 调用微信支付统一下单接口
            String resXml = HttpUtil.postData(url, requestXML);

            // 解析微信支付结果
            Map map = null;
            try {
                map = XMLUtil4jdom.doXMLParse(resXml);
                if ("SUCCESS".equals(map.get("result_code"))) {
                    HashMap<String, String> hashMap = new HashMap<>(2);
                    hashMap.put("code_url", map.get("code_url").toString());
                    hashMap.put("order", generateOrder);
                    GoodsOrder goodsOrder = new GoodsOrder();
                    goodsOrder.setOrderId(generateOrder);
                    goodsOrder.setActivityId(activity.getId());
                    goodsOrder.setActivityName(activity.getActivityName());
                    goodsOrder.setPayAmount(activity.getDiscountPrice());
                    goodsOrder.setDifferenceAmount(activity.getDifferencePrice());
                    goodsOrder.setPayStatus("0");
                    if (!StringUtils.isEmpty(remark)) {
                        goodsOrder.setRemark(remark);
                    } else {
                        goodsOrder.setRemark("");
                    }
                    goodsOrder.setBelongPeople(activity.getCreateId());
                    goodsOrder.setCreateTime(new Date());
                    goodsOrder.setTradeType("NATIVE");
                    goodsOrder.setIpAddress(ipAddress);
                    goodsOrder.setWithdrawStatus("0");
                    goodsOrderMapper.insertSelective(goodsOrder);
                    return ResultVO.success(hashMap);
                } else {
                    return ResultVO.failure(map.get("return_msg").toString());
                }
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ResultVO.failure("支付失败");
    }

    @Override
    public String weChatNotify(HttpServletRequest request, HttpServletResponse response) {
        //读取参数
        InputStream inputStream;
        StringBuffer sb = null;
        try {
            sb = new StringBuffer();
            inputStream = request.getInputStream();
            String s;
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((s = in.readLine()) != null) {
                sb.append(s);
            }
            in.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //解析xml成map
        Map<String, String> map = new HashMap<String, String>();
        try {
            map = XMLUtil4jdom.doXMLParse(sb.toString());
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //过滤空 设置 TreeMap
        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String parameter = (String) it.next();
            String parameterValue = map.get(parameter);
            String v = "";
            if (null != parameterValue) {
                v = parameterValue.trim();
            }
            packageParams.put(parameter, v);
        }
        //判断签名是否正确
        if (PayToolUtil.isTenpaySign("UTF-8", packageParams, api_key)) {
            //------------------------------
            //处理业务开始
            //------------------------------
            String resXml = "";
            if ("SUCCESS".equals((String) packageParams.get("result_code"))) {
                // 这里是支付成功
                //////////执行自己的业务逻辑////////////////
                String mch_id = (String) packageParams.get("mch_id");
                String openid = (String) packageParams.get("openid");
                String is_subscribe = (String) packageParams.get("is_subscribe");
                String out_trade_no = (String) packageParams.get("out_trade_no");

                String total_fee = (String) packageParams.get("total_fee");

                //////////执行自己的业务逻辑////////////////
                GoodsOrder goodsOrder = goodsOrderMapper.selectByPrimaryKey(out_trade_no);
                goodsOrder.setPayStatus("2");
                goodsOrderMapper.updateByPrimaryKey(goodsOrder);
                //暂时使用最简单的业务逻辑来处理：只是将业务处理结果保存到session中
                //（根据自己的实际业务逻辑来调整，很多时候，我们会操作业务表，将返回成功的状态保留下来）
//                request.getSession().setAttribute("_PAY_RESULT", "OK");

                log.info("支付成功");

                //通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
                resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                        + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";

            } else {
                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                        + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
                return ("fail");
            }
            //------------------------------
            //处理业务完毕
            //------------------------------
            try {
                BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
                out.write(resXml.getBytes());
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            log.info("通知签名验证失败");
        }
        return ("success");

    }

}
