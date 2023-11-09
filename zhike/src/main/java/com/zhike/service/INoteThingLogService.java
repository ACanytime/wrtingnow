package com.zhike.service;

import com.zhike.exception.ServiceException;
import com.zhike.pojo.NoteThingLog;

import javax.sql.rowset.serial.SerialException;

//笔记小记日志业务
public interface INoteThingLogService {

    /**
     * 新增一个笔记小记的日志记录
     * @param log 日志对象
     * @param isRollback 是否需要回滚
     * @throws SerialException 业务异常 回滚业务异常
     */
    void addOneLog(NoteThingLog log,boolean isRollback) throws ServiceException;
}
