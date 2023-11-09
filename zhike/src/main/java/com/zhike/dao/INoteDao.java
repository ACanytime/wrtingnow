package com.zhike.dao;

import com.mybatisflex.core.BaseMapper;
import com.zhike.pojo.Note;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

//关于小记的数据库接口
@Mapper
@Repository
public interface INoteDao extends BaseMapper<Note> {

}
