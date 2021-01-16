package com.workingman.service;

import com.workingman.javaBean.InformationBean;
import com.workingman.javaBean.ResponseData;
import com.workingman.javaBean.UserBean;
import com.workingman.javaBean.state.OSSUrl;
import com.workingman.javaBean.state.ResponseState;
import com.workingman.javaBean.state.RoleId;
import com.workingman.mapper.InformationMapper;
import com.workingman.mapper.LaborerMapper;
import com.workingman.mapper.UserMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.security.interfaces.RSAKey;
import java.util.List;

@Service
@Validated
public class LaborerService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private InformationMapper informationMapper;
    @Autowired
    private LaborerMapper laborerMapper;
    @Autowired
    private Logger logger;
    @Autowired
    private OSSService ossService;

    /**
     * 申请成为打工人
     *
     * @param user：当前登录的用户
     * @return responseData
     */
    public ResponseData applyForLaborer(MultipartFile face, MultipartFile identification, UserBean user) {
        logger.info(user.getPhone() + "正在申请成为打工人");
        try {
            InformationBean information = informationMapper.getInformationById(user.getId());
            if (information == null) {
                logger.warn("该用户尚未完善信息");
                return new ResponseData(ResponseState.INFORMATION_NOT_EXIST.getMessage(), ResponseState.WITHOUT_PERMISSION.getValue());
            }
            if (information.getState() == 1) {
                logger.warn(ResponseState.OPERATION_HAI_FINISH.getMessage());
                return new ResponseData(ResponseState.OPERATION_HAI_FINISH.getMessage(), ResponseState.OPERATION_HAI_FINISH.getValue());
            }
            if (information.getState() == 2) {
                logger.warn("该用户已经成为打工人");
                return new ResponseData(ResponseState.HAS_BE_WORKINGMAN.getMessage(), ResponseState.HAS_BE_WORKINGMAN.getValue());
            }
            String faceUrl = ossService.uploadFile(face.getInputStream(), OSSUrl.LABORER + user.getPhone() + "/face.jpg");
            String identificationUrl = ossService.uploadFile(identification.getInputStream(), OSSUrl.LABORER.getUrl() + user.getPhone() + "/identification.jpg");
            InformationBean informationBean = new InformationBean();
            informationBean.setFace(faceUrl);
            informationBean.setIdentification(identificationUrl);
            informationBean.setState(2);
            informationBean.setUserId(user.getId());
            laborerMapper.applyForLaborer(informationBean);
            logger.info("申请成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(), (ResponseState.SUCCESS.getValue()));
        } catch (Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }

    }

    public ResponseData applyForLaborer(InformationBean informationBean, UserBean user) {
        logger.info(user.getPhone() + "正在申请成为打工人");
        try {
            InformationBean information = informationMapper.getInformationById(user.getId());
            if (information == null) {
                logger.warn("该用户尚未完善信息");
                return new ResponseData(ResponseState.INFORMATION_NOT_EXIST.getMessage(), ResponseState.WITHOUT_PERMISSION.getValue());
            }
            if (information.getState() == 1) {
                logger.warn(ResponseState.OPERATION_HAI_FINISH.getMessage());
                return new ResponseData(ResponseState.OPERATION_HAI_FINISH.getMessage(), ResponseState.OPERATION_HAI_FINISH.getValue());
            }
            if (information.getState() == 2) {
                logger.warn("该用户已经成为打工人");
                return new ResponseData(ResponseState.HAS_BE_WORKINGMAN.getMessage(), ResponseState.HAS_BE_WORKINGMAN.getValue());
            }
            informationBean.setState(2);
            informationBean.setUserId(user.getId());
            laborerMapper.applyForLaborer(informationBean);
            logger.info("申请成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(), (ResponseState.SUCCESS.getValue()));
        } catch (Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }
    }

    /**
     * 审核打工人的申请
     *
     * @param userId：申请人ID
     * @param state：审核状态
     * @param user：当前登录的用户
     * @return responseData
     */
    @Transactional
    public ResponseData checkLaborer(@Min(value = 1, message = "userId最小为1") Integer userId, Integer state, UserBean user) {
        logger.info(user.getPhone() + "的管理员正在审核userId为" + userId + "的打工人身份认证");
        if (state != 3 && state != 1) {
            logger.warn("state错误，只能为1或3");
            return new ResponseData("state错误，只能为1或3", ResponseState.PARAM_IS_ERROR.getValue());
        }
        InformationBean information = informationMapper.getInformationById(userId);
        if (information.getState() == 1) {
            logger.warn("该用户已成为打工人");
            return new ResponseData(ResponseState.OPERATION_HAI_FINISH.getMessage(), ResponseState.OPERATION_HAI_FINISH.getValue());
        }
        if (information.getState() != 2) {
            logger.warn("该用户未提交申请");
            return new ResponseData(ResponseState.APPLY_NOT_EXIST.getMessage(), ResponseState.APPLY_NOT_EXIST.getValue());
        }
        informationMapper.updateState(userId, state);
        if (state == 1) {
            userMapper.insertRole(userId, RoleId.WORKINGMAN.getValue());
        }
        logger.info("操作成功");
        return new ResponseData(ResponseState.SUCCESS.getMessage(), ResponseState.SUCCESS.getValue());
    }

    /**
     * 根据申请状态查询用户信息
     *
     * @param state：申请状态
     * @param user：当前登录的用户
     * @return inforamtions
     */
    public ResponseData getApplyUsers(int state, UserBean user) {
        logger.info(user.getPhone() + "正在查询正在申请成为打工人的用户信息");
        try {
            List<InformationBean> informationBeans = laborerMapper.getApplyUsers(state);
            logger.info("查询成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(), ResponseState.SUCCESS.getValue(), "informations", informationBeans);
        } catch (Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }
    }
}

