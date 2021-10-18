package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.nari.slsd.msrv.common.utils.BeanUtils;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterBuildingManagerService;
import com.nari.slsd.msrv.waterdiversion.model.fourth.po.WmProjectLastR;
import com.nari.slsd.msrv.waterdiversion.mapper.fourth.WmProjectLastRMapper;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWmProjectLastRService;
import com.nari.slsd.msrv.waterdiversion.model.vo.BuildingExt;
import com.nari.slsd.msrv.waterdiversion.model.vo.WmProjectLastRVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 测站采集最新水情表 服务实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-30
 */
@Service
public class WmProjectLastRServiceImpl extends MppServiceImpl<WmProjectLastRMapper, WmProjectLastR> implements IWmProjectLastRService {

    @Resource
    IWaterBuildingManagerService waterBuildingManagerService;

    @Override
    public List<BuildingExt> getNewestDataWithBuildingExt(List<String> mngUnitIds, List<Integer> unitLevels, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels) {
        //获取返回结构
        List<BuildingExt> resultList = waterBuildingManagerService.getBuildingExtListByMng(mngUnitIds, unitLevels, buildingTypes, fillReport, buildingLevels);
        //获取测站Code 因为数据表的主键是测站编码
        List<String> stcds = resultList.stream().map(BuildingExt::getBuildingCode).distinct().collect(Collectors.toList());
        if (stcds.size() == 0) {
            return resultList;
        }
        List<WmProjectLastR> dataList = getNewestData(stcds);
        //转型
        List<WmProjectLastRVO> dataVOList = convert2VOList(dataList);
        //没必要设置测站名  外层已有测站名
        Map<String, WmProjectLastRVO> voMap = dataVOList.stream().collect(Collectors.toMap(WmProjectLastRVO::getStcd, vo -> vo, (o1, o2) -> o1));
        resultList.forEach(result -> {
            if (voMap.containsKey(result.getBuildingCode())) {
                result.setData(voMap.get(result.getBuildingCode()));
            }
        });
        return resultList;
    }


    @Override
    public List<WmProjectLastR> getNewestData(List<String> stcds) {
        //TODO 考虑SQL效率 可能修改
        QueryWrapper<WmProjectLastR> wrapper = new QueryWrapper<>();
        wrapper.in(stcds != null && stcds.size() != 0, "A.STCD", stcds);
        return baseMapper.getNewestWmProjectLastRListGroupByStcd(wrapper);
    }


    protected WmProjectLastRVO convert2VO(WmProjectLastR po) {
        WmProjectLastRVO vo = new WmProjectLastRVO();
        BeanUtils.copyProperties(po, vo);
        if (po.getTm() != null) {
            vo.setTm(DateUtils.convertDateToLong(po.getTm()));
        }
        return vo;
    }

    protected List<WmProjectLastRVO> convert2VOList(List<WmProjectLastR> poList) {
        List<WmProjectLastRVO> voList = new ArrayList<>();
        poList.forEach(po -> voList.add(convert2VO(po)));
        return voList;
    }

}
