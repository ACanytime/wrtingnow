package com.zhike.service;

import com.zhike.exception.ServiceException;
import com.zhike.pojo.Note;
import com.zhike.pojo.Thing;

import javax.sql.rowset.serial.SerialException;
import java.util.Date;
import java.util.List;

//关于笔记的业务接口
public interface INoteService {
    /**
     * 保存正在编辑的笔记
     * @param noteId 笔记编号
     * @param userId 用户编号
     * @param title 笔记标题
     * @param body 笔记内容
     * @param content 笔记内容完整的
     * @return 响应数据
     * @throws ServiceException 业务异常
     */
    Date saveEditingNote(int noteId, int userId, String title, String body, String content) throws ServiceException;
    /**
     * 获取编辑的笔记信息
     * @param noteId 笔记编号
     * @param userId 用户编号
     * @return 响应数据
     * @throws ServiceException 业务异常
     */
    Note getEditNote(int noteId, int userId) throws ServiceException;

    /**
     * 创建笔记(并且初始化笔记)
     * @param userId 用户编号
     * @return 新笔记
     * @throws ServiceException 业务异常
     */
    int createNoteInit(int userId) throws ServiceException;
    /**
     * 置顶笔记(取消置顶笔记)
     * @param isTop 是否置顶笔记
     * @param noteId 笔记编号
     * @param userId 用户编号
     * @throws ServiceException 业务异常
     */
    void topNote(boolean isTop,int noteId,int userId)throws ServiceException;
    /**
     *删除笔记
     * @param complete 是否彻底删除笔记
     * @param noteId  笔记编号
     * @param userId 用户编号
     * @param isRecycleBin 是否是回收站中的操作
     * @throws ServiceException 业务异常
     */
    void deleteNoteById(boolean complete,int noteId,int userId,boolean isRecycleBin)throws ServiceException;
    /**
     * 获取用户正常的笔记
     * @param userId 用户id
     * @return 笔记列表
     * @throws SerialException 业务异常
     */
    List<Note> getUserNormalNotes(int userId) throws ServiceException;
}
