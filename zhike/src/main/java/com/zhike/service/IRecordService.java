package com.zhike.service;

import com.zhike.exception.ServiceException;
import com.zhike.pojo.NoteThingLog;

import java.util.List;

public interface IRecordService {
    List<NoteThingLog> getRecordList(String search,int userId) throws ServiceException;
}
