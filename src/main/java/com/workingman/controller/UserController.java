package com.workingman.controller;


import com.workingman.javaBean.ResponseData;
import com.workingman.javaBean.UserBean;
import com.workingman.javaBean.state.ResponseState;
import com.workingman.service.UserService;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.Pattern;
import java.util.Map;



@RestController
@RequestMapping(value = "/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private Logger logger;

    /**
     * 用户登录
     * @param userBean：请求参数，包含手机号和密码
     * @return user信息、token
     */
    @PostMapping("/loginByPass")
    public ResponseData login(@RequestBody @Validated(UserBean.Update.class) UserBean userBean){
        return userService.login(userBean);
    }

    @PostMapping("/loginByMes")
    public ResponseData loginByCode(@RequestBody @Validated(UserBean.Phone.class) UserBean userBean){
        return userService.loginByCode(userBean);
    }

    @GetMapping("/login/message")
    public ResponseData getLoginCode(@Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "手机号格式错误")@RequestParam(name = "phone",required = true) String phone){
        return userService.getLoginCode(phone);
    }
    /**
     * 用户注册
     * @return responseData
     */

    @PostMapping("/register")
    public ResponseData register(@RequestBody @Validated(value = UserBean.Insert.class) UserBean userBean){
        try {
            return userService.register(userBean);
        } catch (DuplicateKeyException e) {
            logger.warn("该手机号已注册");
            logger.warn(e.getMessage());
            return new ResponseData(ResponseState.USER_IS_EXIST.getMessage(),ResponseState.USER_NOT_EXIST.getValue());
        } catch (Exception e){
            logger.error("注册失败");
            logger.error(e.getMessage());
            System.out.println(e);
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
    }

    /**
     * 获取验证码
     * @param phone：手机号
     * @return responseData
     */

    @GetMapping("/register")
    public ResponseData getCode(@Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "手机号格式错误")@RequestParam(name = "phone",required = true) String phone){
        return userService.getCode(phone);
    }

    /**
     * 退出登录
     * @param user：当前登录的用户信息
     * @return responseData
     */
    @DeleteMapping("/logout")
    public ResponseData logout(@ModelAttribute("user")UserBean user){
        return userService.logout(user);
    }

    /**
     * 获取用户信息
     * @param phone：手机号，可为空，为空查询自动的信息
     * @param user：当前登录的用户信息
     * @return responseData(user)
     */
    @GetMapping
    public ResponseData getUser(@Pattern(regexp = "[1]([3-9])[0-9]{9}$") String phone, @ApiIgnore @ModelAttribute("user")UserBean user){
        return userService.getUser(phone,user);
    }

    /**
     * 更新token
     * @param param n：refreshToken
     * @return responseData:token,refreshToken
     */

    @PostMapping("/refresh")
    public ResponseData refreshToken(@RequestBody Map<String,String> param){
        return userService.refreshToken(param.get("refreshToken"));
    }


    /**
     * 找回密码
     * @param userBean:包含phone,password,code
     * @return responseData
     */
    @PostMapping("/forget")
    public ResponseData forgetPassword(@Validated(UserBean.Update.class) @RequestBody UserBean userBean){
        return userService.forgetPassword(userBean);
    }

    /**
     * 找回密码时获取验证码
     * @param phone：手机号
     * @return responseData
     */

    @GetMapping("/forget")
    public ResponseData getForgetCode(@Pattern(regexp = "[1]([3-9])[0-9]{9}$") @RequestParam(name = "phone",required = true)String phone){
        return userService.getForgetCode(phone);
    }

    /*@GetMapping("/logoff/getCode")
    public ResponseData getLogoffCode(@ModelAttribute("user")UserBean user){
        return userService.getLogoffCode(user);
    }

    @DeleteMapping()
    protected ResponseData logoff(@RequestBody Map<String,String> param,@ModelAttribute("user")UserBean user){
        user.setCode(param.get("code"));
        return userService.logoff(user);
    }*/

    /**
     * 获取用户请求更换手机号的验证码
     * @param phone：要更换成的手机号
     * @return responseData
     */

    @GetMapping("/changePhone")
    public ResponseData getChangePhoneCode(@Pattern(regexp = "^[1]([3-9])[0-9]{9}$")@RequestParam(name = "phone",required = true) String phone){
        return userService.getChangePhoneCode(phone);
    }

    /**
     * 修改用户的手机号
     * @param userBean：包含用户id，要修改成的手机号phone，验证码code
     * @return responseData
     */

    @PutMapping("/changePhone")
    public ResponseData changePhone(@Validated(value = UserBean.Phone.class) @RequestBody UserBean userBean,@ApiIgnore @ModelAttribute("user")UserBean user){
        try {
            userBean.setId(user.getId());
            return userService.changePhone(userBean);
        } catch (DuplicateKeyException e) {
            logger.warn("该手机号已注册");
            logger.warn(e.getMessage());
            return new ResponseData(ResponseState.USER_IS_EXIST.getMessage(),ResponseState.USER_NOT_EXIST.getValue());
        } catch (Exception e) {
            logger.error("id为"+userBean.getId()+"的用户更换手机号时发生错误");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
    }

    /**
     * 修改密码
     * @param param：包含新旧密码oldPassword,newPassword
     * @param user：当前登录的用户
     * @return responseData
     */

    @PutMapping
    public ResponseData changePassword( @ApiParam(value = "需要包含oldPassword,newPassword")@RequestBody Map<String,String> param,
                                        @ApiIgnore @ModelAttribute("user")UserBean user){
        return userService.changePassword(param.get("oldPassword"),param.get("newPassword"),user);
    }

    /**
     * 用户账户被封后申请解禁账户
     * @param userBean：需包含手机号
     * @return responseData
     */

    @PostMapping("/state")
    public ResponseData applyRelease(@RequestBody @Validated(UserBean.Phone.class)UserBean userBean){
        return userService.applyRelease(userBean);
    }

    @PostMapping("/merchant/login")
    public ResponseData merchantLogin(@RequestBody UserBean userBean){
        return userService.merchantLogin(userBean);
    }
    @PostMapping("/a")
    public ResponseData a(){
        return new ResponseData(ResponseState.SUCCESS.getMessage(), ResponseState.SUCCESS.getValue());
    }
}


