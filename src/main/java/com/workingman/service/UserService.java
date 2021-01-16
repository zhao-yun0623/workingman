package com.workingman.service;

import com.alibaba.fastjson.JSONObject;
import com.workingman.config.WxConfig;
import com.workingman.javaBean.ResponseData;
import com.workingman.javaBean.RoleBean;
import com.workingman.javaBean.UserBean;
import com.workingman.javaBean.state.RedisHeader;
import com.workingman.javaBean.state.ResponseState;
import com.workingman.javaBean.state.RoleId;
import com.workingman.mapper.InformationMapper;
import com.workingman.mapper.UserMapper;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.management.relation.RoleList;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户管理Service层
 *
 * @author 赵云
 * @date 2020/09/10
 */

@Service
@Validated
public class UserService {
    @Autowired
    private PhoneService phoneService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate<String, RoleBean> redisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private Logger logger;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private InformationMapper informationMapper;
    /**
     * 用户登录逻辑
     * @param userBean：用户信息
     * @return user信息、token
     */
    public ResponseData login(UserBean userBean) {
//        System.out.println(userBean.getWxCode());
        logger.info("手机号为"+userBean.getPhone()+"的用户正在尝试登录");
        UserBean user=userMapper.getUserByPhone(userBean.getPhone());
        if(user==null){
            logger.warn("手机号为"+userBean.getPhone()+"的用户不存在");
            return new ResponseData(ResponseState.USER_NOT_EXIST.getMessage(), ResponseState.USER_NOT_EXIST.getValue());
        }
        if(user.getState()!=1){
            logger.warn("该账号涉嫌违规，无法登录");
            return new ResponseData(ResponseState.ACCOUNT_IS_ILLEGAL.getMessage(),ResponseState.ACCOUNT_IS_ILLEGAL.getValue());
        }
        if(!passwordEncoder.matches(userBean.getPassword(),user.getPassword())){
            logger.warn("手机号为"+userBean.getPhone()+"的用户登录密码错误");
            return new ResponseData(ResponseState.PASSWORD_IS_ERROR.getMessage(),ResponseState.PASSWORD_IS_ERROR.getValue());
        }
        try {
            String token=jwtService.getToken(user);
            //将token存入redis
            stringRedisTemplate.opsForValue().set(RedisHeader.TOKEN.getHeader() +userBean.getPhone(),token);
            stringRedisTemplate.expire("token"+userBean.getPhone(),1,TimeUnit.HOURS);
            String refreshToken=jwtService.getRefreshToken(user);
            //将redis存入redis
            stringRedisTemplate.opsForValue().set(RedisHeader.REFRESH_TOKEN.getHeader()+userBean.getPhone(),refreshToken);
            stringRedisTemplate.expire("refreshToken"+userBean.getPhone(),10,TimeUnit.DAYS);
            List<RoleBean> roleBean=userMapper.getRoles(user.getId());
            user.setRoles(roleBean);
            redisTemplate.delete(RedisHeader.ROLE.getHeader()+userBean.getPhone());
            redisTemplate.opsForList().leftPushAll(RedisHeader.ROLE.getHeader()+userBean.getPhone(),roleBean);
//            stringRedisTemplate.opsForValue().set(RedisHeader.ROLE.getHeader()+userBean.getPhone(), user.getRole().getRole());
            user.setPassword(null);
//            String openId=getOpenId(userBean.getWxCode());
//            System.out.println(openId);
//            if(openId==null||openId.equals("null")){
//                logger.error("openId获取失败");
//                return new ResponseData(ResponseState.OPEN_ID_GET_ERROR.getMessage(),ResponseState.OPEN_ID_GET_ERROR.getValue());
//            }
//            stringRedisTemplate.opsForValue().set(RedisHeader.OPEN_ID.getHeader()+userBean.getPhone(),openId);
            logger.info("手机号为"+userBean.getPhone()+"的用户登录成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue(),token,refreshToken,"user",user);
        } catch (Exception e) {
            logger.error("手机号为"+userBean.getPhone()+"的用户登录时发生错误");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
    }


    public ResponseData loginByCode(UserBean userBean) {
//        System.out.println(userBean.getWxCode());
        logger.info("手机号为"+userBean.getPhone()+"的用户正在尝试通过短信验证码登录");
        try {
            String code=userBean.getCode();
            String rightCode=stringRedisTemplate.opsForValue().get(RedisHeader.LOGIN_CODE.getHeader()+userBean.getPhone());
            System.out.println(code);
            if(rightCode==null){
                logger.warn("手机号为"+userBean.getPhone()+"的用户注册时未获取验证码或验证码已过期");
                return new ResponseData(ResponseState.CODE_NOT_EXIST.getMessage(),ResponseState.CODE_NOT_EXIST.getValue());//107
            }
            if(code==null||!code.equals(rightCode)){
                logger.warn("手机号为"+userBean.getPhone()+"的用户注册时验证码输入错误");
                return new ResponseData(ResponseState.CODE_IS_ERROR.getMessage(),ResponseState.CODE_IS_ERROR.getValue());
            }
            UserBean user=userMapper.getUserByPhone(userBean.getPhone());
            if(user==null){
                logger.warn("手机号为"+userBean.getPhone()+"的用户不存在");
                return new ResponseData(ResponseState.USER_NOT_EXIST.getMessage(), ResponseState.USER_NOT_EXIST.getValue());
            }
            if(user.getState()!=1){
                logger.warn("该账号涉嫌违规，无法登录");
                return new ResponseData(ResponseState.ACCOUNT_IS_ILLEGAL.getMessage(),ResponseState.ACCOUNT_IS_ILLEGAL.getValue());
            }
            String token=jwtService.getToken(user);
            //将token存入redis
            stringRedisTemplate.opsForValue().set(RedisHeader.TOKEN.getHeader() +userBean.getPhone(),token);
            stringRedisTemplate.expire("token"+userBean.getPhone(),1,TimeUnit.HOURS);
            String refreshToken=jwtService.getRefreshToken(user);
            //将redis存入redis
            stringRedisTemplate.opsForValue().set(RedisHeader.REFRESH_TOKEN.getHeader()+userBean.getPhone(),refreshToken);
            stringRedisTemplate.expire("refreshToken"+userBean.getPhone(),10,TimeUnit.DAYS);
            List<RoleBean> roleBean=userMapper.getRoles(user.getId());
            user.setRoles(roleBean);
            redisTemplate.delete(RedisHeader.ROLE.getHeader()+userBean.getPhone());
            redisTemplate.opsForList().leftPushAll(RedisHeader.ROLE.getHeader()+userBean.getPhone(),roleBean);
//            stringRedisTemplate.opsForValue().set(RedisHeader.ROLE.getHeader()+userBean.getPhone(), user.getRole().getRole());
            user.setPassword(null);
//            String openId=getOpenId(userBean.getWxCode());
//            if(openId==null||openId.equals("null")){
//                logger.error("openId获取失败");
//                return new ResponseData(ResponseState.OPEN_ID_GET_ERROR.getMessage(),ResponseState.OPEN_ID_GET_ERROR.getValue());
//            }
//            stringRedisTemplate.opsForValue().set(RedisHeader.OPEN_ID.getHeader()+userBean.getPhone(),openId);
            logger.info("手机号为"+userBean.getPhone()+"的用户登录成功");
            stringRedisTemplate.delete(RedisHeader.LOGIN_CODE.getHeader()+userBean.getPhone());
            return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue(),token,refreshToken,"user",user);
        } catch (Exception e) {
            logger.error("操作失败");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue());
        }

    }

    public ResponseData getLoginCode(String phone) {
        logger.info("手机号为"+phone+"的用户正在尝试获取验证码");
        try {
            String code=phoneService.getLoginCode(phone);
            stringRedisTemplate.opsForValue().set(RedisHeader.LOGIN_CODE.getHeader()+phone,code);
            stringRedisTemplate.expire(RedisHeader.LOGIN_CODE.getHeader()+phone,5, TimeUnit.MINUTES);
            return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error("手机号为"+phone+"的用户获取验证码失败");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
    }
    /**
     * 用户注册
     * @param userBean：注册的用户信息
     * @return responseData
     */
    @Transactional
    public ResponseData register(UserBean userBean) throws Exception{
        logger.info("手机号为"+userBean.getPhone()+"的用户正在尝试注册");
        String code=userBean.getCode();
        String rightCode=stringRedisTemplate.opsForValue().get(RedisHeader.REGISTER_CODE.getHeader()+userBean.getPhone());
        System.out.println(code);
        if(rightCode==null){
            logger.warn("手机号为"+userBean.getPhone()+"的用户注册时未获取验证码或验证码已过期");
            return new ResponseData(ResponseState.CODE_NOT_EXIST.getMessage(),ResponseState.CODE_NOT_EXIST.getValue());//107
        }
        if(code==null||!code.equals(rightCode)){
            logger.warn("手机号为"+userBean.getPhone()+"的用户注册时验证码输入错误");
            return new ResponseData(ResponseState.CODE_IS_ERROR.getMessage(),ResponseState.CODE_IS_ERROR.getValue());
        }
//            UserBean user=userMapper.getUserByPhone(userBean.getPhone());
//            if(user!=null){
//                logger.warn("手机号为"+userBean.getPhone()+"的用户已经被注册");
//                return new ResponseData("该用户已存在",ResponseState.USER_IS_EXIST.getValue());//106
//            }
        userBean.setPassword(passwordEncoder.encode(userBean.getPassword()));
        userBean.setState(1);
        userMapper.insertUser(userBean);
        stringRedisTemplate.delete(RedisHeader.REGISTER_CODE.getHeader()+userBean.getPhone());
        userMapper.insertRole(userBean.getId(), RoleId.USER.getValue());
        logger.info("手机号为"+userBean.getPhone()+"的用户注册成功");
        return new ResponseData(ResponseState.SUCCESS.getMessage(), ResponseState.SUCCESS.getValue());
    }

    /**
     * 获取验证码
     * @param phone：手机号
     * @return responseData
     */
    public ResponseData getCode(String phone) {
        logger.info("手机号为"+phone+"的用户正在尝试获取验证码");
        try {
            String code=phoneService.getRegisterCode(phone);
            stringRedisTemplate.opsForValue().set(RedisHeader.REGISTER_CODE.getHeader()+phone,code);
            stringRedisTemplate.expire(RedisHeader.REGISTER_CODE.getHeader()+phone,5, TimeUnit.MINUTES);
            return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error("手机号为"+phone+"的用户获取验证码失败");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
    }

    /**
     * 退出登录
     * @param user：当前登录的用户信息
     * @return responseData
     */
    public ResponseData logout(UserBean user) {
        logger.info("手机号为"+user.getPhone()+"的用户正在尝试退出登录");
        try {
            stringRedisTemplate.delete(RedisHeader.TOKEN.getHeader()+user.getPhone());
            stringRedisTemplate.delete(RedisHeader.REGISTER_CODE.getHeader()+user.getPhone());
            stringRedisTemplate.delete(RedisHeader.ROLE.getHeader()+user.getPhone());
            logger.info("手机号为"+user.getPhone()+"的用户退出登录成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error("手机号为"+user.getPhone()+"的用户退出登录时发生错误");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
    }

    /**
     * 获取用户信息
     * @param phone：手机号，可为空，为空查询自动的信息
     * @param user：当前登录的用户信息
     * @return responseData(user)
     */
    public ResponseData getUser(String phone, UserBean user) {
        System.out.println(user);
        if(phone==null){
            logger.info("手机号为"+user.getPhone()+"的用户正在查询个人信息");
            phone=user.getPhone();
        }else {
            logger.info("手机号为"+user.getPhone()+"的用户正在查询手机号为"+phone+"的用户的信息");
        }
        try {
            UserBean userBean=userMapper.getUserByPhone(phone);
            if(userBean==null){
                logger.info("手机号为"+phone+"的用户不存在");
                return new ResponseData(ResponseState.USER_NOT_EXIST.getMessage(),ResponseState.USER_NOT_EXIST.getValue());//104
            }
            userBean.setRoles(user.getRoles());
            userBean.setPassword(null);
            logger.info("手机号为"+user.getPhone()+"的用户查询用户信息成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue(),"user",userBean);
        } catch (Exception e) {
            logger.error("手机号为"+user.getPhone()+"的用户查询用户信息时发生错误");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
    }

    /**
     * 更新token
     * @param refreshToken：refreshToken
     * @return responseData:token,refreshToken
     */
    public ResponseData refreshToken(String refreshToken) {
        logger.info("正在进行更新token的操作");
        try {
            UserBean userBean= null;
            try {
                userBean = jwtService.getUser(refreshToken);
            } catch (ExpiredJwtException e) {
                logger.warn("refreshToken已过期");
                logger.warn(e.getMessage());
                return new ResponseData(ResponseState.REFRESH_TOKEN_IS_EXPIRED.getMessage(),ResponseState.REFRESH_TOKEN_IS_EXPIRED.getValue());
            } catch (Exception e) {
                logger.warn("refreshToken解析时发生错误");
                logger.warn(e.getMessage());
                return new ResponseData(ResponseState.REFRESH_TOKEN_IS_ERROR.getMessage(),ResponseState.REFRESH_TOKEN_IS_ERROR.getValue());
            }
            String rightRefreshToken=stringRedisTemplate.opsForValue().get(RedisHeader.REFRESH_TOKEN.getHeader()+userBean.getPhone());
            if(!refreshToken.equals(rightRefreshToken)){
                logger.warn("refreshToken已过期");
                return new ResponseData(ResponseState.REFRESH_TOKEN_IS_EXPIRED.getMessage(),ResponseState.REFRESH_TOKEN_IS_EXPIRED.getValue());
            }
            String newToken=jwtService.getToken(userBean);
            stringRedisTemplate.opsForValue().set(RedisHeader.TOKEN.getHeader()+userBean.getPhone(),newToken);
            String newRefreshToken=jwtService.getRefreshToken(userBean);
            stringRedisTemplate.opsForValue().set(RedisHeader.REFRESH_TOKEN.getHeader()+userBean.getPhone(),newRefreshToken);
            ResponseData responseData=new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue());
            responseData.setTokens(newToken,newRefreshToken);
            return responseData;
        } catch (Exception e){
            logger.error("发生错误");
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
    }

    /**
     * 找回密码
     * @param userBean：用户信息，包括phone，password，code
     * @return responseData
     */
    public ResponseData forgetPassword(UserBean userBean) {
        logger.info("手机号为"+userBean.getPhone()+"的用户正在尝试找回密码");
        try {
            String code=userBean.getCode();
            String rightCode=stringRedisTemplate.opsForValue().get(RedisHeader.FORGET_CODE.getHeader()+userBean.getPhone());
            if(rightCode==null){
                logger.warn("手机号为"+userBean.getPhone()+"的用户找回密码时未获取验证码或验证码已过期");
                return new ResponseData(ResponseState.CODE_NOT_EXIST.getMessage(),ResponseState.CODE_NOT_EXIST.getValue());//107
            }
            if(code==null||!code.equals(rightCode)){
                logger.warn("手机号为"+userBean.getPhone()+"的用户找回密码时验证码输入错误");
                return new ResponseData(ResponseState.CODE_IS_ERROR.getMessage(),ResponseState.CODE_IS_ERROR.getValue());
            }
            UserBean user=userMapper.getUserByPhone(userBean.getPhone());
            userBean.setPassword(passwordEncoder.encode(userBean.getPassword()));
            Boolean isSuccess=userMapper.changePassword(userBean);
            if(!isSuccess){
                logger.warn("该用户不存在");
                return new ResponseData(ResponseState.USER_NOT_EXIST.getMessage(), ResponseState.USER_NOT_EXIST.getValue());
            }
            stringRedisTemplate.delete(RedisHeader.FORGET_CODE.getHeader()+userBean.getPhone());
            stringRedisTemplate.delete(RedisHeader.TOKEN.getHeader()+user.getPhone());
            stringRedisTemplate.delete(RedisHeader.REGISTER_CODE.getHeader()+user.getPhone());
            stringRedisTemplate.delete(RedisHeader.ROLE.getHeader()+user.getPhone());
        } catch (Exception e) {
            logger.error("手机号为"+userBean.getPhone()+"的用户找回密码时发生错误");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
        logger.info("手机号为"+userBean.getPhone()+"的用户找回密码成功");
        return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue());
    }

    /**
     * 找回密码时获取验证码
     * @param phone：手机号
     * @return responseData
     */
    public ResponseData getForgetCode(String phone) {
        logger.info("手机号为"+phone+"的用户正在尝试获取找回密码的验证码");
        try {
            String code=phoneService.getForgetCode(phone);
            stringRedisTemplate.opsForValue().set(RedisHeader.FORGET_CODE.getHeader()+phone,code);
            stringRedisTemplate.expire(RedisHeader.REGISTER_CODE.getHeader()+phone,5, TimeUnit.MINUTES);
            return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error("手机号为"+phone+"的用户获取验证码失败");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
    }

    /**
     * 获取用户请求更换手机号的验证码
     * @param phone：要更换成的手机号
     * @return responseData
     */
    public ResponseData getChangePhoneCode(String phone) {
        logger.info("手机号为"+phone+"的用户正在尝试获取更换手机号的验证码");
        try {
            stringRedisTemplate.opsForValue().set(RedisHeader.CHANGE_PHONE_CODE.getHeader()+phone,"111111");
            stringRedisTemplate.expire(RedisHeader.CHANGE_PHONE_CODE.getHeader()+phone,5, TimeUnit.MINUTES);
            return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error("手机号为"+phone+"的用户获取验证码失败");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
    }

    /**
     * 修改用户的手机号
     * @param userBean：包含用户id，要修改成的手机号phone，验证码code
     * @return responseData
     */

    @Transactional
    public ResponseData changePhone(UserBean userBean){
        logger.info("id为"+userBean.getId()+"的用户正在尝试更换手机号");
        String code=userBean.getCode();
        String rightCode=stringRedisTemplate.opsForValue().get(RedisHeader.CHANGE_PHONE_CODE.getHeader()+userBean.getPhone());
        if(rightCode==null){
            logger.warn("未获取验证码或验证码已过期");
            return new ResponseData(ResponseState.CODE_NOT_EXIST.getMessage(),ResponseState.CODE_NOT_EXIST.getValue());//107
        }
        if(code==null||!code.equals(rightCode)){
            logger.warn("验证码错误");
            return new ResponseData(ResponseState.CODE_IS_ERROR.getMessage(),ResponseState.CODE_IS_ERROR.getValue());
        }
        userMapper.changePhone(userBean);
        informationMapper.changePhone(userBean.getPhone(),userBean.getId());
        logger.info("ID为"+userBean.getId()+"的用户更换手机号成功");
        return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue());
    }

    /**
     * 修改密码
     * @param oldPassword：原密码
     * @param newPassword：新密码
     * @param user：当前登录的用户
     * @return responseData
     */
    public ResponseData changePassword(String oldPassword,
                                @NotNull @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$",message = "密码长度必须在8-16位之间,至少1个大写字母，1个小写字母和1个数字,不能包含特殊字符")String newPassword,
                                UserBean user) {
        logger.info("手机号为"+user.getPhone()+"的用户正在尝试修改密码");
        try {
            UserBean userBean=userMapper.getUserByPhone(user.getPhone());
            if(!passwordEncoder.matches(oldPassword,userBean.getPassword())){
                logger.warn("手机号为"+user.getPhone()+"的用户原密码错误");
                return new ResponseData(ResponseState.PASSWORD_IS_ERROR.getMessage(),ResponseState.PASSWORD_IS_ERROR.getValue());
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            userMapper.changePassword(user);
            stringRedisTemplate.delete(RedisHeader.TOKEN.getHeader()+user.getPhone());
            stringRedisTemplate.delete(RedisHeader.REGISTER_CODE.getHeader()+user.getPhone());
            stringRedisTemplate.delete(RedisHeader.ROLE.getHeader()+user.getPhone());
            logger.info("修改成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue());
        } catch (Exception e){
            logger.error("修改失败");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
    }

    /**
     * 用户账户被封后申请解禁账户
     * @param userBean：需包含手机号
     * @return responseData
     */
    public ResponseData applyRelease(UserBean userBean) {
        logger.info(userBean.getPhone()+"的用户正在申请解禁账户");
        try {
            UserBean user=userMapper.getUserByPhone(userBean.getPhone());
            if(user==null){
                logger.warn(ResponseState.USER_NOT_EXIST.getMessage());
                return new ResponseData(ResponseState.USER_NOT_EXIST.getMessage(), ResponseState.USER_NOT_EXIST.getValue());
            }
            switch (user.getState()){
                //用户状态正常
                case 1:
                    logger.warn(ResponseState.USER_IS_NORMAL.getMessage());
                    return new ResponseData(ResponseState.USER_IS_NORMAL.getMessage(), ResponseState.USER_IS_NORMAL.getValue());
                //用户已提交申请
                case -2:
                    logger.warn(ResponseState.OPERATION_HAI_FINISH.getMessage());
                    return new ResponseData(ResponseState.OPERATION_HAI_FINISH.getMessage(),ResponseState.OPERATION_HAI_FINISH.getValue());
                default:
                    userBean.setState(-2);
                    userMapper.changeState(userBean);
                    logger.warn(ResponseState.SUCCESS.getMessage());
                    return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue());
            }
        } catch (Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }

    }

    public ResponseData merchantLogin(UserBean userBean) {
        UserBean user=userMapper.getUserByPhone(userBean.getPhone());
        if(user==null){
            logger.warn("手机号为"+userBean.getPhone()+"的用户不存在");
            return new ResponseData(ResponseState.USER_NOT_EXIST.getMessage(), ResponseState.USER_NOT_EXIST.getValue());
        }
        if(user.getState()!=1){
            logger.warn("该账号涉嫌违规，无法登录");
            return new ResponseData(ResponseState.ACCOUNT_IS_ILLEGAL.getMessage(),ResponseState.ACCOUNT_IS_ILLEGAL.getValue());
        }
        if(!passwordEncoder.matches(userBean.getPassword(),user.getPassword())){
            logger.warn("手机号为"+userBean.getPhone()+"的用户登录密码错误");
            return new ResponseData(ResponseState.PASSWORD_IS_ERROR.getMessage(),ResponseState.PASSWORD_IS_ERROR.getValue());
        }
        try {
            List<RoleBean> roleBean=userMapper.getRoles(user.getId());
            if(!RoleBean.isMerchant(roleBean)){
                logger.warn("该用户不是商户");
                return new ResponseData(ResponseState.WITHOUT_PERMISSION.getMessage(),ResponseState.WITHOUT_PERMISSION.getValue());
            }
            String token=jwtService.getToken(user);
            //将token存入redis
            stringRedisTemplate.opsForValue().set(RedisHeader.TOKEN.getHeader() +userBean.getPhone(),token);
            stringRedisTemplate.expire("token"+userBean.getPhone(),1,TimeUnit.HOURS);
            String refreshToken=jwtService.getRefreshToken(user);
            //将redis存入redis
            stringRedisTemplate.opsForValue().set(RedisHeader.REFRESH_TOKEN.getHeader()+userBean.getPhone(),refreshToken);
            stringRedisTemplate.expire("refreshToken"+userBean.getPhone(),10,TimeUnit.DAYS);

            user.setRoles(roleBean);
            redisTemplate.delete(RedisHeader.ROLE.getHeader()+userBean.getPhone());
            redisTemplate.opsForList().leftPushAll(RedisHeader.ROLE.getHeader()+userBean.getPhone(),roleBean);
//            stringRedisTemplate.opsForValue().set(RedisHeader.ROLE.getHeader()+userBean.getPhone(), user.getRole().getRole());
            user.setPassword(null);
            logger.info("手机号为"+userBean.getPhone()+"的用户登录成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue(),token,refreshToken,"user",user);
        } catch (Exception e) {
            logger.error("手机号为"+userBean.getPhone()+"的用户登录时发生错误");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
    }

    public String getOpenId(String code) {
//        loadData();
        String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token";
        String param = "appid=" + WxConfig.appId + "&secret=" + WxConfig.appSecret + "&code="
                + code + "&grant_type=authorization_code";
        String result = HttpRequest.sendGet(accessTokenUrl, param);
        Map<String,String> res = (Map<String, String>) JSONObject.parse(result);
        System.out.println(res);
        return res.get("openid");
    }
}
