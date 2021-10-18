package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WaterPointDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterPoint;

import java.util.List;
import java.util.Map;

/**
 * @author Created by ZHD
 * @program: IWaterPointService
 * @description:
 * @date: 2021/8/16 13:51
 */
public interface IWaterPointService extends IService<WaterPoint> {
    List<WaterPointDTO> getWaterPointId(List<String> buildings,List<String> pointType);//根据测站id、引水口id获取相关测点信息
}
