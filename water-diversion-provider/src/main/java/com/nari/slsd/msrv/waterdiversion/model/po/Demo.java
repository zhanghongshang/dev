package com.nari.slsd.msrv.waterdiversion.model.po;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description: 数据库实体类
 * @author: Created by ZHD
 * @date: 2021/4/1 15:55
 * @return:
 */
@Entity
@Table(name ="demo")
@Getter
@Setter
@ToString
public class Demo implements Serializable {

    @Id
    @Column
    private Long id;

    private Integer age;

    private String name;

    private LocalDateTime date;

}
