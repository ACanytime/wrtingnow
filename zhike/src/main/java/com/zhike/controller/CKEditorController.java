package com.zhike.controller;

//关于富文本编辑器的控制层

import cn.hutool.core.lang.Validator;
import com.zhike.exception.ServiceException;
import com.zhike.service.IMailService;
import com.zhike.util.code.EventCode;
import com.zhike.util.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ckeditor")//url: http://127.0.0.1:18081/zhike-notes/ckeditor
public class CKEditorController {

    /**
     *富文本编辑器图像
     * url: http://127.0.0.1:18081/zhike-notes/ckeditor/upload/pic
     * 请求方式：POST
     * @param file 图像文件
     *
     * @return 响应数据 查询验证码的关键词
     */
   @PostMapping("/upload/pic")
  public HashMap<String, Object> uploadPic(@RequestParam("upload") MultipartFile file, HttpServletRequest request){
       HashMap<String,Object> responseData=new HashMap<>();//响应数据的一层数据
       HashMap<String,String> responseDataInfo=new HashMap<>();//响应数据的二层数据

       //判断是否有上传的文件
       if(file.isEmpty()){
         responseDataInfo.put("message","未找到图片源！");
         responseData.put("error",responseDataInfo);
         return responseData;
       }
       //上传到哪个磁盘文件夹下
       String fileMkdirsPath="C:"+ File.separator+"ck-file"+File.separator+"image";
       //上传到哪个磁盘文件夹下的虚拟路劲地址
       String urlImagePath=request.getScheme() + "://"+request.getLocalAddr()+":"
               +request.getServerPort()+request.getContextPath()+"/image/";

       //获取上传文件的名称
       String originalFileName=file.getOriginalFilename();
       if(originalFileName==null){
           responseDataInfo.put("message","未找到图片源！");
           responseData.put("error",responseDataInfo);
           return responseData;
       }
       //获取上传文件的后缀名
       String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
       //上传后的文件名称
       String fileName=System.currentTimeMillis()+extension;
       //判断上传到的文件夹是否存在，如果不存在则创建
       File temp = new File(fileMkdirsPath);
       if(!temp.exists()) temp.mkdirs();

       //最终上传的文件对象
       File localFile = new File(fileMkdirsPath + File.separator + fileName);
       try {
           //上传文件
           file.transferTo(localFile);
       } catch (IOException e) {
           e.printStackTrace();
           responseDataInfo.put("message","上传失败！");
           responseData.put("error",responseDataInfo);
           return responseData;
       }

       responseData.put("url",urlImagePath+fileName);
       return responseData;
   }
}
