package com.sysm.back.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sysm.back.Enums.ResultEnum;
import com.sysm.back.Enums.UserRoleEnum;
import com.sysm.back.Enums.UserStatusEnum;
import com.sysm.back.VO.ResultVO;
import com.sysm.back.dao.SysUserMapper;
import com.sysm.back.dao.SysUserRoleMapper;
import com.sysm.back.dao.UserMapper;
import com.sysm.back.entity.UserRole;
import com.sysm.back.model.SysUser;
import com.sysm.back.model.SysUserExample;
import com.sysm.back.model.SysUserRole;
import com.sysm.back.service.SysUserService;
import com.sysm.back.utils.JwtUtil;
import com.sysm.back.utils.MD5Util;
import com.sysm.back.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    SysUserMapper sysUserMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    UserMapper userMapper;
    @Autowired
    SysUserRoleMapper sysUserRoleMapper;

    @Override
    public Map<String, Object> userLogin(String userName, String password) {
        SysUserExample sysUserExample = new SysUserExample();
        password = MD5Util.toMd5(password);
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
    public Map<String, Object> userregister(String userName, String password, String token, UserRoleEnum userRoleEnum) {
        SysUserExample sysUserExample = new SysUserExample();
        sysUserExample.createCriteria().andUserNameEqualTo(userName);
        List<SysUser> userList = sysUserMapper.selectByExample(sysUserExample);
        if (userList.isEmpty()) {
            SysUser sysUser = new SysUser();
            sysUser.setUserName(userName);
            sysUser.setPassword(MD5Util.toMd5(password));
            sysUser.setCreateTime(new Date());
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
        PageHelper.startPage(pageNum, 10);
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
}
