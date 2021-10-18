package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Created by bigb
 * @program: ModelRequestDto
 * @description:模型请求DTO
 * @date: 2021/8/21 15:06
 */
@Data
public class ModelRequestDto {

    /**
     * 调整日期
     */
    private Long resizeDate;
    /**
     * 操作人
     */
    private String personId;
    /**
     * 调度模式
     */
    private String dispatchType;

    private List<ModelRequestInnerDto> dtoList;
}
