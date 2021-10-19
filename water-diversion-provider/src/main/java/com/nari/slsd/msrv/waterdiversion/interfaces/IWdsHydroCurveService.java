package com.nari.slsd.msrv.waterdiversion.interfaces;

import java.util.List;
import java.util.Map;

public interface IWdsHydroCurveService {
   
/*    List<WdsHydroCurveReponse> findCurveById(Long pid,Integer typeId);

    List<Map<String, Object>> findValueBydim2(long curveId,String x,String y);

    List<Map<String, Object>> findValueBydim3(long curveId,String x,String y,String z);*/

    Map<String,Object> getDataByother(Map<String,Object> map);
}
