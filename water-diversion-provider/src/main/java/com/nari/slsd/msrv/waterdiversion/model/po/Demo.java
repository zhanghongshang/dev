package com.nari.slsd.msrv.waterdiversion.model.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 数据库实体类
 * @author: Created by ZHD
 * @date: 2021/4/1 15:55
 * @return:
 */
@TableName(value = "DEMO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Demo implements Serializable {

    @TableId(type= IdType.ASSIGN_ID)
    private Long id;

    private Integer age;

    private String name;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date date;

    /**
     * 乐观锁
     */
    @Version
    private Integer version;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;

}
