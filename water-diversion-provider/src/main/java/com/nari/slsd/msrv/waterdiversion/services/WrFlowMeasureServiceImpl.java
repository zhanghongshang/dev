package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.utils.BeanUtils;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.cache.interfaces.IModelCacheService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterBuildingManagerService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrFlowMeasureDTO;
import com.nari.slsd.msrv.waterdiversion.model.secondary.po.WrFlowMeasure;
import com.nari.slsd.msrv.waterdiversion.mapper.secondary.WrFlowMeasureMapper;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrFlowMeasureService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrFlowMeasureVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 断面实测数据表 服务实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-05
 */
@Service
public class WrFlowMeasureServiceImpl extends ServiceImpl<WrFlowMeasureMapper, WrFlowMeasure> implements IWrFlowMeasureService {


    @Resource
    TransactionTemplate transactionTemplate;

    @Resource
    IWaterBuildingManagerService waterBuildingManagerService;

    @Resource
    IModelCacheService cacheService;

    @Override
    public void saveBatch(List<WrFlowMeasureDTO> dtoList) {
        List<WrFlowMeasure> poList = convert2DOList(dtoList);
        transactionTemplate.executeWithoutResult(transactionStatus -> saveBatch(poList));
    }

    @Override
    public void update(WrFlowMeasureDTO dto) {
        WrFlowMeasure po = convert2DO(dto);
        transactionTemplate.executeWithoutResult(transactionStatus -> updateById(po));
    }

    @Override
    public void delete(String id) {
        transactionTemplate.executeWithoutResult(transactionStatus -> removeById(id));
    }

    @Override
    public DataTableVO getDataPage(Integer pageIndex, Integer pageSize, String stationId) {

        IPage<WrFlowMeasure> page = lambdaQuery()
                .eq(StringUtils.isNotEmpty(stationId), WrFlowMeasure::getStationId, stationId)
                .orderByDesc(WrFlowMeasure::getTime)
                .page(new Page<>(pageIndex, pageSize));

        List<WrFlowMeasure> poList = page.getRecords();

        List<WrFlowMeasureVO> voList = convert2VOList(poList);
        //整合测站名、操作人、编码
        List<String> stationIds = voList.stream().filter(vo -> StringUtils.isNotEmpty(vo.getStationId())).map(WrFlowMeasureVO::getStationId).distinct().collect(Collectors.toList());
        if (stationIds.size() != 0) {
            Map<String, WrBuildingAndDiversion> buildingMap = waterBuildingManagerService.getBuildingMapByIds(stationIds);
            voList.forEach(vo -> {
                if (buildingMap.containsKey(vo.getStationId())) {
                    WrBuildingAndDiversion wrBuildingAndDiversion = buildingMap.get(vo.getStationId());
                    vo.setStationName(wrBuildingAndDiversion.getBuildingName());
                    vo.setStationCode(wrBuildingAndDiversion.getBuildingCode());
                }
                if (StringUtils.isNotEmpty(vo.getPersonId())) {
                    vo.setPersonName(cacheService.getUserName(vo.getPersonId()));
                }
            });
        }

        DataTableVO dataTableVO = new DataTableVO();
        dataTableVO.setRecordsFiltered(page.getTotal());
        dataTableVO.setRecordsTotal(page.getTotal());
        dataTableVO.setData(voList);
        return dataTableVO;
    }

    @Override
    public List<WrFlowMeasureVO> getDataList(Long sdt, Long edt, String stationId) {

        List<WrFlowMeasure> poList = lambdaQuery()
                .eq(StringUtils.isNotEmpty(stationId), WrFlowMeasure::getStationId, stationId)
                .between(sdt != null && edt != null, WrFlowMeasure::getTime, DateUtils.convertTimeToDate(sdt), DateUtils.convertTimeToDate(edt))
                .orderByAsc(WrFlowMeasure::getWaterLevel)
                .list();
        List<WrFlowMeasureVO> voList = convert2VOList(poList);
        //整合测站名、操作人、编码
        List<String> stationIds = voList.stream().filter(vo -> StringUtils.isNotEmpty(vo.getStationId())).map(WrFlowMeasureVO::getStationId).distinct().collect(Collectors.toList());
        if (stationIds.size() != 0) {
            Map<String, WrBuildingAndDiversion> buildingMap = waterBuildingManagerService.getBuildingMapByIds(stationIds);
            voList.forEach(vo -> {
                if (buildingMap.containsKey(vo.getStationId())) {
                    WrBuildingAndDiversion wrBuildingAndDiversion = buildingMap.get(vo.getStationId());
                    vo.setStationName(wrBuildingAndDiversion.getBuildingName());
                    vo.setStationCode(wrBuildingAndDiversion.getBuildingCode());
                }
                if (StringUtils.isNotEmpty(vo.getPersonId())) {
                    vo.setPersonName(cacheService.getUserName(vo.getPersonId()));
                }
            });
        }
        return voList;
    }


    protected WrFlowMeasure convert2DO(WrFlowMeasureDTO dto) {
        WrFlowMeasure po = new WrFlowMeasure();
        BeanUtils.copyProperties(dto, po);
        if (StringUtils.isEmpty(dto.getId())) {
            po.setId(IDGenerator.getId());
        }
        if (dto.getTime() != null) {
            po.setTime(DateUtils.convertTimeToDate(dto.getTime()));
        }
        if (dto.getUpdateTime() != null) {
            po.setUpdateTime(DateUtils.convertTimeToDate(dto.getUpdateTime()));
        } else {
            po.setUpdateTime(new Date());
        }
        return po;
    }


    protected List<WrFlowMeasure> convert2DOList(List<WrFlowMeasureDTO> dtoList) {
        List<WrFlowMeasure> poList = new ArrayList<>();
        dtoList.forEach(dto -> {
            WrFlowMeasure po = convert2DO(dto);
            poList.add(po);
        });
        return poList;
    }


    protected WrFlowMeasureVO convert2VO(WrFlowMeasure po) {
        WrFlowMeasureVO vo = new WrFlowMeasureVO();
        BeanUtils.copyProperties(po, vo);

        if (po.getTime() != null) {
            vo.setTime(DateUtils.convertDateToLong(po.getTime()));
        }
        if (po.getUpdateTime() != null) {
            vo.setUpdateTime(DateUtils.convertDateToLong(po.getUpdateTime()));
        }
        return vo;
    }

    protected List<WrFlowMeasureVO> convert2VOList(List<WrFlowMeasure> poList) {
        List<WrFlowMeasureVO> voList = new ArrayList<>();
        poList.forEach(po -> {
            WrFlowMeasureVO vo = convert2VO(po);
            voList.add(vo);
        });
        return voList;
    }
}
