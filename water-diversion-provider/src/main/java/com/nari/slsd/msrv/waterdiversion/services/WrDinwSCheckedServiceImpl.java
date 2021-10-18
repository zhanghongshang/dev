package com.nari.slsd.msrv.waterdiversion.services;

import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.waterdiversion.commons.WrDataEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterBuildingManagerService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.third.po.WrDinwSChecked;
import com.nari.slsd.msrv.waterdiversion.mapper.third.WrDinwSCheckedMapper;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDinwSCheckedService;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrDataVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrStationDataVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 地表水取水口日引水监测表（已审批日监测数据表） 服务实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-30
 */
@Service
public class WrDinwSCheckedServiceImpl extends MppServiceImpl<WrDinwSCheckedMapper, WrDinwSChecked> implements IWrDinwSCheckedService {
    @Resource
    IWaterBuildingManagerService waterBuildingManagerService;

    @Override
    public List<WrDinwSChecked> getWrDinwSCheckedData(List<String> swfcds, Long sdt, Long edt) {
        Date sDate = DateUtils.convertTimeToDate(sdt);
        Date eDate = DateUtils.convertTimeToDate(edt);
        return lambdaQuery().in(swfcds != null && swfcds.size() != 0, WrDinwSChecked::getSwfcd, swfcds)
                .between(sdt != null && edt != null, WrDinwSChecked::getMntm, sDate, eDate)
                .orderByAsc(WrDinwSChecked::getMntm)
                .list();
    }

    @Override
    public List<WrStationDataVO> getWrStationData(List<String> swfcds, Long sdt, Long edt, List<String> valTypes) {
        List<WrStationDataVO> resultList = new ArrayList<>();
        List<WrDinwSChecked> poList = getWrDinwSCheckedData(swfcds, sdt, edt);
        Map<String, List<WrDinwSChecked>> groupMap = poList.stream().collect(Collectors.groupingBy(WrDinwSChecked::getSwfcd));
        Map<String, WrBuildingAndDiversion> nameMap = waterBuildingManagerService.getBuildingMapByCodes(swfcds);

        groupMap.keySet().forEach(swfcd -> {
            List<WrDinwSChecked> data = groupMap.get(swfcd);
            WrStationDataVO stationDataVO = new WrStationDataVO();
            stationDataVO.setStationCode(swfcd);
            // 测站名
            if (nameMap.containsKey(swfcd)) {
                stationDataVO.setStationName(nameMap.get(swfcd).getBuildingName());
            }
            stationDataVO.setData(convert2WrDataVOList(data, valTypes));
            resultList.add(stationDataVO);
        });
        return resultList;
    }

    private WrDataVO convert2WrDataVO(WrDinwSChecked po, List<String> valTypes) {
        WrDataVO vo = new WrDataVO();
        vo.setTime(po.getMntm().getTime());
        Map<String, Double> dataMap = new HashMap<>(5);
        if (valTypes != null && valTypes.size() != 0) {
            valTypes.forEach(type -> {
                switch (type) {
                    case WrDataEnum.VAL_TYPE_Z:
                        dataMap.put(WrDataEnum.VAL_TYPE_Z, po.getAvz());
                        break;
                    case WrDataEnum.VAL_TYPE_Q:
                        dataMap.put(WrDataEnum.VAL_TYPE_Q, po.getDvq());
                        break;
                    case WrDataEnum.VAL_TYPE_W:
                        dataMap.put(WrDataEnum.VAL_TYPE_W, po.getFwqt());
                        break;
                    default:
                }
            });
        }
        vo.setData(dataMap);
        return vo;
    }

    private List<WrDataVO> convert2WrDataVOList(List<WrDinwSChecked> poList, List<String> valTypes) {
        List<WrDataVO> voList = new ArrayList<>();
        poList.forEach(po -> voList.add(convert2WrDataVO(po, valTypes)));
        return voList;
    }
}
