package com.nari.slsd.msrv.waterdiversion.services;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.hu.mplat.imc.client.Param.Param;
import com.nari.slsd.hu.mplat.imc.client.dto.DataRetoreRequest;
import com.nari.slsd.hu.mplat.imc.client.dto.UpdateData;
import com.nari.slsd.hu.mplat.imc.client.service.ImcDataProxy;
import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.common.utils.BeanUtils;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.cache.interfaces.IModelCacheService;
import com.nari.slsd.msrv.waterdiversion.commons.PointTypeEnum;
import com.nari.slsd.msrv.waterdiversion.commons.WrBuildingEnum;
import com.nari.slsd.msrv.waterdiversion.commons.WrDayInputEnum;
import com.nari.slsd.msrv.waterdiversion.config.excel.WrDayInputInMonthModel;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterBuildingManagerService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterPointService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDayInMonthInputService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDayInputService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrDayInmonthInputMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrDiversionPortMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.WaterPointDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDayInmonthInput;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDayInput;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDiversionPort;
import com.nari.slsd.msrv.waterdiversion.model.vo.MngUnitAndBuilding;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrDayInmonthInputTable;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrDayInmonthInputVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrFlowDayInmonthRow;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.nari.slsd.msrv.waterdiversion.commons.WrBuildingEnum.BUILDING_LEVEL_1_2;
import static com.nari.slsd.msrv.waterdiversion.utils.CommonUtil.getDayNumOfCurrentMonth;

/**
 * <p>
 * 日水情录入 服务实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-19
 */
@Service
@Slf4j
public class WrDayInMonthInputServiceImpl extends ServiceImpl<WrDayInmonthInputMapper, WrDayInmonthInput> implements IWrDayInMonthInputService {
    private static final String MONTH = "month";

    private static Map<Integer, Field> MONTH_FIELD_MAP = new HashMap<>();

    @Resource
    TransactionTemplate transactionTemplate;

    @Resource
    IWaterBuildingManagerService waterBuildingManagerService;

    @Resource
    IModelCacheService cacheService;

    @Resource
    IWaterPointService waterPointService;

    @Autowired
    private IWrDayInputService wrDayInputService;

    @Resource
    ImcDataProxy imcDataProxy;

    @Autowired
    private WrDiversionPortMapper wrDiversionPortMapper;

    static {
        ReflectionUtils.doWithLocalFields(WrDayInputInMonthModel.class, field -> {
            String fieldName = field.getName();
            if (fieldName.startsWith(MONTH)) {
                ReflectionUtils.makeAccessible(field);
                MONTH_FIELD_MAP.put(Integer.parseInt(fieldName.substring(MONTH.length())), field);
            }
        });
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void updateBatch(WrFlowDayInmonthRow rows) {
        List<WrDayInmonthInput> poList = convert2DOList(rows);
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            updateBatchById(poList);
            List<String> ids = poList.stream().map(WrDayInmonthInput::getId).collect(Collectors.toList());
            List<WrDayInmonthInput> dataList = listByIds(ids);
            //逐日水情数据审核后 入WDS
            List<String> buildingIds = dataList.stream().map(WrDayInmonthInput::getStationId).distinct().collect(Collectors.toList());
            if (buildingIds.size() != 0) {
                List<WaterPointDTO> waterPointDTOList = new ArrayList<>();
                try {
                    //根据引水口ID 测项类型查测点
                    waterPointDTOList = waterPointService.getWaterPointId(buildingIds, Arrays.asList(PointTypeEnum.FLOW.getId()));
                } catch (RuntimeException e) {
                    log.error("调用测项信息失败");
                    e.printStackTrace();
                }
                //按测站ID+测项类型分组
                Map<String, WaterPointDTO> pointMap = waterPointDTOList.stream().collect(Collectors.toMap(o -> o.getBuildingId() + ":" + o.getPointType(), o -> o, (o1, o2) -> o1));
                List<DataRetoreRequest> requestList = new ArrayList<>();
                dataList.forEach(data -> {
                    String flowKey = data.getStationId() + ":" + PointTypeEnum.FLOW.getId();
                    if (pointMap.containsKey(flowKey)) {
                        WaterPointDTO pointDTO = pointMap.get(flowKey);
                        DataRetoreRequest dataRetoreRequest = new DataRetoreRequest();
                        dataRetoreRequest.setAppType(Param.AppType.APP_Type_WDS);
                        dataRetoreRequest.setTimeType(Param.RunDataType.RUN_DAY);
                        dataRetoreRequest.setValueType(Param.ValType.Special_V);
                        if (StringUtils.isNotEmpty(pointDTO.getCorrelationCode())) {
                            dataRetoreRequest.setSenid(Long.valueOf(pointDTO.getCorrelationCode()));
                        }
                        //数据
                        UpdateData updateData = new UpdateData();
                        updateData.setNewValue(data.getWaterFlow());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(data.getTime());
                        updateData.setTime(calendar);
                        dataRetoreRequest.setDatalist(Collections.singletonList(updateData));
                        requestList.add(dataRetoreRequest);
                    }
                });
                try {
                    imcDataProxy.savePointData(requestList);
                } catch (RuntimeException e) {
                    log.error(e.getMessage());
                }
            }
        });

    }

    private List<WrDayInmonthInput> convert2DOList(WrFlowDayInmonthRow row) {
        List<WrDayInmonthInput> poList = new ArrayList<>();
        List<WrDayInmonthInputVO> voList = row.getFlow();
        voList.forEach(vo -> {
            WrDayInmonthInput po = convert2DO(vo);
            po.setApproveId(row.getApproveId());
            po.setApproveTime(DateUtils.getDateTime());
            po.setStatus(WrDayInputEnum.DAY_INPUT_STATUS_CHECKED);
            poList.add(po);
        });
        return poList;
    }


    @Override
    public WrDayInmonthInputTable getDayInmonthInputTable(List<String> mngUnitIds, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels, Long time, Integer status) {
        WrDayInmonthInputTable table = new WrDayInmonthInputTable();
        //月初起始时间
        Date sDate = DateUtils.convertTimeToDate(time);
        //月末结束时间
        Date eDate = DateUtils.convertTimeToDate(getMonthDate(DateUtils.convertTimeToDate(time), 1).getTime() - 1);
        //本月应有记录数(天数)
        int days = DateUtils.getLastDayOfMonth(sDate);

        //返回结构体
        List<MngUnitAndBuilding> resultList = waterBuildingManagerService.getMngAndBuildingsByMng(mngUnitIds, buildingTypes, fillReport, buildingLevels);
        //管理单位下无引水口，直接返回
        if (resultList == null || resultList.size() == 0) {
            return table;
        }

        //引水口ID集合
        List<String> stationIds = resultList.stream().map(MngUnitAndBuilding::getBuildingId).distinct().collect(Collectors.toList());
        //引水口下本月所有逐日水情数据，按时间正序
        List<WrDayInmonthInput> wrDayInmonthInputList = lambdaQuery().in(WrDayInmonthInput::getStationId, stationIds).between(WrDayInmonthInput::getTime, sDate, eDate).orderByAsc(WrDayInmonthInput::getTime).list();
        //按引水口+日过滤 保证每天拿一条数据
        wrDayInmonthInputList = wrDayInmonthInputList.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> getDayOfMonth(o.getTime()) + ";" + o.getStationId()))), ArrayList::new));

        wrDayInmonthInputList.sort(Comparator.comparing(WrDayInmonthInput::getTime));

        //按测站分组
        Map<String, List<WrDayInmonthInput>> groupMap = wrDayInmonthInputList.stream().collect(Collectors.groupingBy(WrDayInmonthInput::getStationId));

        //先存入数据后按状态过滤 为了能拿到所有状态计数
        resultList.forEach(result -> {
            WrFlowDayInmonthRow row = new WrFlowDayInmonthRow();
            if (groupMap.containsKey(result.getBuildingId())) {
                List<WrDayInmonthInput> stationData = groupMap.get(result.getBuildingId());
                row = convert2WrFlowDayInmonthRow(stationData, days);
            } else {
                row = fillEmptyMonthData(time, result.getBuildingId());
            }
            result.setData(row);
        });

        int lackedCount = Math.toIntExact(resultList.stream().map(result -> (WrFlowDayInmonthRow) result.getData()).filter(row -> WrDayInputEnum.DAY_INPUT_STATUS_LACKED.equals(row.getStatus())).count());
        int uncheckedCount = Math.toIntExact(resultList.stream().map(result -> (WrFlowDayInmonthRow) result.getData()).filter(row -> WrDayInputEnum.DAY_INPUT_STATUS_UNCHECKED.equals(row.getStatus())).count());
        int checkedCount = Math.toIntExact(resultList.stream().map(result -> (WrFlowDayInmonthRow) result.getData()).filter(row -> WrDayInputEnum.DAY_INPUT_STATUS_CHECKED.equals(row.getStatus())).count());


        Iterator<MngUnitAndBuilding> it = resultList.iterator();
        while (it.hasNext()) {
            MngUnitAndBuilding result = it.next();
            WrFlowDayInmonthRow row = (WrFlowDayInmonthRow) result.getData();
            //未校核时包括两种
            if (WrDayInputEnum.DAY_INPUT_STATUS_UNCHECKED.equals(status)) {
                if (!WrDayInputEnum.DAY_INPUT_STATUS_UNCHECKED.equals(row.getStatus()) && !WrDayInputEnum.DAY_INPUT_STATUS_LACKED.equals(row.getStatus())) {
                    it.remove();
                }
            } else if (status != null) {
                if (!status.equals(row.getStatus())) {
                    it.remove();
                }
            }
        }

        table.setTotalCount(stationIds.size());
        table.setUncheckedCount(lackedCount + uncheckedCount);
        table.setCheckedCount(checkedCount);
        table.setData(resultList);
        return table;
    }

    /**
     * 日水情录入月导入方式
     * @param inputList
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void importInMonthForDayInput(String operator , String year , List<WrDayInputInMonthModel> inputList){
        //引水口id
        setBuildingId(inputList);
        //获取导入的月份
        WrDayInputInMonthModel model = inputList.get(0);
        List<String> importMonthList = new ArrayList<>();
        for(int index = 1;index <=12 ; index++){
            Field monthField = MONTH_FIELD_MAP.get(index);
            if (null != monthField) {
                Object val = ReflectionUtils.getField(monthField, model);
                if (val instanceof Double) {
                    importMonthList.add(String.valueOf(index));
                }
            }
        }
        if(importMonthList.size() == 0){
            throw new TransactionException(CodeEnum.NO_PARAM,"日水情导入模板中不包含任何月数据");
        }
        for (String month : importMonthList) {
            generateAndSaveDayInput(operator, year, inputList, month);
        }
    }

    private void generateAndSaveDayInput(String operator, String year, List<WrDayInputInMonthModel> inputList, String month) {
        //日水情录入
        List<WrDayInput> dayInputListInMonth = new ArrayList();
        for (WrDayInputInMonthModel localModel : inputList) {
            if(StringUtils.isEmpty(localModel.getBuildingId())){
                log.error("当前引水口编码没有配置适合的引水口信息，其编码为：{}",localModel.getBuildingCode());
                continue;
            }
            Field monthField = MONTH_FIELD_MAP.get(Integer.parseInt(month));
            Object val = null;
            if (null != monthField) {
                val = ReflectionUtils.getField(monthField, localModel);
            }
            if(!(val instanceof Double)){
                log.error("当前月份没有填报任何流量，月份为：{}，引水口名称为：{}", month,localModel.getBuildingName());
                continue;
            }
            Double avgWaterFlow = (Double) val;
            int days = getDayNumOfCurrentMonth(Integer.parseInt(year), Integer.parseInt(month));
            for(int i=1;i<=days;i++){
                WrDayInput wrDayInput = new WrDayInput();
                wrDayInput.setId(IDGenerator.getId());
                wrDayInput.setAuto(0);
                wrDayInput.setManualWaterFlow(avgWaterFlow);
                wrDayInput.setStationId(localModel.getBuildingId());
                wrDayInput.setTimeType(4);
                String dayStr = StringUtils.leftPad(String.valueOf(i),2,'0');
                //构建每一天
                String day = StringUtils.join(new String[]{year,StringUtils.leftPad(month,2,'0'),dayStr},'-');
                Date now = DateUtil.date();
                wrDayInput.setTime(DateUtils.convertStringTimeToDateExt(day));
                wrDayInput.setOperateTime(now);
                wrDayInput.setApproveTime(now);
                wrDayInput.setOperatorId(operator);
                wrDayInput.setApproveId(operator);
                wrDayInput.setStatus(1);
                dayInputListInMonth.add(wrDayInput);
            }
        }
        if(dayInputListInMonth.size() > 0){
            wrDayInputService.saveBatch(dayInputListInMonth);
        }
    }

    private void setBuildingId(List<WrDayInputInMonthModel> inputList) {
        Map<String, WrDayInputInMonthModel> inputMap = inputList.stream().collect(Collectors.toMap(WrDayInputInMonthModel::getBuildingCode, e -> e));
        LambdaQueryWrapper<WrDiversionPort> wrapper = new QueryWrapper().lambda();
        wrapper.in(WrDiversionPort::getBuildingCode,inputMap.keySet());
        wrapper.in(WrDiversionPort::getBuildingLevel,Arrays.asList(WrBuildingEnum.BUILDING_LEVEL_2,BUILDING_LEVEL_1_2));
        List<WrDiversionPort> portList = wrDiversionPortMapper.selectList(wrapper);
        if(CollectionUtils.isNotEmpty(portList)){
            Map<String, WrDiversionPort> map = portList.stream()
                    .filter(e -> StringUtils.isNotEmpty(e.getBuildingCode()))
                    .collect(Collectors.toMap(WrDiversionPort::getBuildingCode, e -> e));
            inputMap.entrySet().forEach(entry -> {
                String code = entry.getKey();
                WrDayInputInMonthModel model = entry.getValue();
                WrDiversionPort port = map.get(code);
                if(null != port){
                    model.setBuildingId(port.getId());
                }
            });
        }
    }

    /**
     * 空行填充
     *
     * @param time      当月一号时间戳
     * @param stationId 测站ID
     * @return 填充空值后的测站数据行
     */
    private WrFlowDayInmonthRow fillEmptyMonthData(Long time, String stationId) {
        WrFlowDayInmonthRow row = new WrFlowDayInmonthRow();
        row.setStationId(stationId);
        row.setStatus(WrDayInputEnum.DAY_INPUT_STATUS_LACKED);
        List<WrDayInmonthInputVO> voList = new ArrayList<>();
        //下个月的时间
        long nextMonthDayTime = getMonthDate(DateUtils.convertTimeToDate(time), 1).getTime();
        long dayTime = 24 * 60 * 60 * 1000;
        long currentDayTime = time;
        while (currentDayTime < nextMonthDayTime) {
            WrDayInmonthInputVO vo = new WrDayInmonthInputVO();
            vo.setTime(currentDayTime);
            voList.add(vo);
            currentDayTime += dayTime;
        }
        row.setFlow(voList);
        return row;
    }


    private WrFlowDayInmonthRow convert2WrFlowDayInmonthRow(List<WrDayInmonthInput> dataList, int days) {
        WrFlowDayInmonthRow row = new WrFlowDayInmonthRow();
        //设置初始状态为已审核，如果有数据未审核则改为未审核，如果数据条数不对则改为缺数
        row.setStatus(WrDayInputEnum.DAY_INPUT_STATUS_CHECKED);
        row.setStationId(dataList.get(0).getStationId());

        List<WrDayInmonthInputVO> dayDataList = new ArrayList<>();
        //无数据时 填充空数据
        long firstDayTime = DateUtils.getFirstDayOfMonthToDate(dataList.get(0).getTime()).getTime();
        long lastDayTime = DateUtils.getLastDayOfMonthToDate(dataList.get(0).getTime()).getTime();
        long dayTime = 24 * 60 * 60 * 1000;
        long currentDayTime = firstDayTime;
        int index = 0;
        while (currentDayTime <= lastDayTime) {
            long currentDataTime = dataList.get(index).getTime().getTime();
            if (currentDayTime != currentDataTime) {
                WrDayInmonthInputVO vo = new WrDayInmonthInputVO();
                vo.setTime(currentDayTime);
                dayDataList.add(vo);
                if (!WrDayInputEnum.DAY_INPUT_STATUS_LACKED.equals(row.getStatus())) {
                    row.setStatus(WrDayInputEnum.DAY_INPUT_STATUS_LACKED);
                }
            } else {
                WrDayInmonthInputVO vo = convert2VO(dataList.get(index));
                dayDataList.add(vo);
                if (WrDayInputEnum.DAY_INPUT_STATUS_CHECKED.equals(row.getStatus()) && WrDayInputEnum.DAY_INPUT_STATUS_UNCHECKED.equals(dataList.get(index).getStatus())) {
                    row.setStatus(WrDayInputEnum.DAY_INPUT_STATUS_UNCHECKED);
                }
                if (index < dataList.size() - 1) {
                    index++;
                }
            }
            currentDayTime += dayTime;
        }

        row.setFlow(dayDataList);
        Double avgFlowData = dayDataList.stream().filter(o -> o.getWaterFlow() != null).mapToDouble(WrDayInmonthInputVO::getWaterFlow).average().orElse(0);
        Double monthlyFlowData = avgFlowData * days * 60 * 60 * 24 / 10000.0;
        //保留两位小数 四舍五入
        row.setAvgFlow(new BigDecimal(avgFlowData).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        row.setMonthlyFlow(new BigDecimal(monthlyFlowData).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        //获取最后一次提交的记录
        WrDayInmonthInput lastInput = dataList.stream().filter(o -> o.getApproveTime() != null).max(Comparator.comparing(WrDayInmonthInput::getApproveTime)).orElse(null);
        if (lastInput != null) {
            //设置校核时间
            row.setApproveTime(DateUtils.convertDateToLong(lastInput.getApproveTime()));
            row.setApproveId(lastInput.getApproveId());
            row.setApproveName(cacheService.getUserName(lastInput.getApproveId()));
        }
        if (dataList.size() != days) {
            row.setStatus(WrDayInputEnum.DAY_INPUT_STATUS_LACKED);
        }
        return row;
    }

    /**
     * 获取startDate日期后month月的日期
     *
     * @param startDate 开始日期
     * @param month     几个月后
     * @return Date日期
     */
    public static Date getMonthDate(Date startDate, int month) {
        LocalDateTime localDateTime = startDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime().plusMonths(month);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取当前天
     *
     * @param date 日期
     * @return 该月第几天
     */
    public static int getDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    protected WrDayInmonthInputVO convert2VO(WrDayInmonthInput po) {
        WrDayInmonthInputVO vo = new WrDayInmonthInputVO();
        BeanUtils.copyProperties(po, vo);
        if (po.getTime() != null) {
            vo.setTime(DateUtils.convertDateToLong(po.getTime()));
        }
        return vo;
    }

    protected WrDayInmonthInput convert2DO(WrDayInmonthInputVO vo) {
        WrDayInmonthInput po = new WrDayInmonthInput();
        BeanUtils.copyProperties(vo, po);
        return po;
    }
}


