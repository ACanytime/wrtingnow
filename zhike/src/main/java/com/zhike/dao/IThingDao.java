package com.zhike.dao;

import com.mybatisflex.core.BaseMapper;
import com.zhike.pojo.Thing;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

//关于小记的数据库接口
@Mapper
@Repository
public interface IThingDao extends BaseMapper<Thing> {

}
