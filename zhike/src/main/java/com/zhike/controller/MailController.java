package com.zhike.controller;

//关于邮箱的控制层

import cn.hutool.core.lang.Validator;
import cn.hutool.crypto.SecureUtil;
import com.zhike.exception.ServiceException;
import com.zhike.service.IMailService;
import com.zhike.service.IUserService;
import com.zhike.util.code.EventCode;
import com.zhike.util.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/mail")//url: http://127.0.0.1:18081/zhike-notes/mail
public class MailController {

    @Autowired
    private IMailService mailService;//邮箱的业务

    /**
     *获取邮箱注册账号验证码
     * url: http://127.0.0.1:18081/zhike-notes/mail/register/vc
     * 请求方式：POST
     * @param email 邮箱
     *
     * @return 响应数据 查询验证码的关键词
     */
    @GetMapping("/register/vc")
  public ResponseData getEmailRegisterAccountVC(String email){
        //验证邮箱参数是否为空
        if(Validator.isEmpty(email)) return new ResponseData(false,"邮箱参数有误", EventCode.ACCOUNT_EMAIL_WRONG);


        //调用邮箱注册账号的验证码业务
        try {
            String emailRegisterVC = mailService.getEmailRegisterVC(email);
            return new ResponseData(true,"发送成功", EventCode.EMAIL_SEND_VC_SUCCESS,emailRegisterVC);

        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false,e.getMessage(),e.getCode());
        }

    }
}
