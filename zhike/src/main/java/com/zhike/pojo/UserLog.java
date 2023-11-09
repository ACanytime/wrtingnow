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

@Table("z_user_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLog {
    @Id(keyType = KeyType.Auto)
    private Integer id;//用户日志编号
    private String desc;//描述
     private Date time;//注册时间
    private String event;//事件码
    @Column("u_id")
    private Integer userId;//日志所对应的用户编号
}
