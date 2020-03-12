package com.sysm.back.service;

import com.sysm.back.Enums.UserRoleEnum;
import com.sysm.back.Enums.UserStatusEnum;

import java.util.Map;

public interface SysUserService {
    /**
     * 用户登录
     *
     * @param userName
     * @param password
     * @return
     */
    Map<String, Object> userLogin(String userName, String password);

    /**
     * 创建用户
     *
     * @param userName
     * @param password
     * @return
     */
    Map<String, Object> userregister(String userName, String password, String token, UserRoleEnum userRoleEnum);

    /**
     * 修改密码
     *
     * @param originalPassword
     * @param newPasswordOne
     * @param newPasswordTow
     * @param token
     * @return
     */
    Map<String, Object> modifyPassword(String userName, String originalPassword, String newPasswordOne, String newPasswordTow, String token);

    /**
     * 帐户启用禁用
     *
     * @param userId
     * @param userStatusEnum
     * @param token
     * @return
     */
    Map<String, Object> userOperate(Integer userId, UserStatusEnum userStatusEnum, String token);

    /**
     * 查询列表
     *
     * @param token
     * @return
     */
    Map<String, Object> listUser(String token, String userName, String userStatus, Integer pageNum, Integer pageSize);

    /**
     * 第三方注册
     *
     * @param sign
     * @return
     */
    Map<String, Object> registrationUser(String sign);

    /**
     * 第三方修改密码
     *
     * @param sign
     * @return
     */
    Map<String, Object> otherModifyPassword(String sign);
}
