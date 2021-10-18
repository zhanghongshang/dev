package com.nari.slsd.msrv.waterdiversion.model.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * gis实时调度
 * </p>
 *
 * @author bigb
 * @since 2021-08-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GisRealtimeDispatchListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String resizeDateStr;

    private Date resizeDate;

    private Long resizeDateLong;
    /**
     * 管理单位ID
     */
    private String mngUnitId;
    /**
     * 管理单位名称
     */
    private String mngUnitName;

    private List<GisRealtimeDispatchVO> voList = new ArrayList<>();


}
