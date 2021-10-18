package com.nari.slsd.msrv.waterdiversion.services;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.utils.BeanUtils;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDwaMonthService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WaterBuildingManagerMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrDwaMonthMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterBuildingManager;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDwaMonth;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrDwaMonthVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @title
 * @description 月滚存指标服务类
 * @author bigb
 * @updateTime 2021/9/20 20:14
 * @throws
 */
@Service
public class WrDwaMonthImpl extends ServiceImpl<WrDwaMonthMapper, WrDwaMonth>  implements IWrDwaMonthService {

    @Autowired
    WaterBuildingManagerMapper waterBuildingManagerMapper;
    /**
     *  多条件查询指标数据
     * @param mngUnitIds
     * @param buildName
     * @param time
     * @return
     */
    @Override
    public DataTableVO findWdaValue(List<String> mngUnitIds, String buildName, String time,List<Integer> buildingLevels,
                                    Integer pageIndex,Integer pageSize) {
        List<WrDwaMonthVO> wrPlanAdjustVOList = new ArrayList<>();

        QueryWrapper<WrDwaMonth> wrapper = new QueryWrapper<>();
        //获取管理站下的引水口
        if (CollectionUtils.isNotEmpty(mngUnitIds)){
            List<String> buildingIds = getBuildingIdByMngUnitId(mngUnitIds,buildingLevels);
            wrapper.in("BUILDING_ID",buildingIds);
        }
        if (StringUtils.isNotEmpty(buildName)){
            wrapper.like("BUILDING_NAME",buildName);
        }
        if (StringUtils.isNotEmpty(time)){
            List<String> times = Arrays.asList(time.split("-"));
            if (times.size()==2){
                wrapper.eq("YEAR",times.get(0));
                wrapper.eq("MONTH",times.get(1));
            }else{
                throw new TransactionException(CodeEnum.NO_PARAM,"参数为格式有误");
            }
        }
        IPage<WrDwaMonth> page = new Page<>(pageIndex, pageSize);
        IPage<WrDwaMonth> selectPage = baseMapper.selectPage(page,wrapper);
        List<WrDwaMonth> wrDwaMonthList = selectPage.getRecords();
        for (WrDwaMonth wrDwaMonth:wrDwaMonthList){
            WrDwaMonthVO wrDwaMonthVO = new WrDwaMonthVO();
            BeanUtils.copyProperties(wrDwaMonth, wrDwaMonthVO);
            wrPlanAdjustVOList.add(wrDwaMonthVO);
        }
        //result
        DataTableVO dataTableVO = new DataTableVO();
        dataTableVO.setRecordsTotal(page.getTotal());
        dataTableVO.setRecordsFiltered(page.getTotal());
        dataTableVO.setData(wrPlanAdjustVOList);
        return dataTableVO;
    }
    /**
     * 通过管理站id查询引水口id
     * @param mngUnitIds
     * @return
     */
    public List<String> getBuildingIdByMngUnitId(List<String> mngUnitIds, List<Integer> buildingLevels) {
        List<String> buildingIds = new ArrayList<>();

        QueryWrapper<WaterBuildingManager> wrapper = new QueryWrapper<>();
        wrapper.in("wd.MNG_UNIT_ID", mngUnitIds);
        wrapper.in("wd.BUILDING_LEVEL", buildingLevels);
        List<WrBuildingAndDiversion> wrBuildingAndDiversionList = waterBuildingManagerMapper.getBuildingAndDiversionList(wrapper);
        wrBuildingAndDiversionList.forEach(wrBuildingAndDiversion -> {
            buildingIds.add(wrBuildingAndDiversion.getId());
        });
        return buildingIds;
    }
}
