package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.utils.BeanUtils;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterBuildingManagerService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanTaskService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanTaskSubService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrPlanTaskMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrPlanTaskSubMapper;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanTask;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanTaskSub;
import com.nari.slsd.msrv.waterdiversion.model.vo.BuildingExt;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanTaskVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitSimpleVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  用水计划任务 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2021-08-13
 */
@Service
public class WrPlanTaskSubImpl extends ServiceImpl<WrPlanTaskSubMapper, WrPlanTaskSub> implements IWrPlanTaskSubService {

}
