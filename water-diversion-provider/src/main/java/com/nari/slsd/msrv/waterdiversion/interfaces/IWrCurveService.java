package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrCurveDTO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrCurveTransDTO;
import com.nari.slsd.msrv.waterdiversion.model.secondary.po.WrCurve;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 曲线维护 服务类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-08
 */
public interface IWrCurveService extends IService<WrCurve> {
    /**
     * 保存曲线
     *
     * @param dto
     */
    void saveCurve(WrCurveTransDTO dto);

    /**
     * 查看率定曲线
     *
     * @param pageIndex
     * @param pageSize
     * @param stationIds
     * @return
     */
    DataTableVO getCurvePage(Integer pageIndex, Integer pageSize, List<String> stationIds);

    /**
     * 获取曲线详情
     *
     * @param id
     * @return
     */
    WrCurveTransDTO getCurve(String id);

    /**
     * 获取曲线详情
     *
     * @param stationId
     * @param time
     * @return
     */
    WrCurveTransDTO getCurve(String stationId, Long time);

    /**
     * 更新曲线(审核)
     *
     * @param dto
     * @return
     */
    void updateCurve(WrCurveDTO dto);

    /**
     * 删除曲线
     *
     * @param id
     */
    void deleteCurve(String id);

    /**
     * 唯一性校验
     *
     * @param code
     * @return
     */
    Boolean checkUniqueCode(String code);
}
