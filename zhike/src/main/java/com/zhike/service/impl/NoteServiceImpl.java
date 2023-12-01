package com.zhike.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.zhike.dao.INoteDao;
import com.zhike.exception.ServiceException;
import com.zhike.exception.ServiceRollBackException;
import com.zhike.pojo.Note;
import com.zhike.pojo.NoteThingLog;
import com.zhike.pojo.Thing;
import com.zhike.service.INoteService;
import com.zhike.service.INoteThingLogService;
import com.zhike.util.code.EventCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import static com.zhike.pojo.table.Tables.NOTE;
import static com.zhike.pojo.table.Tables.THING;

//笔记的业务实现类
@Service
public class NoteServiceImpl implements INoteService {

    @Autowired
    private INoteDao noteDao;//笔记的数据库接口
    @Autowired
    private INoteThingLogService noteThingLogService;//笔记小记日志接口

    @Override
    public List<Note> recycle(int userId) throws ServiceException {
        QueryWrapper wrapper = QueryWrapper.create()
                .select(NOTE.UPDATE_TIME,NOTE.TITLE,NOTE.CONTENT,NOTE.TYPE,NOTE.ID)
                .where(NOTE.STATUS.eq(0))
                .and(NOTE.USER_ID.eq(userId));
        List<Note> note=null;
        try {
           return  noteDao.selectListByQuery(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new ServiceException("查询笔记服务器异常",EventCode.SELECT_EXCEPTION);
        }


    }


    @Override
    public Date saveEditingNote(int noteId, int userId, String title, String body, String content) throws ServiceException {
        QueryWrapper wrapper=QueryWrapper.create()
                .where(NOTE.ID.eq(noteId))
                .and(NOTE.USER_ID.eq(userId))
                .and(NOTE.STATUS.eq(1));
        Date localTime=new Date();//时间

        Note note=Note.builder()
                .title(title)
                .body(body)
                .content(content)
                .updateTime(localTime)
                .build();

        int count= 0;
        try {
            count = noteDao.updateByQuery(note,wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("保存失败",EventCode.UPDATE_EXCEPITION);
        }

        if(count!=1){
            throw  new ServiceRollBackException("保存失败",EventCode.UPDATE_ERROR);
        }
        //新增笔记的日志对象
        NoteThingLog log= NoteThingLog.builder()
                .time(localTime)
                .event(EventCode.NOTE_CREATE_SUCCESS)
                .desc("保存笔记")
                .noteId(noteId)
                .userId(userId)
                .build();
        //新增笔记日志
        noteThingLogService.addOneLog(log,true);
        return localTime;
    }

    //获取编辑笔记的信息
    @Override
    public Note getEditNote(int noteId, int userId) throws ServiceException {
        //封装查询的条件
        QueryWrapper wrapper = QueryWrapper.create()
                .select(NOTE.UPDATE_TIME,NOTE.TITLE,NOTE.CONTENT)
                .where(NOTE.STATUS.eq(1))
                .and(NOTE.ID.eq(noteId))
                .and(NOTE.USER_ID.eq(userId));
        Note note=null;
        try {
            note = noteDao.selectOneByQuery(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new ServiceException("查询笔记服务器异常",EventCode.SELECT_EXCEPTION);
        }
        //判断小记是否存在
        if(note==null){
            throw  new ServiceException("笔记不存在，请刷新后再试",EventCode.SELECT_NONE);
        }

        //将查询到的笔记返回出去
        return note;
    }

    //创建笔记
    @Override
    public int createNoteInit(int userId) throws ServiceException {
        Date localTime=new Date();//时间
        //新增笔记的对象
       Note note=Note.builder()
               .time(localTime)
               .updateTime(localTime)
               .userId(userId)
               .build();
        int count= 0;
        try {
            count = noteDao.insert(note);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new ServiceException("新增笔记失败",EventCode.INSERT_EXCEPITON);
        }

        if(count!=1) throw new ServiceRollBackException("新建笔记失败",EventCode.INSERT_ERROR);

        //新增笔记的日志对象
        NoteThingLog log=NoteThingLog.builder()
                .noteId(note.getId())
                .time(localTime)
                .event((EventCode.NOTE_CREATE_SUCCESS))
                .desc("新增笔记")
                .userId(note.getUserId())
                .build();
        //新增笔记日志
        noteThingLogService.addOneLog(log,true);
        //返回新增笔记的编号
        return note.getId();
    }
    //删除和彻底删除笔记
    @Override
    public void deleteNoteById(boolean complete, int noteId, int userId, boolean isRecycleBin) throws ServiceException {
        //默认为正常删除操作，并不是彻底删除，也不是回收站中的删除
        String desc="删除笔记";
        String event=EventCode.NOTE_DELETE_SUCCESS;
        int beforeStatus =1;//删除之前的状态
        int afterStatus=0;//删除之后的状态
        if(complete){
            event=EventCode.NOTE_COMPLETE_DELETE_SUCCESS;
            desc="彻底删除笔记";
            afterStatus=-1;
            if(isRecycleBin) beforeStatus=0;//在回收站中的小记状态都是已删除的
        }



        //使得status变为0或者-1
        //封装修改条件
        QueryWrapper wrapper = QueryWrapper.create().where(NOTE.ID.eq(noteId))
                .and(NOTE.USER_ID.eq(userId))
                .and(NOTE.STATUS.eq(beforeStatus));
        //操作的时间
        Date localTime=new Date();
        //要修改的哪些字段:status
        Note note=Note.builder().status(afterStatus).updateTime(localTime).build();

        int count = 0;
        try {
            //调用修改语句（数据库接口）
            count = noteDao.updateByQuery(note, wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(desc+"失败",EventCode.UPDATE_EXCEPITION);
        }
        if(count!=1){
            throw new ServiceRollBackException(desc+"失败",EventCode.UPDATE_ERROR);
        }
        //添加小记删除日志(删除)
        NoteThingLog log= NoteThingLog.builder()
                .time(localTime)
                .event(event)
                .desc(desc)
                .noteId(noteId)
                .userId(userId)
                .build();
        //新增笔记小记日志记录
        noteThingLogService.addOneLog(log,true);

    }

    @Override
    public void deleteNoterecycle(boolean complete, int noteId, int userId, boolean isRecycleBin) throws ServiceException {
        //默认为正常删除操作，并不是彻底删除，也不是回收站中的删除
        String desc="删除笔记";
        String event=EventCode.NOTE_DELETE_SUCCESS;
        int beforeStatus =1;//删除之前的状态
        int afterStatus=0;//删除之后的状态
        if(complete){
            event=EventCode.NOTE_COMPLETE_DELETE_SUCCESS;
            desc="彻底删除笔记";
            afterStatus=-1;
            if(isRecycleBin) beforeStatus=0;//在回收站中的小记状态都是已删除的
        }



        //使得status变为0或者-1
        //封装修改条件
        QueryWrapper wrapper = QueryWrapper.create().where(NOTE.ID.eq(noteId))
                .and(NOTE.USER_ID.eq(userId))
                .and(NOTE.STATUS.eq(0));
        //操作的时间
        Date localTime=new Date();
        //要修改的哪些字段:status
        Note note=Note.builder().status(afterStatus).updateTime(localTime).build();

        int count = 0;
        try {
            //调用修改语句（数据库接口）
            count = noteDao.updateByQuery(note, wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(desc+"失败",EventCode.UPDATE_EXCEPITION);
        }
        if(count!=1){
            throw new ServiceRollBackException(desc+"失败",EventCode.UPDATE_ERROR);
        }
        //添加小记删除日志(删除)
        NoteThingLog log= NoteThingLog.builder()
                .time(localTime)
                .event(event)
                .desc(desc)
                .noteId(noteId)
                .userId(userId)
                .build();
        //新增笔记小记日志记录
        noteThingLogService.addOneLog(log,true);

    }



    //置顶和取消置顶笔记
    @Override
    public void topNote(boolean isTop, int noteId, int userId) throws ServiceException {
        int beforetop=0;//修改之前的top字段值
        int aftertop=1;//修改之后的top字段值
        String desc="置顶笔记";
        String eventSuccess=EventCode.NOTE_TOP_SUCCESS;//事件代码
        String eventFailed=EventCode.NOTE_TOP_FAILED;
        if(!isTop){
            eventSuccess=EventCode.NOTE_CANCEL_TOP_SUCCESS;//事件代码
            eventFailed=EventCode.NOTE_CANCEL_TOP_FAILED;
            desc="取消置顶笔记";
            beforetop=1;
            aftertop=0;
        }
        //封装修改的条件 where 'id'=? and 'u_id'=? and 'status'=1 and 'top'=1/0
        QueryWrapper wrapper = QueryWrapper.create().where(THING.ID.eq(noteId))
                .and(THING.USER_ID.eq(userId))
                .and(THING.STATUS.eq(1))
                .and(THING.TOP.eq(beforetop));
        //封装修改的字段 top
        Note note=Note.builder().top(aftertop).build();

        //根据wrapper条件来修改小记的top字段
        int count = 0;
        try {
            count = noteDao.updateByQuery(note, wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new ServiceRollBackException(desc+"服务异常",EventCode.UPDATE_EXCEPITION);
        }
        if(count!=1){
            throw  new ServiceRollBackException(desc+"服务异常",eventFailed);
        }

        Date localTime=new Date();//时间
        //新增小记日志对象(置顶业务)
        NoteThingLog log= NoteThingLog.builder()
                .time(localTime)
                .event(eventSuccess)
                .desc(desc)
                .noteId(noteId)
                .userId(userId)
                .build();
        //新增笔记小记日志记录
        noteThingLogService.addOneLog(log,true);


    }

    @Override
    public List<Note> getUserNormalNotes(int userId) throws ServiceException {

        //查询字段：编号，标题，内容，是否置顶，最后一次操作时间
        //查询条件： ‘status’ =1 AND ‘u_id’ =？
        //排序规则： 置顶靠前，最后操作的时间离当前时间最近的靠前
        QueryWrapper wrapper=QueryWrapper.create()
                .select(NOTE.ID,NOTE.TITLE,NOTE.BODY,NOTE.TOP,NOTE.UPDATE_TIME)
                .where(NOTE.STATUS.eq(1))
                .and(NOTE.USER_ID.eq(userId))
                .orderBy(NOTE.TOP.desc(),NOTE.UPDATE_TIME.desc());

        //查询用户的笔记列表
        List<Note> notes=null;
        try {
            notes = noteDao.selectListByQuery(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("获取笔记失败", EventCode.SELECT_EXCEPTION);
        }
        //将笔记列表返回给调用者
        return notes;
    }
}
