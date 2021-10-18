package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanInterTdayService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrPlanInterTdayMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanInterTdayDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterPlanFillinYear;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanInterDay;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanInterTday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
/**
 * @Description 旬迭代 实现类
 * @Author ZHS
 * @Date 2021/9/9 11:36
 */
@Service
public class WrPlanInterTdayServiceImpl extends ServiceImpl<WrPlanInterTdayMapper, WrPlanInterTday> implements IWrPlanInterTdayService {

    @Autowired
    WrPlanInterTdayMapper wrPlanInterTdayMapper;
    /**
     *  批量更新旬迭代数据
     * @param wrPlanInterTDayDTOList
     */
    @Override
    public void updateTday(List<WrPlanInterTdayDTO> wrPlanInterTDayDTOList) {
        for (WrPlanInterTdayDTO wrPlanInterTdayDTO:wrPlanInterTDayDTOList){
            UpdateWrapper<WrPlanInterTday> updateWrapper = new UpdateWrapper<WrPlanInterTday>();
            updateWrapper.eq("SUPPLY_TIME",wrPlanInterTdayDTO.getSupplyTime());
            updateWrapper.eq("BUILDING_ID",wrPlanInterTdayDTO.getBuildingId());
            WrPlanInterTday WrPlanIntertday = new WrPlanInterTday();
            WrPlanIntertday.setWaterQuantity(wrPlanInterTdayDTO.getWaterQuantity());
            WrPlanIntertday.setWaterFlow(wrPlanInterTdayDTO.getWaterFlow());
            wrPlanInterTdayMapper.update(WrPlanIntertday,updateWrapper);
        }

    }
    /**
     *  多条件查询单条信息
     * @param time
     */
    @Override
    public WrPlanInterTday wrPlanInterTday(Date time, String buildingId,String timeType) {
        //查看是否存在审批中的年计划填报
        QueryWrapper<WrPlanInterTday> wrapper = new QueryWrapper();
        wrapper.eq("SUPPLY_TIME",time);
        wrapper.eq("BUILDING_ID",buildingId);
        if (timeType!=null){
            wrapper.eq("TIME_TYPE",timeType);
        }else {
            wrapper.ne("TIME_TYPE","4");
        }
        WrPlanInterTday wrPlanInterTday = wrPlanInterTdayMapper.selectOne(wrapper);
        return wrPlanInterTday;
    }
}
