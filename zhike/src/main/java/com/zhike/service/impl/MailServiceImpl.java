package com.zhike.service.impl;


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.mail.MailUtil;
import com.zhike.exception.ServiceException;
import com.zhike.exception.ServiceRollBackException;

import com.zhike.service.IMailService;

import com.zhike.service.IUserService;
import com.zhike.util.code.EventCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.TimeUnit;

//邮箱业务的实现层
@Service
@Transactional(rollbackFor = {ServiceRollBackException.class})
public class MailServiceImpl implements IMailService {

    @Autowired
    private StringRedisTemplate redisTemplate;//redis对象

    @Autowired
    private IUserService userService;//用户的业务
    @Override
    public String getEmailRegisterVC(String email) throws ServiceException {
        //判断该邮箱是否被注册过
  userService.getEamilAccountIsExist(email);

        //发送验证码到注册的邮箱中,使用Hutool中的RandomUtil
         String code= RandomUtil.randomString(6);//随机生成6位字符（数值+字母）
        int time=15;
        String content="<p>【知科团队】尊敬的"+ email + ":</p>"+
                "<p>您正在申请注册账号服务，如本人操作，请勿泄露改验证码！</p>"+
                "<p>验证码为：<b style='font-size: 20px;color:blue;'>"+code+"</b></p>"
                +"有效时间为"+time+"分钟";

        try {
            MailUtil.send(email,"邮箱账号注册验证码",content,true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("验证码发送失败", EventCode.EMAIL_SEND_VC_ERROR);
        }
        //将验证码保存到redis中15分钟
        String eravcTokenKey="eravcToken:"+email+":"+ IdUtil.randomUUID();

        try {
            redisTemplate.opsForValue().set(eravcTokenKey,code,time, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("验证码存储失败",EventCode.EMAIL_SEND_VC_SAVE_REDIS_ERROR);
        }

        return eravcTokenKey;
    }
}
