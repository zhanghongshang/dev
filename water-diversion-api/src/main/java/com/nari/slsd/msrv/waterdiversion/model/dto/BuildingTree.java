package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @title
 * @description 维护上下游引水口层级关系
 * @author bigb
 * @updateTime 2021/8/26 10:19
 * @throws
 */
@Data
public class BuildingTree {

    private String id;

    private boolean isLeaf = true;

    private List<String> subIdList;
}
