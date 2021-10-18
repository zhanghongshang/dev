package com.nari.slsd.msrv.waterdiversion.services;

import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.waterdiversion.commons.WrDataEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterBuildingManagerService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.third.po.WrHinwS;
import com.nari.slsd.msrv.waterdiversion.mapper.third.WrHinwSMapper;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrHinwSService;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrDataVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrStationDataVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 地表水取水口实时监测表 服务实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-30
 */
@Service
public class WrHinwSServiceImpl extends MppServiceImpl<WrHinwSMapper, WrHinwS> implements IWrHinwSService {

    @Resource
    IWaterBuildingManagerService waterBuildingManagerService;

    @Override
    public List<WrHinwS> getWrHinwSData(List<String> swfcds, Long sdt, Long edt) {
        Date sDate = DateUtils.convertTimeToDate(sdt);
        Date eDate = DateUtils.convertTimeToDate(edt);

        return lambdaQuery().in(swfcds != null && swfcds.size() != 0, WrHinwS::getSwfcd, swfcds)
                .between(sdt != null && edt != null, WrHinwS::getMntm, sDate, eDate)
                .orderByAsc(WrHinwS::getMntm)
                .list();
    }

    @Override
    public List<WrStationDataVO> getWrStationData(List<String> swfcds, Long sdt, Long edt, List<String> valTypes) {
        List<WrStationDataVO> resultList = new ArrayList<>();
        List<WrHinwS> poList = getWrHinwSData(swfcds, sdt, edt);
        Map<String, List<WrHinwS>> groupMap = poList.stream().collect(Collectors.groupingBy(WrHinwS::getSwfcd));
        Map<String, WrBuildingAndDiversion> nameMap = waterBuildingManagerService.getBuildingMapByCodes(swfcds);

        groupMap.keySet().forEach(swfcd -> {
            List<WrHinwS> data = groupMap.get(swfcd);
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

    private WrDataVO convert2WrDataVO(WrHinwS po, List<String> valTypes) {
        WrDataVO vo = new WrDataVO();
        vo.setTime(po.getMntm().getTime());
        Map<String, Double> dataMap = new HashMap<>(5);
        if (valTypes != null && valTypes.size() != 0) {
            valTypes.forEach(type -> {
                switch (type) {
                    case WrDataEnum.VAL_TYPE_Z:
                        dataMap.put(WrDataEnum.VAL_TYPE_Z, po.getZ());
                        break;
                    case WrDataEnum.VAL_TYPE_Q:
                        dataMap.put(WrDataEnum.VAL_TYPE_Q, po.getQ());
                        break;
                    case WrDataEnum.VAL_TYPE_W:
                        dataMap.put(WrDataEnum.VAL_TYPE_W, po.getAccpw());
                        break;
                    default:
                }
            });
        }
        vo.setData(dataMap);
        return vo;
    }

    private List<WrDataVO> convert2WrDataVOList(List<WrHinwS> poList, List<String> valTypes) {
        List<WrDataVO> voList = new ArrayList<>();
        poList.forEach(po -> voList.add(convert2WrDataVO(po, valTypes)));
        return voList;
    }
}
