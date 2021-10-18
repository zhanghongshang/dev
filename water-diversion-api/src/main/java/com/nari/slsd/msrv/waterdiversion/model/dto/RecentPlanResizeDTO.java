package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author bigb*/
@Getter
@Setter
public class RecentPlanResizeDTO {

    /**
     * 引水口id
     */
    private String buildingId;
    /**
     * 调整时间
     */
    private Timestamp resizeDate;
    /**
     * 调整后水量
     */
    private String resizeWater;
}
