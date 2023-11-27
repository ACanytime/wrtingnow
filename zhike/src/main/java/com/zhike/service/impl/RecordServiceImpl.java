package com.zhike.service.impl;

import cn.hutool.core.lang.Validator;
import com.mybatisflex.core.query.QueryWrapper;
import com.zhike.dao.INoteThingLogDao;
import com.zhike.exception.ServiceException;
import com.zhike.pojo.Note;
import com.zhike.pojo.NoteThingLog;
import com.zhike.service.INoteThingLogService;
import com.zhike.service.IRecordService;
import com.zhike.util.code.EventCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.zhike.pojo.table.Tables.*;
import static com.zhike.pojo.table.Tables.THING;

@Service
public class RecordServiceImpl implements IRecordService {
    @Autowired
    private INoteThingLogDao noteThingLogDao;

    @Autowired
    private IRecordService recordService;
    @Override
    public List<NoteThingLog> getRecordList(String search,int userId) throws ServiceException {
        //查询字段：编号，标题，内容，是否置顶，最后一次操作时间
        //查询条件： ‘status’ =1 AND ‘u_id’ =？
        //排序规则： 置顶靠前，最后操作的时间离当前时间最近的靠前
        QueryWrapper wrapper=QueryWrapper.create()
                .select(NOTE_THING_LOG.ID,NOTE_THING_LOG.TIME,NOTE_THING_LOG.DESC,NOTE_THING_LOG.USER_ID,NOTE_THING_LOG.THING_ID,NOTE_THING_LOG.NOTE_ID,NOTE_THING_LOG.EVENT)
                .where(NOTE_THING_LOG.USER_ID.eq(userId));

        if(Validator.isNotEmpty(search)){
            wrapper.and(
                    NOTE_THING_LOG.DESC.like(search).or(NOTE_THING_LOG.NOTE_ID.like(search)).or(NOTE_THING_LOG.THING_ID.like(search))
            );
        }
        wrapper.orderBy(NOTE_THING_LOG.TIME.desc());
        //查询用户的笔记列表
        List<NoteThingLog> noteThingLogs=null;
        try {
            noteThingLogs = noteThingLogDao.selectListByQuery(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("获取最近操作列表失败", EventCode.SELECT_EXCEPTION);
        }
        //将笔记列表返回给调用者
        return noteThingLogs;
    }
}
