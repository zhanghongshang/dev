package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @title
 * @description 调度方案结果vo
 * @author bigb
 * @updateTime 2021/8/21 14:38
 * @throws
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrDispatchSchemeResultVO {
    /**
     * ID
     */
    private String id;

    /**
     * 方案id
     */
    private String schemeId;

    /**
     * 引水口id
     */
    private String buildingId;

    /**
     * 引水口名称
     */
    private String buildingName;

    /**
     * 设备id
     */
    private String objectId;

    /**
     * 执行开始时间
     */
    private Long execStartTime;

    /**
     * 测点号
     */
    private String senId;

    /**
     * 目标值
     */
    private Double setValue;

    /**
     * 调整时序
     */
    private Long adjustOrder;

    /**
     * 完成后等待时间
     */
    private Long waitTime;

}
