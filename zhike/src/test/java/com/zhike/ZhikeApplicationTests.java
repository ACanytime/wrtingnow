package com.zhike;

import cn.hutool.extra.mail.MailUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.zhike.dao.IUserDao;
import com.zhike.exception.ServiceException;
import com.zhike.pojo.User;
import com.zhike.service.IMailService;
import com.zhike.service.impl.MailServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static com.zhike.pojo.table.Tables.USER;

@SpringBootTest
class ZhikeApplicationTests {
    @Autowired
    private IMailService iMailService;//关于用户的数据库接口


    @Test
    void contextLoads() throws ServiceException {


        String emailRegisterVC = iMailService.getEmailRegisterVC("571836771@qq.com");
        System.out.println(emailRegisterVC);

    }

}
