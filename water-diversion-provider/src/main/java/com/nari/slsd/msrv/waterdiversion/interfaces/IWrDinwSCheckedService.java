package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.nari.slsd.msrv.waterdiversion.model.third.po.WrDinwSChecked;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrStationDataVO;

import java.util.List;

/**
 * <p>
 * 地表水取水口日引水监测表（已审批日监测数据表） 服务类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-30
 */
public interface IWrDinwSCheckedService extends IMppService<WrDinwSChecked> {

    List<WrDinwSChecked> getWrDinwSCheckedData(List<String> swfcds, Long sdt, Long edt);

    List<WrStationDataVO> getWrStationData(List<String> swfcds, Long sdt, Long edt, List<String> valTypes);



}
