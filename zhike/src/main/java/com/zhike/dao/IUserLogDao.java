package com.zhike.dao;

import com.mybatisflex.core.BaseMapper;
import com.zhike.pojo.User;
import com.zhike.pojo.UserLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

//关于用户日志的数据库接口
@Mapper
@Repository
public interface IUserLogDao extends BaseMapper<UserLog> {

}
