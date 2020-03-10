package com.sysm.back.service.impl;

import com.sysm.back.service.UserService;
import com.sysm.back.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    RedisUtil redisUtil;

    @Override
    public String loadUserByUsername(String username) {
        redisUtil.get(username);
        return redisUtil.get(username).toString();
    }
}
