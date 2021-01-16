package com.workingman.service;

import com.github.wxpay.sdk.WXPayUtil;
import com.workingman.config.WxConfig;
import com.workingman.javaBean.FoodBean;
import com.workingman.javaBean.OrderBean;
import com.workingman.javaBean.ResponseData;
import com.workingman.javaBean.UserBean;
import com.workingman.javaBean.state.OrderState;
import com.workingman.javaBean.state.RedisHeader;
import com.workingman.javaBean.state.ResponseState;
import com.workingman.mapper.FoodMapper;
import com.workingman.mapper.OrderMapper;
import com.workingman.service.utils.DateUtils;
import com.workingman.service.utils.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.*;

@Service
public class OrderService {
    @Autowired
    private PhoneService phoneService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private FoodMapper foodMapper;
    @Autowired
    private Logger logger;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    public ResponseData postOrder(OrderBean orderBean, UserBean user) {
        if(orderBean.getType()==2){
            if(orderBean.getReward()==null){
                logger.error("参数错误，商家为空");
                return new ResponseData(ResponseState.PARAM_IS_ERROR.getMessage(), ResponseState.PARAM_IS_ERROR.getValue());
            }
        }
        if(orderBean.getReward()!=null){
            if(orderBean.getReward()<3.0||orderBean.getReward()>5.0){
                logger.warn("赏金范围必须在3-5之间");
                return new ResponseData("赏金范围必须在3-5之间",ResponseState.PARAM_IS_ERROR.getValue());
            }
        }
        try {
            logger.info(user.getPhone()+"正在生成订单"+orderBean);
            //查看当前是否有订单未完成
            List<OrderBean> order=orderMapper.getNowOrder(user.getId());
            if(order.size()!=0){
                logger.warn("当前尚有订单未完成");
                return new ResponseData(ResponseState.ORDER_NOT_FINISH.getMessage(), ResponseState.ORDER_NOT_FINISH.getValue());
            }
            //插入用户ID
            orderBean.setUserId(user.getId());
            //更新订单状态
            orderBean.setState(OrderState.COPY_SUCCESS.getValue());
            //填入菜品信息
            FoodBean foodBean=foodMapper.getFoodById(orderBean.getFoodId());
            orderBean.setFoodInformation(foodBean);
            if(!DateUtils.isTrue(Calendar.getInstance())){
                logger.warn("未到点餐时间");
                return new ResponseData(ResponseState.ORDER_TIME_ERROR.getMessage(), ResponseState.ORDER_TIME_ERROR.getValue());
            }
            //填入订单生成时间
            orderBean.setStartTime(new Timestamp(new Date().getTime()));
            //生成订单号
            orderBean.setNumber(StringUtils.getRandomString(18));
            String openId=stringRedisTemplate.opsForValue().get(RedisHeader.OPEN_ID.getHeader()+user.getPhone());
//            int money=(int)(orderBean.getPrice()*100);
//            String prepayId=getPrepayId(openId,orderBean.getNumber(),orderBean.getFoodName(), String.valueOf(money),request);
//            if(prepayId==null){
//                logger.error("prepayId获取失败");
//                return new ResponseData(ResponseState.PREPAY_ID_GET_ERROR.getMessage(), ResponseState.PREPAY_ID_GET_ERROR.getValue());
//            }
//            Map<String,String> mes=sendMap(prepayId);
//            if(mes==null){
//                logger.error("信息获取失败");
//                return new ResponseData(ResponseState.INFORMATION_NOT_EXIST.getMessage(), ResponseState.INFORMATION_GET_ERROR.getValue());
//            }
            orderMapper.postOrder(orderBean);
            logger.info("提交成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(), ResponseState.SUCCESS.getValue(),"order",orderBean);
            } catch (Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }
    }

    /**
     * 商户获取订单信息
     * @param orderBean：可包含type和state
     * @param user
     * @return
     */
    public ResponseData merchantGetOrders(OrderBean orderBean, UserBean user) {
        logger.info(user.getPhone()+"的商户正在获取订单信息");
        try {
            orderBean.setUserId(user.getId());
            List<OrderBean> orders=orderMapper.merchantGetOrders(orderBean);
            logger.info("查询成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue(),"orders",orders);
        } catch (Exception e) {
            logger.error("查询失败");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }
    }

    public ResponseData userGetOrders(OrderBean orderBean, UserBean user) {
        logger.info(user.getPhone()+"的用户正在获取订单信息");
        try {
            orderBean.setUserId(user.getId());
            List<OrderBean> orders=orderMapper.userGetOrders(orderBean);
            logger.info("查询成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue(),"orders",orders);
        } catch (Exception e) {
            logger.error("查询失败");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }
    }

    /**
     * 商家接单
     * @param orderBean
     * @param user
     * @return
     */
    public ResponseData merchantReceiveOrder(OrderBean orderBean, UserBean user) {
        logger.info(user.getPhone()+"商家正在接单");
        try {
            if(orderBean.getState()!=OrderState.MERCHANT_GET_ORDER.getValue()&&orderBean.getState()!=OrderState.MERCHANT_NOT_GET_ORDER.getValue()){
                logger.warn("state参数错误");
                return new ResponseData("参数错误，只能是2或10",ResponseState.PARAM_IS_ERROR.getValue());
            }
            //判断当前状态
            OrderBean order=orderMapper.getOrderByNumber(orderBean.getNumber());
            if(order==null){
                logger.info("该订单不存在");
                return new ResponseData("该订单不存在",ResponseState.ORDER_NOT_EXIST.getValue());
            }
            if(order.getState()!=OrderState.COPY_SUCCESS.getValue()){
                logger.warn("当前订单状态无法接单");
                return new ResponseData(ResponseState.THIS_STATE_CANNOT_RECEIVE.getMessage(), ResponseState.THIS_STATE_CANNOT_RECEIVE.getValue());
            }
            //商户拒绝接单
            if(orderBean.getState()==OrderState.MERCHANT_NOT_GET_ORDER.getValue()){
                logger.info("商家试图拒绝接单");
                //设置拒绝接单时间
                orderBean.setMerchantTime(new Timestamp(new Date().getTime()));
                orderMapper.changeOrder(orderBean);
                logger.info("拒绝接单成功");
                return new ResponseData(ResponseState.SUCCESS.getMessage(), ResponseState.SUCCESS.getValue());
            }
            //商户接单
            if(orderBean.getState()==OrderState.MERCHANT_GET_ORDER.getValue()){
                logger.info("商家试图接单");
                //设置接单时间
                orderBean.setMerchantTime(new Timestamp(new Date().getTime()));
                //生成取件码
                int rawCode= Integer.parseInt(stringRedisTemplate.opsForValue().get(RedisHeader.ORDER_CODE.getHeader()));
                rawCode++;
                String code= String.valueOf(rawCode);
                code=StringUtils.addZero(code);
                orderBean.setCode(code);
                stringRedisTemplate.opsForValue().set(RedisHeader.ORDER_CODE.getHeader(),code);
                orderMapper.changeOrder(orderBean);
                logger.info("接单成功");
                return new ResponseData(ResponseState.SUCCESS.getMessage(), ResponseState.SUCCESS.getValue());
            }
            logger.error(ResponseState.ERROR.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        } catch (Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }
    }

    /**
     * 打工人接单
     * @param orderBean
     * @param user
     * @return
     */
    public ResponseData laborerReceiveOrder(OrderBean orderBean, UserBean user) {
        System.out.println(user);
        logger.info(user.getPhone()+"打工人正在接单");
        try {
            //判断当前状态
            OrderBean order=orderMapper.getOrderByNumber(orderBean.getNumber());
            if(order==null){
                logger.info("该订单不存在");
                return new ResponseData("该订单不存在",ResponseState.ORDER_NOT_EXIST.getValue());
            }
            if(order.getState()!=OrderState.MERCHANT_GET_ORDER.getValue()||order.getType()!=2){
                logger.warn("当前订单状态无法接单");
                return new ResponseData(ResponseState.THIS_STATE_CANNOT_RECEIVE.getMessage(), ResponseState.THIS_STATE_CANNOT_RECEIVE.getValue());
            }
            //设置状态码
            orderBean.setState(OrderState.WORKINGMAN_GET_ORDER.getValue());
            logger.info("商家试图接单");
            //设置打工人接单时间
            orderBean.setLaborerTime(new Timestamp(new Date().getTime()));
            //设置打工人信息
            orderBean.setLaborerId(user.getId());
            orderBean.setLaborerPhone(user.getPhone());
            orderMapper.changeOrder(orderBean);
            logger.info("接单成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(), ResponseState.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }
    }

    /**
     * 打工人查看订单
     * @param orderBean
     * @param user
     * @return
     */
    public ResponseData getLaborerOwnOrder(OrderBean orderBean, UserBean user) {
        logger.info(user.getPhone()+"打工人正在查询自己的订单");
        try {
            orderBean.setLaborerId(user.getId());
            List<OrderBean> orders=orderMapper.getLaborerOwnOrder(orderBean);
            logger.info("查询成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(), ResponseState.SUCCESS.getValue(),"orders",orders);
        } catch (Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }
    }

    public ResponseData getLaborerOrder(OrderBean orderBean, UserBean user) {
        logger.info(user.getPhone()+"打工人正在查询可接的订单");
        try {
            List<OrderBean> orders=orderMapper.getLaborerOrder();
            logger.info("查询成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(), ResponseState.SUCCESS.getValue(),"orders",orders);
        } catch (Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }
    }

    /**
     * 通知用户取餐
     * @param orderBean
     * @param user
     * @return
     */
    public ResponseData changeStateToGetFood(OrderBean orderBean, UserBean user) {
        logger.info(user.getPhone()+"正在通知用户取餐");
        try {
            //判断当前状态
            OrderBean order=orderMapper.getOrderByNumber(orderBean.getNumber());
            if(order==null){
                logger.info("该订单不存在");
                return new ResponseData("该订单不存在",ResponseState.ORDER_NOT_EXIST.getValue());
            }
            if((order.getState()!=OrderState.WORKINGMAN_GET_ORDER.getValue()||order.getType()!=2)&&(order.getState()!=OrderState.MERCHANT_GET_ORDER.getValue()||order.getType()!=1)){
                logger.warn("当前订单状态无法通知取餐");
                return new ResponseData(ResponseState.THIS_STATE_CANNOT_RECEIVE.getMessage(), ResponseState.THIS_STATE_CANNOT_RECEIVE.getValue());
            }
//            if(order.getType()==1){
//                phoneService.userGetFood(order.getNumber(),order.getCode(),order.getUserPhone());
//            }
            //设置状态码
            orderBean.setState(OrderState.FOOD_HAS_COOK.getValue());
            //设置待取餐的时间
            orderBean.setGetTime(new Timestamp(new Date().getTime()));
            orderMapper.changeOrder(orderBean);
            logger.info("操作成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(), ResponseState.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }
    }

    /**
     * 已取餐
     * @param orderBean
     * @param user
     * @return
     */
    public ResponseData merchantFinishFood(OrderBean orderBean, UserBean user) {
        logger.info(user.getPhone()+"正在设置已取餐");
        try {
            //判断当前状态
            OrderBean order=orderMapper.getOrderByNumber(orderBean.getNumber());
            if(order==null){
                logger.info("该订单不存在");
                return new ResponseData("该订单不存在",ResponseState.ORDER_NOT_EXIST.getValue());
            }
            if(order.getState()!=OrderState.FOOD_HAS_COOK.getValue()){
                logger.warn("当前订单无法更改为已取餐");
                return new ResponseData(ResponseState.THIS_STATE_CANNOT_RECEIVE.getMessage(), ResponseState.THIS_STATE_CANNOT_RECEIVE.getValue());
            }
            if(order.getType()==1){
                //设置状态码
                orderBean.setState(OrderState.ORDER_FINISH.getValue());
                //设置配送开始时间
                orderBean.setSendTime(new Timestamp(new Date().getTime()));
            }else if(order.getType()==2){
                orderBean.setState(OrderState.WORKINGMAN_GET_FOOD.getValue());
                //设置订单结束时间
                orderBean.setEndTime(new Timestamp(new Date().getTime()));
            }
            orderMapper.changeOrder(orderBean);
            logger.info("操作成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(), ResponseState.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }
    }


    /**
     * 打工人完成订单
     * @param orderBean
     * @param user
     * @return
     */
    public ResponseData finishOrder(OrderBean orderBean, UserBean user) {
        logger.info(user.getPhone()+"打工人正在完成订单");
        try {
            //判断当前状态
            OrderBean order=orderMapper.getOrderByNumber(orderBean.getNumber());
            if(order==null){
                logger.info("该订单不存在");
                return new ResponseData("该订单不存在",ResponseState.ORDER_NOT_EXIST.getValue());
            }
            if(order.getType()!=2||order.getState()!=OrderState.WORKINGMAN_GET_FOOD.getValue()){
                logger.warn("当前订单状态无法完成订单");
                return new ResponseData(ResponseState.THIS_STATE_CANNOT_RECEIVE.getMessage(), ResponseState.THIS_STATE_CANNOT_RECEIVE.getValue());
            }
            //设置状态码
            orderBean.setState(OrderState.ORDER_FINISH.getValue());
            //设置订单完成的时间
            orderBean.setEndTime(new Timestamp(new Date().getTime()));
            orderMapper.changeOrder(orderBean);
            logger.info("操作成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(), ResponseState.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }
    }


    public String getPrepayId(String openId, String orderId, String orderName,
                               String money, HttpServletRequest request) {
//        loadData();
        // 拼接统一下单地址参数
        Map<String, String> paraMap = new HashMap<String, String>();
        // 获取请求ip地址
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.indexOf(",") != -1) {
            String[] ips = ip.split(",");
            ip = ips[0].trim();
        }

        paraMap.put("appid", WxConfig.appId);
        orderName = orderName.replace(" ", "");
        try {
            paraMap.put("body", new String(orderName.getBytes("ISO8859-1"),
                    "utf-8"));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        paraMap.put("mch_id", WxConfig.mchId);
        paraMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paraMap.put("openid", WxConfig.openId);
        paraMap.put("out_trade_no", orderId);// 订单号
        paraMap.put("spbill_create_ip", ip);
        paraMap.put("total_fee", money);
        paraMap.put("trade_type", "JSAPI");
        paraMap.put("notify_url", request.getServerName() + "/" + "index.html");// 此路径是微信服务器调用支付结果通知路径随意写
        String prepay_id = "";// 预支付id
        try {
            String sign = WXPayUtil.generateSignature(paraMap, WxConfig.key);
            paraMap.put("sign", sign);
            String xml = WXPayUtil.mapToXml(paraMap);// 将所有参数(map)转xml格式
            System.out.println(xml);
            String xmlStr = HttpRequest.sendPost("https://api.mch.weixin.qq.com/pay/unifiedorder", xml);// 发送post请求"统一下单接口"返回预支付id:prepay_id
            // 以下内容是返回前端页面的json数据
            Map<String, String> map = WXPayUtil.xmlToMap(xmlStr);
            if (xmlStr.indexOf("SUCCESS") != -1) {
                prepay_id = (String) map.get("prepay_id");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return prepay_id;
    }

    public Map<String, String> sendMap(String prepay_id) {
//        loadData();
        Map<String, String> payMap = new HashMap<String, String>();
        payMap.put("appId", WxConfig.appId);
        payMap.put("timeStamp", WXPayUtil.getCurrentTimestamp() + "");
        payMap.put("nonceStr", WXPayUtil.generateNonceStr());
        payMap.put("signType", "MD5");
        payMap.put("package", "prepay_id=" + prepay_id);
        String paySign;
        try {
            paySign = WXPayUtil.generateSignature(payMap, WxConfig.key);
            payMap.put("paySign", paySign);
            payMap.put("packageMsg", "prepay_id=" + prepay_id);
            System.out.println(payMap);
            return payMap;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 取消订单
     * @param orderBean
     * @param user
     * @return
     */
    public ResponseData cancelOrder(OrderBean orderBean, UserBean user) {
        logger.info(user.getPhone()+"正在取消订单号为"+orderBean.getNumber()+"的订单");
        try {
            OrderBean order=orderMapper.getOrderByNumber(orderBean.getNumber());
            if(order.getState()>=6){
                logger.info("该订单已结束");
                return new ResponseData("该订单已结束",ResponseState.ORDER_CAN_NOT_CANCEL.getValue());
            }
            if((order.getState()>1&&order.getState()<6&&order.getType()==1)||(order.getState()>2&&order.getState()<6&&order.getType()==2)){
                logger.info("该订单正在进行，无法取消");
                return new ResponseData(ResponseState.ORDER_CAN_NOT_CANCEL.getMessage(), ResponseState.ORDER_CAN_NOT_CANCEL.getValue());
            }
            //设置订单状态
            orderBean.setState(OrderState.ORDER_HAS_CANCEL.getValue());
            //设置结束时间
            orderBean.setEndTime(new Timestamp(new Date().getTime()));
            orderMapper.changeOrder(orderBean);
            logger.info(ResponseState.SUCCESS.getMessage());
            return new ResponseData(ResponseState.SUCCESS.getMessage(), ResponseState.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }
    }


}
