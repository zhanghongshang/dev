package com.nari.slsd.msrv.waterdiversion.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.nacos.api.config.filter.IFilterConfig;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.common.utils.BeanUtils;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.cache.RedisCacheKeyDef;
import com.nari.slsd.msrv.waterdiversion.cache.interfaces.IModelCacheService;
import com.nari.slsd.msrv.waterdiversion.commons.TreeEnum;
import com.nari.slsd.msrv.waterdiversion.config.RedisUtil;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrContrastService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanGenerateMonthService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanInterDayService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrUseUnitManagerService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WaterBuildingManagerMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrUseUnitManagerMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.PersonTransDTO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WaterBuildingManager;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrUseUnitManager;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrUseUnitPerson;
import com.nari.slsd.msrv.waterdiversion.model.vo.PlanContrast;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanDataContrast;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitManagerVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitNode;
import com.nari.slsd.msrv.waterdiversion.utils.CommonUtil;
import jdk.nashorn.internal.objects.annotations.Where;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class WrContrastServiceImp extends ServiceImpl<WrUseUnitManagerMapper, WrUseUnitManager> implements IWrContrastService {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    IWrUseUnitManagerService wrUseUnitManagerService;
    @Autowired
    IModelCacheService modelCacheService;
    @Autowired
    WaterBuildingManagerMapper waterBuildingManagerMapper;
    @Autowired
    IWrPlanGenerateMonthService wrPlanGenerateMonthService;

    @Override
    public List<WrUseUnitNode> getAllWaterUseUnitList() {
        List<WrUseUnitNode> wrUseUnitNodes =  wrUseUnitManagerService.getAllTreeFromCache();
        wrUseUnitNodes.forEach(wrUseUnitNode->{
            buildTreeNode(wrUseUnitNode.getChildren());
        });
        return wrUseUnitNodes;
    }
    @Override
    public List<PlanContrast> getContrast() {
        String key = new StringBuffer(RedisCacheKeyDef.ModelKey.CONTRAST).toString();
       // String value = JSON.toJSONString(node);
        //redisUtil.get();
        return null;
    }

    /**
     * 根据用水单位获取引水口id
     * @param WaterUnitId
     * @return
     */
    @Override
    public List<WrUseUnitNode> getBuildingIdByWaterUnitId(String WaterUnitId) {
        QueryWrapper<WaterBuildingManager> wrapper = new QueryWrapper<>();
        wrapper.in("wd.WATER_UNIT_ID", WaterUnitId);
        List<WrBuildingAndDiversion> buildingList = waterBuildingManagerMapper.getBuildingAndDiversionList(wrapper);
        List<WrUseUnitNode> wrUseUnitNodes = new ArrayList<>();
        buildingList.forEach(wr->{
            WrUseUnitNode wrUseUnitNode = new WrUseUnitNode();
            wrUseUnitNode.setId(wr.getId());
            wrUseUnitNode.setName(wr.getBuildingName());
            wrUseUnitNode.setIsLeaf(true);
            wrUseUnitNode.setPid(wr.getWaterUnitId());
            wrUseUnitNodes.add(wrUseUnitNode);
        });
        return wrUseUnitNodes;
    }
    /**
     * 递归获取树型结构数据
     * @param srcList 父节点id
     * @param srcList 源list
     * @return
     */
    public  void buildTreeNode(List<WrUseUnitNode> srcList){
        for (WrUseUnitNode temp : srcList) {
            List<WrUseUnitNode> list = temp.getChildren();
            if (CollectionUtils.isEmpty(list)){
                //获取底层引水口id
                temp.setChildren(getBuildingIdByWaterUnitId(temp.getId()));
            }else{
                //递归
                buildTreeNode(list);
            }
        }
    }
    @Override
    public PlanContrast buildingContrast(String waterUnitId,String levels){
        List<String> buildingIds = new ArrayList<>();
        //获取引水口id集合
        if(StringUtils.isNotEmpty(levels)){
            List<WrBuildingAndDiversion> wrBuildingAndDiversions = wrPlanGenerateMonthService.getAllWaterBuildingForAppointUseUnit(waterUnitId);
            wrBuildingAndDiversions.forEach(wd->{
                buildingIds.add(wd.getId());
            });
        }else {
            buildingIds.add(waterUnitId);
        }
        PlanContrast planContrast = getContastTree(buildingIds);
        return planContrast;
    }
    //获取redis中同期比对值
    public PlanContrast getContastTree(List<String> buildingIds) {
        String key = new StringBuffer(RedisCacheKeyDef.ModelKey.CONTRAST).toString();
        Object node = redisUtil.get(key);
        List<WrPlanDataContrast> jsonObject = JSON.parseArray(node.toString(),WrPlanDataContrast.class);
        PlanContrast result = new PlanContrast();
        Double planMtdValue = 0.0;
        Double planYtdValue = 0.0;
        Double planStlmValue = 0.0;
        Double planYtlmValue = 0.0;
        Double actMtdValue = 0.0;
        Double actYtdValue = 0.0;
        Double actStlmValue = 0.0;
        Double actStlyValue = 0.0;
        for (WrPlanDataContrast wrPlanDataContrast:jsonObject){
            String buildingId = wrPlanDataContrast.getBuildingId();
            PlanContrast planContrast = wrPlanDataContrast.getPlanAndActContrast();
            for (String s:buildingIds) {
                if (s.equals(buildingId)) {
                    if (planContrast.getPlanMtd()!=null){
                        planMtdValue += planContrast.getPlanMtd().doubleValue();
                    }
                    if (planContrast.getPlanYtd()!=null){
                        planYtdValue += planContrast.getPlanYtd().doubleValue();
                    }
                    if (planContrast.getPlanStlm()!=null){
                        planStlmValue += planContrast.getPlanStlm().doubleValue();
                    }
                    if (planContrast.getPlanStly()!=null){
                        planYtlmValue += planContrast.getPlanStly().doubleValue();
                    }
                    if (planContrast.getActMtd()!=null){
                        actMtdValue += planContrast.getActMtd().doubleValue();
                    }
                    if (planContrast.getActYtd()!=null){
                        actYtdValue += planContrast.getActYtd().doubleValue();
                    }
                    if (planContrast.getActStlm()!=null){
                        actStlmValue += planContrast.getActStlm().doubleValue();
                    }
                    if (planContrast.getActStly()!=null){
                        actStlyValue += planContrast.getActStly().doubleValue();
                    }
                }
            }
        }
        result.setPlanMtd(CommonUtil.number(planMtdValue));
        result.setPlanYtd(CommonUtil.number(planYtdValue));
        result.setPlanStlm(CommonUtil.number(planStlmValue));
        result.setPlanStly(CommonUtil.number(planYtlmValue));
        result.setActMtd(CommonUtil.number(actMtdValue));
        result.setActYtd(CommonUtil.number(actYtdValue));
        result.setActStlm(CommonUtil.number(actStlmValue));
        result.setActStly(CommonUtil.number(actStlyValue));

        return result;
    }
}
