package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.utils.BeanUtils;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.cache.interfaces.IModelCacheService;
import com.nari.slsd.msrv.waterdiversion.commons.PointTypeEnum;
import com.nari.slsd.msrv.waterdiversion.commons.WrCurveEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.*;
import com.nari.slsd.msrv.waterdiversion.model.dto.*;
import com.nari.slsd.msrv.waterdiversion.mapper.secondary.WrCurveMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.waterdiversion.model.secondary.po.WrCurve;
import com.nari.slsd.msrv.waterdiversion.model.secondary.po.WrCurveOriginal;
import com.nari.slsd.msrv.waterdiversion.model.secondary.po.WrCurvePointValue;
import com.nari.slsd.msrv.waterdiversion.model.secondary.po.WrCurveVariate;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrCurveVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 曲线维护 服务实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-08
 */
@Service
public class WrCurveServiceImpl extends ServiceImpl<WrCurveMapper, WrCurve> implements IWrCurveService {

    @Resource
    TransactionTemplate transactionTemplate;

    @Resource
    IWrCurvePointValueService wrCurvePointValueService;

    @Resource
    IWrCurveVariateService wrCurveVariateService;

    @Resource
    IWaterBuildingManagerService waterBuildingManagerService;

    @Resource
    IWrCurveOriginalService wrCurveOriginalService;

    @Resource
    IWaterPointService waterPointService;

    @Resource
    IModelCacheService cacheService;

    private List<WrCurveOriginal> convert2WrCurveOriginalList(WrCurveTransDTO dto, String curveId) {
        List<WrCurveOriginal> poList = new ArrayList<>();
        if (dto.getOriginalData() != null && dto.getOriginalData().size() != 0) {
            List<WrCurveOriginalDTO> originalDataList = dto.getOriginalData();
            originalDataList.forEach(original -> {
                WrCurveOriginal po = new WrCurveOriginal();
                BeanUtils.copyProperties(original, po);
                if (original.getTime() != null) {
                    po.setTime(DateUtils.convertTimeToDate(original.getTime()));
                }
                po.setCurveDimension(WrCurveEnum.CURVE_DIMENSIONAL_TWO);
                po.setCurveId(curveId);
                po.setId(IDGenerator.getId());
                poList.add(po);
            });
        }
        return poList;
    }


    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void saveCurve(WrCurveTransDTO dto) {
        //数据拆分
        WrCurve wrCurve = convert2WrCurve(dto);
        //测站ID查水位、流量测点
        //根据枚举过滤测点并存变量表
        List<WaterPointDTO> waterPointDTOList = waterPointService.getWaterPointId(Collections.singletonList(dto.getStationId()), Arrays.asList(PointTypeEnum.WATER_LEVEL.getId(), PointTypeEnum.FLOW.getId()));

        List<WrCurveVariate> wrCurveVariateList = new ArrayList<>();

        waterPointDTOList.forEach(point -> {
            //曲线变量
            WrCurveVariate wrCurveVariate = convert2WrCurveVariate(dto, wrCurve.getId());
            wrCurveVariate.setSenId(point.getCorrelationCode());
            wrCurveVariate.setAppType(point.getCorrelationSource());
            wrCurveVariateList.add(wrCurveVariate);
        });

        List<WrCurvePointValue> wrCurvePointValueList = convert2WrCurvePointValueList(dto, wrCurve.getId());
        List<WrCurveOriginal> wrCurveOriginalList = convert2WrCurveOriginalList(dto, wrCurve.getId());

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            save(wrCurve);
            wrCurveVariateService.saveBatch(wrCurveVariateList);
            wrCurvePointValueService.saveBatch(wrCurvePointValueList);
            wrCurveOriginalService.saveBatch(wrCurveOriginalList);
        });
    }

    @Override
    public DataTableVO getCurvePage(Integer pageIndex, Integer pageSize, List<String> stationIds) {
        //根据测站ID查曲线
        IPage<WrCurve> page = new Page<>(pageIndex, pageSize);
        lambdaQuery().in(stationIds != null && stationIds.size() != 0, WrCurve::getStationId, stationIds)
                .orderByDesc(WrCurve::getUpdateTime)
                .page(page);

        List<WrCurve> curveList = page.getRecords();
        List<WrCurveVO> voList = convert2VOList(curveList);
        //整合测站名称、操作人名称、审核人名称、测站编码
        List<String> ids = voList.stream().map(WrCurveVO::getStationId).distinct().collect(Collectors.toList());
        if (ids.size() != 0) {
            Map<String, WrBuildingAndDiversion> buildingMap = waterBuildingManagerService.getBuildingMapByIds(ids);
            voList.forEach(vo -> {
                if (buildingMap.containsKey(vo.getStationId())) {
                    WrBuildingAndDiversion wrBuildingAndDiversion = buildingMap.get(vo.getStationId());
                    vo.setStationName(wrBuildingAndDiversion.getBuildingName());
                    vo.setStationCode(wrBuildingAndDiversion.getBuildingCode());
                }
                if (StringUtils.isNotEmpty(vo.getPersonId())) {
                    String personName = cacheService.getUserName(vo.getPersonId());
                    vo.setPersonName(personName);
                }
                if (StringUtils.isNotEmpty(vo.getApproverId())) {
                    String approverName = cacheService.getUserName(vo.getApproverId());
                    vo.setApproverName(approverName);
                }
            });
        }
        DataTableVO dataTableVO = new DataTableVO();
        dataTableVO.setRecordsTotal(page.getTotal());
        dataTableVO.setRecordsFiltered(page.getTotal());
        dataTableVO.setData(voList);
        return dataTableVO;
    }

    @Override
    public WrCurveTransDTO getCurve(String id) {
        //根据ID查曲线定义、曲线变量（未使用）、曲线点值、原始数据四张表
        WrCurve curve = getById(id);
        //curveVariate暂时用不到
        //List<WrCurveVariate> variateList = wrCurveVariateService.lambdaQuery().eq(WrCurveVariate::getCurveId, id).list();
        List<WrCurvePointValue> pointValueList = wrCurvePointValueService.lambdaQuery().eq(WrCurvePointValue::getCurveId, id).orderByAsc(WrCurvePointValue::getV0).list();
        List<WrCurveOriginal> originalList = wrCurveOriginalService.lambdaQuery().eq(WrCurveOriginal::getCurveId, id).orderByAsc(WrCurveOriginal::getV0).list();
        //组合曲线定义和数据
        WrCurveTransDTO transDTO = convert2WrCurveTransDTO(curve, pointValueList, originalList);

        //整合测站名称、操作人名称、审核人名称
        if (StringUtils.isNotEmpty(transDTO.getStationId())) {
            Map<String, WrBuildingAndDiversion> buildingMap = waterBuildingManagerService.getBuildingMapByIds(Collections.singletonList(transDTO.getStationId()));
            if (buildingMap.containsKey(transDTO.getStationId())) {
                WrBuildingAndDiversion wrBuildingAndDiversion = buildingMap.get(transDTO.getStationId());
                transDTO.setStationName(wrBuildingAndDiversion.getBuildingName());
                transDTO.setStationCode(wrBuildingAndDiversion.getBuildingCode());

            }
        }
        if (StringUtils.isNotEmpty(transDTO.getPersonId())) {
            String personName = cacheService.getUserName(transDTO.getPersonId());
            transDTO.setPersonName(personName);
        }
        if (StringUtils.isNotEmpty(transDTO.getApproverId())) {
            String approverName = cacheService.getUserName(transDTO.getApproverId());
            transDTO.setApproverName(approverName);
        }
        return transDTO;
    }

    @Override
    public WrCurveTransDTO getCurve(String stationId, Long time) {
        //通过测站ID time 确定曲线
        List<WrCurve> wrCurveList = lambdaQuery()
                .eq(WrCurve::getCurveType, WrCurveEnum.CURVE_TYPE_WATERLV_FLOW)
                .eq(StringUtils.isNotEmpty(stationId), WrCurve::getStationId, stationId)
                .le(time != null, WrCurve::getStartTime, DateUtils.convertTimeToDate(time))
                .and(time != null, wrapper ->
                        wrapper.gt(WrCurve::getEndTime, DateUtils.convertTimeToDate(time)).or().isNull(WrCurve::getEndTime))
                .list();
        if (wrCurveList != null && wrCurveList.size() != 0) {
            return getCurve(wrCurveList.get(0).getId());
        } else {
            return new WrCurveTransDTO();
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void updateCurve(WrCurveDTO dto) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            //如果该测站还有其他同一类型的曲线，设置状态为停用
            WrCurve updateCurve = getById(dto.getId());
            List<WrCurve> oldCurveList = lambdaQuery()
                    .eq(WrCurve::getStationId, updateCurve.getStationId())
                    .eq(WrCurve::getCurveType, updateCurve.getCurveType())
                    .eq(WrCurve::getState, WrCurveEnum.CURVE_STATE_ENABLED)
                    .ne(WrCurve::getId, updateCurve.getId())
                    .list();
            oldCurveList.forEach(curve -> {
                curve.setState(WrCurveEnum.CURVE_STATE_DISABLED);
                curve.setEndTime(DateUtils.getDateTime());
                curve.setUpdateTime(DateUtils.getDateTime());
                updateById(curve);
            });
            updateCurve.setState(WrCurveEnum.CURVE_STATE_ENABLED);
            updateCurve.setStartTime(DateUtils.getDateTime());
            updateCurve.setUpdateTime(DateUtils.getDateTime());
            updateCurve.setApproverId(dto.getApproverId());
            if (StringUtils.isNotEmpty(dto.getCurveCode())) {
                updateCurve.setCurveCode(dto.getCurveCode());
            }
            if (StringUtils.isNotEmpty(dto.getCurveName())) {
                updateCurve.setCurveName(dto.getCurveName());
            }
            if (StringUtils.isNotEmpty(dto.getRemark())) {
                updateCurve.setRemark(dto.getRemark());
            }
            updateById(updateCurve);
        });
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void deleteCurve(String id) {
        //从4张表分别删除数据
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            wrCurveOriginalService.lambdaUpdate().eq(WrCurveOriginal::getCurveId, id).remove();
            wrCurvePointValueService.lambdaUpdate().eq(WrCurvePointValue::getCurveId, id).remove();
            wrCurveVariateService.lambdaUpdate().eq(WrCurveVariate::getCurveId, id).remove();
            removeById(id);
        });
    }

    @Override
    public Boolean checkUniqueCode(String code) {
        int count = lambdaQuery().eq(StringUtils.isNotEmpty(code), WrCurve::getCurveCode, code).count();
        return count == 0;
    }


    private WrCurveTransDTO convert2WrCurveTransDTO(WrCurve curve, List<WrCurvePointValue> pointValueList, List<WrCurveOriginal> originalList) {
        WrCurveTransDTO transDTO = new WrCurveTransDTO();
        if (curve == null) {
            return transDTO;
        }
        BeanUtils.copyProperties(curve, transDTO);
        if (curve.getStartTime() != null) {
            transDTO.setStartTime(DateUtils.convertDateToLong(curve.getStartTime()));
        }
        if (curve.getUpdateTime() != null) {
            transDTO.setUpdateTime(DateUtils.convertDateToLong(curve.getUpdateTime()));
        }
        if (curve.getEndTime() != null) {
            transDTO.setEndTime(DateUtils.convertDateToLong(curve.getEndTime()));
        }
        //这里不用整合 curveVariate
        int length = pointValueList.size();
        Double[][] data = new Double[length][];

        for (int i = 0; i < length; i++) {
            WrCurvePointValue point = pointValueList.get(i);
            data[i] = new Double[]{point.getV0(), point.getV1()};
        }
        transDTO.setData(data);

        List<WrCurveOriginalDTO> wrCurveOriginalDTOList = new ArrayList<>();
        originalList.forEach(original -> {
            WrCurveOriginalDTO dto = new WrCurveOriginalDTO();
            BeanUtils.copyProperties(original, dto);
            if (original.getTime() != null) {
                dto.setTime(DateUtils.convertDateToLong(original.getTime()));
            }
            wrCurveOriginalDTOList.add(dto);
        });
        transDTO.setOriginalData(wrCurveOriginalDTOList);

        return transDTO;
    }


    private WrCurve convert2WrCurve(WrCurveTransDTO dto) {
        WrCurve po = new WrCurve();
        BeanUtils.copyProperties(dto, po);
        if (StringUtils.isEmpty(dto.getId())) {
            po.setId(IDGenerator.getId());
            //设置状态为未审核
            po.setState(WrCurveEnum.CURVE_STATE_UNREVIEWED);
        }
        //设置曲线类型 水位流量曲线
//        if (dto.getCurveType() == null) {
        po.setCurveType(WrCurveEnum.CURVE_TYPE_WATERLV_FLOW);
//        }
        if (dto.getDimensionality() == null) {
            po.setDimensionality(WrCurveEnum.CURVE_DIMENSIONAL_TWO.toString());
        }
        if (StringUtils.isEmpty(dto.getDimExplain())) {
            po.setDimExplain("V0:V1:");
        }
        if (dto.getUpdateTime() != null) {
            po.setUpdateTime(DateUtils.convertTimeToDate(dto.getUpdateTime()));
        } else {
            po.setUpdateTime(new Date());
        }
        if (dto.getStartTime() != null) {
            po.setStartTime(DateUtils.convertTimeToDate(dto.getStartTime()));
        }
        if (dto.getEndTime() != null) {
            po.setEndTime(DateUtils.convertTimeToDate(dto.getEndTime()));
        }
        return po;
    }

    private WrCurveVariate convert2WrCurveVariate(WrCurveTransDTO dto, String curveId) {
        WrCurveVariate po = new WrCurveVariate();
        BeanUtils.copyProperties(dto, po);
        po.setId(IDGenerator.getId());
        po.setCurveId(curveId);
        if (dto.getDimensionality() != null) {
            po.setDimKey(Integer.valueOf(dto.getDimensionality()));
        }
        return po;
    }

    private List<WrCurvePointValue> convert2WrCurvePointValueList(WrCurveTransDTO dto, String curveId) {
        Double[][] data = dto.getData();
        return Arrays.stream(data).map(arr -> {
            WrCurvePointValue val = new WrCurvePointValue();
            val.setId(IDGenerator.getId());
            val.setCurveId(curveId);
            val.setV0(arr[0]);
            val.setV1(arr[1]);
            return val;
        }).collect(Collectors.toList());
    }

    protected WrCurveVO convert2VO(WrCurve po) {
        WrCurveVO vo = new WrCurveVO();
        BeanUtils.copyProperties(po, vo);
        if (po.getStartTime() != null) {
            vo.setStartTime(DateUtils.convertDateToLong(po.getStartTime()));
        }
        if (po.getEndTime() != null) {
            vo.setEndTime(DateUtils.convertDateToLong(po.getEndTime()));
        }
        if (po.getUpdateTime() != null) {
            vo.setUpdateTime(DateUtils.convertDateToLong(po.getUpdateTime()));
        }
        return vo;
    }

    protected List<WrCurveVO> convert2VOList(List<WrCurve> poList) {
        List<WrCurveVO> voList = new ArrayList<>();
        if (poList == null || poList.size() == 0) {
            return voList;
        }
        poList.forEach(po -> {
            WrCurveVO vo = convert2VO(po);
            voList.add(vo);
        });
        return voList;
    }
}
