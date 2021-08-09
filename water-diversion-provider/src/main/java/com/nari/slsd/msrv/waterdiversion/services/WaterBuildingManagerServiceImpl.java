package com.nari.slsd.msrv.waterdiversion.services;

import com.nari.slsd.msrv.waterdiversion.model.po.WaterBuildingManager;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WaterBuildingManagerMapper;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterBuildingManagerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 水工建筑物管理 服务实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-03
 */
@Service
public class WaterBuildingManagerServiceImpl extends ServiceImpl<WaterBuildingManagerMapper, WaterBuildingManager> implements IWaterBuildingManagerService {

}
