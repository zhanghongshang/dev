package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrFeeRateService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrFeeRateMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrFeeRateDto;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrFeeRate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.nari.slsd.msrv.waterdiversion.utils.CommonUtil.convert2EntityList;


/**
 * @title
 * @description 水费费率服务类
 * @author bigb
 * @updateTime 2021/8/21 11:13
 * @throws
 */
@Slf4j
@Service
public class WrFeeRateServiceImpl extends ServiceImpl<WrFeeRateMapper, WrFeeRate> implements IWrFeeRateService {
    @Autowired
    private WrFeeRateMapper wrFeeRateMapper;

    /**
     * @title addWrFeeRate
     * @description 水费费率新增
     * @author bigb
     * @param: wrFeeRateDto
     * @updateTime 2021/8/22 10:19
     * @return: int
     * @throws
     */
    @Override
    public int addWrFeeRate(WrFeeRateDto wrFeeRateDto){
        if(null == wrFeeRateDto){
            throw new TransactionException(CodeEnum.NO_PARAM,"未传入相关费率信息！");
        }
        WrFeeRate wrFeeRate = new WrFeeRate();
        wrFeeRate.setId(IDGenerator.getId());
        BeanUtils.copyProperties(wrFeeRateDto,wrFeeRate);
        return wrFeeRateMapper.insert(wrFeeRate);
    }

    /**
     * @title batchUpdateWrFee
     * @description 批量操作费率信息
     * @author bigb
     * @param: categoryId
     * @param: wrFeeRateDtoList
     * @updateTime 2021/8/23 13:26
     * @return: boolean
     * @throws
     */
    @Override
    public boolean batchUpdateWrFee(String categoryId , List<WrFeeRateDto> wrFeeRateDtoList){
        if(StringUtils.isEmpty(categoryId) || CollectionUtils.isEmpty(wrFeeRateDtoList)){
            throw new TransactionException(CodeEnum.NO_PARAM,"未传入相关费率信息！");
        }
        QueryWrapper<WrFeeRate> wrapper = new QueryWrapper<>();
        wrapper.eq("CATEGORY_ID",categoryId);
        //数据库中已存在的费率
        List<WrFeeRate> existRateList = wrFeeRateMapper.selectList(wrapper);
        //前台传入
        List<WrFeeRate> paramList = convert2EntityList(wrFeeRateDtoList, WrFeeRate.class, null);
        List<WrFeeRate> allUpdateList = new ArrayList<>();
        //交集为更新
        List<WrFeeRate> updateList = (List<WrFeeRate>) CollectionUtils.intersection(existRateList, paramList);
        if(CollectionUtils.isNotEmpty(updateList)){
            allUpdateList.addAll(updateList);
        }
        //existRateList-paramList为删除部分
        List<WrFeeRate> deleteList = (List<WrFeeRate>) CollectionUtils.subtract(existRateList, paramList);
        if(CollectionUtils.isNotEmpty(deleteList)){
            deleteList.stream().forEach(delete -> {
                delete.setActiveFlag(0);
            });
            allUpdateList.addAll(deleteList);
        }
        //paramList-existRateList为新增部分
        List<WrFeeRate> addList = (List<WrFeeRate>) CollectionUtils.subtract(paramList,existRateList);
        if(CollectionUtils.isNotEmpty(addList)){
            addList.stream().forEach(add -> {
                add.setId(IDGenerator.getId());
            });
            allUpdateList.addAll(addList);
        }
        if(allUpdateList.size() > 0){
            return this.saveOrUpdateBatch(allUpdateList);
        }
        return false;
    }
}
