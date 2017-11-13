package com.zztaiwll.controller;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.zztaiwll.bean.ArrayBean;
import com.zztaiwll.bean.ListBean;
import com.zztaiwll.dao.AddInfo;
import com.zztaiwll.dao.AddressInfo;
import com.zztaiwll.dao.UserInfo;
import com.zztaiwll.dao.VillageResearch;
import com.zztaiwll.service.LoginService;
import com.zztaiwll.utils.OutExcel;
import sun.misc.BASE64Encoder;
public class LoginController  extends Controller{
	private LoginService loginService = enhance(LoginService.class);
	public void index() {
		renderVelocity("/page/html/login.html");
	}
	/*
	 * 登录
	 */
	@Before(POST.class)
	public void login() throws Exception{
		String userName=getPara("username");
		String passWord=getPara("password");
		MessageDigest md5=MessageDigest.getInstance("MD5");
	    BASE64Encoder base64en = new BASE64Encoder();
	    passWord=base64en.encode(md5.digest(passWord.getBytes("utf-8")));
		System.out.println("username===="+userName+"======password======"+passWord);
		UserInfo userInfo=loginService.login(userName);
		if(userInfo==null){
			renderJson("result",1);
		}else{
			String passWord1=userInfo.getStr("password");
			if(passWord1.equals(passWord)){
				setSessionAttr("userInfo", userInfo);
				renderJson("result",0);
			}else{
				renderJson("result",2);
			}
		}
		
	}
	/*
	 	验证登录
	 */
	@Before(POST.class)
	public void validLogin(){
		UserInfo userInfo=getSessionAttr("userInfo");
		renderJson("userInfo",userInfo);
	}
	/*
	 *登出
	 */
	@Before(POST.class)
	public void loginOut(){
		UserInfo userInfo=getSessionAttr("userInfo");
		if(userInfo!=null){
			removeSessionAttr("userInfo");
		}
		renderJson("result",true);
	}
	/*
	 * 添加新用户
	 */
	@Before(GET.class)
	public void addUser() throws Exception{
		String userName=getPara("username");
		UserInfo isExists=loginService.getUserInfoByName(userName);
		if(isExists==null){
			String passWord=getPara("password");
			MessageDigest md5=MessageDigest.getInstance("MD5");
		    BASE64Encoder base64en = new BASE64Encoder();
		    passWord=base64en.encode(md5.digest(passWord.getBytes("utf-8")));
			int property=getParaToInt("property");
			UserInfo userInfo=getSessionAttr("userInfo");
			if(userInfo==null){
				renderJson("result",5);
			}else{
				int user_property=userInfo.getInt("property");
				if(user_property!=0){
					renderJson("result",2);
				}else{
					UserInfo user=new UserInfo();
					user.set("username", userName);
					user.set("password", passWord);
					user.set("property", property);
					boolean result=user.save();
					renderJson("result",result);
				}
			}
		}else{
			renderJson("result",3);
		}
	}
	/*
	 * 添加地理位置
	 */
	@Before(POST.class)
	public void addlocal(){
		String id=getPara("id","");
		String city=getPara("city", "");
		String county=getPara("county", "");
		String village=getPara("village", "");
		String country=getPara("country", "");
		if(StringUtils.isEmpty(city)||StringUtils.isEmpty(county)||StringUtils.isEmpty(village)||StringUtils.isEmpty(country)||StringUtils.isEmpty(id)){
			renderJson("result",2);
		}else{
			long num=loginService.validateLocalInfo(Long.parseLong(id),city,county,village,country);
			if(num>0){
				renderJson("result",1);
			}else{
				boolean result=loginService.addAddressInfo(Long.parseLong(id),city,county,village,country);
				if(result){
					renderJson("result",0);
				}else{
					renderJson("result",3);
				}
			}
		}
		
	}
	/*
	 * 获取调查信息
	 */
	@Before(POST.class)
	public void getVillageInformation(){
		int parent_id=getParaToInt("parent_id");
		int page=getParaToInt("page");
		String city=getPara("city", "");
		String county=getPara("county", "");
		String village=getPara("village", "");
		String country=getPara("country", "");
		int year=getParaToInt("year");
		UserInfo userInfo=getSessionAttr("userInfo");
		if(userInfo==null){
			renderJson("result",0);
		}else{
			String locations="";
			Page<VillageResearch> list=new Page<VillageResearch>();
			List<VillageResearch> user=loginService.getUserByCityAndParentId(parent_id,city,county,village,country,year);
			if(user!=null&&user.size()>0){
				if(page-1<=user.size()){
					VillageResearch vill=user.get(page-1);
					long user_id=vill.getLong("user_id");
					locations=vill.getStr("country");
					List<VillageResearch> result=loginService.getVillByUserAndParentId(user_id,parent_id);
					list=new Page<VillageResearch>(result,page,result.size(),user.size(),100000);
				}
			}
			Map<String,Object> result=new HashMap<String,Object>();
			result.put("result", 1);
			result.put("page",list);
			result.put("locations", locations);
			renderJson("result",result);
		}
	}
	/*
	 * 获取列表栏信息
	 */
	@Before(POST.class)
	public void getParentList(){
		List<AddInfo> list=loginService.getParentVillage();
		renderJson("result",list);
	}
	/*
	 * 获取添加项
	 */
	@Before(POST.class)
	public void getAddColumn(){
		Map<String,Object> map=new HashMap<String,Object>();
		int parent_id=getParaToInt("parent_id");
		String city=getPara("city", "");
		String county=getPara("county", "");
		String village=getPara("village", "");
		String country=getPara("country", "");
		int year=getParaToInt("year");
		UserInfo userInfo=getSessionAttr("userInfo");
		if(userInfo==null){
			renderJson("result",0);
		}else{
			int propery=userInfo.getInt("property");
			if(propery==2){
				map.put("flag", 3);
			}else{
				if("".equals(city)||"".equals(county)||"".equals(village)||"".equals(country)){
						List<AddInfo> list=loginService.getAddColumnList(parent_id);
						map.put("list", list);
						map.put("flag", 2);
				}else{
						List<VillageResearch> list2=loginService.getAddColumnListByAddress(parent_id, city, county, village, country,year);
						if(list2!=null&&list2.size()>0){
							map.put("list", list2);
							map.put("flag", 1);
						}else{
							List<AddInfo> list=loginService.getAddColumnList(parent_id);
							map.put("list", list);
							map.put("flag", 2);
						}
						
					}
			}
			renderJson("result",map);
		}
	}
	/*
	 * 添加调查信息
	 * 
	 */
	@Before(POST.class)
	public void addData(){
		String params=getPara("data");
		int parentid=getParaToInt("parent_id");
		int year=getParaToInt("year");
		UserInfo userInfo=getSessionAttr("userInfo");
		if(userInfo==null){
			renderJson("result",2);
		}else{
			String city=getPara("city", "");
			String county=getPara("county", "");
			String village=getPara("village", "");
			String country=getPara("country", "");
			if("".equals(city)||"".equals(county)||"".equals(village)||"".equals(country)){
				renderJson("result",3);
			}else{
				AddressInfo addressInfo=loginService.getAddressBylocaltion(city, county, village, country);
				if(addressInfo==null){
					renderJson("result",4);
				}else{
					loginService.deleteExists(addressInfo.getLong("id"),parentid);
					JSONArray jsay=new JSONArray(params);
					if(jsay!=null&&jsay.length()>0){
						for(int i=0;i<jsay.length();i++){
							JSONObject obj=jsay.getJSONObject(i);
							int id=obj.getInt("id");
							String value=obj.getString("value");
							AddInfo info=loginService.getSingleInformation(id);
							VillageResearch vill=new VillageResearch();
							vill.set("id", null);
							vill.set("result", value);
							vill.set("user_id",addressInfo.getLong("id"));
							vill.set("type", info.get("parent_id"));
							vill.set("name", info.get("name"));
							vill.set("attr",info.get("attr"));
							vill.set("year", year);
							loginService.addVillageInformation(vill);
						}
					}
					renderJson("result",1);
				}
				
			}
		}
	}
	@Before(POST.class)
	public void updateData(){
		String params=getPara("data");
		UserInfo userInfo=getSessionAttr("userInfo");
		if(userInfo==null){
			renderJson("result",2);
		}else{
			JSONArray jsay=new JSONArray(params);
			if(jsay!=null&&jsay.length()>0){
				for(int i=0;i<jsay.length();i++){
					JSONObject obj=jsay.getJSONObject(i);
					int id=obj.getInt("id");
					String value=obj.getString("value");
					VillageResearch vill=loginService.getSingleVillageInformation(id);
					vill.set("result", value);
					loginService.updateVillageInformation(vill);
					
				 }	
				}
			renderJson("result",1);
		}
	}
	/*
	 * 上传表格
	 */
	@Before(POST.class)
	public void upload() throws Exception{
		UploadFile uploadFile = getFile("file1");
		String fileName=uploadFile.getOriginalFileName();
		Prop p = PropKit.use("db.properties");
		String path=p.get("upload")+fileName;
        File file=uploadFile.getFile();    
        File t=new File(path);
        try {
        	if(!t.exists()){
        		 t.createNewFile();
        		 this.fileChannelCopy(file, t);
        	}
        	List<ListBean> list=this.getOtehrData(file);
            file.delete();
            renderJson("result",list);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            renderJson("result",2);
        }
        
	}

	public void fileChannelCopy(File s, File t) throws Exception {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();// 得到对应的文件通道
            out = fo.getChannel();// 得到对应的文件通道
            in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	@Before(POST.class)
	public void handlerFile() throws Exception{
		String path=getPara("path", "");
		if("".equals(path)){
			renderJson("result",4);
		}else{
			Prop p = PropKit.use("db.properties");
			String paths=p.get("upload")+path;
			File file=new File(paths);
			String filename=file.getName();
			String ext = filename.substring(filename.lastIndexOf("."));
			InputStream is = new FileInputStream(file);
			Workbook wb;
			if(".xls".equals(ext)){
				wb = new HSSFWorkbook(is);
			}else if(".xlsx".equals(ext)){
				wb = new XSSFWorkbook(is);
			}else{
				wb=null;
			}
			if(wb==null){
				throw new Exception("Workbook为null");
			}
			UploadFileExcel data=new UploadFileExcel(wb,file);
			Thread thread = new Thread(data);
	        thread.start();
	        renderJson("result",1);
		}
	}
	/*
	 * 下载文档
	 */
	@Before(GET.class)
	public void download() throws Exception{ 
		int year=getParaToInt("year");
		String city=getPara("city", "");
		String county=getPara("county", "");
		String village=getPara("village", "");
		String country=getPara("country", "");
		int parent_id=getParaToInt("parent_id");
		List<ArrayBean> list=this.getHead(parent_id);
		String result="";
		for(int i=0;i<list.size();i++){
			ArrayBean map=list.get(i);
			int id=map.getId();
			String head[]=map.getArray();
			List<String[]> items=this.getVillInfo(id,city,county,village,country,year);	
			result=this.outExcel(head,items,i+1);
		}
		renderJson("result",result);
	}
	private String outExcel(String []head,List<String[]>items,int num) throws Exception{
		Prop p = PropKit.use("db.properties");
		String paths="AddInfos"+System.currentTimeMillis()+".xls";
		File ff=new File(p.get("download"));
		if(!ff.exists()){
			ff.mkdirs();
		}
	    String outpath=p.get("download")+paths;
	    System.out.println(outpath);
	    OutputStream out= new FileOutputStream(outpath); 
	    OutExcel excel=new OutExcel();
	    excel.exportExcel("map", head, items, out, "yyyy-MM-dd HH:mm:ss");
	    out.close();
	    return paths;
	}
	
	private List<ArrayBean> getHead(int parent_id){
		List<AddInfo> list=loginService.getParentVillageByyearAndParentId(parent_id);
		List<ArrayBean> result=new ArrayList<ArrayBean>();
		ArrayBean map=new ArrayBean();
		String array[]=new String[list.size()+1];
		array[0]="编号";
		for(int j=0;j<list.size();j++){
			AddInfo addInfo1=list.get(j);
			System.out.println(addInfo1.get("name")+"======");
			if(addInfo1.get("name")!=null){
				array[j+1]=addInfo1.getStr("name");
			}else{
				array[j+1]="";
			}
		}
		map.setId(parent_id);
		map.setArray(array);
		result.add(map);
		return result;
	}
	
	private List<String[]> getVillInfo(int parent_id,String city,String county,String village,String country,int year){
		List<String[]> result=new ArrayList<String[]>();
		List<VillageResearch> list=loginService.getUserByCityAndParentId(parent_id,city,county,village,country,year);
		for(int i=0;i<list.size();i++){
			VillageResearch vill=list.get(i);
			long user_id=vill.getLong("user_id");
			List<VillageResearch> list2=loginService.getVillByUserId(parent_id,user_id,year);
			String[] array=new String[list2.size()+2];
			array[0]=user_id+"";
			AddressInfo info=loginService.getSingleAddress(user_id);
			for(int j=0;j<list2.size();j++){
				array[j+1]=list2.get(j).getStr("result");
			}
			if(info!=null){
				array[list2.size()+1]=info.get("country");
			}
			result.add(array);
		}
		
		return result;
	}
	@Before(POST.class)
	public void getCity(){
		List<UserInfo> list=loginService.getCity();
		renderJson("result",list);
	}
	@Before(POST.class)
	public void getCounty(){
		String city=getPara("city");
		List<UserInfo> list=loginService.getCounty(city);
		renderJson("result",list);
	}
	@Before(POST.class)
	public void getVillage(){
		String county=getPara("county");
		List<UserInfo> list=loginService.getVillage(county);
		renderJson("result",list);
	}
	@Before(POST.class)
	public void getCountry(){
		String village=getPara("village");
		List<UserInfo> list=loginService.getCountry(village);
		renderJson("result",list);
	}
	@Before(POST.class)
	public void getYear(){
		List<VillageResearch> list=loginService.getYear();
		renderJson("result",list);
	}
	@Before(POST.class)
	public void getUserCity(){
		UserInfo userInfo=getSessionAttr("userInfo");
		renderJson("userInfo",userInfo);
	}
	
	public List<ListBean> getOtehrData(File file) throws Exception{
		String filename=file.getName();
		String ext = filename.substring(filename.lastIndexOf("."));
		InputStream is = new FileInputStream(file);
		Workbook wb;
		if(".xls".equals(ext)){
			wb = new HSSFWorkbook(is);
		}else if(".xlsx".equals(ext)){
			wb = new XSSFWorkbook(is);
		}else{
			wb=null;
		}
		if(wb==null){
			throw new Exception("Workbook对象为空！");
		}
		 Sheet sheet=wb.getSheetAt(1);
		 List<ListBean>  listBean=new ArrayList<ListBean>();
		 int rowNum = sheet.getLastRowNum();
		 int num=20;
		 if(rowNum<20){
			 num=rowNum;
		 }
		 int columnNum=sheet.getRow(2).getPhysicalNumberOfCells();
		 for(int i=0;i<num;i++){
			 Row row = sheet.getRow(i);
			 ListBean bean=new ListBean();
			 bean.setId(i);
			 List<String> list=new ArrayList<String>();
			 for(int j=0;j<columnNum;j++){
				 if(row.getCell(j)!=null){
					 list.add(row.getCell(j).toString());
				 }else{
					 list.add("");
				 }
			 }
			 bean.setList(list);
			 listBean.add(bean);
		 }
		 is.close();
		 wb.close();
		 return listBean;
	}
	class UploadFileExcel implements Runnable{
		private Workbook wb;
		private Sheet sheet;
		private Row row;
		private File file;
		public UploadFileExcel(){};
		public UploadFileExcel(Workbook wb,File file){
			this.wb=wb;
			this.file=file;
		
		}
		public List<VillageResearch> readExcelContent(int sheetNum) throws Exception{
			if(wb==null){
				throw new Exception("Workbook对象为空！");
			}
			List<VillageResearch> list=new ArrayList<VillageResearch>();
			Map<Integer,String> map=new HashMap<Integer,String>();
			Map<Integer,String> map1=new HashMap<Integer,String>();
			sheet = wb.getSheetAt(sheetNum);
			row = sheet.getRow(1);
			int columnNum=row.getPhysicalNumberOfCells();
			for(int i=0;i<columnNum;i++){
				if(row.getCell(i)!=null){
					map.put(i, row.getCell(i).toString());
				}else{
					map.put(i, "");
				}
				
			}
			row = sheet.getRow(2);
			for(int i=1;i<columnNum;i++){
				if(row.getCell(i)!=null){
					map1.put(i, row.getCell(i).toString());
				}else{
					map1.put(i, "");
				}
			}
			// 得到总行数
			int rowNum = sheet.getLastRowNum();
			VillageResearch vill=null;
			int num=1;
			for (int i = 3; i <= rowNum; i++) {
				row = sheet.getRow(i);
				String ids=row.getCell(0).toString();
				long user_id=0;			
				if(ids.contains("-")){
					String arr_ids=ids.split("-")[1];
					user_id=(long) Double.parseDouble(arr_ids);					
				}else{
					user_id=(long) Double.parseDouble(ids);	
				}
				int addressId=Integer.parseInt(String.valueOf(user_id).substring(0,4));
				String years=user_id+"";
				List<AddInfo> addInfoList=loginService.getAddInfoByParentId(sheetNum);
				int year=Integer.parseInt(years.substring(4, 8));
				for(int j=1;j<columnNum;j++){
					vill=new VillageResearch();
					vill.set("type", sheetNum);
					vill.set("user_id",user_id);
					vill.set("address_id", addressId);
					if(map.get(j)!=null){
						vill.set("name",map.get(j));
					}else{
						vill.set("name","");
					}
					long info_id=addInfoList.get(j-1).getLong("id");
					vill.set("info_id",info_id );
					vill.set("year", year);
					vill.set("id",null);
					if(map1.get(j)!=null){
						vill.set("attr",map1.get(j));
					}else{
						vill.set("attr","");
					}
					num++;
					if(row.getCell(j)!=null){
						vill.set("result",row.getCell(j).toString());
					}else{
						vill.set("result","");
					}
					
					list.add(vill);
				}
			}
			return list;
		}
		public List<AddInfo> readExcelMocket() throws Exception{
			if(wb==null){
				throw new Exception("Workbook对象为空！");
			}
			List<AddInfo> list=new ArrayList<AddInfo>();
			list.addAll(getAddInfo());
			for(int j=1;j<=9;j++){
				Map<Integer,String> map=new HashMap<Integer,String>();
				sheet = wb.getSheetAt(j);
				row = sheet.getRow(1);
				int columnNum=row.getPhysicalNumberOfCells();
				for(int i=0;i<columnNum;i++){
					if(row.getCell(i)!=null){
						map.put(i, row.getCell(i).toString());
					}else{
						map.put(i,"");
					}
					
				}
				row = sheet.getRow(2);
				for(int z=1;z<columnNum;z++){
					AddInfo info=new AddInfo();
					if(map.get(z)!=null){
						info.set("name",map.get(z));
					}else{
						info.set("name","");
					}
					
					info.set("parent_id",j);
					info.set("status",0);
					if(row.getCell(z)!=null){
						info.set("attr",row.getCell(z).toString());
					}else{
						info.set("attr","");
					}
					list.add(info);
				}
			}
			
			return list;
		}
		public List<AddInfo> getAddInfo(){
			List<AddInfo> list=new ArrayList<AddInfo>();
			AddInfo info1=new AddInfo();
			info1.set("id",1);
			info1.set("parent_id",0);
			info1.set("name","村基本情况");
			info1.set("status",0);
			list.add(info1);
			AddInfo info2=new AddInfo();
			info2.set("id",2);
			info2.set("parent_id",0);
			info2.set("name","村经济发展情况");
			info2.set("attr","");
			info2.set("status",0);
			list.add(info2);
			AddInfo info3=new AddInfo();
			info3.set("id",3);
			info3.set("parent_id",0);
			info3.set("name","村集体土地确权、使用、流转情况");
			info3.set("attr","");
			info3.set("status",0);
			list.add(info3);
			AddInfo info4=new AddInfo();
			info4.set("id",4);
			info4.set("parent_id",0);
			info4.set("name","村基础设施建设与公共服务情况");
			info4.set("attr","");
			info4.set("status",0);
			list.add(info4);
			AddInfo info5=new AddInfo();
			info5.set("id",5);
			info5.set("parent_id",0);
			info5.set("name","农村金融情况");
			info5.set("attr","");
			info5.set("status",0);
			list.add(info5);
			AddInfo info6=new AddInfo();
			info6.set("id",6);
			info6.set("parent_id",0);
			info6.set("name","农业生产经营服务情况");
			info6.set("attr","");
			info6.set("status",0);
			list.add(info6);
			AddInfo info7=new AddInfo();
			info7.set("id",7);
			info7.set("parent_id",0);
			info7.set("name","农村环境卫生情况");
			info7.set("attr","");
			info7.set("status",0);
			list.add(info7);
			AddInfo info8=new AddInfo();
			info8.set("id",8);
			info8.set("parent_id",0);
			info8.set("name","乡村治理情况");
			info8.set("attr","");
			info8.set("status",0);
			list.add(info8);
			AddInfo info9=new AddInfo();
			info9.set("id",9);
			info9.set("parent_id",0);
			info9.set("name","相关建议");
			info9.set("attr","");
			info9.set("status",0);
			list.add(info9);
			return list;
		}
		public List<AddressInfo> readExcelAdress() throws Exception{
			if(wb==null){
				throw new Exception("Workbook对象为空！");
			}
			Map<Integer,String> map=new HashMap<Integer,String>();
			sheet = wb.getSheetAt(0);
			int rowNum = sheet.getLastRowNum();
			List<AddressInfo> list=new ArrayList<AddressInfo>();
			for (int i = 2; i <= rowNum; i++) {
				row = sheet.getRow(i);
				AddressInfo addressInfo=new AddressInfo();
				String ids=row.getCell(0).toString();
				long user_id=0;
				if(ids.contains("-")){
					String arr_ids=ids.split("-")[1];
					user_id=(long) Double.parseDouble(arr_ids);
				}else{
					user_id=(long) Double.parseDouble(ids);
				}
				addressInfo.set("id",user_id);
				String city="";
				if(row.getCell(1)!=null){
					city=row.getCell(1).toString();
				}
				String county="";
				if(row.getCell(2)!=null){
					county=row.getCell(2).toString();
				}
				String village="";
				if(row.getCell(3)!=null){
					village=row.getCell(3).toString();
				}
				String country="";
				if(row.getCell(4)!=null){
					country=row.getCell(4).toString();
				}
				addressInfo.set("city",city);
				addressInfo.set("county",county);
				addressInfo.set("village",village);
				addressInfo.set("country",country);
				addressInfo.set("status", 0);
				list.add(addressInfo);
			}
			return list;
		}
		public void run(){
			// TODO Auto-generated method stub
			try{
				long num=loginService.getAddInfoCount();
				if(num==0){
					List<AddInfo> list3=this.readExcelMocket();
					for(AddInfo info:list3){
						System.out.println(info);
					}
					loginService.AddInfoListSave(list3);
				}
				for(int i=1;i<10;i++){
					List<VillageResearch> list1=this.readExcelContent(i);
					loginService.AddVillageResearch(list1);
				}
				List<AddressInfo> list2=this.readExcelAdress();
				loginService.AddUserInfoList(list2);
				
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					if(wb!=null){
						wb.close();
						wb=null;
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				file.delete();
			}
		}
	}
}  
