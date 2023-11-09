package com.zhike.service;

import com.zhike.exception.ServiceException;

import java.util.Map;

//业务接口，邮箱的业务
public interface IMailService {
//获取邮箱注册的验证码，返回查询验证码的关键词（redis）

    String getEmailRegisterVC(String email) throws ServiceException;
}
