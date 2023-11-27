package com.zhike.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.json.JSONUtil;
import com.zhike.exception.ServiceException;
import com.zhike.pojo.Note;
import com.zhike.pojo.NoteThingLog;
import com.zhike.pojo.User;
import com.zhike.service.INoteService;
import com.zhike.service.INoteThingLogService;
import com.zhike.service.IRecordService;
import com.zhike.util.code.EventCode;
import com.zhike.util.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/record")
public class RecordController {
    @Autowired
    private IRecordService recordService;//的业务接口
    @Autowired
    private StringRedisTemplate redisTemplate;//redis对象
    /**
     *获取最近操作的列表
     * url: http://127.0.0.1:18081/zhike-notes/record/list
     * 请求方式：GET
     * @param userToken redis key,登录用户的信息
     * @return 响应数据
     */
    @GetMapping("/list")
    public ResponseData getUserNoteList(String search,@RequestHeader String userToken){
        //验证userToken是否为空
        if(Validator.isEmpty(userToken)) return new ResponseData(false,"登录状态有误", EventCode.PARAM_USER_TOKEN_WRONG);
        //从redis中获取登录用户的信息
        String userTokenRedisValue= null;
        try {
            userTokenRedisValue = redisTemplate.opsForValue().get(userToken);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseData(false,"小记服务错误",EventCode.REDIS_SERVE_ERROR);
        }
        //判断是否登录失效
        if(Validator.isEmpty(userTokenRedisValue))
            return new ResponseData(false,"登录失效",EventCode.LOGIN_INVALID);
        //将userTokenRedisValue通过JSON转化为登录的用户的对象
        User user = JSONUtil.toBean(userTokenRedisValue, User.class);
        try {
            //调用用户的小记列表的业务
            List<NoteThingLog> recordList = recordService.getRecordList(search,user.getId());
            return new ResponseData(true,"获取成功",EventCode.SELECT_SUCCESS,recordList);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false,e.getMessage(),e.getCode());
        }


    }
}
