package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.*;

import java.io.Serializable;

/**
 * Created by asus on 2018/11/16.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeptStationRequest implements Serializable {

    private String stationid;

    /**
     * 名称 varchar 20
     */
    private String staname = "";

    /**
     * 当前部门的层级 varchar 200
     */
    private String type = "";


    /**
     * 最后一次操作人的ID varchar 20
     */
    private String operator = "";

    /**
     * 最后一次操作时间 时间戳
     */
    private Long operateTime = System.currentTimeMillis();

    /**
     * 最后一次操作的IP varchar 20
     */
    private String operateIp = "";

}
