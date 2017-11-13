package com.zztaiwll.service;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.zztaiwll.dao.AddInfo;
import com.zztaiwll.dao.AddressInfo;
import com.zztaiwll.dao.UserInfo;
import com.zztaiwll.dao.VillageResearch;


public class LoginService {
	
	/*
	 * 登录
	 */
	public UserInfo login(String userName){
		String sql="select * from userInfo where username=?";
		UserInfo userInfo=UserInfo.dao.findFirst(sql,userName);
		return userInfo;
	}
	/*
	 * 获取调查数据列表
	 */
	public Page<VillageResearch> getResearchList(int parent_id,int page,int pagesize,String city,String county,String village,String country){
		Page<VillageResearch> list=null;
		StringBuffer sb=new StringBuffer();
		sb.append("from villageresearch a join addressInfo b on a.user_id=b.id ");
		if(!"".equals(city)){
			sb.append(" and b.city='"+city+"'");
		}
		if(!"".equals(county)){
			sb.append(" and b.county='"+county+"' ");
		}
		if(!"".equals(village)){
			sb.append(" and b.village='"+village+"' ");
		}
		if(!"".equals(country)){
			sb.append(" and b.country='"+country+"' ");
		}
		sb.append(" where  type=?");
		list=VillageResearch.dao.paginate(page, pagesize, "select a.*",sb.toString(),parent_id);
		return list;
	}
	
	//获取调查信息列表
	public List<AddInfo> getParentVillage(){
		String sql="select * from addinfo where parent_id=0";
		List<AddInfo> list=AddInfo.dao.find(sql);
		return list;
	}
	
	public List<AddInfo> getParentVillageByyearAndParentId(int parent_id){
		String sql="select * from addinfo where parent_id=?";
		List<AddInfo> list=AddInfo.dao.find(sql,parent_id);
		return list;
	}
	//获取
	//添加调查信息
	public boolean addVillageInformation(VillageResearch vill){
		boolean bool=vill.save();
		return bool;
	}
	//更新调查信息
	public boolean updateVillageInformation(VillageResearch vill ){
		boolean bool=vill.update();
		return bool;
	}
	//获取添加项
	public List<AddInfo> getAddColumnList(int parent_id){
		String sql="select * from AddInfo where parent_id=?";
		List<AddInfo> list=AddInfo.dao.find(sql,parent_id);
		return list;
	}
	//根据parent_ id以及地址获取添加或者修改的数据
	public List<VillageResearch> getAddColumnListByAddress(int parent_id,String city,String county,String village,String country,int year){
		StringBuffer sb=new StringBuffer();
		sb.append("select a.* from villageresearch a join addressInfo b on a.user_id=b.id ");
		if(!"".equals(city)){
			sb.append(" and b.city='"+city+"'");
		}
		if(!"".equals(county)){
			sb.append(" and b.county='"+county+"' ");
		}
		if(!"".equals(village)){
			sb.append(" and b.village='"+village+"' ");
		}
		if(!"".equals(country)){
			sb.append(" and b.country='"+country+"' ");
		}
		sb.append(" where  a.type=? and a.year=?");
		String sql=sb.toString();
		List<VillageResearch> list=VillageResearch.dao.find(sql,parent_id,year);
		return list;
	}
	//获取单个记录
	public AddInfo getSingleInformation(int id){
		AddInfo record=AddInfo.dao.findById(id);
		return record;
	}
	//获取单个调查记录
	public VillageResearch getSingleVillageInformation(int id){
		VillageResearch record=VillageResearch.dao.findById(id);
		return record;
	}
	
	//获取城市
	public List<UserInfo> getCity(){
		String sql="select distinct city from addressInfo";
		List<UserInfo> records=UserInfo.dao.find(sql);
		return records;
	}
	//获取县，根据城市
	public List<UserInfo> getCounty(String city){
		String sql="select distinct county from addressInfo where city=?";
		List<UserInfo> records=UserInfo.dao.find(sql,city);
		return records;
	}
	//获取乡，根据县
	public List<UserInfo> getVillage(String county){
		String sql="select distinct village from addressInfo where county=?";
		List<UserInfo> records=UserInfo.dao.find(sql,county);
		return records;
	}
	//获取村，根据乡
	public List<UserInfo> getCountry(String village){
		String sql="select distinct country from addressInfo where village=?";
		List<UserInfo> records=UserInfo.dao.find(sql,village);
		return records;
	}
	//获取时间列表
	public List<VillageResearch> getYear(){
		String sql="select distinct year from villageResearch ";
		List<VillageResearch> records=VillageResearch.dao.find(sql);
		return records;
	}
	public void AddVillageResearch(List<VillageResearch> vill){
		Db.batchSave(vill, vill.size());
	}
	public void AddUserInfoList(List<AddressInfo> addressInfo){
		Db.batchSave(addressInfo, addressInfo.size());
	}
	
	public void AddInfoListSave(List<AddInfo> addInfo){
		for(AddInfo info:addInfo){
			info.save();
		}
//		Db.batchSave(addInfo, addInfo.size());
	}
	/*
	 * 通过名称检查用户是否存在
	 */
	public UserInfo getUserInfoByName(String name){
		String sql="select *  from UserInfo where username=? and status=0";
		UserInfo record=UserInfo.dao.findFirst(sql,name);
		return record;
	}
    
	public List<VillageResearch> getVillByUserId(int parent_id,long user_id,int year){
		String sql="select  result from villageResearch where type=? and user_id=? and year=?";
		List<VillageResearch> list=VillageResearch.dao.find(sql,parent_id,user_id,year);
		return list;	
	}
	
	public UserInfo getUserById(long user_id){
		UserInfo info=UserInfo.dao.findById(user_id);
		return info;
	}
	/*
	 * 获取调查数据中的user_id
	 */
	public List<VillageResearch> getUserByCityAndParentId(int  parent_id,String city,String county,String village,String country,int year){
		StringBuffer sb=new StringBuffer();
		sb.append("SELECT  distinct a.user_id,b.country  FROM villageResearch a JOIN AddressInfo b ON a.user_id=b.id ");
		if(!"".equals(city)){
			sb.append(" and b.city='"+city+"'");
		}
		if(!"".equals(county)){
			sb.append(" and b.county='"+county+"' ");
		}
		if(!"".equals(village)){
			sb.append(" and b.village='"+village+"' ");
		}
		if(!"".equals(country)){
			sb.append(" and b.country='"+country+"' ");
		}
		sb.append(" join addinfo c on c.id=a.type and c.id=?");
		sb.append(" where a.year=?");
		String sql=sb.toString();
		List<VillageResearch> list=VillageResearch.dao.find(sql,parent_id,year);
		return list;
	}
	/*
	 * 获取添加项的数量
	 */
	public long getAddInfoCount(){
		String sql="select count(*) as num  from addInfo ";
		AddInfo addInfo=AddInfo.dao.findFirst(sql);
		return addInfo.get("num");
	}
	/*
	 * 通过type和userid获取调查问卷
	 *
	 */
	public List<VillageResearch> getVillByUserAndParentId(long user_id,int parent_id){
		String sql="select * from VillageResearch where user_id=? and type=?";
		List<VillageResearch> list=VillageResearch.dao.find(sql,user_id,parent_id);
		return list;
	}
	//添加新地址
	public AddressInfo saveAddress(AddressInfo info){
		info.save();
		return info;
	}
	//查询地址是否存在
	public AddressInfo getAddressBylocaltion(String city,String county,String village,String country){
		StringBuffer sb=new StringBuffer();
		sb.append("select  * from  AddressInfo where  status=0 ");
		if(!"".equals(city)){
			sb.append(" and city='"+city+"'");
		}
		if(!"".equals(county)){
			sb.append(" and county='"+county+"' ");
		}
		if(!"".equals(village)){
			sb.append(" and village='"+village+"' ");
		}
		if(!"".equals(country)){
			sb.append(" and country='"+country+"' ");
		}
		String sql=sb.toString();
		AddressInfo info=AddressInfo.dao.findFirst(sql);
		return info;
	}
	/*
	 * 删除已有的类别
	 */
	public void deleteExists(long userId,int parentId){
		String sql="delete from VillageResearch  where user_id=? and type=?";
		Db.update(sql,userId,parentId);
	}
	/*
	 * 根据id获取村子信息
	 */
	public AddressInfo getSingleAddress(long id){
		return  AddressInfo.dao.findById(id);
	}
	/*
	 * 查询地区表中是否由于当前数据重复的数据
	 * 
	 */
	public long validateLocalInfo(long id,String city,String county,String village,String country){
		String sql="select count(*) as num from AddressInfo where id=? or(city=? and county=? and village=? and country=?)";
		AddressInfo info=AddressInfo.dao.findFirst(sql,id,city,county,village,country);
		if(info!=null){
			return info.getLong("num");
		}
		return 0;
	}
	/*
	 * 添加地区
	 */
	public boolean  addAddressInfo(long id,String city,String county,String village,String country ){
		AddressInfo info=new AddressInfo();
		info.set("id", id);
		info.set("city", city);
		info.set("county", county);
		info.set("village", village);
		info.set("country", country);
		return info.save();
	}
	/*
	 * 获取addinfo通过parent_id
	 *
	 */
	public List<AddInfo> getAddInfoByParentId(int parent_id){
		String sql="select * from addinfo where parent_id=?";
		List<AddInfo> list=AddInfo.dao.find(sql,parent_id);
		return list;
	}
}
