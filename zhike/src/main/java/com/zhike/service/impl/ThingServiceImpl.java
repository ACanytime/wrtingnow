package com.zhike.service.impl;
import cn.hutool.core.lang.Validator;
import com.mybatisflex.core.query.QueryWrapper;
import com.zhike.dao.INoteThingLogDao;
import com.zhike.dao.IThingDao;
import com.zhike.exception.ServiceException;
import com.zhike.exception.ServiceRollBackException;
import com.zhike.pojo.NoteThingLog;
import com.zhike.pojo.Thing;
import com.zhike.service.INoteThingLogService;
import com.zhike.service.IThingService;
import com.zhike.util.code.EventCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.zhike.pojo.table.Tables.THING;
import static com.zhike.pojo.table.Tables.USER;


//小记业务的实现层
@Service
@Transactional(rollbackFor = {ServiceRollBackException.class})
public class ThingServiceImpl implements IThingService {

    @Autowired
    private IThingDao thingDao;//小记的数据库接口
    @Autowired
    private INoteThingLogService noteThingLogService;//笔记小记日志的业务接口

    @Override
    public void updateThing(Thing thing) throws ServiceException {
        //修改小记的条件
        QueryWrapper wrapper = QueryWrapper
                .create()
                .where(THING.ID.eq(thing.getId()))
                .and(THING.USER_ID.eq(thing.getUserId()))
                .and(THING.STATUS.eq(1));


        Thing updateColumn=Thing.builder()
                .title(thing.getTitle())
                .tags(thing.getTags())
                .content(thing.getContent())
                .finished(thing.getFinished())
                .top(thing.getTop())
                .updateTime(thing.getUpdateTime())
                .build();

          int count =0;
        try {
           count = thingDao.updateByQuery(updateColumn, wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("修改失败",EventCode.UPDATE_EXCEPITION);
        }

        if(count!=1){
            throw  new ServiceRollBackException("修改失败",EventCode.UPDATE_ERROR);
        }
        //添加修改小记的日志
        //添加小记修改日志(修改)
        NoteThingLog log= NoteThingLog.builder()
                .time(thing.getUpdateTime())
                .event(EventCode.THING_UPDATE_SUCCESS)
                .desc("修改小记")
                .thingId(thing.getId())
                .userId(thing.getUserId())
                .build();
        //新增笔记小记日志记录
        noteThingLogService.addOneLog(log,true);

    }

    @Override
    public Thing getEditThing(int thingId, int userId) throws ServiceException {
        //封装查询的条件
        QueryWrapper wrapper = QueryWrapper.create()
                .select(THING.TITLE,THING.TOP,THING.TAGS,THING.CONTENT,THING.USER_ID)
                .where(THING.ID.eq(thingId))
                .and(THING.USER_ID.eq(userId))
                .and(THING.STATUS.eq(1));
      Thing thing=null;
        try {
            thing = thingDao.selectOneByQuery(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new ServiceException("查询小记服务器异常",EventCode.SELECT_EXCEPTION);
        }
        //判断小记是否存在
        if(thing==null){
            throw  new ServiceException("小记不存在，请刷新后再试",EventCode.SELECT_NONE);
        }
        //将查询到的小记返回出去
        return thing;
    }

    @Override
    public void newCreateThing(Thing thing) throws ServiceException {
        //新增小记
        int count = 0;
        try {
            count = thingDao.insert(thing);
        } catch (Exception e) {
         e.printStackTrace();
         throw new ServiceException("新增小记失败",EventCode.THING_CREATE_EXCIPITION);
        }
        if(count!=1){
            throw new ServiceRollBackException("新增小记失败",EventCode.THING_CREATE_ERROR);
        }

        //新增小记的日志
        NoteThingLog log= NoteThingLog.builder()
                .time(thing.getUpdateTime())
                .event(EventCode.THING_CREATE_SUCCESS)
                .desc("新增小记")
                .thingId(thing.getId())
                .userId(thing.getUserId())
                .build();
        //新增笔记小记日志记录
        noteThingLogService.addOneLog(log,true);

    }

    @Override
    public void deleteThingById(boolean complete, int thingId, int userId, boolean isRecycleBin) throws ServiceException {
        //默认为正常删除操作，并不是彻底删除，也不是回收站中的删除
        String desc="删除小记";
        String event=EventCode.THING_DELETE_SUCCESS;
        int beforeStatus =1;//删除之前的状态
        int afterStatus=0;//删除之后的状态
        if(complete){
            event=EventCode.THING_COMPLETE_DELETE_SUCCESS;
            desc="彻底删除小记";
            afterStatus=-1;
            if(isRecycleBin) beforeStatus=0;//在回收站中的小记状态都是已删除的
        }



        //使得status变为0或者-1
        //封装修改条件
        QueryWrapper wrapper = QueryWrapper.create().where(THING.ID.eq(thingId))
                .and(THING.USER_ID.eq(userId))
                .and(THING.STATUS.eq(beforeStatus));
        //操作的时间
        Date localTime=new Date();
          //要修改的哪些字段:status
        Thing thing=Thing.builder().status(afterStatus).updateTime(localTime).build();

        int count = 0;
        try {
            //调用修改语句（数据库接口）
            count = thingDao.updateByQuery(thing, wrapper);
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
                .thingId(thingId)
                .userId(userId)
                .build();
        //新增笔记小记日志记录
        noteThingLogService.addOneLog(log,true);

    }

    @Override
    public void topThing(boolean isTop, int thingId, int userId) throws ServiceException {
        int beforetop=0;//修改之前的top字段值
        int aftertop=1;//修改之后的top字段值
        String desc="置顶小记";
        String eventSuccess=EventCode.THING_TOP_SUCCESS;//事件代码
        String eventFailed=EventCode.THING_TOP_FAILED;
        if(!isTop){
            eventSuccess=EventCode.THING_CANCEL_TOP_SUCCESS;//事件代码
            eventFailed=EventCode.THING_CANCEL_TOP_FAILED;
            desc="取消置顶小记";
            beforetop=1;
            aftertop=0;
        }
        //封装修改的条件 where 'id'=? and 'u_id'=? and 'status'=1 and 'top'=1/0
        QueryWrapper wrapper = QueryWrapper.create().where(THING.ID.eq(thingId))
                .and(THING.USER_ID.eq(userId))
                .and(THING.STATUS.eq(1))
                .and(THING.TOP.eq(beforetop));
        //封装修改的字段 top
        Thing thing=Thing.builder().top(aftertop).build();

        //根据wrapper条件来修改小记的top字段
        int count = 0;
        try {
            count = thingDao.updateByQuery(thing, wrapper);
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
               .thingId(thingId)
               .userId(userId)
               .build();
       //新增笔记小记日志记录
       noteThingLogService.addOneLog(log,true);


    }

    /**
     * @param search 查询关键词(标题含有或者标签含有)
     * @param filter 过滤[null:默认,0:只查询未完成,1:只查询已完成]
     * @param userId 用户编号
     * @return 小记对象集合
     * @throws ServiceException 业务异常
     */
    @Override
    public List<Thing> getUserNormalThing(String search,Integer filter,int userId) throws ServiceException {
        //WHERE u_id= ? AND 'status' =1
        QueryWrapper wrapper=QueryWrapper.create()
                .select(THING.ID,THING.TITLE,THING.TOP,THING.TAGS,THING.UPDATE_TIME,THING.FINISHED)
                .where(THING.USER_ID.eq(userId))
                .and(THING.STATUS.eq(1));
        //是否有关键词查询AND('title'= ? OR 'tags'=?)
        if(Validator.isNotEmpty(search)){
            wrapper.and(
                THING.TITLE.like(search).or(THING.TAGS.like(search))
            );
        }
        //是否有过滤条件(过滤finished字段)
            if(filter!=null &&(filter==0||filter==1)){
                wrapper.and(THING.FINISHED.eq(filter));
            }
        //排序  ORDERBY 'finished'.'top' desc,'update_time'desc
        wrapper.orderBy(THING.FINISHED.asc(),THING.TOP.desc(),THING.UPDATE_TIME.desc());
        try {
            //根据条件查询用户的小记
           return thingDao.selectListByQuery(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new ServiceException("小记列表服务器异常", EventCode.SELECT_EXCEPTION);
        }

    }
}
