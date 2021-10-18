package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrCategoryDto;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrCategory;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrCategoryVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @title
 * @description 用水性质管理服务类
 * @author bigb
 * @updateTime 2021/8/23 11:12
 * @throws
 */
public interface IWrCategoryService extends IService<WrCategory> {

    List<WrCategoryVO> getAllWrCategory(WrCategoryDto dto);

    int saveWrCategory(WrCategoryDto dto);

    @Transactional
    void deleteWrCategory(String categoryId);
}
