package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author bigb*/
@Getter
@Setter
public class WrDispatchInstructionDto {

    /**
     * 指令id
     */
    private String instructionId;
    /**
     * 引水口id
     */
    private String buildingId;
    /**
     * 引水口名称
     */
    private String buildingName;
    /**
     * 调整时间
     */
    private Long resizeTime;
    /**
     * 目标值
     */
    private Double setValue;
    /**
     * 调整目标值
     */
    private Double modifyValue;
}
