package com.workingman.config;



import com.workingman.filter.CORSInterceptor;
import com.workingman.filter.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {
    @Autowired
    private LoginInterceptor loginInterceptor;
    //这个和swagger没有关系，是配置跨域的
    @Autowired
    private CORSInterceptor corsInterceptor;



    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        super.addResourceHandlers(registry);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] unLoginPaths=new String[]{
                "/user/loginByPass",
                "/user/loginByMes",
                "/user/register",
                "/user/forget",
                "/user/login/message",
                "/user/merchant/login",
                "/error",
                "/user/refresh",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/v2/**",
                "/state",
                "/user/a"
        };
//        registry.addInterceptor(corsInterceptor).addPathPatterns("/**");
        registry.addInterceptor(corsInterceptor).addPathPatterns("/**");
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns(unLoginPaths).excludePathPatterns();;
    }
}
