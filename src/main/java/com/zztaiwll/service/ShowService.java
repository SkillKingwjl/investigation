package com.zztaiwll.service;

import java.util.List;

import com.zztaiwll.dao.AddInfo;
import com.zztaiwll.dao.VillageResearch;

public class ShowService {
	/*
	 * 根据parent_id获取要显示为图片分析的选项
	 */
	public List<AddInfo> getShowFromAddInfo(int parent_id){
		String sql="select * from addinfo where analys=1 and parent_id=?";
		List<AddInfo> list=AddInfo.dao.find(sql,parent_id);
		return list;
	}
	/*
	 * 根据id获取info的数据
	 */
	public AddInfo getSingleAddInfoById(int id){
		return AddInfo.dao.findById(id);
	}
	/*
	 * 根据info_id获取需要获得的数据
	 */
	public List<VillageResearch> getShowVillageDataByInfoId(int info_id){
		String sql="select a.num as num, a.num/COUNT(b.result) as bai,a.result as result from " +
				"(select count(result) as num,result from villageResearch where info_id=? and result regexp '^[0.0-9.0]+$' group by result) as a, " +
				"(select result from villageResearch where info_id=? and result regexp '^[0.0-9.0]+$') as b  GROUP BY a.result;";
		return VillageResearch.dao.find(sql,info_id,info_id);
	}
	/*
	 * 根据info_id和时间或者addRess_id获取需要获得的数据
	 */
	public List<VillageResearch> getShowVillageDataByInfoIdAndAddress(int info_id,int address_id,String result){
		String sql="";
		if(address_id==0){
			sql="select a.num,a.result,a.`year`,a.num/b.num as bai from (select count(result) as num,result,year from villageResearch where info_id=? and result=? and result regexp '^[0.0-9.0]+$')as a,(select count(result) as num from villageResearch where info_id=? and result regexp '^[0.0-9.0]+$') as b group by result,year order by result ,year;";
			return VillageResearch.dao.find(sql,info_id,result,info_id);
		}else{
			sql="SELECT a.num as num,a.result as result,a.`YEAR` as year,a.result/b.num as bai FROM(SELECT count(result) AS num, result, YEAR FROM villageResearch WHERE info_id =? AND address_id =? AND result =? AND result REGEXP '^[0.0-9.0]+$') as a,(SELECT count(result) AS num FROM villageResearch WHERE info_id =? AND address_id =? AND result REGEXP '^[0.0-9.0]+$' ) as b GROUP BY result, year ORDER BY year";
			return VillageResearch.dao.find(sql,info_id,address_id,result,info_id,address_id);
		}
		
	}
	/*
	 * 根据info_id和时间获取需要获得的数据
	 */
	public List<VillageResearch> getShowVillageDataByInfoIdAndTime(int info_id,int time,String result){
		String sql="";
		if(time==0){
			sql="select a.num,a.address_id,a.result,a.num/b.num as bai  from (select count(result) as num,result,address_id from villageResearch where info_id= ? and result=? AND result REGEXP '^[0.0-9.0]+$' group by result,address_id order by result,address_id) as a,(select count(result) as num ,address_id from villageResearch where info_id=? AND result REGEXP '^[0.0-9.0]+$' group by address_id order by address_id) as b  where a.address_id=b.address_id  GROUP BY a.address_id  ORDER BY a.address_id";
			return VillageResearch.dao.find(sql,info_id,result,info_id);
		}else{
			sql="select a.num,a.result,a.address_id,a.num/b.num as bai from (select count(result) as num,result,address_id from villageResearch where info_id=? and year=? and result=? AND result REGEXP '^[0.0-9.0]+$'group by result,address_id order by result,address_id)as a,(select count(result) as num,address_id from villageResearch where info_id=? and year=? AND result REGEXP '^[0.0-9.0]+$'group by address_id order by address_id) as b where a.address_id=b.address_id  group by a.address_id order by a.address_id";
			return VillageResearch.dao.find(sql,info_id,time,result,info_id,time);
		}
		
	}
	/*
	 * 根据info_id查询year
	 */
	public List<VillageResearch> getYearByInfo_id(int info_id){
		String sql="select distinct year from villageResearch where info_id=?";
		return VillageResearch.dao.find(sql,info_id);
	}
	
	/*
	 * 根据info_id查询address
	 */
	public List<VillageResearch> getAddressByInfo_id(int info_id){
		String sql="select distinct address_id from villageResearch where info_id=?";
		return VillageResearch.dao.find(sql,info_id);
	}
	
	/*
	 * 获取info_id下所有的result
	 */
	public List<VillageResearch> getResultByInfo_id(int info_id){
		String sql="select distinct result from villageResearch where info_id=? order by result";
		return VillageResearch.dao.find(sql,info_id);
	}
	/*
	 * 根据parent_id获取所有的地理信息 
	 */
	public List<VillageResearch> getAddressByParent_id(int parent_id){
		String sql="select distinct address_id from villageResearch where type=?";
		return VillageResearch.dao.find(sql,parent_id);
		
	}
	
	/*
	 * 根据parent_id查询year
	 */
	public List<VillageResearch> getYearByParent_id(int parent_id){
		String sql="select distinct year from villageResearch where type=? ";
		return VillageResearch.dao.find(sql,parent_id);
	}
}
