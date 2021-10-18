package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.nari.slsd.msrv.waterdiversion.model.vo.WrStationDataVO;

import java.util.List;

/**
 * @Program: water-diversion
 * @Description:
 * @Author: reset kalar
 * @Date: 2021-09-01 09:08
 **/

public interface IWrDataService {

    List<WrStationDataVO> getWrData(List<String> swfcds, Long sdt, Long edt, String runDataType, List<String> valTypes);
}
