package com.zhike.dao;

import com.mybatisflex.core.BaseMapper;
import com.zhike.pojo.NoteThingLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

//关于小记日志的数据库接口
@Mapper
@Repository
public interface INoteThingLogDao extends BaseMapper<NoteThingLog> {

}
