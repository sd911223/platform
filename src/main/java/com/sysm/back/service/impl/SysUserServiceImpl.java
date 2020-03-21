package com.sysm.back.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sysm.back.Enums.ResultEnum;
import com.sysm.back.Enums.UserRoleEnum;
import com.sysm.back.Enums.UserStatusEnum;
import com.sysm.back.VO.ResultVO;
import com.sysm.back.bean.dto.User;
import com.sysm.back.bean.dto.UserRole;
import com.sysm.back.dao.SysUserMapper;
import com.sysm.back.dao.SysUserRoleMapper;
import com.sysm.back.dao.UserMapper;
import com.sysm.back.model.SysUser;
import com.sysm.back.model.SysUserExample;
import com.sysm.back.model.SysUserRole;
import com.sysm.back.service.SysUserService;
import com.sysm.back.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    SysUserMapper sysUserMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    UserMapper userMapper;
    @Autowired
    SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    HttpServletRequest request;

    @Value("${ras.privateKey}")
    private String privateKey;

    @Override
    public Map<String, Object> userLogin(String userName, String password) {
        SysUserExample sysUserExample = new SysUserExample();
        password = MD5Util.Md5(userName, password);
        sysUserExample.createCriteria().andUserNameEqualTo(userName).andPasswordEqualTo(password).andIsEffectiveEqualTo("0");
        List<SysUser> listUsers = sysUserMapper.selectByExample(sysUserExample);
        if (listUsers.isEmpty()) {
            return ResultVO.failure(ResultEnum.USER_LOGIN_FAILED, false);
        }
        SysUser sysUser = listUsers.get(0);
        UserRole userRoles = userMapper.findByAdminUserId(sysUser.getId());
        //生成token
        String token = JwtUtil.sign(sysUser.getUserName(), sysUser.getId().toString());

        redisUtil.set(token, token);
        return ResultVO.success(userRoles, token);
    }

    @Override
    public Map<String, Object> userregister(String userName, String password, String mailbox, String phoneNum, String remark, String maturityTime, String token, UserRoleEnum userRoleEnum) {
        SysUserExample sysUserExample = new SysUserExample();
        sysUserExample.createCriteria().andUserNameEqualTo(userName);
        List<SysUser> userList = sysUserMapper.selectByExample(sysUserExample);
        SysUser user = JwtUtil.getSysUser(token);
        if (userList.isEmpty()) {
            SysUser sysUser = new SysUser();
            sysUser.setUserName(userName);
            sysUser.setMailbox(mailbox);
            sysUser.setPhoneNum(phoneNum);
            sysUser.setRemark(remark);
            sysUser.setMaturityTime(DateUtil.fomatDate(maturityTime));
            sysUser.setPassword(MD5Util.Md5(userName, password));
            sysUser.setCreateTime(new Date());
            sysUser.setIp(AccessAddressUtil.getIpAddress(request));
            sysUser.setBelongAgent(user.getUserName());
            sysUser.setBelongSoftware("1");
            sysUser.setPlatformMark("1");
            //0为有效
            sysUser.setIsEffective("0");
            int selective = sysUserMapper.insertSelective(sysUser);
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setSysUserId(sysUser.getId());
            if ("ROLE_ADMIN".equals(userRoleEnum.getCode())) {
                sysUserRole.setSysRoleId(1);
            }
            if ("ROLE_USER".equals(userRoleEnum.getCode())) {
                sysUserRole.setSysRoleId(2);
            }
            sysUserRoleMapper.insert(sysUserRole);
            if (selective > 0) {
                return ResultVO.success();
            }
        }
        return ResultVO.failure(ResultEnum.ACCOUNT_IS_REPEAT, false);
    }

    @Override
    public Map<String, Object> modifyPassword(String userName, String originalPassword, String newPasswordOne, String newPasswordTow, String token) {
        SysUserExample sysUserExample = new SysUserExample();
        sysUserExample.createCriteria().andPasswordEqualTo(MD5Util.toMd5(originalPassword)).andUserNameEqualTo(userName);
        List<SysUser> sysUserList = sysUserMapper.selectByExample(sysUserExample);
        if (sysUserList.isEmpty()) {
            return ResultVO.failure(ResultEnum.USER_LOGIN_FAILED, false);
        }
        SysUser sysUser = sysUserList.get(0);
        sysUser.setPassword(MD5Util.toMd5(newPasswordOne));
        int primaryKey = sysUserMapper.updateByPrimaryKey(sysUser);
        if (primaryKey > 0) {
            return ResultVO.success();
        }
        return ResultVO.failure(ResultEnum.USER_LOGIN_FAILED, false);
    }

    @Override
    public Map<String, Object> userOperate(Integer userId, UserStatusEnum userStatusEnum, String token) {
        SysUser user = sysUserMapper.selectByPrimaryKey(userId);
        user.setId(userId);
        user.setIsEffective(userStatusEnum.getCode());
        int i = sysUserMapper.updateByPrimaryKey(user);
        if (i > 0) {
            return ResultVO.success();
        }
        return ResultVO.failure(ResultEnum.FAILURE, false);
    }

    @Override
    public Map<String, Object> listUser(String token, String userName, String userStatus, Integer pageNum, Integer pageSize) {

        SysUserExample example = new SysUserExample();
        PageHelper.startPage(pageNum, pageSize);
        SysUserExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(userName)) {
            criteria.andUserNameLike(userName);
        }
        if (!StringUtils.isEmpty(userStatus)) {
            criteria.andIsEffectiveEqualTo(userStatus);
        }
        example.setOrderByClause("create_time DESC");


        List<SysUser> sysUsers = sysUserMapper.selectByExample(example);
        PageInfo<SysUser> pageInfo = new PageInfo<>(sysUsers);
        return ResultVO.success(pageInfo, token);
    }

    @Override
    public Map<String, Object> registrationUser(String sign) {
        String s = RSAUtils.decryptDataOnJava(sign, privateKey);
        User user = JSON.parseObject(s, User.class);
        if (null == user) {
            log.info("对象为空");
            return ResultVO.failure(401, "参数对象不能为空");
        }
        if ("".equals(user.getPhoneNum())) {
            log.info("手机号为空");
            return ResultVO.failure(401, "手机号不能为空");
        }
        if ("".equals(user.getPassword())) {
            log.info("密码为空");
            return ResultVO.failure(401, "密码不能为空");
        }
        if ("".equals(user.getPlatformSign())) {
            log.info("平台标识为空");
            return ResultVO.failure(401, "平台标识不能为空");
        }
        if ("".equals(user.getIp())) {
            log.info("IP为空");
            return ResultVO.failure(401, "IP不能为空");
        }
        SysUserExample example = new SysUserExample();
        SysUserExample.Criteria criteria = example.createCriteria();
        criteria.andUserNameEqualTo(user.getPhoneNum());
        List<SysUser> list = sysUserMapper.selectByExample(example);
        if (list.isEmpty()) {
            SysUser sysUser = new SysUser();
            sysUser.setIsEffective("0");
            sysUser.setUserName(user.getPhoneNum());
            sysUser.setPassword(MD5Util.Md5(user.getPhoneNum(), user.getPassword()));
            sysUser.setPhoneNum(user.getPhoneNum());
            sysUser.setCreateTime(new Date());
            sysUser.setPlatformMark(user.getPlatformSign());
            sysUser.setIp(user.getIp());
            int i = sysUserMapper.insertSelective(sysUser);
            if (i > 0) {
                return ResultVO.success();
            }
        }
        return ResultVO.failure(402, "注册失败");
    }

    @Override
    public Map<String, Object> otherModifyPassword(String sign) {
        String s = RSAUtils.decryptDataOnJava(sign, privateKey);
        User user = JSON.parseObject(s, User.class);
        if (null == user) {
            return ResultVO.failure(401, "参数对象不能为空");
        }
        if ("".equals(user.getPhoneNum())) {
            return ResultVO.failure(401, "手机号不能为空");
        }
        if ("".equals(user.getPassword())) {
            return ResultVO.failure(401, "密码不能为空");
        }
        SysUserExample example = new SysUserExample();
        SysUserExample.Criteria criteria = example.createCriteria();
        criteria.andUserNameEqualTo(user.getPhoneNum());
        List<SysUser> users = sysUserMapper.selectByExample(example);
        if (users.isEmpty()) {
            return ResultVO.failure(ResultEnum.USER_LOGIN_FAILED, "没有该账户", false);
        }
        SysUser sysUser = users.get(0);
        sysUser.setPassword(MD5Util.Md5(sysUser.getUserName(), user.getPassword()));
        int i = sysUserMapper.updateByPrimaryKey(sysUser);
        if (i > 0) {
            return ResultVO.success();
        }
        return ResultVO.failure(ResultEnum.USER_LOGIN_FAILED, "没有该账户", false);
    }

    @Override
    public Map<String, Object> resetPassword(String token, Integer userId, String passWord, String passWordTwo) {
        if (!passWord.equals(passWordTwo)) {
            return ResultVO.failure(ResultEnum.USER_LOGIN_FAILED, "两次密码不一致", false);
        }
        SysUser sysUser = sysUserMapper.selectByPrimaryKey(userId);
        sysUser.setPassword(MD5Util.Md5(sysUser.getUserName(), passWord));
        int i = sysUserMapper.updateByPrimaryKey(sysUser);
        if (i > 0) {
            return ResultVO.success();
        }
        return ResultVO.failure(ResultEnum.USER_LOGIN_FAILED, "没有该账户", false);
    }
}
