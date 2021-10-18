//package com.nari.slsd.msrv.waterdiversion.services;
//
//import com.nari.slsd.hu.mplat.imc.client.dto.Chardata;
//import com.nari.slsd.hu.mplat.imc.client.dto.FetchParam;
//import com.nari.slsd.hu.mplat.imc.client.dto.Param;
//import com.nari.slsd.hu.mplat.imc.client.util.ToolsUtil;
//import com.nari.slsd.msrv.waterdiversion.model.dto.InsertMethod;
//import com.nari.slsd.msrv.waterdiversion.model.secondary.po.CZYDataCurve;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.lang.Nullable;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
///**
// * @Author ：
// * @Date ： @Description： //TODO
// * @Version: 1.0
// */
//@Service
//@Slf4j
//public class RealDataCalcServiceImpl {
//    private static final String SENID = "senid";
//
////    @Autowired
////    private IBaseDao baseDao;
////
////    @Autowired
////    private IPubDao pubDao;
////    @Autowired
////    ICommonDataService imcDataService;
////    @Autowired
////    private NcControlService controlService;
//
//    // @Value("${realCalc.idleTime}")
//    private long idleTime = 10 * 60;
//    // 服务缓存
//    protected static HashMap<String, Object> cacheMap = new HashMap<>();
//
//    public synchronized CZYDataCurve getDataCurve(HashMap<String, Object> params) {
//        // 20170309时间过滤从wds_hydro_time_curved表取数
//        Calendar time = (Calendar) params.get(Param.TimeType.EQ_BTIME);
//        CZYDataCurve dataCurve = null;
//        String id = "";
//        Integer exInt = (Integer) params.get(Param.ExParamType.EX_INT);
//        Object[] ObjIdarray = (Object[]) params.get(Param.IdType.STR_IDARRAY);
//        if (ObjIdarray.length > 0) {
//            id = (String) ObjIdarray[0];
//        }
//        boolean existed = false;
//        // 20170309时间过滤从wds_hydro_time_curved表取数
//        Object curveObject = null;
//        if (time == null) {
//            curveObject = cacheMap.putIfAbsent(id, new CZYDataCurve());
//        }
//        if (curveObject != null) {
//            existed = true;
//            dataCurve = (CZYDataCurve) curveObject;
//            dataCurve.setVisitTime(new Date());
//        }
//        //根据exInt,设置dataCurve
//        setDataCurve(dataCurve, exInt);
//        // 20170309时间函数不做限制
//        if (existed) {
//            return dataCurve;
//        }
//
//        HashMap<String, Object> inmap = new HashMap<>();
//        ArrayList<String> idArrayList = new ArrayList<>();
//        List resultList;
//        idArrayList.add(id);
//        if (dataCurve.getM_ItemNum() == 3) {
//            int size0 = -99, size1 = -99, size2 = -99;
//
//            inmap.put(Param.IdType.STR_IDARRAY, new Object[]{id});
//            //v0 size
//            size0 = getSize(time, id, inmap, "v0");
//            //v1 size
//            size1 = getSize(time, id, inmap, "v1");
//            //v2 size
//            size2 = getSize(time, id, inmap, "v2");
//
//            //比较出三者最大,并进行相关处理
//            //这个地方应该是用else if?
//            int maxSize = Math.max(Math.max(size0, size1), size2);
//            if (maxSize == size0) {
//                dataCurve.getM_Col()[0] = 1;
//                dataCurve.getM_Col()[1] = 2;
//                dataCurve.getM_Col()[2] = 0;
//            } else if (maxSize == size1) {
//                dataCurve.getM_Col()[0] = 0;
//                dataCurve.getM_Col()[1] = 2;
//                dataCurve.getM_Col()[2] = 1;
//            } else if (maxSize == size2) {
//                dataCurve.getM_Col()[0] = 0;
//                dataCurve.getM_Col()[1] = 1;
//                dataCurve.getM_Col()[2] = 2;
//            }
//            //v0
//            setCzyDataCurve(time, dataCurve, id, inmap, 0);
//            //v1
//            setCzyDataCurve(time, dataCurve, id, inmap, 1);
//            //v0,v1,v2多表
//            setDataCureExt(time, dataCurve, id, inmap);
//
//            dataCurve.setM_SupplementWay(dataCurve.getM_v0().size() >= dataCurve.getM_v1().size() ? 0 : 1);
//            dataCurve.setCurrentInsertMethod(InsertMethod.M_3_LINE);
//        } else if (dataCurve.getM_ItemNum() == 2) {
//            // 20170309时间过滤从wds_hydro_time_curved表取数
//            if (time == null) {
//                FetchParam fetchParam = new FetchParam(Param.AppType.APP_Type_WDS);
//                fetchParam.idarrayStrings = new String[]{id};
//                String sql = "select v0,v1,v2,v3"
//                        + " from pubuser.SL_WATER_CURVE_POINT_VALUE where CURVE_ID in (?) order by v0,v1,v2,v3";
//                inmap.put(Param.ExParamType.EX_SQL, sql);
//                resultList = baseDao.queryBySQL(sql, new Object[]{id});
//                if (CollectionUtils.isNotEmpty(resultList)) {
//                    resultList.stream().forEach(e -> {
//                        Object[] array = (Object[]) e;
//                        double d0 = -1, d1 = -1/*, d2 = -1*/;
//                        if (array.length >= 2) {
//                            d0 = Double.valueOf(array[0].toString());
//                            d1 = Double.valueOf(array[1].toString());
//                            /**d2 = Double.valueOf(array[2].toString());**/
//                            dataCurve.getM_v0().add(d0);
//                            dataCurve.getM_v1().add(d1);
//                        }
//                    });
//                }
//            } else {
//                inmap.put(Param.IdType.STR_IDARRAY, idArrayList);
//                inmap.put(Param.TimeType.EQ_BTIME, time);
//                inmap.put(Param.TimeType.EQ_ETIME, time);
//                String sql = "select v0,v1 from pubuser.wds_hydro_time_curved where curveid in(?) and v0 >= 0 and v1>=0 and STTIME<=[TB] and EDTIME>[TE] order by v0,v1";
//
//                inmap.put(Param.ExParamType.EX_SQL, sql);
//                resultList = baseDao.queryBySQL(sql, new Object[]{id});
//                if (CollectionUtils.isNotEmpty(resultList)) {
//                    resultList.stream().forEach(e -> {
//                        double d0 = -1, d1 = -1;
//                        Object[] array = (Object[]) e;
//                        d0 = Double.valueOf(array[0].toString());
//                        d1 = Double.valueOf(array[1].toString());
//                        dataCurve.getM_v0().add(d0);
//                        dataCurve.getM_v1().add(d1);
//                    });
//                }
//            }
//        }
//        dataCurve.setM_bHaveInit(true);
//        dataCurve.setVisitTime(new Date());
//        if (time == null) {
//            /*if (cacheMap.keySet().size() >= 100) {
//
//            }*/
//            cacheMap.put(id, dataCurve);
//        }
//
//        return dataCurve;
//    }
//
//    @Nullable
//    private void setDataCureExt(Calendar time, CZYDataCurve dataCurve, String id, HashMap<String, Object> inParamMap) {
//        List resultList;
//        inParamMap.remove(Param.ExParamType.EX_SQL);
//        String sv = "v" + dataCurve.getM_Col()[0] + ",v" + dataCurve.getM_Col()[1] + ",v" + dataCurve.getM_Col()[2];
//        // 20170309时间过滤从wds_hydro_time_curved表取数
//        if (time == null) {
//            sql = "select " + sv + " from pubuser.SL_WATER_CURVE_POINT_VALUE where CURVE_ID in(?)" + " and v"
//                    + dataCurve.getM_Col()[0] + ">=0" + " and" + " v" + dataCurve.getM_Col()[1] + ">=0" + " and"
//                    + " v" + dataCurve.getM_Col()[2] + ">=0" + " order by v" + dataCurve.getM_Col()[0] + ",v"
//                    + dataCurve.getM_Col()[1];
//        } else {
//            sql = "select " + sv + " from pubuser.wds_hydro_time_curved where curveid in([IDARRAY])" + " and v"
//                    + dataCurve.getM_Col()[0] + ">=0" + " and" + " v" + dataCurve.getM_Col()[1] + ">=0" + " and"
//                    + " v" + dataCurve.getM_Col()[2] + ">=0  and STTIME<=[TB] and EDTIME>[TE] " + " order by v"
//                    + dataCurve.getM_Col()[0] + ",v" + dataCurve.getM_Col()[1];
//        }
//        inParamMap.put(Param.ExParamType.EX_SQL, sql);
//        resultList = baseDao.queryBySQL(sql, new Object[]{id});
//        if (CollectionUtils.isEmpty(resultList)) {
//            throw new RuntimeException("getAndSetDataCureExt no relative curved found!");
//        }
//        Double matrix[][] = new Double[dataCurve.getM_v0().size()][dataCurve.getM_v1().size()];
//
//        int nRow = dataCurve.getM_v0().size();
//        int nCol = dataCurve.getM_v1().size();
//        for (int row = 0; row < nRow; row++) {
//            Double[] innerArray = new Double[nCol];
//            Arrays.fill(innerArray, -99.0);
//            matrix[row] = innerArray;
//        }
//        dataCurve.setMatrix(matrix);
//
//        resultList.forEach(e -> {
//            Object[] array = (Object[]) e;
//            double d0 = -1, d1 = -1, d2 = -1;
//
//            if (array.length == 3) {
//                d0 = Double.valueOf(array[0].toString());
//                d1 = Double.valueOf(array[1].toString());
//                d2 = Double.valueOf(array[2].toString());
//            }
//
//            int row = dataCurve.getM_v0().indexOf(d0);
//            int col = dataCurve.getM_v1().indexOf(d1);
//            // 有重复的项值自相关
//            if (matrix[row][col] >= 0) {
//                dataCurve.getSpecialv0List().add(d0);
//                dataCurve.getSpecialv1List().add(d1);
//            }
//            // dw=1表示是有�?
//            matrix[row][col] = d2;
//
//            dataCurve.getM_v0_col().set(row, dataCurve.getM_v0_col().get(row) + 1);
//            dataCurve.getM_v1_row().set(col, dataCurve.getM_v1_row().get(col) + 1);
//        });
//    }
//
//    @Nullable
//    private void setCzyDataCurve(Calendar time, CZYDataCurve dataCurve, String id, HashMap<String, Object> inParamMap, int index) {
//        String sql;
//        inParamMap.remove(Param.ExParamType.EX_SQL);
//        String sv = ("v" + dataCurve.getM_Col()[index]);
//        // 20170309时间过滤从wds_hydro_time_curved表取数
//        if (time == null) {
//            sql = "select " + sv + " from pubuser.SL_WATER_CURVE_POINT_VALUE where CURVE_ID in(?) and " + sv
//                    + ">= 0 group by (" + sv + ") order by " + sv;
//        } else {
//            sql = "select " + sv + " from pubuser.wds_hydro_time_curved where curveid in([IDARRAY]) and " + sv
//                    + ">= 0  and STTIME<=[TB] and EDTIME>[TE] group by (" + sv + ") order by " + sv;
//        }
//        inParamMap.put(Param.ExParamType.EX_SQL, sql);
//
//        List resultList = baseDao.queryBySQL(sql, new Object[]{id});
//
//        if (CollectionUtils.isEmpty(resultList)) {
//            throw new RuntimeException("getAndSetCzyDataCurve no relative curved found!");
//        }
//
//        resultList.stream().mapToDouble(e -> Double.parseDouble(e.toString())).filter(e -> e >= 0).forEach(val -> {
//            if (index == 0) {
//                dataCurve.getM_v0().add(val);
//                dataCurve.getM_v0_col().add(0);
//            } else if (index == 1) {
//                dataCurve.getM_v1().add(val);
//                dataCurve.getM_v1_col().add(0);
//            }
//        });
//    }
//
//    /**
//     * @param time
//     * @param id
//     * @param inParamMap
//     * @return
//     */
//    private int getSize(Calendar time, String id, HashMap<String, Object> inParamMap, String tableName) {
//        String sql;
//        inParamMap.remove(Param.ExParamType.EX_SQL);
//        // 20170309时间过滤从wds_hydro_time_curved表取数
//        if (time == null) {
//            sql = "select count(distinct(" + tableName + ")) from pubuser.SL_WATER_CURVE_POINT_VALUE where CURVE_ID in(?) and " + tableName + " >= 0";
//        } else {
//            sql = "select count(distinct(" + tableName + ")) from pubuser.wds_hydro_time_curved where curveid in([IDARRAY]) and " + tableName + " >= 0  and STTIME<=[TB] and EDTIME>[TE]";
//        }
//        inParamMap.put(Param.ExParamType.EX_SQL, sql);
//        List resultList = baseDao.queryBySQL(sql, new Object[]{id});
//
//        if (CollectionUtils.isEmpty(resultList)) {
//            throw new RuntimeException("getSize no relative curved found!");
//        }
//        return Integer.valueOf((resultList.get(0).toString()));
//    }
//
//    /**
//     * 设置DataCurve
//     *
//     * @param dataCurve
//     * @param exInt
//     */
//    private void setDataCurve(CZYDataCurve dataCurve, Integer exInt) {
//        // 当前的插值类�?曲线维数,根据exInt来确�?
//        switch (exInt) {
//            case 1:
//                dataCurve.setM_ItemNum(2);
//                dataCurve.setCurrentInsertType(InsertType.D_1_2);
//                break;
//            case 2:
//                dataCurve.setM_ItemNum(2);
//                dataCurve.setCurrentInsertType(InsertType.D_2_1);
//                break;
//            case 11:
//                dataCurve.setM_ItemNum(3);
//                dataCurve.setCurrentInsertType(InsertType.D_12_3);
//
//                break;
//            case 12:
//                dataCurve.setM_ItemNum(3);
//                dataCurve.setCurrentInsertType(InsertType.D_13_2);
//
//                break;
//            case 13:
//                dataCurve.setM_ItemNum(3);
//                dataCurve.setCurrentInsertType(InsertType.D_23_1);
//
//                break;
//            default:
//                dataCurve.setCurrentInsertType(InsertType.D_NULL);
//                break;
//        }
//    }
//
//    private Chardata calcByFunct(String funcId, Map destPoint) {
//        Calendar now = Calendar.getInstance();
//        String calc_senid = destPoint.get(SENID).toString();
//        WaterFormular waterFormular = (WaterFormular) pubDao.getEntity(WaterFormular.class, funcId);
//        if (waterFormular == null) {
//            return null;
//        }
//        String waterFormular_script;
//        try {
//            waterFormular_script = ToolsUtil.Clob2Str(waterFormular.getFormular_script());
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            log.error("calcByFunct方法执行失败,error is {}", e);
//            return null;
//        }
//        String hql = "from SlCalcVariate fv where fv.calc_senid=:calc_senid";
//        List<SlCalcVariate> variates = pubDao.find(hql, new String[]{"calc_senid"}, new Object[]{calc_senid});
//        if (CollectionUtils.isEmpty(variates)) {
//            return null;
//        }
//        Map<String, List<String>> varMap = new HashMap<>();
//        variates.stream().forEach(var -> {
//            if (var.getSenid() != null && var.getApp_type() != null) {
//                varMap.putIfAbsent(var.getApp_type(), new ArrayList<String>());
//                varMap.get(var.getApp_type()).add(var.getSenid());
//            }
//        });
//        if (varMap.size() == 0) {
//            return null;
//        }
//        List<FetchParam> fetchParams = new ArrayList<FetchParam>();
//        varMap.keySet().stream().forEach(key -> {
//            FetchParam fetchParam = new FetchParam(key);
//            fetchParam.idarrayStrings = varMap.get(key).toArray(new String[0]);
//            fetchParam.valtype = Param.ValType.Special_V;
//            fetchParam.rundatatype = Param.RunDataType.RUN_RTREAL;
//            fetchParams.add(fetchParam);
//        });
//        HashMap<Long, Chardata>[] dataList = imcDataService.getSpecialData(fetchParams.toArray(new FetchParam[0]));
//        Calendar lastTime = null;
//        for (HashMap<Long, Chardata> cMap : dataList) {
//            for (SlCalcVariate var : variates) {
//                Long senId = Long.valueOf(var.getSenid());
//                if (cMap.containsKey(senId)) {
//                    Chardata cd = cMap.get(senId);
//                    if (cd != null && now.getTimeInMillis() - cd.getTime().getTimeInMillis() < idleTime * 1000) {
//                        waterFormular_script.replaceAll("#" + var.getVariate_name() + "#", cd.getV().toString());
//                    }
//                    if (lastTime == null || cd.getTime().after(lastTime)) {
//                        lastTime = cd.getTime();
//                    }
//                }
//            }
//        }
//        if (waterFormular_script.contains("#")) {
//            return null;
//        } else {
//            Double val = (Double) ScriptEngineUtil.eval(waterFormular_script);
//            Long senid = Long.valueOf(destPoint.get(SENID).toString());
//            Chardata cd = new Chardata(senid, lastTime);
//            cd.setS(2L);
//            cd.setV(val);
//            cd.setValuetype(Param.ValType.Special_V);
//            cd.setRundatatype(Param.RunDataType.RUN_RT);
//            return cd;
//        }
//
//    }
//
//    private Chardata calcByCurve(String curveId, Integer insertType, Map destPoint) {
//        Double v1, v2;
//        List<Double> values = new ArrayList<>();
//        Calendar now = Calendar.getInstance();
//        Long destSenId = Long.valueOf(destPoint.get(SENID).toString());
//        HashMap<String, Object> param = new HashMap<>();
//        param.put(Param.ExParamType.EX_INT, insertType);
//        param.put(Param.IdType.STR_IDARRAY, new String[]{curveId});
//        CZYDataCurve dataCurve = getDataCurve(param);
//        String hql = "from SlCalcVariate cvar where cvar.calc_senid=:calc_senid order by cvar.variate_name asc";
//        List<SlCalcVariate> variates = pubDao.find(hql, new String[]{"calc_senid"}, new Object[]{calc_senid});
//        Map<String, List<String>> varMap = new HashMap<>();
//        for (SlCalcVariate var : variates) {
//            if (var.getSenid() != null && var.getApp_type() != null) {
//                if (varMap.containsKey(var.getApp_type())) {
//                    varMap.get(var.getApp_type()).add(var.getSenid());
//                } else {
//                    ArrayList<String> list = new ArrayList<>();
//                    list.add(var.getSenid());
//                    varMap.put(var.getApp_type(), list);
//                }
//            }
//        }
//        if (varMap.isEmpty()) {
//            return null;
//        }
//        List<FetchParam> fetchParams = new ArrayList<>();
//        varMap.keySet().stream().forEach(key -> {
//            FetchParam fetchParam = new FetchParam(key);
//            fetchParam.idarrayStrings = varMap.get(key).toArray(new String[0]);
//            fetchParam.valtype = Param.ValType.Special_V;
//            fetchParam.rundatatype = Param.RunDataType.RUN_RTREAL;
//            fetchParams.add(fetchParam);
//        });
//        HashMap<Long, Chardata>[] resultData = imcDataService.getSpecialData(fetchParams.toArray(new FetchParam[0]));
//        /*
//         * if(businessResult.getIsSuccess() && businessResult.getData() !=null)
//         */
//        {
//            Calendar lastTime = null;
//            for (SlCalcVariate var : variates) {
//                Long senId = Long.valueOf(var.getSenid());
//                if (ToolsUtil.isEmpty(var.getSenid()) || senId.equals(destSenId)) {
//                    continue;
//                }
//                boolean exist = false;
//                for (HashMap<Long, Chardata> cmap : resultData) {
//                    if (exist = cmap.containsKey(senId)) {
//                        Chardata cd = cmap.get(senId);
//                        if (cd != null && now.getTimeInMillis() - cd.getTime().getTimeInMillis() < idleTime * 1000) {
//                            values.add(cd.getV());
//                        } else {
//                            // 无法计算
//                            // 直接return是否合适???
//                            return null;
//                        }
//                        if (lastTime == null || cd.getTime().after(lastTime)) {
//                            lastTime = cd.getTime();
//                        }
//                    }
//                }
//                if (!exist) {
//                    //是否合理???
//                    return null;
//                }
//            }
//            v1 = values.size() >= 1 ? values.get(0) : null;
//            v2 = values.size() >= 2 ? values.get(1) : null;
//            List<Chardata> cds = pubDao.getDSInsertFromCurve(dataCurve, v1, v2);
//            if (ToolsUtil.isEmpty(cds)) {
//                return null;
//            }
//            Chardata cd = cds.get(0);
//            if (cd != null) {
//                cd.setS(2L);
//                cd.setTime(lastTime);
//                cd.setSenid(destSenId);
//                cd.setValuetype(Param.ValType.Special_V);
//                cd.setRundatatype(RunDataType.RUN_RT);
//            }
//            return cd;
//        }
//    }
//
//
//    public void autoCalc() {
//        String hql = "from SlCalcPoint pt where pt.enable=1";
//        List<SlCalcPoint> waterPoints = baseDao.queryByHQL(hql, new Object[]{});
//        List<WaterCalcParam> params = new ArrayList<WaterCalcParam>();
//        for (SlCalcPoint dest : waterPoints) {
//            WaterCalcParam param = new WaterCalcParam();
//            param.setDestPoint(dest);
//            params.add(param);
//        }
//        calcPoint(params, true);
//    }
//
//    public List<Chardata> manualCalc(List<String> destId) {
//        StringBuilder hql = new StringBuilder("from SlCalcPoint pt where pt.enable=1 where pt.id in (");
//        List<Object> params = new ArrayList<>();
//        List<WaterCalcParam> calcParams = new ArrayList<WaterCalcParam>();
//        if (ToolsUtil.isEmpty(destId)) {
//            return null;
//        }
//        for (String id : destId) {
//            hql.append("?,");
//            params.add(id);
//        }
//        hql.substring(0, hql.length() - 1);
//        hql.append(")");
//        List<SlCalcPoint> waterpoints = baseDao.queryByHQL(hql.toString(), params.toArray());
//        for (int i = 0; i < destId.size(); i++) {
//            String id = destId.get(i);
//            for (SlCalcPoint dest : waterpoints) {
//                if (dest.getSenid().equals(id)) {
//                    WaterCalcParam cparam = new WaterCalcParam();
//                    cparam.setDestPoint(dest);
//                    calcParams.add(cparam);
//                }
//            }
//        }
//
//        return calcPoint(calcParams, false);
//    }
//
//    public List<Chardata> calcPoint(List<WaterCalcParam> calcParams, boolean auto) {
//        Map<String, List<Chardata>> cdsMap = new HashMap<>();
//        List<Chardata> retcds = new ArrayList<Chardata>();
//        HashMap<Long, Float> sendMap = new HashMap<>();
//        for (WaterCalcParam param : calcParams) {
//            SlCalcPoint point = param.getDestPoint();
//            String senId = point.getSenid();
//            String appType = point.getAppType();
//            String calcMode = point.getCalcMode();
//            Chardata cd = null;
//            // 公式
//            if (calcMode.equals("2")) {
//                String funcId = point.getCalcId();
//                Map<String, Object> destPoint = new HashMap<>();
//                destPoint.put(SENID, senId);
//                destPoint.put("appType", appType);
//                cd = calcByFunct(funcId, destPoint);
//
//            } // 曲线
//            else if (calcMode.equals("1")) {
//                Integer inserType = point.getInsertType() == null ? null : Integer.valueOf(point.getInsertType());
//                String curveId = point.getCalcId();
//                Map<String, Object> destPoint = new HashMap<>();
//                destPoint.put(SENID, senId);
//                destPoint.put("appType", appType);
//                cd = calcByCurve(curveId, inserType, destPoint);
//            }
//            if (cd != null) {
//                retcds.add(cd);
//                if (point.getAutoDispatch() == 1) {
//                    sendMap.put(cd.getSenid(), cd.getV().floatValue());
//                }
//                if (cdsMap.containsKey(appType)) {
//                    cdsMap.get(appType).add(cd);
//                } else {
//                    List<Chardata> cds = new ArrayList<Chardata>();
//                    cds.add(cd);
//                    cdsMap.put(appType, cds);
//                }
//            }
//        }
//        if (auto) {
//            cdsMap.entrySet().stream().forEach(entry -> {
//                String appType = entry.getKey();
//                FetchParam fetchParam = new FetchParam(appType);
//                fetchParam.rundatatype = Param.RunDataType.RUN_RT;
//                List<Chardata> cds = entry.getValue();
//                imcDataService.saveSpecialData(fetchParam, cds, null);
//            });
//
//            controlService.sendDataToNc(sendMap);
//        }
//        return retcds;
//    }
//}