package com.nari.slsd.msrv.waterdiversion.services;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.commons.RedisOperationTypeEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrChargeService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrChargeMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrChargeDto;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrCharge;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrChargeVO;
import com.nari.slsd.msrv.waterdiversion.utils.UniqueCodeGenerateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nari.slsd.msrv.waterdiversion.processer.ProcessorFactory.CONVERTER_DATETIME_TO_LONG;
import static com.nari.slsd.msrv.waterdiversion.processer.ProcessorFactory.getConverterInstance;
import static com.nari.slsd.msrv.waterdiversion.utils.CommonUtil.convert2EntityList;


/**
 * @title
 * @description 水费收费服务类
 * @author bigb
 * @updateTime 2021/8/21 11:13
 * @throws
 */
@Slf4j
@Service
public class WrChargeServiceImpl extends ServiceImpl<WrChargeMapper, WrCharge> implements IWrChargeService {
    @Autowired
    private WrChargeMapper wrChargeMapper;

    @Autowired
    private UniqueCodeGenerateUtil uniqueCodeGenerateUtil;

    public int saveWrCharge(WrChargeDto dto){
        if(null == dto){
            throw new TransactionException(CodeEnum.NO_PARAM,"未传入相关水费收费信息！");
        }
        WrCharge wrCharge = new WrCharge();
        wrCharge.setId(IDGenerator.getId());
        BeanUtils.copyProperties(dto,wrCharge);
        //编号
        String key = RedisOperationTypeEnum.FEE_RATE + DateUtil.today();
        String uniqueCode = uniqueCodeGenerateUtil.generateUniqueCode(key, RedisOperationTypeEnum.WATER_CHARGE, true, 8);
        wrCharge.setRecordCode(uniqueCode);
        //年份
        wrCharge.setYear(String.valueOf(DateUtil.thisYear()));
        return wrChargeMapper.insert(wrCharge);
    }

    @Override
    public DataTableVO getAllWrCharge(Integer pageIndex, Integer pageSize, WrChargeDto dto) {
        QueryWrapper<WrCharge> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(dto.getYear())){
            wrapper.eq("YEAR",dto.getYear());
        }
        if(StringUtils.isNotEmpty(dto.getWaterUnitId())){
            wrapper.eq("WATER_UNIT_ID",dto.getWaterUnitId());
        }
        IPage<WrCharge> page = new Page<>(pageIndex, pageSize);
        this.page(page, wrapper);

        List<WrCharge> wrChargeList = page.getRecords();
        if(CollectionUtils.isNotEmpty(wrChargeList)){
            DataTableVO dataTableVO = new DataTableVO();
            dataTableVO.setRecordsTotal(page.getTotal());
            dataTableVO.setRecordsFiltered(page.getTotal());
            Map<String,Object> setMap = new HashMap<String, Object>(4){
                {
                    put("createTime",getConverterInstance(CONVERTER_DATETIME_TO_LONG));
                }
            };
            List<WrChargeVO> voList = convert2EntityList(wrChargeList, WrChargeVO.class, setMap);
            dataTableVO.setData(voList);
            return dataTableVO;
        }
        return null;
    }
}
