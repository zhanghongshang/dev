package com.nari.slsd.msrv.waterdiversion.services;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.waterdiversion.commons.RedisOperationTypeEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrCategoryService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrCategoryMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrFeeRateMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrCategoryDto;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrCategory;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrFeeRate;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrCategoryTempVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrCategoryVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrFeeRateVO;
import com.nari.slsd.msrv.waterdiversion.utils.UniqueCodeGenerateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nari.slsd.msrv.waterdiversion.processer.ProcessorFactory.CONVERTER_DATETIME_TO_LONG;
import static com.nari.slsd.msrv.waterdiversion.processer.ProcessorFactory.getConverterInstance;
import static com.nari.slsd.msrv.waterdiversion.utils.CommonUtil.convert2Entity;


/**
 * @title
 * @description 用水性质管理服务类
 * @author bigb
 * @updateTime 2021/8/23 11:13
 * @throws
 */
@Slf4j
@Service
public class WrCategoryServiceImpl extends ServiceImpl<WrCategoryMapper, WrCategory> implements IWrCategoryService {
    @Autowired
    private WrCategoryMapper wrCategoryMapper;

    @Autowired
    private WrFeeRateMapper wrFeeRateMapper;

    @Autowired
    private UniqueCodeGenerateUtil uniqueCodeGenerateUtil;

    /**
     * 获取所有用水性质信息
     * @param dto
     * @return
     */
    @Override
    public List<WrCategoryVO> getAllWrCategory(WrCategoryDto dto){
        QueryWrapper<WrCategory> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(dto.getCategoryCode())){
            wrapper.eq("CATEGORY_CODE",dto.getCategoryCode());
        }
        if(null != dto.getStartTime() && null != dto.getEndTime()){
            wrapper.between("CREATE_TIME", DateUtils.convertTimeToDate(dto.getStartTime()), DateUtils.convertTimeToDate(dto.getEndTime()));
        }
        List<WrCategoryTempVO> wrCategoryAndFeeList = baseMapper.getWrCategoryAndFeeList(wrapper);
        if(CollectionUtils.isNotEmpty(wrCategoryAndFeeList)){
            Map<String, List<WrCategoryTempVO>> cateMap = wrCategoryAndFeeList.stream().collect(Collectors.groupingBy(WrCategoryTempVO::getId));
            List<WrCategoryVO> resultList = new ArrayList<>();
            cateMap.entrySet().stream().forEach(entry -> {
                List<WrCategoryTempVO> voList = entry.getValue();
                Map<String,Object> setMap = new HashMap<String, Object>(4){
                    {
                        put("createTime",getConverterInstance(CONVERTER_DATETIME_TO_LONG));
                        put("updateTime",getConverterInstance(CONVERTER_DATETIME_TO_LONG));
                    }
                };
                WrCategoryVO categoryVO = convert2Entity(voList.get(0), WrCategoryVO.class, setMap);
                voList.stream().forEach(vo -> {
                    WrFeeRateVO rateVO = new WrFeeRateVO();
                    BeanUtils.copyProperties(vo,rateVO);
                    categoryVO.getRateVOList().add(rateVO);
                });
                resultList.add(categoryVO);
            });
            return resultList;
        }
        return null;
    }

    /**
     * 新增用水性质
     * @param dto
     * @return
     */
    @Override
    public int saveWrCategory(WrCategoryDto dto){
        WrCategory wrCategory = new WrCategory();
        //id
        wrCategory.setId(IDGenerator.getId());
        BeanUtils.copyProperties(dto,wrCategory);
        //用水性质唯一编码
        String key = RedisOperationTypeEnum.WATER_CHARGE + DateUtil.today();
        String uniqueCode = uniqueCodeGenerateUtil.generateUniqueCode(key, RedisOperationTypeEnum.WATER_CHARGE, true, 8);
        wrCategory.setCategoryCode(uniqueCode);
        return wrCategoryMapper.insert(wrCategory);
    }

    /**
     * 删除用水性质
     * @param categoryId
     * @return
     */
    @Transactional
    @Override
    public void deleteWrCategory(String categoryId){
        if(StringUtils.isEmpty(categoryId)){
            throw new TransactionException(CodeEnum.NO_PARAM,"请传入用水性质ID！");
        }
        //删除用水性质
        QueryWrapper<WrCategory> categoryWrapper = new QueryWrapper<>();
        categoryWrapper.eq("ID",categoryId);
        wrCategoryMapper.delete(categoryWrapper);
        //删除关联的费率信息
        QueryWrapper<WrFeeRate> feeWrapper = new QueryWrapper<>();
        categoryWrapper.eq("CATEGORY_ID",categoryId);
        wrFeeRateMapper.delete(feeWrapper);
    }
}
