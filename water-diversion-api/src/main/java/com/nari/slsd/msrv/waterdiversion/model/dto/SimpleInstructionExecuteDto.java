package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @title
 * @description 指令执行dto
 * @author bigb
 * @updateTime 2021/8/22 10:10
 * @throws
 */
@Data
public class SimpleInstructionExecuteDto {
    /**
     * idList
     */
    private String id;
    /**
     * operateType
     */
    private String operateType;
    /**
     * personId
     */
    private String personId;

}
