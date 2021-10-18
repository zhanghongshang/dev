package com.nari.slsd.msrv.waterdiversion.interfaces;


import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrRightTradeDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrRightTrade;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrRightTradeVO;

import java.util.List;

/**
 * @author Created by ZHD
 * @program: IWrRightTradeService
 * @description:水权交易接口
 * @date: 2021/8/17 10:29
 */
public interface IWrRightTradeService extends IService<WrRightTrade> {
    DataTableVO getWrRightTrade(Integer pageIndex, Integer pageSize,String year);


    Boolean save(WrRightTradeDTO dto);

    Boolean update(WrRightTradeDTO dto);

    void delete(String id);

    List<WrRightTradeVO> getAllRightTradeInYear();
}
