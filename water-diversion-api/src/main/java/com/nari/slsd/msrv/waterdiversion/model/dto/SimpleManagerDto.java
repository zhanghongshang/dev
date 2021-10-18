package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

/**
 * @title
 * @description 审批dto
 * @author bigb
 * @updateTime 2021/8/22 10:10
 * @throws
 */
@Data
public class SimpleManagerDto {
    /**
     * managerId
     */
    private String managerId;
    /**
     * approveStatus
     */
    private String approveStatus;
    /**
     * approveName
     */
    private String approveName;
    /**
     * approveContent
     */
    private String approveContent;


}
