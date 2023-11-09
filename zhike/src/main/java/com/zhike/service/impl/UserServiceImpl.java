package com.zhike.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.zhike.dao.IUserDao;
import com.zhike.dao.IUserLogDao;
import com.zhike.exception.ServiceException;
import com.zhike.exception.ServiceRollBackException;
import com.zhike.pojo.User;
import com.zhike.pojo.UserLog;
import com.zhike.service.IUserService;
import com.zhike.util.code.EventCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.zhike.pojo.table.Tables.USER;
@Service
@Transactional(rollbackFor = {ServiceRollBackException.class})
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserDao userDao;//用户的数据库接口
    @Autowired
    private IUserLogDao userLogDao;//用户的日志接口
    @Autowired
    private StringRedisTemplate redisTemplate;//redis对象

    @Override
    public void registerAccountByEmail(String email) throws ServiceException {
        getEamilAccountIsExist(email);//获取邮箱号是否被注册
        //新增用户记录
        Date localTime=new Date();//时间
        String password= RandomUtil.randomString(6);//初始密码
        User user=User.builder()
                .email(email)
                .password(SecureUtil.md5(password))//密码加密
                .time(localTime)
                .build();

        int count = 0;
        try {
            count = userDao.insert(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("注册失败",EventCode.INSERT_EXCEPITON);
        }
        if(count!=1) throw new ServiceRollBackException("注册失败",EventCode.INSERT_ERROR);

        //新增一个用户的日志
        UserLog log=UserLog.builder()
                .event(EventCode.ACCOUNT_EMAIL_REGISTER_SUCCESS)
                .desc("邮箱注册")
                .time(new Date())
                .userId(user.getId())
                .build();


        try {
            count = userLogDao.insert(log);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("注册失败",EventCode.ACCOUNT_EMAIL_REGISTER_LOG_EXCEPITON);
        }
        if(count!=1) throw new ServiceRollBackException("登录服务错误",EventCode.ACCOUNT_EMAIL_REGISTER_LOG_ERROR);

        //邮箱通知注册的用户其初始密码
        String content="<p>【知科团队】尊敬的"+ email + ":</p>"+
                "<p>您已成功注册知科笔记账号，其初始密码为：<b style='font-size: 20px;color:blue;'>"+password+"</b></p>"+
                "<p>请您尽快登录账号，修改初始密码！</p>";

        try {
            MailUtil.send(email,"知科账号注册通知",content,true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceRollBackException("注册失败",EventCode.EMAIL_SEND_INIT_PASSWORD_EXCEPITION);
        }

    }

    //获取邮箱是否已被注册
    @Override
    public void getEamilAccountIsExist(String email) throws ServiceException {
        QueryWrapper wrapper = QueryWrapper.create().where(USER.EMAIL.eq(email));
        long count= 0;
        try {
            count = userDao.selectCountByQuery(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("查询服务异常",EventCode.SELECT_EXCEPTION);
        }

        if(count!=0) throw  new ServiceException("该邮箱账号已被注册",EventCode.ACCOUNT_EMAIL_REGISTERED);

    }

    //登录账号密码
    @Override
    public Map loginByEmailAndPassword(String email, String password) throws ServiceException {
        //根据邮箱和密码查询用户记录
        QueryWrapper wrapper = QueryWrapper.create().select(USER.ID,USER.EMAIL,USER.NICKNAME,USER.HEAD_PIC,USER.LEVEL,USER.STATUS,USER.TIME)
                .where(USER.EMAIL.eq(email))
                .and(USER.PASSWORD.eq(password));

        User user = null;
        try {
            user = userDao.selectOneByQuery(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            //执行查询报错
            throw new ServiceException("登陆服务器错误", EventCode.SELECT_EXCEPTION);
        }

        if(user==null){
            //账号或密码错误
         throw new ServiceException("账号或密码错误",EventCode.SELECT_NONE);
        }
        if(user.getStatus()==0) throw  new ServiceException("账号被锁定",EventCode.ACCOUNT_CLOCK);

        //新增用户日志(登录）
        UserLog log=UserLog.builder()
                .event(EventCode.LOGIN_EMAIL_PASSWORD_SUCCESS)
                .desc("邮箱密码登录")
                .time(new Date())
                .userId(user.getId())
                .build();

        int count = 0;
        try {
            count = userLogDao.insert(log);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("登录服务错误",EventCode.LOGIN_LOGIN_CREATE_EXCEPTION);
        }
        if(count!=1) throw new ServiceRollBackException("登录服务错误",EventCode.LOGIN_LOGIN_CREATE_FAIL);

        //将登录的信息存储到redis中，因为要记录客户端下次访问时从redis中来取，看是否存在，14天，并将查询登录的用户的关键词返回客户端
        //生成唯一的key值 使用Hutool唯一id工具 UUid
        String userTokenKey = "userToken:" + IdUtil.randomUUID();
        try {
            redisTemplate.opsForValue().set(
                    userTokenKey,
                    JSONUtil.toJsonStr(user),
                    14,
                    TimeUnit.DAYS
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceRollBackException("登录服务错误",EventCode.LOGIN_SAVE_USER_REDIS_EXCEPTION);
        }
        //将登录的用户信息和查询redis的用户信息的关键词返回出去
        Map<String,Object> map=new HashMap<>();
        map.put("user",user);
        map.put("userTokenKey",userTokenKey);
        return map;
    }
}
