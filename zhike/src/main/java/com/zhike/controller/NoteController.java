package com.zhike.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.json.JSONUtil;
import com.zhike.exception.ServiceException;
import com.zhike.pojo.Note;
import com.zhike.pojo.Thing;
import com.zhike.pojo.User;
import com.zhike.service.INoteService;
import com.zhike.util.code.EventCode;
import com.zhike.util.response.ResponseData;
import com.zhike.util.validate.TokenValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/note")//url: http://127.0.0.1:18081/zhike-notes/note
public class NoteController {

    @Autowired
    private INoteService noteService;//笔记的业务接口
    @Autowired
    private StringRedisTemplate redisTemplate;//redis对象


    /**
     *获取用户在回收站种的小记列表
     * url: http://127.0.0.1:18081/zhike-notes/note/recycle
     * 请求方式：GET
     * @param userToken redis key,登录用户的信息
     * @return 响应数据
     */
    @GetMapping("/recycle")
    public ResponseData recycleNote(@RequestHeader String userToken){
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
            List<Note> notes = noteService.recycle(user.getId());
            return new ResponseData(true,"获取成功",EventCode.SELECT_SUCCESS,notes);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false,e.getMessage(),e.getCode());
        }


    }
    /**
     *保存笔记
     * url: http://127.0.0.1:18081/zhike-notes/note/save
     * 请求方式：POST
     * @param noteId 笔记编号
     *   * @param title 笔记标题
     *      * @param body 笔记内容
     *      * @param content 笔记内容完整的
     * @param userToken redis key,登录用户的信息
     * @return 响应数据
     */
    @PostMapping ("/save")
    public ResponseData saveEditThingNote(int noteId,String title,String body,String content,@RequestHeader String userToken){

        try {
            //判断登录函数
            User user = TokenValidateUtil.validateUserToken(userToken, redisTemplate);
            //验证小记编号参数
            if(Validator.isEmpty(noteId)) return  new ResponseData(false,"笔记编号参数有误",EventCode.PARAM_THING_ID_WRONG);
            //调用保存笔记业务
           Date time= noteService.saveEditingNote(noteId, user.getId(),title,body,content);
            return new ResponseData(true,"保存笔记成功",EventCode.NOTE_UPDATE_SUCCESS,time);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false,e.getMessage(),e.getCode());
        }


    }
    /**
     *编辑笔记
     * url: http://127.0.0.1:18081/zhike-notes/note/edit
     * 请求方式：GET
     * @param noteId 笔记编号
     * @param userToken redis key,登录用户的信息
     * @return 响应数据
     */
    @GetMapping ("/edit")
    public ResponseData getUserEditThing(int noteId,@RequestHeader String userToken){

        try {
            //判断登录函数
            User user = TokenValidateUtil.validateUserToken(userToken, redisTemplate);
            //验证小记编号参数
            if(Validator.isEmpty(noteId)) return  new ResponseData(false,"笔记编号参数有误",EventCode.PARAM_THING_ID_WRONG);
            //调用编辑小记业务
            Note editNote = noteService.getEditNote(noteId, user.getId());
            return new ResponseData(true,"编辑笔记成功",EventCode.UPDATE_SUCCESS,editNote);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false,e.getMessage(),e.getCode());
        }


    }

    /**
     *新增笔记
     * url: http://127.0.0.1:18081/zhike-notes/note/create
     * 请求方式：PUT
     * @param userToken redis key,登录用户的信息
     * @return 响应数据
     */
    @PutMapping("/create")
    public ResponseData CreateNote(@RequestHeader String userToken){

        try {
            //判断登录函数
            User user = TokenValidateUtil.validateUserToken(userToken, redisTemplate);

            //调用用户的笔记列表的业务
        int noteId=noteService.createNoteInit(user.getId());
            return new ResponseData(true,"新增笔记成功",EventCode.NOTE_CREATE_SUCCESS,noteId);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false,e.getMessage(),e.getCode());
        }


    }
    /**
     *删除笔记
     * url: http://127.0.0.1:18081/zhike-notes/note/delete
     * 请求方式：DELETE
     * @param complete 是否为彻底删除
     * @param noteId 笔记编号
     * @param isRecycleBin 是否为回收站的操作
     * @param userToken redis key,登录用户的信息
     * @return 响应数据
     */
    @DeleteMapping("/delete")
    public ResponseData deleteThing(boolean complete,int noteId,boolean isRecycleBin,@RequestHeader String userToken){

        try {
            //判断登录函数
            User user = TokenValidateUtil.validateUserToken(userToken, redisTemplate);
            //验证回收站参数
            if(Validator.isEmpty(isRecycleBin)) return  new ResponseData(false,"删除参数有误",EventCode.PARAM_DELETE_RECYCLE_BIN_WRONG);
            //验证彻底删除参数
            if(Validator.isEmpty(complete)) return  new ResponseData(false,"删除参数有误",EventCode.PARAM_DELETE_COMPLETE_WRONG);
            //验证笔记编号参数
            if(Validator.isEmpty(noteId)) return  new ResponseData(false,"笔记编号参数有误",EventCode.PARAM_THING_ID_WRONG);
            //调用用户的笔记列表的业务
            noteService.deleteNoteById(complete,noteId, user.getId(),isRecycleBin);
            return new ResponseData(true,complete?"彻底删除成功" :"删除成功",EventCode.UPDATE_SUCCESS);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false,e.getMessage(),e.getCode());
        }


    }
    @DeleteMapping("/deleterecycle")
    public ResponseData deleteNoterecycle(boolean complete,int noteId,boolean isRecycleBin,@RequestHeader String userToken){

        try {
            //判断登录函数
            User user = TokenValidateUtil.validateUserToken(userToken, redisTemplate);
            //验证回收站参数
            if(Validator.isEmpty(isRecycleBin)) return  new ResponseData(false,"删除参数有误",EventCode.PARAM_DELETE_RECYCLE_BIN_WRONG);
            //验证彻底删除参数
            if(Validator.isEmpty(complete)) return  new ResponseData(false,"删除参数有误",EventCode.PARAM_DELETE_COMPLETE_WRONG);
            //验证笔记编号参数
            if(Validator.isEmpty(noteId)) return  new ResponseData(false,"笔记编号参数有误",EventCode.PARAM_THING_ID_WRONG);
            //调用用户的笔记列表的业务
            noteService.deleteNoterecycle(complete,noteId, user.getId(),isRecycleBin);
            return new ResponseData(true,complete?"彻底删除成功" :"删除成功",EventCode.UPDATE_SUCCESS);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false,e.getMessage(),e.getCode());
        }


    }

    /**
     *置顶小记
     * url: http://127.0.0.1:18081/zhike-notes/note/top
     * 请求方式：GET
     *
     * @param userToken redis key,登录用户的信息
     * @return 响应数据
     */
    @GetMapping ("/top")
    public ResponseData topNote(boolean isTop,int noteId,@RequestHeader String userToken){

        try {
            //判断登录函数
            User user = TokenValidateUtil.validateUserToken(userToken, redisTemplate);
            //验证置顶参数
            if(Validator.isEmpty(isTop)) return  new ResponseData(false,"置顶参数有误",EventCode.PARAM_TOP_WRONG);
            //验证小记编号参数
            if(Validator.isEmpty(noteId)) return  new ResponseData(false,"笔记编号参数有误",EventCode.PARAM_ID_WRONG);
            //调用用户的小记列表的业务
            noteService.topNote(isTop,noteId, user.getId());
            return new ResponseData(true,isTop?"置顶成功" :"取消置顶成功",EventCode.UPDATE_SUCCESS);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false,e.getMessage(),e.getCode());
        }


    }
    /**
     *获取用户的小记列表
     * url: http://127.0.0.1:18081/zhike-notes/note/list
     * 请求方式：GET
     * @param userToken redis key,登录用户的信息
     * @return 响应数据
     */
    @GetMapping("/list")
    public ResponseData getUserNoteList(@RequestHeader String userToken){
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
            List<Note> notes = noteService.getUserNormalNotes(user.getId());
            return new ResponseData(true,"获取成功",EventCode.SELECT_SUCCESS,notes);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false,e.getMessage(),e.getCode());
        }


    }
}
