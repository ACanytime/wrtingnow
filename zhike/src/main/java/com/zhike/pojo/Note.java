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

@Table("z_note")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Note {
    @Id(keyType = KeyType.Auto)
    private Integer id;//编号
    private String title;//标题
    private String body;//内容
    private String content;//内容(完整的)
    private Date time;//创建时间
    @Column("update_time")
    private Date updateTime;//最后一次操作时间

    @Column("u_id")
    private Integer userId;//小记所属用户编号
    @Column(onInsertValue = "0")
    private Integer top;//是否置顶【1：置顶、0：不置顶】
    @Column(onInsertValue = "1")
    private Integer status;//状态【0：删除，1：正常、-1：彻底删除】
    @Column(onInsertValue = "1")
    private Integer type;//类别【1：笔记、2：小记】
}
