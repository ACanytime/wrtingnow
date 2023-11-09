package com.zhike.util.validate;

import cn.hutool.core.lang.Validator;
import cn.hutool.json.JSONUtil;
import com.zhike.exception.ValidateParamException;
import com.zhike.pojo.User;
import com.zhike.util.code.EventCode;
import com.zhike.util.response.ResponseData;
import org.springframework.data.redis.core.StringRedisTemplate;
//封装函数，验证用户是否登录或者登录是否失效

public class TokenValidateUtil{
    public static User validateUserToken(String userToken, StringRedisTemplate redisTemplate) throws ValidateParamException {
        //验证userToken是否为空
        if(Validator.isEmpty(userToken))
            throw new ValidateParamException("登录状态有误",EventCode.PARAM_USER_TOKEN_WRONG);

        //从redis中获取登录用户的信息
        String userTokenRedisValue= null;
        try {
            userTokenRedisValue = redisTemplate.opsForValue().get(userToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValidateParamException("服务错误",EventCode.REDIS_SERVE_ERROR);

        }
        //判断是否登录失效
        if(Validator.isEmpty(userTokenRedisValue))
            throw new ValidateParamException("登录失效",EventCode.LOGIN_INVALID);

        //将userTokenRedisValue通过JSON转化为登录的用户的对象
        return JSONUtil.toBean(userTokenRedisValue, User.class);
    }
}
