package com.nari.slsd.msrv.waterdiversion.services;


import com.nari.slsd.hu.mplat.imc.client.Param.Param;
import com.nari.slsd.hu.mplat.imc.client.dto.Chardata;
import com.nari.slsd.hu.mplat.imc.client.dto.FetchParam;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrCurveService;
import com.nari.slsd.msrv.waterdiversion.mapper.secondary.WrCurveMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.CZYDataCurve;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrCurveOriginalDTO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrCurveTransDTO;
import com.nari.slsd.msrv.waterdiversion.model.secondary.po.WrCurvePointValue;
import com.nari.slsd.msrv.waterdiversion.param.InsertMethod;
import com.nari.slsd.msrv.waterdiversion.param.InsertType;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWdsHydroCurveService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WdsHydroCurveServicelmpl implements IWdsHydroCurveService {


	@Autowired
	private IWrCurveService wrCurveService;
	/**
	 *查询曲线类型对应的曲线号、曲线名称、维度

	@Override
	public List<WdsHydroCurveReponse> findCurveById(Long pid,Integer typeId) {

		List<WdsHydroCurve> list =  wdsHydroCurveRepository.findByPIdAndTypeId(pid, typeId);
		List<WdsHydroCurveReponse> curvelist = new ArrayList<WdsHydroCurveReponse>();
		for(WdsHydroCurve curves:list) {
			WdsHydroCurveReponse wdsHydroCurveRe = new WdsHydroCurveReponse();
			BeanUtils.copyProperties(curves, wdsHydroCurveRe);

			curvelist.add(wdsHydroCurveRe);
		}
		return curvelist;
	}*/
	/**
	 * 查询二维曲线的信息

	@Override
	public List<Map<String, Object>> findValueBydim2(long curveId,String x,String y) {
		List<WrCurvePointValue> curvedlist = new ArrayList<>();
		if (x.equals("v0")){
			curvedlist = wdsHydroCurvedRepository.findCurveidV0(curveId);
		}else if(x.equals("v1")){
			curvedlist = wdsHydroCurvedRepository.findCurveidV1(curveId);
		}else if(x.equals("v2")){
			curvedlist = wdsHydroCurvedRepository.findCurveidV2(curveId);
		}

		List<Map<String, Object>> allCurvedlist = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < curvedlist.size(); i++) {
			WrCurvePointValue wdsHydroCurved = curvedlist.get(i);
			Map<String, Object> maps = new HashMap<String, Object>();
			maps.put("v0", wdsHydroCurved.getId().getV0());
			maps.put("v1", wdsHydroCurved.getId().getV1());
			allCurvedlist.add(maps);
		}

		List<WdsHydroCurve> curvelist = wdsHydroCurveRepository.findById(curveId);
		List<Map<String, Object>> allCurvelist = new ArrayList<Map<String, Object>>();
		for (WdsHydroCurve wdsHydroCurve : curvelist) {
			map.put("curveId", wdsHydroCurve.getId());
			map.put("name", wdsHydroCurve.getName());
			map.put("dim", wdsHydroCurve.getDim());
			map.put("v0",wdsHydroCurve.getV0());
			map.put("v1",wdsHydroCurve.getV1());
			map.put("v", allCurvedlist);
			allCurvelist.add(map);
		}
		return allCurvelist;
	}*/
	/**
	 * 查询三维曲线的信息

	@Override
	public List<Map<String, Object>> findValueBydim3(long curId,String x,String y,String z) {
		List<WrCurvePointValue> curvedlist = new ArrayList<WrCurvePointValue>();
		//判断x的坐标为哪个则调哪个sql语句
		if(x.equals("v2")){
			curvedlist = wdsHydroCurvedRepository.findCurveidV2(curId);
		}else if(x.equals("v1")){
			curvedlist = wdsHydroCurvedRepository.findCurveidV1(curId);
		}else if(x.equals("v0")){
			curvedlist = wdsHydroCurvedRepository.findCurveidV0(curId);
		}
		Map<String, List<Map<String,Object>>> resultMap = wdsCurved(curvedlist,z);
		//获取有z坐标的值整合到set集合中
		Set<String> setV= resultMap.keySet();
		//将set中的元素转为Double类型然后添加到list中
		List<Double> lists = new ArrayList<>();
		for (String set:setV){
			Double d = Double.valueOf(set.toString());
			lists.add(d);
		}
		//将list中的值进行排序
		Collections.sort(lists);
		List<String> listV = new ArrayList<String>();
		for (Double set1:lists){
			listV.add(set1.toString());
		}
		List<WdsHydroCurve> curvelist = wdsHydroCurveRepository.findById(curId);
		//将两个接口的查询结果整合到一个map中
		Map<String, Object> map = new HashMap<String, Object>();
		for (WdsHydroCurve wdsHydroCurve : curvelist) {
			map.put("curveId", wdsHydroCurve.getId());
			map.put("name", wdsHydroCurve.getName());
			map.put("dim", wdsHydroCurve.getDim());
			map.put("v0",wdsHydroCurve.getV0());
			map.put("v1",wdsHydroCurve.getV1());
			map.put("v2", wdsHydroCurve.getV2());
			map.put("v",listV);
		}
		map.putAll(resultMap);

		//将整合好的map结构添加到resultlist中
		List<Map<String, Object>> resultlist = new ArrayList<Map<String, Object>>();
		resultlist.add(map);
		return resultlist;
	}
	//将三维曲线查询的值通过v0进行分组添加到map中
	public static Map<String, List<Map<String,Object>>> wdsCurved(List<WrCurvePointValue> curvedlist,String z){
		List<Map<String, Object>> curvedlists = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < curvedlist.size(); i++) {
			WrCurvePointValue wdsHydroCurved = curvedlist.get(i);
			Map<String, Object> maps = new HashMap<String, Object>();
			maps.put("v0", wdsHydroCurved.getId().getV0());
			maps.put("v1", wdsHydroCurved.getId().getV1());
			maps.put("v2", wdsHydroCurved.getId().getV2());
			curvedlists.add(maps);
		}
		Map<String, List<Map<String,Object>>>   resultMap =  new HashMap<String, List<Map<String,Object>>>();
		//判断z轴为哪个值再进行分组
		if(z.equals("v1")){
			resultMap = curvedlists.stream().collect(Collectors.groupingBy((Map m) -> m.get("v1").toString()));
		}else if(z.equals("v0")){
			resultMap = curvedlists.stream().collect(Collectors.groupingBy((Map m) -> m.get("v0").toString()));
		}else if(z.equals("v2")){
			resultMap = curvedlists.stream().collect(Collectors.groupingBy((Map m) -> m.get("v2").toString()));
		}
		return resultMap;
	}*/
	/**
	 * 曲线互查
	 */
	@Override
	public Map<String,Object> getDataByother(Map<String,Object> map) {
		Double v0 = null;
		Double v1= null;
		Long curveId = Long.valueOf( map.get("curveId").toString());

		//判断key中是否存在v0,v1,v2
		boolean v0Key = map.containsKey("v0");
		boolean v1Key = map.containsKey("v1");
		boolean v2Key = map.containsKey("v2");
		int othersV = 0;
		//根据所传的key,value判断查询方式与v0，v1的取值
		if(v0Key&&!v1Key&&!v2Key){
			//v0查v1
			othersV=1;
			v0 = Double.valueOf(map.get("v0").toString());
		}else if(v1Key&&!v0Key&&!v2Key){
			//v1查v0
			othersV=2;
			v0 = Double.valueOf(map.get("v1").toString());
		}else if(v0Key&&v1Key&&!v2Key){
			//v0,v1查v2
			othersV=11;
			v0 = Double.valueOf(map.get("v0").toString());
			v1= Double.valueOf(map.get("v1").toString());
		}else if(v0Key&&v2Key&&!v1Key){
			//v0,v2查v1
			othersV=12;
			v0 = Double.valueOf(map.get("v0").toString());
			v1= Double.valueOf(map.get("v2").toString());
		}else if(v1Key&&v2Key&&!v0Key){
			//v1,v2查v0
			othersV=13;
			v0 = Double.valueOf(map.get("v1").toString());
			v1= Double.valueOf(map.get("v2").toString());
		}
		HashMap<String, Object> params = new HashMap<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		List<Long> longs = new ArrayList<>();
		longs.add(curveId);
		params.put(Param.TimeType.EQ_BTIME,calendar);//所传时间
		params.put(Param.ExParamType.EX_INT,othersV);//查询类型
		params.put(Param.IdType.LONG_IDARRAY,longs.toArray());//所传测点
		CZYDataCurve czyDataCurve = getDataCurve(params);
		List<Chardata> chardatas = getDSInsertFromCurve(czyDataCurve,v0,v1);
		Double v = null;
		if(chardatas.size()>0) {
			Chardata chardata = chardatas.get(0);
			v = chardata.getV();
		}
		//将查询结果保留2位有效小数
		BigDecimal b = new BigDecimal(v);
		Double vs = b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
		Map<String,Object> mapv = new HashMap<String, Object>();
		mapv.put("v", vs);
		return mapv;
	}

	public synchronized CZYDataCurve getDataCurve(HashMap<String, Object> params) {

		Calendar time = (Calendar) params.get(Param.TimeType.EQ_BTIME);// 20170309时间过滤从wds_hydro_time_curved表取数
		CZYDataCurve dataCurve = new CZYDataCurve();
		String id = "";
		Integer exInt = (Integer) params.get(Param.ExParamType.EX_INT);
		Object[] ObjIdarray = (Object[]) params.get(Param.IdType.STR_IDARRAY);
		if (ObjIdarray.length > 0)
			id = (String) ObjIdarray[0];
		boolean existed = false;

		Object curveObject = null;// 20170309时间过滤从wds_hydro_time_curved表取数
		if (time == null)
//            curveObject = cacheMap.get(id.toString());
			if (curveObject != null) {
				existed = true;
				dataCurve = (CZYDataCurve) curveObject;
				dataCurve.setVisitTime(new Date());
			} else {
				dataCurve = new CZYDataCurve();
			}

		// 当前的插值类??曲线维数,根据exInt来确??
		switch (exInt) {
			case 1:
				dataCurve.setM_ItemNum(2);
				dataCurve.setCurrentInsertType(InsertType.D_1_2);
				break;
			case 2:
				dataCurve.setM_ItemNum(2);
				dataCurve.setCurrentInsertType(InsertType.D_2_1);
				break;
			case 11:
				dataCurve.setM_ItemNum(3);
				dataCurve.setCurrentInsertType(InsertType.D_12_3);

				break;
			case 12:
				dataCurve.setM_ItemNum(3);
				dataCurve.setCurrentInsertType(InsertType.D_13_2);

				break;
			case 13:
				dataCurve.setM_ItemNum(3);
				dataCurve.setCurrentInsertType(InsertType.D_23_1);

				break;
			default:
				dataCurve.setCurrentInsertType(InsertType.D_NULL);
				break;
		}

		if (existed)// 20170309时间函数不做限制
		{
			return dataCurve;
		}

		HashMap<String, Object> inmap = new HashMap<String, Object>();
		ArrayList<Long> idArrayList = new ArrayList<Long>();
		List resultList;
		Iterator iterator;
		//idArrayList.add(id);
		//String ids = idArrayList.toString();
		//String idin = ids.substring(1,ids.length()-1);
		WrCurveTransDTO wrCurTrans=wrCurveService.getCurve(id);
		if(wrCurTrans==null)
			return dataCurve;
		if (dataCurve.getM_ItemNum() == 3) {
			int size0=-99, size1 = -99, size2 = -99;
			size0 = (int) wrCurTrans.getOriginalData().stream().filter(obj -> obj.getV0() != null).count();
			if (size0<=0) {
				// throw new RuntimeException("no relative curved found!");
				return dataCurve;
			}

			size1 = (int) wrCurTrans.getOriginalData().stream().filter(obj -> obj.getV1() != null).count();
			if (size1<=0) {
				// throw new RuntimeException("no relative curved found!");
				return dataCurve;
			}

			size2 = (int) wrCurTrans.getOriginalData().stream().filter(obj -> obj.getV2() != null).count();
			if (size2<=0) {
				// throw new RuntimeException("no relative curved found!");
				return dataCurve;
			}

			int maxSize = Math.max(Math.max(size0, size1), size2);
			if (maxSize == size0) {
				dataCurve.getM_Col()[0] = 1;
				dataCurve.getM_Col()[1] = 2;
				dataCurve.getM_Col()[2] = 0;
				// dataCurve.getM_Col()[0] = 2;
				// dataCurve.getM_Col()[1] = 0;
				// dataCurve.getM_Col()[2] = 1;
			}
			if (maxSize == size1) {
				dataCurve.getM_Col()[0] = 0;
				dataCurve.getM_Col()[1] = 2;
				dataCurve.getM_Col()[2] = 1;
			}
			if (maxSize == size2) {
				dataCurve.getM_Col()[0] = 0;
				dataCurve.getM_Col()[1] = 1;
				dataCurve.getM_Col()[2] = 2;
			}

			String sv;
			sv = ("v" + dataCurve.getM_Col()[0]);

			for (WrCurveOriginalDTO curveOriginalDTO:wrCurTrans.getOriginalData()) {
				Double d=null;
				if(sv.equals("v0"))
					d=curveOriginalDTO.getV0()==null?null:Double.valueOf(curveOriginalDTO.getV0());
				else if(sv.equals("v1"))
					d=curveOriginalDTO.getV1()==null?null:Double.valueOf(curveOriginalDTO.getV1());
				else if(sv.equals("v2"))
					d=curveOriginalDTO.getV2()==null?null:Double.valueOf(curveOriginalDTO.getV2());
				dataCurve.getM_v0().add(d);
				dataCurve.getM_v0_col().add(0);
			}

			sv = ("v" + dataCurve.getM_Col()[1]);

			for (WrCurveOriginalDTO curveOriginalDTO:wrCurTrans.getOriginalData()) {
				Double d=null;
				if(sv.equals("v0"))
					d=curveOriginalDTO.getV0()==null?null:Double.valueOf(curveOriginalDTO.getV0());
				else if(sv.equals("v1"))
					d=curveOriginalDTO.getV1()==null?null:Double.valueOf(curveOriginalDTO.getV1());
				else if(sv.equals("v2"))
					d=curveOriginalDTO.getV2()==null?null:Double.valueOf(curveOriginalDTO.getV2());
				dataCurve.getM_v1().add(d);
				dataCurve.getM_v1_row().add(0);
			}
/*
			inmap.remove(Param.ExParamType.EX_SQL);
			int row, col;
			sv = "v" + dataCurve.getM_Col()[0] + ",v" + dataCurve.getM_Col()[1] + ",v" + dataCurve.getM_Col()[2];
			sql = "select " + sv + " from pubuser.wds_hydro_curved where curveid in("+idin+") " + " and v"
					+ dataCurve.getM_Col()[0] + ">=0" + " and" + " v" + dataCurve.getM_Col()[1] + ">=0" + " and"
					+ " v" + dataCurve.getM_Col()[2] + ">=0" + " order by v" + dataCurve.getM_Col()[0] + ",v"
					+ dataCurve.getM_Col()[1];
			inmap.put(Param.ExParamType.EX_SQL, sql);
			Query query5 = entityManager.createNativeQuery(sql);
			resultList = query5.getResultList();

			if (resultList == null || resultList.size() < 1) {
				return null;
			}

			System.out.println("sql" + sql);*/
			int row, col;
			Double matrix[][] = new Double[dataCurve.getM_v0().size()][dataCurve.getM_v1().size()];

			int nRow = dataCurve.getM_v0().size();
			int nCol = dataCurve.getM_v1().size();

			for (row = 0; row < nRow; row++) {
				for (col = 0; col < nCol; col++) {
					matrix[row][col] = -99.0;
					// matrix[row][col] = 0.0;
				}
			}
			dataCurve.setMatrix(matrix);
			for (int i = 0; i < wrCurTrans.getOriginalData().size(); i++) {
				// String strTmp = resultList.get(i).toString();
				WrCurveOriginalDTO originalDTO = (WrCurveOriginalDTO) wrCurTrans.getOriginalData().get(i);
				double d0 = -1, d1 = -1, d2 = -1;

				if (originalDTO.getV0()!=null &&originalDTO.getV1()!=null && originalDTO.getV2()!=null) {
					d0 = Double.valueOf(originalDTO.getV0());
					d1 = Double.valueOf(originalDTO.getV1());
					d2 = Double.valueOf(originalDTO.getV2());
				}

				row = dataCurve.getM_v0().indexOf(d0);
				col = dataCurve.getM_v1().indexOf(d1);
				// 有重复的项值自相关
				if (matrix[row][col] >= 0) {
					dataCurve.getSpecialv0List().add(d0);
					dataCurve.getSpecialv1List().add(d1);
				}
				matrix[row][col] = d2;// dw=1表示是有??

				dataCurve.getM_v0_col().set(row, dataCurve.getM_v0_col().get(row) + 1);
				dataCurve.getM_v1_row().set(col, dataCurve.getM_v1_row().get(col) + 1);
			}
			dataCurve.setM_SupplementWay(dataCurve.getM_v0().size() >= dataCurve.getM_v1().size() ? 0 : 1);
			dataCurve.setCurrentInsertMethod(InsertMethod.M_3_LINE);
		} else if (dataCurve.getM_ItemNum() == 2) {
				// throw new RuntimeException("????查询的曲线数据不存在，无法进行插值计算！");
				for (WrCurveOriginalDTO d : wrCurTrans.getOriginalData()) {
					dataCurve.getM_v0().add(Double.valueOf(d.getV0()));
					dataCurve.getM_v1().add(Double.valueOf(d.getV0()));
				}

		}
		dataCurve.setM_id(id);
		dataCurve.setM_bHaveInit(true);
		dataCurve.setVisitTime(new Date());
//        if (time == null) {
//            if (cacheMap.keySet().size() >= 100) {
//                String cid = getOldestCacheCurveID();
//                cacheMap.remove(cid);
//            }
//
//            // 陈意修改 2018年7月19日，小浪底曲线数据经常修改，不能缓存
//            if (isCache)
//                cacheMap.put(id.toString(), dataCurve);
//        }

		return dataCurve;
	}

	/**
	 * 根据参数（params）中的曲线�?已有的维值获取另�?��插�? 使用方法�? params.put("DataCurve",CZYDataCurve
	 * dataCurve) 压入数据曲线 params.put("v0",double) 压入第一维数�? params.put("v1",double)
	 * 压入第二维数�?如果�?维的�?
	 *
	 * @param
	 * @return 如果参数不正确，返回null
	 */
	public  List<Chardata> getDSInsertFromCurve(CZYDataCurve dataCurve, Double v0, Double v1) {
		// Integer exInt = (Integer)params.get(Param.ExParamType.EX_INT);
		// CZYDataCurve dataCurve = (CZYDataCurve)params.get("DataCurve");

		if (v0 == null || Double.isInfinite(v0)) {
			return null;
		}

		if (dataCurve == null)
			return null;

		if (dataCurve.getCurrentInsertType() == InsertType.D_NULL) {
			return null;
		}
		// 有曲线定义，无曲线数据
		if (dataCurve.getM_v0() == null || dataCurve.getM_v0().size() == 0)
			return null;

		ArrayList<Chardata> resultList = new ArrayList<Chardata>();

		if (dataCurve.getM_ItemNum() == 3) {
			if (v1 == null || v1.isInfinite())
				return null;
			if (dataCurve.getM_v0_col() == null || dataCurve.getM_v0_col().size() == 0)
				return null;
			if (dataCurve.getM_v1() == null || dataCurve.getM_v1().size() == 0 || dataCurve.getM_v1_row() == null
					|| dataCurve.getM_v1_row().size() == 0)
				return null;
			Double rt = Insert3(dataCurve, v0, v1);
			if (rt != null) {
				Chardata chardata = new Chardata(1L, null);

				if (rt < 0)
					rt = 0D;

				chardata.setV(rt);
				resultList.add(chardata);
			}
		} else if (dataCurve.getM_ItemNum() == 2) {

			Double rt = Insert2(v0, dataCurve);
			if (rt != null) {
				Chardata chardata = new Chardata(1L, null);
				chardata.setV(rt);
				resultList.add(chardata);
			}
		}
		return resultList;
	}

	private static double Insert3(CZYDataCurve dataCurve, double data1, double data2) {
		double rt = 0;

		int[] cola = new int[3];
		switch (dataCurve.getCurrentInsertType()) {
			case D_12_3:
				cola[0] = 0;
				cola[1] = 1;
				cola[2] = 2;
				break;
			case D_13_2:
				cola[0] = 0;
				cola[1] = 2;
				cola[2] = 1;
				break;
			case D_23_1:
				cola[0] = 1;
				cola[1] = 2;
				cola[2] = 0;
				break;
			default:
				return rt;
		}

		double _data1 = data1;
		double _data2 = data2;

		double y0 = 0, y1 = 0;
		int row, col;

		int[] m_Col = dataCurve.getM_Col();
		Double[][] matrix = dataCurve.getMatrix();

		if (cola[2] == m_Col[2]) // 1,2->3
		{
			int index = dataCurve.getSpecialv0List().indexOf(data1);
			if (index >= 0) {
				return dataCurve.getSpecialv0List().get(index);
			}

			if (m_Col[0] > m_Col[1]) {
				_data1 = data2;
				_data2 = data1;
			}

			IntWrap row0 = new IntWrap();
			row0.i = 0;
			IntWrap row1 = new IntWrap();
			row1.i = 0;
			_arrySearchBound1(dataCurve.getM_v0(), _data1, row0, row1);
			IntWrap col0 = new IntWrap();
			col0.i = 0;
			IntWrap col1 = new IntWrap();
			col1.i = 0;
			_arrySearchBound1(dataCurve.getM_v1(), _data2, col0, col1);

			// int colCount0=m_v0_col.get(row0.i);
			// int colCount1=m_v0_col.get(row1.i);

			// 插补数据
			_Supplement(row0.i, true, dataCurve);
			_Supplement(row1.i, true, dataCurve);
			_Supplement(col0.i, false, dataCurve);
			_Supplement(col1.i, false, dataCurve);

			col = col0.i;
			y0 = _mathInsertPT(dataCurve.getM_v0().get(row0.i), matrix[row0.i][col], dataCurve.getM_v0().get(row1.i),
					matrix[row1.i][col], _data1);

			col = col1.i;
			y1 = _mathInsertPT(dataCurve.getM_v0().get(row0.i), matrix[row0.i][col], dataCurve.getM_v0().get(row1.i),
					matrix[row1.i][col], _data1);

			rt = _mathInsertPT(dataCurve.getM_v1().get(col0.i), y0, dataCurve.getM_v1().get(col1.i), y1, _data2);
		} else if (cola[2] == m_Col[0]) // 2,3->1
		{
			int index = dataCurve.getSpecialv1List().indexOf(data1);
			if (index >= 0) {
				return dataCurve.getSpecialv0List().get(index);
			}
			if (m_Col[1] > m_Col[2]) {
				_data1 = data2;
				_data2 = data1;
			}

			IntWrap col0 = new IntWrap();
			col0.i = 0;
			IntWrap col1 = new IntWrap();
			col1.i = 0;
			_arrySearchBound1(dataCurve.getM_v1(), _data1, col0, col1);

			// 插补数据
			_Supplement(col0.i, false, dataCurve);
			_Supplement(col1.i, false, dataCurve);

			int rowCount = Math.min(dataCurve.getM_v1_row().get(col0.i), dataCurve.getM_v1_row().get(col1.i));
			List<Double> v2 = new ArrayList<Double>();
			for (row = 0; row < rowCount; row++) {
				double d0 = matrix[row][col0.i];
				double d1 = matrix[row][col1.i];
				double dd = _mathInsertPT(dataCurve.getM_v1().get(col0.i), d0, dataCurve.getM_v1().get(col1.i), d1,
						_data1);
				v2.add(dd);
			}

			rt = _mathInsertLine(v2, dataCurve.getM_v0(), _data2);
		} else // cola[2]==m_Col[1] 1,3->2
		{
			int index = dataCurve.getSpecialv0List().indexOf(data1);
			if (index >= 0) {
				return dataCurve.getSpecialv1List().get(index);
			}
			if (m_Col[0] > m_Col[2]) {
				_data1 = data2;
				_data2 = data1;
			}

			IntWrap row0 = new IntWrap();
			row0.i = 0;
			IntWrap row1 = new IntWrap();
			row1.i = 0;
			_arrySearchBound1(dataCurve.getM_v0(), _data1, row0, row1);

			// 插补数据
			_Supplement(row0.i, true, dataCurve);
			_Supplement(row1.i, true, dataCurve);

			List<Double> v2 = new ArrayList<Double>();
			// int colCount = Math.min(dataCurve.getM_v0_col().get(row0.i),
			// dataCurve.getM_v0_col().get(row1.i));
			int colCount = Math.min(matrix[row0.i].length, matrix[row1.i].length);

			for (col = 0; col < colCount; col++) {
				double d0 = matrix[row0.i][col];
				double d1 = matrix[row1.i][col];
				double dd = _mathInsertPT(dataCurve.getM_v0().get(row0.i), d0, dataCurve.getM_v0().get(row1.i), d1,
						_data1);
				v2.add(dd);
			}

			rt = _mathInsertLine(v2, dataCurve.getM_v1(), _data2);
		}

		return rt;
	}

	/**
	 * 二维插�?函数
	 *
	 * @param val       实际�?
	 * @param dataCurve 曲线
	 * @return 返回插�?结果
	 */
	private static double Insert2(double val, CZYDataCurve dataCurve) {
		Double retV = -1.0;
		double biggerV0, biggerV1, smallerV0, smallerV1;

		int index_bigger = -1;
		int index_littleBigger = -1;
		int index_smaller = -1;
		int index_littleSmaller = -1;

		double biggerPrepared = Double.MAX_VALUE;
		double smallerPrepared = Double.MIN_VALUE;
		int index_biggerPrepared = -1;
		int index_smallerPrepared = -1;

		switch (dataCurve.getCurrentInsertType()) {
			case D_1_2:
				double tempV0_big = Double.MAX_VALUE;
				double tempV0_small = Double.MIN_VALUE;
				for (int i = 0; i < dataCurve.getM_v0().size(); i++) {
					double temp = dataCurve.getM_v0().get(i);
					if (temp == val) {
						return dataCurve.getM_v1().get(i);
					}
					if (index_bigger != -1 && temp > tempV0_big && temp < biggerPrepared) {
						biggerPrepared = temp;
						index_biggerPrepared = i;
					}
					if (index_smaller != -1 && temp < tempV0_small && temp > smallerPrepared) {
						smallerPrepared = temp;
						index_smallerPrepared = i;
					}
					if (temp > val && temp < tempV0_big) {
						index_littleBigger = index_bigger;
						tempV0_big = temp;
						index_bigger = i;
					}
					if (temp < val && temp > tempV0_small) {
						index_littleSmaller = index_smaller;
						tempV0_small = temp;
						index_smaller = i;
					}
				}

				// 没有找到比给定�?大的�?
				if (index_bigger == -1 && index_smaller != -1 && index_littleSmaller != -1) {
					biggerV0 = dataCurve.getM_v0().get(index_littleSmaller);
					biggerV1 = dataCurve.getM_v1().get(index_littleSmaller);
					smallerV0 = tempV0_small;
					smallerV1 = dataCurve.getM_v1().get(index_smaller);

					retV = biggerV1 + (val - biggerV0) * (biggerV1 - smallerV1) / (biggerV0 - smallerV0);
					// return retV;
				}
				// 没有找到比给定�?更小的�?
				else if (index_smaller == -1 && index_bigger != -1 && index_littleBigger != -1) {
					biggerV0 = tempV0_big;
					biggerV1 = dataCurve.getM_v1().get(index_bigger);
					smallerV0 = dataCurve.getM_v0().get(index_littleBigger);
					smallerV1 = dataCurve.getM_v1().get(index_littleBigger);

					retV = smallerV1 - (biggerV1 - smallerV1) * (smallerV0 - val) / (biggerV0 - smallerV0);
					// return retV;
				}
				// 都找到了
				else if (index_smaller != -1 && index_bigger != -1) {
					biggerV0 = tempV0_big;
					biggerV1 = dataCurve.getM_v1().get(index_bigger);
					smallerV0 = tempV0_small;
					smallerV1 = dataCurve.getM_v1().get(index_smaller);

					retV = smallerV1 + (biggerV1 - smallerV1) * (val - smallerV0) / (biggerV0 - smallerV0);
				}
				// 没有找到比给定�?更小的�?,�?��用到准备好的第二大的�?
				else if (index_bigger != -1 && index_biggerPrepared != -1) {
					smallerV0 = dataCurve.getM_v0().get(index_bigger);
					smallerV1 = dataCurve.getM_v1().get(index_bigger);
					biggerV0 = dataCurve.getM_v0().get(index_biggerPrepared);
					biggerV1 = dataCurve.getM_v1().get(index_biggerPrepared);
					retV = smallerV1 - (biggerV1 - smallerV1) * (smallerV0 - val) / (biggerV0 - smallerV0);
				}
				// 没有找到比给定�?更大的�?,�?��用到准备好的第二小的�?
				else if (index_smaller != -1 && index_smallerPrepared != -1) {
					smallerV0 = dataCurve.getM_v0().get(index_smallerPrepared);
					smallerV1 = dataCurve.getM_v1().get(index_smallerPrepared);
					biggerV0 = dataCurve.getM_v0().get(index_smaller);
					biggerV1 = dataCurve.getM_v1().get(index_smaller);
					retV = biggerV1 + (val - biggerV0) * (biggerV1 - smallerV1) / (biggerV0 - smallerV0);
				} else {
					retV = -1.0;
				}

				break;
			case D_2_1:
				double tempV1_big = Double.MAX_VALUE;
				double tempV1_small = Double.MIN_VALUE;
				for (int i = 0; i < dataCurve.getM_v1().size(); i++) {
					double temp = dataCurve.getM_v1().get(i);
					if (temp == val) {
						return dataCurve.getM_v0().get(i);
					}
					if (index_bigger != -1 && temp > tempV1_big && temp < biggerPrepared) {
						biggerPrepared = temp;
						index_biggerPrepared = i;
					}
					if (index_smaller != -1 && temp < tempV1_small && temp > smallerPrepared) {
						smallerPrepared = temp;
						index_smallerPrepared = i;
					}
					if (temp > val && temp < tempV1_big) {
						index_littleBigger = index_bigger;
						tempV1_big = temp;
						index_bigger = i;
					}
					if (temp < val && temp > tempV1_small) {
						index_littleSmaller = index_smaller;
						tempV1_small = temp;
						index_smaller = i;
					}
				}

				// 没有找到比给定�?大的�?
				if (index_bigger == -1 && index_smaller != -1 && index_littleSmaller != -1) {
					biggerV0 = dataCurve.getM_v0().get(index_littleSmaller);
					biggerV1 = dataCurve.getM_v1().get(index_littleSmaller);
					smallerV1 = tempV1_small;
					smallerV0 = dataCurve.getM_v0().get(index_smaller);

					retV = biggerV0 + (val - biggerV1) * (biggerV0 - smallerV0) / (biggerV1 - smallerV1);
					// return retV;
				}
				// 没有找到比给定�?更小的�?
				else if (index_smaller == -1 && index_bigger != -1 && index_littleBigger != -1) {
					biggerV1 = tempV1_big;
					biggerV0 = dataCurve.getM_v0().get(index_bigger);
					smallerV0 = dataCurve.getM_v0().get(index_littleBigger);
					smallerV1 = dataCurve.getM_v1().get(index_littleBigger);

					retV = smallerV0 - (smallerV1 - val) * (biggerV0 - smallerV0) / (biggerV1 - smallerV1);
					// return retV;
				}
				// 都找到了
				else if (index_smaller != -1 && index_bigger != -1) {
					biggerV1 = tempV1_big;
					biggerV0 = dataCurve.getM_v0().get(index_bigger);
					smallerV1 = tempV1_small;
					smallerV0 = dataCurve.getM_v0().get(index_smaller);

					retV = smallerV0 + (val - smallerV1) * (biggerV0 - smallerV0) / (biggerV1 - smallerV1);
				}
				// 没有找到比给定�?更小的�?,�?��用到准备好的第二大的�?
				else if (index_bigger != -1 && index_biggerPrepared != -1) {
					smallerV0 = dataCurve.getM_v0().get(index_bigger);
					smallerV1 = dataCurve.getM_v1().get(index_bigger);
					biggerV0 = dataCurve.getM_v0().get(index_biggerPrepared);
					biggerV1 = dataCurve.getM_v1().get(index_biggerPrepared);
					retV = smallerV0 - (smallerV1 - val) * (biggerV0 - smallerV0) / (biggerV1 - smallerV1);
				}
				// 没有找到比给定�?更大的�?,�?��用到准备好的第二小的�?
				else if (index_smaller != -1 && index_smallerPrepared != -1) {
					smallerV0 = dataCurve.getM_v0().get(index_smallerPrepared);
					smallerV1 = dataCurve.getM_v1().get(index_smallerPrepared);
					biggerV0 = dataCurve.getM_v0().get(index_smaller);
					biggerV1 = dataCurve.getM_v1().get(index_smaller);
					retV = biggerV0 + (val - biggerV1) * (biggerV0 - smallerV0) / (biggerV1 - smallerV1);
				} else {
					retV = -1.0;
				}
				/*
				 * biggerV1 = tempV1_big; biggerV0 = dataCurve.getM_v0().get(index_bigger);
				 * smallerV1 = tempV1_small; smallerV0 = dataCurve.getM_v0().get(index_smaller);
				 *
				 * retV = smallerV0 + (val-smallerV1)*(biggerV0-smallerV0)/(biggerV1-smallerV1);
				 */
				break;
		}

		/*
		 * if(UnvCalc.jw.db_type=="Sqlserver") { String sql =
		 * "select wds.Interpolation_2Dms("+val+","+CurveID+","+direction; String strRet
		 * = UnvCalc.jw.submitRequest(3, sql, null); retV = Double.valueOf(strRet); }
		 * if(UnvCalc.jw.db_type=="Oracle") {
		 *
		 * }
		 */

		if (retV != null && retV < 0)
			retV = 0D;

		return retV;
	}

	private static double _mathInsertLine(List<Double> xa, List<Double> ya, double x) {
		if (xa.size() != ya.size())
			return -1;

		double y = 0;
		IntWrap i0 = new IntWrap();
		IntWrap i1 = new IntWrap();
		if (!_arrySearchBound1(xa, x, i0, i1))
			return y;
		y = _mathInsertPT(xa.get(i0.i), ya.get(i0.i), xa.get(i1.i), ya.get(i1.i), x);
		return y;
	}

	// 插�?-四点
	private static double _mathInsertPT(double x0, double y0, double x1, double y1, double x) {
		double y = 0;
		if (x0 == x1) {
			y = (y0 + y1) / 2;
			return y;
		}

		if (Math.abs(x1 - x0) < 0.0001)
			y = y0;
		else
			y = y0 + (x - x0) * (y1 - y0) / (x1 - x0);
		return y;
	}

	private static boolean _Supplement(int rc, boolean bRow, CZYDataCurve dataCurve) {
		if (bRow)// 补充�?
		{
			int colcount = dataCurve.getM_v1().size();
			int v0size = dataCurve.getM_v0_col().size();
			if (v0size <= rc || rc < 0)
				return true;
			if (dataCurve.getM_v0_col().get(rc) >= colcount)
				return true;
			for (int col = 0; col < colcount; col++)
				if (!_SupplementCell(rc, col, dataCurve))
					return false;
		} else// 补充�?
		{
			int rowcount = dataCurve.getM_v0().size();
			int v1size = dataCurve.getM_v1_row().size();
			if (v1size <= rc || rc < 0)
				return true;
			if (dataCurve.getM_v1_row().get(rc) >= rowcount)
				return true;
			for (int row = 0; row < rowcount; row++)
				if (!_SupplementCell(row, rc, dataCurve))
					return false;
		}

		return true;
	}

	private static boolean _SupplementCell(int row, int col, CZYDataCurve dataCurve) {
		Double[][] matrix = dataCurve.getMatrix();

		double v = matrix[row][col];
		if (v > 0)
			return true;// 是有效�?
		// m_SupplementWay
		switch (dataCurve.getM_SupplementWay()) {
			case 0: {
				int row0 = row - 1;
				int row1 = row + 1;
				while (row0 >= 0) {
					// if (matrix[row0][col] > 0)
					if (matrix[row0][col] >= 0)
						break;
					row0--;
				}
				if (row0 < 0) {
					row0 = 0;
					while (row0 < dataCurve.getM_v0().size() - 1) {
						// if (matrix[row0][col] > 0)
						if (matrix[row0][col] >= 0)
							break;
						row0++;
					}
				}
				while (row1 < dataCurve.getM_v0().size()) {
					// if (matrix[row1][col] > 0)
					if (matrix[row1][col] >= 0)
						break;
					row1++;
				}
				if (row1 >= dataCurve.getM_v0().size()) {
					row1 = dataCurve.getM_v0().size() - 1;
					while (row1 > 0) {
						if (matrix[row1][col] >= 0)
							break;
						row1--;
					}
				}
				// if(row0<0) {row0=row1;}
				int tempRow = row1 + 1;
				if (row0 == row1) {
					while (tempRow < (dataCurve.getM_v0().size())) {
						if (matrix[tempRow][col] < 0) {
							tempRow++;
						} else {
							break;
						}
					}
					if (tempRow < dataCurve.getM_v0().size())
						row1 = tempRow;
				}
				if (row1 == row0) {
					tempRow = row0 - 1;
					while (tempRow >= 0) {
						if (matrix[tempRow][col] < 0) {
							tempRow--;
						} else {
							break;
						}
					}
					if (tempRow >= 0) {
						row0 = tempRow;
					}
				}

				matrix[row][col] = _mathInsertPT(dataCurve.getM_v0().get(row0), matrix[row0][col],
						dataCurve.getM_v0().get(row1), matrix[row1][col], dataCurve.getM_v0().get(row));
				// pcell->dword=2;//设为有效 and 插补
				dataCurve.getM_v0_col().set(row, dataCurve.getM_v0_col().get(row) + 1);
			}
			break;
			case 1: {
				int col0 = col - 1;
				int col1 = col + 1;
				while (col0 >= 0) {
					// if (matrix[row][col0] > 0)
					if (matrix[row][col0] >= 0)
						break;
					col0--;
				}
				if (col0 < 0) {
					col0 = 0;
					while (col0 < dataCurve.getM_v1().size() - 1) {
						// if (matrix[row][col0] > 0)
						if (matrix[row][col0] >= 0)
							break;
						col0++;
					}
				}
				while (col1 < dataCurve.getM_v1().size()) {
					// if (matrix[row][col1] > 0)
					if (matrix[row][col1] >= 0)
						break;
					col1++;
				}
				if (col1 >= dataCurve.getM_v1().size()) {
					col1 = dataCurve.getM_v1().size() - 1;
					// while (col1 > 0) {
					while (col1 >= 0) {
						if (matrix[row][col1] >= 0)
							break;
						col1--;
					}
				}

				int tmpCol = col1 + 1;
				if (col1 == col0) {
					while (tmpCol < (dataCurve.getM_v1().size())) {
						if (matrix[row][tmpCol] < 0) {
							tmpCol++;
						} else {
							break;
						}
					}
					if (tmpCol < dataCurve.getM_v1().size())
						col1 = tmpCol;
				}
				if (col1 == col0) {
					tmpCol = col0 - 1;
					while (tmpCol >= 0) {
						if (matrix[row][tmpCol] < 0) {
							tmpCol--;
						} else {
							break;
						}
					}
					if (tmpCol >= 0) {
						col0 = tmpCol;
					}
				}
				// if (col1 >= dataCurve.getM_v1().size()) {
				// col1 = col0;
				// }
				// if(col0>0) {col0--;}

				matrix[row][col] = _mathInsertPT(dataCurve.getM_v1().get(col0), matrix[row][col0],
						dataCurve.getM_v1().get(col1), matrix[row][col1], dataCurve.getM_v1().get(col));
				// pcell->dword=2;//设为有效 and 插补
				dataCurve.getM_v1_row().set(col, dataCurve.getM_v1_row().get(col) + 1);
			}
			break;
		}

		// 本行有效值增�?
		// dataCurve.getM_v0_col().set(row, dataCurve.getM_v0_col().get(row) + 1);
		// 本列有效值增�?
		// dataCurve.getM_v1_row().set(col, dataCurve.getM_v1_row().get(col) + 1);

		return true;
	}

	private static boolean _arrySearchBound1(List<Double> ar, double data, IntWrap low, IntWrap high) {
		if (ar.size() == 0) {
			low.i = high.i = -1;
			return false;
		}
		if (ar.size() == 1) {
			low.i = high.i = 0;
			return true;
		}
		if (ar.size() == 2) {
			low.i = 0;
			high.i = 1;
			return true;
		}
		low.i = 0;
		high.i = ar.size() - 1;
		_arrySearchBound(ar, data, low, high);
		return true;
	}

	private static void _arrySearchBound(List<Double> ar, double data, IntWrap low, IntWrap high) {
		if ((high.i - low.i) <= 1)
			return;

		int mid = (low.i + high.i) / 2;
		if ((ar.get(low.i) < ar.get(high.i) && data > ar.get(mid))
				|| (ar.get(low.i) > ar.get(high.i) && data < ar.get(mid))) {
			low.i = mid;
		} else {
			high.i = mid;
		}
		_arrySearchBound(ar, data, low, high);
	}
	static class IntWrap {
		public int i;
	}

/*
	public  HashMap<Long, List<WrCurvePointValue>> getCurved(FetchParam inparam) {

		String ids = Arrays.asList(inparam.idarrayLongs).toString();

		String sql = "select * from pubuser.wds_hydro_curved where curveid in("+ids.substring(1,ids.length()-1) +") ";
		Query query1 = entityManager.createNativeQuery(sql,WrCurvePointValue.class);

		return curvedresultToHashMap((List<WrCurvePointValue>)query1.getResultList());
	}
*/

	public static HashMap<String, List<WrCurvePointValue>> curvedresultToHashMap(List<WrCurvePointValue> charalist)
	{
		HashMap<String, List<WrCurvePointValue>> outmap = new HashMap<String, List<WrCurvePointValue>>();
		String newid = "";
		// 记录站号变化位置
		int j = 0;
		if(charalist.size() == 1)
		{
			WrCurvePointValue sp1 = charalist.get(0);
			newid = sp1.getCurveId();
		}
		for(int i = 0; i < charalist.size() - 1; i++)
		{
			WrCurvePointValue sp1 = charalist.get(i);
			WrCurvePointValue sp2 = charalist.get(i + 1);
			newid = sp2.getCurveId();
			// 如果后一条记录和前一条记录站号相同，则继续向后，找到不同记录的位置
			if(sp1.getCurveId().equals(sp2.getCurveId()))
				continue;
			else
			{
				List<WrCurvePointValue> aryid = new ArrayList<WrCurvePointValue>();
				// for(SpanData spanData:splist)
				// 记录列表中站号开始变化的位置和新站号

				for(int k = j; k <= i; k++)
				{
					aryid.add(charalist.get(k));
					// 记录变化站号的位置
				}
				j = i + 1;
				newid = sp2.getCurveId();
				outmap.put(sp1.getCurveId(), aryid);
			}

		}
		List<WrCurvePointValue> aryid = new ArrayList<WrCurvePointValue>();
		for(int k = j; k < charalist.size(); k++)
		{
			aryid.add(charalist.get(k));
		}
		if(!newid.isEmpty())
			outmap.put(newid, aryid);
		// 最后一组
		return outmap;
	}
/*
	public static Criterion[] combCreterion(List[] objlist)
	{

		ArrayList<String> propertyNames = new ArrayList<String>();
		ArrayList<String> condition= new ArrayList<String>();
		ArrayList<Object> propertyValue= new ArrayList<Object>();

		if(objlist==null)
			return null;
		for(List litemp:objlist)
		{
			if(litemp==null) continue;
			for(int i=0;i<litemp.size();i=i+3)
			{
				propertyNames.add(litemp.get(i).toString());
				condition.add(litemp.get(i+1).toString());
				propertyValue.add(litemp.get(i+2));

			}
		}

		String[] proStr=new String[propertyNames.size()];
		propertyNames.toArray(proStr);

		String[] condStr=new String[condition.size()];
		condition.toArray(condStr);

		Object[] proObj=new Object[propertyValue.size()];
		propertyValue.toArray(proObj);
		Criterion[] criterions=createCriterion(proStr, condStr, proObj);
		return criterions;
	}


	/**
	 * 组装Criterion数组的工具方法
	 *
	 * @param propertyNames
	 *            属性名称
	 * @param condition
	 *            比较条件
	 * @param propertyValue
	 *            属性值
	 * @return
	 *//*
	public static Criterion[] createCriterion(String[] propertyNames, String[] condition, Object[] propertyValue) {
		if (propertyNames.length == condition.length && propertyNames.length == condition.length) {

			Criterion[] criterion = new Criterion[propertyNames.length];

			for (int i = 0; i < propertyNames.length; i++) {
				if (condition[i].equals("="))
					criterion[i] = Restrictions.eq(propertyNames[i], propertyValue[i]);
				else if (condition[i].equals(">="))
					criterion[i] = Restrictions.ge(propertyNames[i], propertyValue[i]);
				else if (condition[i].equals("<="))
					criterion[i] = Restrictions.le(propertyNames[i], propertyValue[i]);
				else if (condition[i].equals(">"))
					criterion[i] = Restrictions.gt(propertyNames[i], propertyValue[i]);
				else if (condition[i].equals("<"))
					criterion[i] = Restrictions.lt(propertyNames[i], propertyValue[i]);
				else if (condition[i].equals("like"))
					criterion[i] = Restrictions.like(propertyNames[i], propertyValue[i]);
				else if (condition[i].equals("in"))
					criterion[i] = Restrictions.in(propertyNames[i], (Object[]) propertyValue[i]);
			}
			return criterion;
		} else {
			return null;
		}
	}*/
}

