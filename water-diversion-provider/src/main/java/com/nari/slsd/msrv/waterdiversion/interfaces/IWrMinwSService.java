package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.nari.slsd.msrv.waterdiversion.model.third.po.WrMinwS;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrStationDataVO;

import java.util.List;

/**
 * <p>
 * 地表水取水口月监测表 服务类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-30
 */
public interface IWrMinwSService extends IMppService<WrMinwS> {

    List<WrMinwS> getWrMinwSData(List<String> swfcds, Long sdt, Long edt);

    List<WrStationDataVO> getWrStationData(List<String> swfcds, Long sdt, Long edt, List<String> valTypes);


}
