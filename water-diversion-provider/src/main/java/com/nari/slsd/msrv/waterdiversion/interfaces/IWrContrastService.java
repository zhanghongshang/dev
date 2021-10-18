package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.waterdiversion.config.RedisUtil;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrUseUnitManager;
import com.nari.slsd.msrv.waterdiversion.model.vo.PlanContrast;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitManagerVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitNode;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;

public interface IWrContrastService extends IService<WrUseUnitManager> {

    /**
     * 查询全部用水单位信息
     *
     * @param
     * @return
     */
    List<WrUseUnitNode> getAllWaterUseUnitList();

    /**
     *  根据用水单位id或引水口id查询同期数据对比
     */
    List<PlanContrast> getContrast();

    /**
     * 查询用水单位下的所有引水口
     */
    List<WrUseUnitNode> getBuildingIdByWaterUnitId(String WaterUnitId);

    /**
     * 获取同期对比值
     * @param waterUnitId
     * @param levels
     * @return
     */
    PlanContrast buildingContrast(String waterUnitId,String levels);
}
