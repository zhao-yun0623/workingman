package com.workingman.filter;

import com.alibaba.fastjson.JSON;
import com.workingman.javaBean.ResponseData;
import com.workingman.javaBean.state.ResponseState;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义授权异常处理类
 * SimpleAccessDeniedHandler
 *
 * @author xiaoyunzhao
 * @date 2020/7/19
 * @version 1.0
 */
@Component
public class SimpleAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResponseData responseData=new ResponseData(ResponseState.WITHOUT_PERMISSION.getMessage(), ResponseState.WITHOUT_PERMISSION.getValue());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().append(JSON.toJSONString(responseData));
    }
}
