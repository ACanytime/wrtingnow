package com.zhike.pojo;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

//第一个密码eVF4TJ
@Table("z_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id(keyType = KeyType.Auto)
    private Integer id;//用户编号
    private String email;//邮箱(唯一)
    private String password;//密码(加密)
    private String nickname;//昵称
    @Column("head_pic")
    private String headPic;//头像
    @Column(onInsertValue = "0")
    private Integer level;//等级【0：普通用户，1：VIP用户】
    private Date time;//注册时间
    @Column(onInsertValue = "1")
    private Integer status;//状态【0：锁定，1：正常】
}
