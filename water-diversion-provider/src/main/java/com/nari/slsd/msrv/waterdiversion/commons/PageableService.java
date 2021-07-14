package com.nari.slsd.msrv.waterdiversion.commons;


import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.PageModel;

/**
 * @ClassName: PageableService
 * @Description:  分页接口
 * @Author: sk
 * @Date: 2020/4/13 15:44
 * @Version: 1.0
 * @Remark:
 **/
public interface PageableService {
    DataTableVO page(PageModel pageModel);
}
