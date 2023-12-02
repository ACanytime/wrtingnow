package com.zhike.service;


import com.zhike.exception.ServiceException;
import com.zhike.pojo.Note;
import com.zhike.pojo.Thing;

import java.util.List;

//业务接口,小记的业务
public interface IThingService {
    /**
     * 获取在回收站种的小记信息
     * @param userId 用户编号
     * @return 响应数据
     * @throws ServiceException 业务异常
     */
    List<Thing> recycle(int userId) throws ServiceException;
    /**
     * 新增小记
     * @param thing 小记信息（title,tags,content,userId,finished,updateTime,top,id）
     * @throws ServiceException 业务异常
     */
    void updateThing(Thing thing) throws ServiceException;

    /**
     * 获取编辑的小记信息
     * @param thingId 小记编号
     * @param userId 用户编号
     * @return 响应数据
     * @throws ServiceException 业务异常
     */
    Thing getEditThing(int thingId,int userId) throws ServiceException;
    /**
     * 新增小记
     * @param thing 小记信息（title,tags,content,userId,finished,time,updateTime,top）
     * @throws ServiceException 业务异常
     */
    void newCreateThing(Thing thing) throws ServiceException;
    /**
     *删除小记
     * @param complete 是否彻底删除小记
     * @param thingId  小记编号
     * @param userId 用户编号
     * @param isRecycleBin 是否是回收站中的操作
     * @throws ServiceException 业务异常
     */
    void deleteThingById(boolean complete,int thingId,int userId,boolean isRecycleBin)throws ServiceException;
    /**
     *删除小记
     * @param complete 是否彻底删除小记
     * @param thingId  小记编号
     * @param userId 用户编号
     * @param isRecycleBin 是否是回收站中的操作
     * @throws ServiceException 业务异常
     */
    void deleteThingRecycle(boolean complete,int thingId,int userId,boolean isRecycleBin)throws ServiceException;

    /**
     * 置顶小记(取消置顶小记)
     * @param isTop 是否置顶小记
     * @param thingId 小记编号
     * @param userId 用户编号
     * @throws ServiceException 业务异常
     */

    void topThing(boolean isTop,int thingId,int userId)throws ServiceException;
    /**
     * 获取用户正常的小记
     * @param userId 用户编号
     * @param search 查询关键词(标题含有或者标签含有)
     * @param filter 过滤[null:默认,0:只查询未完成,1:只查询已完成]
     * @return 小记对象集合
     * @throws ServiceException 业务异常
     */
 List<Thing> getUserNormalThing(String search,Integer filter,int userId) throws ServiceException;

}
