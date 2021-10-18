package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrFlowMeasureDTO;
import com.nari.slsd.msrv.waterdiversion.model.secondary.po.WrFlowMeasure;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrFlowMeasureVO;

import java.util.List;

/**
 * <p>
 * 断面实测数据表 服务类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-05
 */
public interface IWrFlowMeasureService extends IService<WrFlowMeasure> {

    /**
     * 批量新增书籍
     *
     * @param dtoList
     */
    void saveBatch(List<WrFlowMeasureDTO> dtoList);

    /**
     * 更新数据
     *
     * @param dto
     */
    void update(WrFlowMeasureDTO dto);

    /**
     * 删除数据
     *
     * @param id
     */
    void delete(String id);

    /**
     * 分页查询数据
     *
     * @param pageIndex
     * @param pageSize
     * @param stationId
     * @return
     */
    DataTableVO getDataPage(Integer pageIndex, Integer pageSize, String stationId);


    /**
     * 按条件查询数据
     *
     * @param sdt
     * @param edt
     * @param stationId
     * @return
     */
    List<WrFlowMeasureVO> getDataList(Long sdt, Long edt, String stationId);

}
