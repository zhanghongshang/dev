package com.nari.slsd.msrv.waterdiversion.model.dto;

import com.nari.slsd.msrv.waterdiversion.param.InsertMethod;
import com.nari.slsd.msrv.waterdiversion.param.InsertType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据曲线
 * 曲线插值所需数据
 * @author Chenyi
 *
 */
public class CZYDataCurve implements Serializable {

	InsertType currentInsertType;
	
	InsertMethod currentInsertMethod;
	
	Date visitTime = null;
	
	List<Double> m_v0 = new ArrayList<Double>();
	List<Double> m_v1 = new ArrayList<Double>();
	//CZYMatrix<double>		m_v2;
	List<Integer> m_v0_col = new ArrayList<Integer>();
	List<Integer> m_v1_row = new ArrayList<Integer>();
	
	List<Double> specialv0List = new ArrayList<Double>();
	List<Double> specialv1List = new ArrayList<Double>();
	
	
	
	public List<Double> getSpecialv0List() {
		return specialv0List;
	}

	public void setSpecialv0List(List<Double> specialv0List) {
		this.specialv0List = specialv0List;
	}

	public List<Double> getSpecialv1List() {
		return specialv1List;
	}

	public void setSpecialv1List(List<Double> specialv1List) {
		this.specialv1List = specialv1List;
	}
	String m_id;
	boolean m_bHaveInit;			//初始化标志量
	
	int m_ItemNum=3;				//维数
	
	Double[][] matrix = null;
	
	public void setMatrix(Double[][] matrix) {
		this.matrix = matrix;
	}
	int[] m_Col = new int[3];
		
	int m_SupplementWay;//插补数据的方向

	public CZYDataCurve()
	{
		init();
	}
	
	private void init()
	{
		m_v0.clear();
		m_v1.clear();
		//m_v2.DeleteAllData();
		m_v0_col.clear();
		m_v1_row.clear();
		m_id="";
		m_ItemNum=0;
		m_bHaveInit=false;
		currentInsertMethod=InsertMethod.M_NULL;
	}	
	
	

	public String getM_id() {
		return m_id;
	}

	public void setM_id(String mId) {
		m_id = mId;
	}

	public int getM_SupplementWay() {
		return m_SupplementWay;
	}

	public void setM_SupplementWay(int mSupplementWay) {
		m_SupplementWay = mSupplementWay;
	}

	public Double[][] getMatrix() {
		return matrix;
	}

	public int[] getM_Col() {
		return m_Col;
	}

	public boolean isM_bHaveInit() {
		return m_bHaveInit;
	}

	public void setM_bHaveInit(boolean mBHaveInit) {
		m_bHaveInit = mBHaveInit;
	}

	public InsertType getCurrentInsertType() {
		return currentInsertType;
	}
	
	public void setCurrentInsertType(InsertType currentInsertType) {
		this.currentInsertType = currentInsertType;
	}

	public InsertMethod getCurrentInsertMethod() {
		return currentInsertMethod;
	}

	public void setCurrentInsertMethod(InsertMethod currentInsertMethod) {
		this.currentInsertMethod = currentInsertMethod;
	}

	public int getM_ItemNum() {
		return m_ItemNum;
	}

	public void setM_ItemNum(int mItemNum) {
		m_ItemNum = mItemNum;
	}

	public List<Double> getM_v0() {
		return m_v0;
	}
	public List<Double> getM_v1() {
		return m_v1;
	}
	
	public List<Integer> getM_v0_col() {
		return m_v0_col;
	}
	public List<Integer> getM_v1_row() {
		return m_v1_row;
	}

	public Date getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(Date visitTime) {
		this.visitTime = visitTime;
	}
	
	
}


