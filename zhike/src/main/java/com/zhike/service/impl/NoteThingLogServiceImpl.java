package com.zhike.service.impl;

import com.zhike.dao.INoteThingLogDao;
import com.zhike.exception.ServiceException;
import com.zhike.exception.ServiceRollBackException;
import com.zhike.pojo.NoteThingLog;
import com.zhike.service.INoteThingLogService;
import com.zhike.util.code.EventCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialException;

//笔记小记日志的数据库接口
@Service
public class NoteThingLogServiceImpl implements INoteThingLogService {

    @Autowired
    private INoteThingLogDao noteThingLogDao;//笔记小记日志的数据库接口
    @Override
    public void addOneLog(NoteThingLog log, boolean isRollback) throws ServiceException {
        String message=log.getDesc()+"失败！";
        int count;
        try {
            count = noteThingLogDao.insert(log);
        } catch (Exception e) {
            e.printStackTrace();
            if(isRollback){
                throw  new ServiceRollBackException(message, EventCode.LOG_CREATE_EXCEPITION);
            }
            else {
                throw  new ServiceException(message,EventCode.LOG_CREATE_EXCEPITION);
            }

        }
        if (count!=1){
            if(isRollback){
                throw  new ServiceRollBackException(message, EventCode.LOG_CREATE_ERROR);
            }
            else {
                throw  new ServiceException(message,EventCode.LOG_CREATE_ERROR);
            }
        }
    }
}
