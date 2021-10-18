package com.nari.slsd.msrv.waterdiversion.services;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.cache.interfaces.IModelCacheService;
import com.nari.slsd.msrv.waterdiversion.commons.RedisOperationTypeEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrRightTradeService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrUseUnitManagerService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrRightTradeMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrSuperYearRecordMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrRightTradeDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrRightTrade;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrSuperYearRecord;
import com.nari.slsd.msrv.waterdiversion.model.vo.SimpleWrSuperYearRecordVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrRightTradeVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitManagerVO;
import com.nari.slsd.msrv.waterdiversion.utils.UniqueCodeGenerateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.nari.slsd.msrv.waterdiversion.utils.CommonUtil.convert2EntityList;
import static com.nari.slsd.msrv.waterdiversion.utils.CommonUtil.number;

/**
 * @author Created by ZHD
 * @program: WrRightTradeServiceImpl
 * @description:
 * @date: 2021/8/17 10:35
 */
@Service
public class WrRightTradeServiceImpl  extends ServiceImpl<WrRightTradeMapper, WrRightTrade> implements IWrRightTradeService {
    @Autowired
    IModelCacheService iModelCacheService;

    @Autowired
    IWrUseUnitManagerService iWrUseUnitManagerService;

    @Autowired
    private UniqueCodeGenerateUtil uniqueCodeGenerateUtil;

    @Autowired
    private WrSuperYearRecordMapper wrSuperYearRecordMapper;

    @Override
    public DataTableVO getWrRightTrade(Integer pageIndex, Integer pageSize, String year) {
        QueryWrapper<WrRightTrade> wrapper = new QueryWrapper<>();

        if (StringUtils.isNotEmpty(year)){
            wrapper.eq("YEAR",year);
        }
        wrapper.in("STATUS",new Integer[]{1,2});
        IPage<WrRightTrade> page = new Page<>(pageIndex, pageSize);
        page(page, wrapper);
        List<WrRightTrade> wrRightTrades = page.getRecords();

        DataTableVO dataTableVO = new DataTableVO();
        dataTableVO.setRecordsTotal(page.getTotal());
        dataTableVO.setRecordsFiltered(page.getTotal());
        dataTableVO.setData(listConvert2DTOList(wrRightTrades));
        return dataTableVO;
    }

    @Override
    public Boolean save(WrRightTradeDTO dto) {
        try {
            dto.setId(IDGenerator.getId());
            //编号
            String key = RedisOperationTypeEnum.RIGHT_TRADE + DateUtil.today();
            String uniqueCode = uniqueCodeGenerateUtil.generateUniqueCode(key, RedisOperationTypeEnum.RIGHT_TRADE, true, 8);
            dto.setUniqueCode(uniqueCode);
            return saveOrUpdate (dto);
        } catch (Exception e) {
            throw new TransactionException (CodeEnum.ERROR,"修改失败");
        }
    }

    @Override
    public Boolean update(WrRightTradeDTO dto) {
        try {
            return saveOrUpdate (dto);
        } catch (Exception e) {
            throw new TransactionException (CodeEnum.ERROR,"修改失败");
        }
    }

    @Override
    public void delete(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new TransactionException (CodeEnum.ERROR,"ID为null");
        }
        try {
            WrRightTrade wrRightTrade = new WrRightTrade();
            wrRightTrade.setStatus(0);
            wrRightTrade.setId(id);
            updateById(wrRightTrade);
        } catch (Exception e) {
            throw new TransactionException (CodeEnum.ERROR,"删除失败");
        }
    }

    protected WrRightTradeDTO convert2DTO(WrRightTrade wrRightTrade, Map<String, WrUseUnitManagerVO> managerVOMap) {
        WrRightTradeDTO wrRightTradeDTO = new WrRightTradeDTO();
        BeanUtils.copyProperties(wrRightTrade,wrRightTradeDTO);
        if (StringUtils.isNotEmpty(wrRightTrade.getTradeTime())){
            wrRightTradeDTO.setTradeTime(DateUtils.dateToLong(wrRightTrade.getTradeTime()));
        }
        if (StringUtils.isNotEmpty(wrRightTrade.getRecorderTime())){
            wrRightTradeDTO.setRecorderTime(DateUtils.dateToLong(wrRightTrade.getRecorderTime()));
        }
        if (StringUtils.isNotEmpty(managerVOMap.get(wrRightTradeDTO.getBuyer()))){
            wrRightTradeDTO.setBuyerName(managerVOMap.get(wrRightTradeDTO.getBuyer()).getUnitName());
        }
        if (StringUtils.isNotEmpty(managerVOMap.get( wrRightTradeDTO.getSaler()))){
            wrRightTradeDTO.setSalerName(managerVOMap.get( wrRightTradeDTO.getSaler()).getUnitName());
        }
        String recorderName = iModelCacheService.getUserName(wrRightTrade.getBuyer());//录入人名称
        wrRightTradeDTO.setRecorderName(recorderName);
        return wrRightTradeDTO;
    }
    protected List<WrRightTradeDTO> listConvert2DTOList(List<WrRightTrade> wrRightTrades) {
        Set<String> unitIds = new HashSet<>();
        wrRightTrades.stream().forEach((wrRightTrade) -> {
            unitIds.add( wrRightTrade.getBuyer());
            unitIds.add(wrRightTrade.getSaler());
        });
        List<WrUseUnitManagerVO> wrUseUnitManagerVOS = iWrUseUnitManagerService.getWaterUseUnitList(new ArrayList<String>(unitIds));
        Map<String, WrUseUnitManagerVO> managerVOMap = wrUseUnitManagerVOS.stream().collect(Collectors.toMap(WrUseUnitManagerVO::getId, a -> a,(k1,k2)->k1));
        List<WrRightTradeDTO> wrRightTradeDTOS = new ArrayList<>();
        if(CollectionUtils.isEmpty(wrRightTrades)) {
            return wrRightTradeDTOS;
        } else {
            wrRightTrades.stream().filter((data) -> data != null).forEach((data) -> {
                wrRightTradeDTOS.add(this.convert2DTO(data,managerVOMap));
            });
            return wrRightTradeDTOS;
        }

    }

    protected  WrRightTrade convert2DO(WrRightTradeDTO  wrRightTradeDTO) {
        WrRightTrade wrRightTrade = new WrRightTrade();
        BeanUtils.copyProperties(wrRightTradeDTO,wrRightTrade);
        if (StringUtils.isNotEmpty(wrRightTradeDTO.getTradeTime())){
            wrRightTrade.setTradeTime(DateUtils.convertTimeToDate((wrRightTradeDTO.getTradeTime())));
        }
        if (StringUtils.isNotEmpty(wrRightTradeDTO.getRecorderTime())){
            wrRightTrade.setRecorderTime(DateUtils.convertTimeToDate((wrRightTradeDTO.getRecorderTime())));
        }
        return wrRightTrade;
    }

    private Boolean saveOrUpdate(WrRightTradeDTO dto) throws TransactionException {
        WrRightTrade wrRightTrade = convert2DO (dto);
        wrRightTrade.setRecorderTime(DateUtils.convertTimeToDate(System.currentTimeMillis()));
        try {
            return saveOrUpdate(wrRightTrade);
        } catch (Exception e) {
            throw new TransactionException (CodeEnum.ERROR,"失败");
        }
    }

    /**
     * 查询所有待填报的水权交易信息
     * @return
     */
    @Override
    public List<WrRightTradeVO> getAllRightTradeInYear(){
        LambdaQueryWrapper<WrRightTrade> wrapper = new QueryWrapper().lambda();
        //获取当年的水权交易信息
        wrapper.eq(WrRightTrade::getYear,String.valueOf(LocalDate.now().getYear()));
        //0-无效 1-有效 2-已填报
        wrapper.eq(WrRightTrade::getStatus,1);
        List<WrRightTrade> wrRightTradeList = this.baseMapper.selectList(wrapper);
        if(CollectionUtils.isEmpty(wrRightTradeList)){
            throw new TransactionException (CodeEnum.NO_DATA,"未查询到任何水权交易信息");
        }
        Map<String, WrRightTrade> tradeMap = wrRightTradeList.stream().collect(Collectors.toMap(WrRightTrade::getUniqueCode, e -> e));
        List<String> tradeCodeList = wrRightTradeList.stream().map(WrRightTrade::getUniqueCode).collect(Collectors.toList());
        List<SimpleWrSuperYearRecordVO> sumTradeWater = getSumTradeWater(tradeCodeList);
        if(CollectionUtils.isNotEmpty(sumTradeWater)){
            for (SimpleWrSuperYearRecordVO vo : sumTradeWater) {
                WrRightTrade wrRightTrade = tradeMap.get(vo.getWaterRegimeCode());
                if(null != wrRightTrade){
                    BigDecimal sub = NumberUtil.sub(number(wrRightTrade.getWaterAmount()), vo.getTotalWater());
                    if(sub.doubleValue() <= 0){
                        tradeMap.remove(vo.getWaterRegimeCode());
                    }else{
                        wrRightTrade.setWaterAmount(sub.doubleValue());
                    }
                }
            }
        }
        return convert2EntityList(new ArrayList<>(tradeMap.values()), WrRightTradeVO.class, null);
    }

    private List<SimpleWrSuperYearRecordVO> getSumTradeWater(List<String> tradeCodeList) {
        LambdaQueryWrapper<WrSuperYearRecord> wrapper = new QueryWrapper().lambda();
        wrapper.in(WrSuperYearRecord::getWaterRegimeCode,tradeCodeList);
        wrapper.groupBy(WrSuperYearRecord::getWaterRegimeCode);
        return wrSuperYearRecordMapper.getSumTradeWater(wrapper);
    }
}
