package com.zhike.controller;

//关于小记的控制层

import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.zhike.exception.ServiceException;
import com.zhike.pojo.Thing;
import com.zhike.pojo.User;
import com.zhike.service.IThingService;
import com.zhike.service.IUserService;
import com.zhike.util.code.EventCode;
import com.zhike.util.response.ResponseData;
import com.zhike.util.validate.TokenValidateUtil;
import jdk.nashorn.internal.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/thing")//url: http://127.0.0.1:18081/zhike-notes/user
public class ThingController {

    @Autowired
    private IThingService thingService;//小记的业务
    @Autowired
    private StringRedisTemplate redisTemplate;//redis对象

    /**
     * 修改小记
     * @param thingId 小记编号
     * @param title 标题
     * @param top 是否置顶
     * @param tags 标签
     * @param content 内容
     * @param finished 是否完成
     * @param userToken 用户信息
     * @return
     */
    @PostMapping ("/update")
    public ResponseData updateThing(int thingId,String title,boolean top,String tags,String content,boolean finished,@RequestHeader String userToken){

        try {
            //判断登录函数
            User user = TokenValidateUtil.validateUserToken(userToken, redisTemplate);
            //验证小记标题参数
            if(Validator.isEmpty(thingId)) return  new ResponseData(false,"小记编号参数有误",EventCode.PARAM_THING_ID_WRONG);
            //验证小记标题参数
            if(Validator.isEmpty(title)) return  new ResponseData(false,"小记标题参数有误",EventCode.PARAM_THING_TITLE_WRONG);
            //验证置顶参数
            if(Validator.isEmpty(top)) return  new ResponseData(false,"小记置顶参数有误",EventCode.PARAM_THING_TOP_WRONG);
            //验证小记标签参数
            if(Validator.isEmpty(tags)) return  new ResponseData(false,"小记标签参数有误",EventCode.PARAM_THING_TAGS_WRONG);
            //验证小记内容参数
            if(Validator.isEmpty(content)) return  new ResponseData(false,"小记内容参数有误",EventCode.PARAM_THING_CONTENT_WRONG);
            //验证小记完成参数
            if(Validator.isEmpty(finished)) return  new ResponseData(false,"小记完成参数有误",EventCode.PARAM_THING_FINISHED_WRONG);

            Date localTime = new Date();
            Thing thing=Thing.builder()
                    .id(thingId)
                    .updateTime(localTime)
                    .title(title)
                    .tags(tags)
                    .content(content)
                    .userId(user.getId())
                    .finished(finished ? 1 : 0)
                    .top(top ? 1 : 0)
                    .build();
            //调用用户的小记列表的业务
            thingService.updateThing(thing);
            return new ResponseData(true,"修改小记成功",EventCode.THING_UPDATE_SUCCESS);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false,e.getMessage(),e.getCode());
        }


    }
    /**
     *编辑小记
     * url: http://127.0.0.1:18081/zhike-notes/thing/edit
     * 请求方式：GET
     * @param thingId 小记编号
     * @param userToken redis key,登录用户的信息
     * @return 响应数据
     */
    @GetMapping ("/edit")
    public ResponseData getUserEditThing(int thingId,@RequestHeader String userToken){

        try {
            //判断登录函数
            User user = TokenValidateUtil.validateUserToken(userToken, redisTemplate);
            //验证小记编号参数
            if(Validator.isEmpty(thingId)) return  new ResponseData(false,"小记编号参数有误",EventCode.PARAM_THING_ID_WRONG);
            //调用编辑小记业务
            Thing editThing = thingService.getEditThing(thingId, user.getId());
            return new ResponseData(true,"编辑小记成功",EventCode.UPDATE_SUCCESS,editThing);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false,e.getMessage(),e.getCode());
        }


    }

    /**
     * 新增小记
     * 请求方式：POST
     * @param title 标题
     * @param top 是否置顶
     * @param tags 标签（“六一，礼物，儿童节”）
     * @param content 待办事项（【{“checked”：ture，“thing‘：”云笔记“}，{{“checked”：ture，“thing‘：”电脑“}}】）
     * @param finished 是否完成
     * @param userToken redis key 登录用户的信息
     * @return 响应数据
     */
    @PostMapping ("/create")
    public ResponseData createThing(String title,boolean top,String tags,String content,boolean finished,@RequestHeader String userToken){

        try {
            //判断登录函数
            User user = TokenValidateUtil.validateUserToken(userToken, redisTemplate);
            //验证小记标题参数
            if(Validator.isEmpty(title)) return  new ResponseData(false,"小记标题参数有误",EventCode.PARAM_THING_TITLE_WRONG);
            //验证置顶参数
            if(Validator.isEmpty(top)) return  new ResponseData(false,"小记置顶参数有误",EventCode.PARAM_THING_TOP_WRONG);
            //验证小记标签参数
            if(Validator.isEmpty(tags)) return  new ResponseData(false,"小记标签参数有误",EventCode.PARAM_THING_TAGS_WRONG);
            //验证小记内容参数
            if(Validator.isEmpty(content)) return  new ResponseData(false,"小记内容参数有误",EventCode.PARAM_THING_CONTENT_WRONG);
            //验证小记完成参数
            if(Validator.isEmpty(finished)) return  new ResponseData(false,"小记完成参数有误",EventCode.PARAM_THING_FINISHED_WRONG);

            Date localTime = new Date();
            Thing thing=Thing.builder()
                    .updateTime(localTime)
                    .time(localTime)
                    .title(title)
                    .tags(tags)
                    .content(content)
                    .userId(user.getId())
                    .finished(finished ? 1 : 0)
                    .top(top ? 1 : 0)
                   .build();
            //调用用户的小记列表的业务
            thingService.newCreateThing(thing);
            return new ResponseData(true,"新增小记成功",EventCode.THING_CREATE_SUCCESS);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false,e.getMessage(),e.getCode());
        }


    }
    /**
     *删除小记
     * url: http://127.0.0.1:18081/zhike-notes/thing/delete
     * 请求方式：DELETE
     * @param complete 是否为彻底删除
     * @param thingId 小记编号
     * @param isRecycleBin 是否为回收站的操作
     * @param userToken redis key,登录用户的信息
     * @return 响应数据
     */
    @DeleteMapping ("/delete")
    public ResponseData deleteThing(boolean complete,int thingId,boolean isRecycleBin,@RequestHeader String userToken){

        try {
            //判断登录函数
            User user = TokenValidateUtil.validateUserToken(userToken, redisTemplate);
            //验证回收站参数
            if(Validator.isEmpty(isRecycleBin)) return  new ResponseData(false,"删除参数有误",EventCode.PARAM_DELETE_RECYCLE_BIN_WRONG);
            //验证彻底删除参数
            if(Validator.isEmpty(complete)) return  new ResponseData(false,"删除参数有误",EventCode.PARAM_DELETE_COMPLETE_WRONG);
            //验证小记编号参数
            if(Validator.isEmpty(thingId)) return  new ResponseData(false,"小记编号参数有误",EventCode.PARAM_THING_ID_WRONG);
            //调用用户的小记列表的业务
            thingService.deleteThingById(complete,thingId, user.getId(),isRecycleBin);
            return new ResponseData(true,complete?"彻底删除成功" :"删除成功",EventCode.UPDATE_SUCCESS);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false,e.getMessage(),e.getCode());
        }


    }

    /**
     *置顶小记
     * url: http://127.0.0.1:18081/zhike-notes/thing/top
     * 请求方式：GET
     *
     * @param userToken redis key,登录用户的信息
     * @return 响应数据
     */
    @GetMapping ("/top")
    public ResponseData topThing(boolean isTop,int thingId,@RequestHeader String userToken){

        try {
            //判断登录函数
            User user = TokenValidateUtil.validateUserToken(userToken, redisTemplate);
            //验证置顶参数
            if(Validator.isEmpty(isTop)) return  new ResponseData(false,"置顶参数有误",EventCode.PARAM_TOP_WRONG);
            //验证小记编号参数
            if(Validator.isEmpty(thingId)) return  new ResponseData(false,"小记编号参数有误",EventCode.PARAM_THING_ID_WRONG);
            //调用用户的小记列表的业务
            thingService.topThing(isTop,thingId, user.getId());
            return new ResponseData(true,isTop?"置顶成功" :"取消置顶成功",EventCode.UPDATE_SUCCESS);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false,e.getMessage(),e.getCode());
        }


    }

    /**
     *获取用户的小记列表
     * url: http://127.0.0.1:18081/zhike-notes/thing/list
     * 请求方式：GET
     *@param search 查询关键词(标题含有或者标签含有)
     * @param filter 过滤[null:默认,0:只查询未完成,1:只查询已完成]
     * @param userToken redis key,登录用户的信息
     * @return 响应数据
     */
    @GetMapping ("/list")
    public ResponseData getUserThingList(String search,Integer filter,@RequestHeader String userToken){
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
            List<Thing> things = thingService.getUserNormalThing(search,filter,user.getId());
            return new ResponseData(true,"获取成功",EventCode.LOGIN_OUT_SUCCESS,things);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false,e.getMessage(),e.getCode());
        }


    }
}
