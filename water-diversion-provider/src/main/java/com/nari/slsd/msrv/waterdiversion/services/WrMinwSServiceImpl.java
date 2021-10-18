package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.waterdiversion.commons.WrDataEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterBuildingManagerService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.third.po.WrMinwS;
import com.nari.slsd.msrv.waterdiversion.mapper.third.WrMinwSMapper;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrMinwSService;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrDataVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrStationDataVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 地表水取水口月监测表 服务实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-30
 */
@Service
public class WrMinwSServiceImpl extends MppServiceImpl<WrMinwSMapper, WrMinwS> implements IWrMinwSService {

    @Resource
    IWaterBuildingManagerService waterBuildingManagerService;

    @Override
    public List<WrMinwS> getWrMinwSData(List<String> swfcds, Long sdt, Long edt) {
        Calendar sCalender = Calendar.getInstance();
        sCalender.setTime(DateUtils.convertTimeToDate(sdt));
        int syr = sCalender.get(Calendar.YEAR);
        //calendar获取的月份是从0-11 所以要+1
        int smnth = sCalender.get(Calendar.MONTH) + 1;
        Calendar eCalender = Calendar.getInstance();
        eCalender.setTime(DateUtils.convertTimeToDate(edt));
        int eyr = eCalender.get(Calendar.YEAR);
        int emnth = eCalender.get(Calendar.MONTH) + 1;
        LambdaQueryWrapper<WrMinwS> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(swfcds != null && swfcds.size() != 0, WrMinwS::getSwfcd, swfcds);
        if (sdt != null && edt != null) {
            wrapper.and(syr == eyr, wr -> wr.eq(WrMinwS::getYr, syr));
            wrapper.and(syr == eyr, wr -> wr.ge(WrMinwS::getMnth, smnth).le(WrMinwS::getMnth, emnth));
            wrapper.and(
                    syr < eyr, wr1 -> wr1.eq(WrMinwS::getYr, syr).ge(WrMinwS::getMnth, smnth)
                            .or(wr2 -> wr2.between(WrMinwS::getYr, syr + 1, eyr - 1))
                            .or(wr3 -> wr3.eq(WrMinwS::getYr, eyr).le(WrMinwS::getMnth, emnth))
            );
        }

        return list(wrapper);
    }

    @Override
    public List<WrStationDataVO> getWrStationData(List<String> swfcds, Long sdt, Long edt, List<String> valTypes) {
        List<WrStationDataVO> resultList = new ArrayList<>();
        List<WrMinwS> poList = getWrMinwSData(swfcds, sdt, edt);
        Map<String, List<WrMinwS>> groupMap = poList.stream().collect(Collectors.groupingBy(WrMinwS::getSwfcd));
        Map<String, WrBuildingAndDiversion> nameMap = waterBuildingManagerService.getBuildingMapByCodes(swfcds);

        groupMap.keySet().forEach(swfcd -> {
            List<WrMinwS> data = groupMap.get(swfcd);
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

    private WrDataVO convert2WrDataVO(WrMinwS po, List<String> valTypes) {
        WrDataVO vo = new WrDataVO();
        int year = po.getYr();
        int month = po.getMnth();
        Calendar calendar = Calendar.getInstance();
        //calendar 月份-1
        calendar.set(year, month - 1, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        vo.setTime(date.getTime());
        Map<String, Double> dataMap = new HashMap<>(5);
        if (valTypes != null && valTypes.size() != 0) {
            valTypes.forEach(type -> {
                switch (type) {
                    case WrDataEnum.VAL_TYPE_Z:
                        dataMap.put(WrDataEnum.VAL_TYPE_Z, po.getAvz());
                        break;
                    case WrDataEnum.VAL_TYPE_Q:
                        dataMap.put(WrDataEnum.VAL_TYPE_Q, po.getTdeq());
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

    private List<WrDataVO> convert2WrDataVOList(List<WrMinwS> poList, List<String> valTypes) {
        List<WrDataVO> voList = new ArrayList<>();
        poList.forEach(po -> voList.add(convert2WrDataVO(po, valTypes)));
        return voList;
    }
}
