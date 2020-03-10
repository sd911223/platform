package com.sysm.back.dao;

import com.sysm.back.entity.UserRole;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface UserMapper {
    UserRole findByAdminUserId(@Param("userId") int userId);

}
