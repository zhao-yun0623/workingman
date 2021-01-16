package com.workingman.filter;

/*@ControllerAdvice
public class ResponseInterceptor implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        ResponseData responseData= (ResponseData) o;
        if(responseData!=null){
            UserBean userBean= (UserBean) responseData.getData("user");
            if(userBean==null){
                return responseData;
            }
            userBean.setPassword(null);
            return responseData;
        }
        return o;
    }
}*/
