package com.nari.slsd.msrv.waterdiversion.services;

import com.nari.slsd.msrv.waterdiversion.commons.WrDataEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDataService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDinwSCheckedService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrHinwSService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrMinwSService;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrStationDataVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * WR数据 服务实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-09-01
 */
@Service
public class WrDataServiceImpl implements IWrDataService {
    @Resource
    IWrMinwSService wrMinwSService;

    @Resource
    IWrDinwSCheckedService wrDinwSCheckedService;

    @Resource
    IWrHinwSService wrHinwSService;

    @Override
    public List<WrStationDataVO> getWrData(List<String> swfcds, Long sdt, Long edt, String runDataType, List<String> valTypes) {
        List<WrStationDataVO> resultList = new ArrayList<>();
        switch (runDataType) {
            case WrDataEnum.TABLE_MINW_S:
                resultList = wrMinwSService.getWrStationData(swfcds, sdt, edt, valTypes);
                break;
            case WrDataEnum.TABLE_DINW_S_CHECKED:
                resultList = wrDinwSCheckedService.getWrStationData(swfcds, sdt, edt, valTypes);
                break;
            case WrDataEnum.TABLE_DINW_S:
                break;
            case WrDataEnum.TABLE_HINW_S:
                resultList = wrHinwSService.getWrStationData(swfcds, sdt, edt, valTypes);
                break;
            default:
        }
        return resultList;
    }
}
