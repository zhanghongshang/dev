package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterPointService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WaterPointMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.WaterPointDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterPoint;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by ZHD
 * @program: WaterPointServiceImpl
 * @description:
 * @date: 2021/8/16 13:54
 */
@Service
public class WaterPointServiceImpl extends ServiceImpl<WaterPointMapper, WaterPoint> implements IWaterPointService{
    @Override
    public List<WaterPointDTO> getWaterPointId(List<String> buildings,List<String> pointType) {

        List<WaterPointDTO> result = new ArrayList<>();
        List<WaterPoint> waterPoints = this.baseMapper.selectList(new LambdaQueryWrapper<WaterPoint>().in(WaterPoint::getPid,buildings)
                .in(WaterPoint::getPointType,pointType));
        if(CollectionUtils.isEmpty(waterPoints)) {
            return result;
        } else {
            waterPoints.stream().filter((data) -> {
                return data != null;
            }).forEach((data) -> {
                result.add(this.convert2DTO(data));
            });
            return result;
        }
    }

    protected WaterPointDTO convert2DTO(WaterPoint waterPoint) {
        WaterPointDTO waterPointDTO = new WaterPointDTO();
        BeanUtils.copyProperties(waterPoint,waterPointDTO);
        waterPointDTO.setBuildingId(waterPoint.getPid());
        return waterPointDTO;
    }
}
