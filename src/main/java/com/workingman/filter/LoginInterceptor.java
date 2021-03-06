package com.workingman.filter;

import com.alibaba.fastjson.JSON;
import com.workingman.javaBean.ResponseData;
import com.workingman.javaBean.state.RedisHeader;
import com.workingman.javaBean.state.ResponseState;
import com.workingman.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * LoginInterceptor类
 *
 * @author 赵云
 * @date 2020/09/10
 */

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private Logger logger;
    @Autowired
    private JwtService jwtService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        System.out.println(request.getRequestURI());
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        ResponseData responseData=new ResponseData();
        String authorization=request.getHeader("Authorization");
        System.out.println(authorization);
        if(authorization==null){
            logger.warn("未传入token");
            responseData.setMessageState("未传入token", ResponseState.TOKEN_NOT_PROVIDE.getValue());//101
            String Json= JSON.toJSONString(responseData);
            response.getWriter().append(Json);
            return false;
        }
        String[] authorizations=authorization.split(" ");
        //判断是否为Bearer类型
        if(!authorizations[0].equals("Bearer")){
            logger.error("token错误，不是Bearer类型");
            responseData=new ResponseData("token错误",ResponseState.TOKEN_IS_ERROR.getValue());
            String Json= JSON.toJSONString(responseData);
            response.getWriter().append(Json);
            return false;
        }
        String token=authorizations[1];
        //判断是否传入token
        if(token==null){
            logger.warn("未传入token");
            responseData.setMessageState("未传入token", ResponseState.TOKEN_NOT_PROVIDE.getValue());//101
            String Json= JSON.toJSONString(responseData);
            response.getWriter().append(Json);
            return false;
        }
        try {
            UserDetails userDetails=null;
            try {
                userDetails = jwtService.getUserDetails(token,request);
            } catch (Exception e) {
                logger.error("token错误,不能正确解析");
                logger.error(e.getMessage());
                responseData=new ResponseData("token错误",ResponseState.TOKEN_IS_ERROR.getValue());
                String Json= JSON.toJSONString(responseData);
                response.getWriter().append(Json);
                return false;
            }
            if(userDetails==null){
                logger.error("解析后user为空");
                responseData.setMessageState("系统错误", ResponseState.ERROR.getValue());//101
                String Json= JSON.toJSONString(responseData);
                response.getWriter().append(Json);
                return false;
            }
            String rightToken=stringRedisTemplate.opsForValue().get(RedisHeader.TOKEN.getHeader()+userDetails.getUsername());
            if(token.equals(rightToken)){
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                        request));
                //        logger.info("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("手机号为"+userDetails.getUsername()+"的用户token验证成功");
                return true;
            }else {
                logger.error("与当前token匹配失败");
                responseData=new ResponseData("token错误",ResponseState.TOKEN_IS_ERROR.getValue());
                String Json= JSON.toJSONString(responseData);
                response.getWriter().append(Json);
                return false;
            }
        } catch (ExpiredJwtException e) {
            logger.warn("token已过期");
            logger.error(e.getMessage());
            responseData.setMessageState("token已过期", ResponseState.TOKEN_IS_EXPIRED.getValue());//101
            String Json= JSON.toJSONString(responseData);
            response.getWriter().append(Json);
            return false;
        } catch (Exception e){
            logger.error("发生错误");
            logger.error(e.getMessage());
            responseData.setMessageState("系统错误", ResponseState.ERROR.getValue());//101
            String Json= JSON.toJSONString(responseData);
            response.getWriter().append(Json);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
