package com.workingman.controller;

import com.workingman.javaBean.ResponseData;
import com.workingman.javaBean.UserBean;
import com.workingman.javaBean.state.ResponseState;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@ControllerAdvice
@ResponseBody
public class GlobalController {
    @Autowired
    private Logger logger;
    /**
     * 处理请求参数格式错误的返回信息,form-data格式时
     * @param e：BindException异常
     * @return 111
     */
    @ExceptionHandler
    public ResponseData BindExceptionHandler(BindException e){
        System.out.println(e);
        BindingResult bindingResult=e.getBindingResult();
        String message=bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return new ResponseData(message, ResponseState.PARAM_IS_ERROR.getValue());
    }

    /**
     * 处理请求参数格式错误的返回信息，json格式时
     * @param e：MethodArgumentNotValidException异常
     * @return 111
     */
    @ExceptionHandler
    public ResponseData MethodArgumentNotValidException(MethodArgumentNotValidException e){
        System.out.println(e);
        List<ObjectError> errors=e.getBindingResult().getAllErrors();
        String message=null;
        for (ObjectError error:errors) {
            if(message!=null){
                message=message+";"+error.getDefaultMessage();
            }else {
                message=error.getDefaultMessage();
            }
        }
        logger.error(message);
        return new ResponseData(message,ResponseState.PARAM_IS_ERROR.getValue());
    }

    /**
     * 处理请求参数格式错误的返回信息，param格式时
     * @param e：MethodArgumentNotValidException异常
     * @return 111
     */
    @ExceptionHandler
    public ResponseData constraintViolationException(ConstraintViolationException e){
        System.out.println(e);
        String message=e.getMessage();
        System.out.println(message);
        return new ResponseData(message,ResponseState.PARAM_IS_ERROR.getValue());
    }
    /**
     * 请求参数错误
     * @param e：请求参数错误
     * @return 111
     */
    @ExceptionHandler
    public ResponseData HttpMessageNotReadableException(HttpMessageNotReadableException e){
        logger.error(ResponseState.PARAM_IS_ERROR.getMessage());
        logger.error(e.getLocalizedMessage());
        return new ResponseData(ResponseState.PARAM_IS_ERROR.getMessage(),ResponseState.PARAM_IS_ERROR.getValue());
    }
    /*@ExceptionHandler
    public ResponseData getException(Exception e){
        System.out.println(e);
        logger.error("系统错误");
        logger.error(e.getMessage());
        return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
    }*/

    /**
     * 获取User对象
     * @param data：储存user对象
     * @param request：HttpServletRequest
     */
    @ModelAttribute
    public void getModel(Map<String,Object> data, HttpServletRequest request){
        UserBean user= (UserBean) request.getAttribute("user");
        data.put("user",user);
    }
}
