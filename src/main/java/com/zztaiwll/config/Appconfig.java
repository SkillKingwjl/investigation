package com.zztaiwll.config;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;
import com.zztaiwll.controller.LoginController;
import com.zztaiwll.controller.ShowController;
import com.zztaiwll.dao.AddInfo;
import com.zztaiwll.dao.AddressInfo;
import com.zztaiwll.dao.UserInfo;
import com.zztaiwll.dao.VillageResearch;

public class Appconfig extends JFinalConfig {
	public void configConstant(Constants me) {
		me.setDevMode(true);
		}
	public void configRoute(Routes me) {
		me.add("/login", LoginController.class);
		me.add("/show", ShowController.class);
	}
	public void configEngine(Engine me) {}
	public void configPlugin(Plugins me) {
		Prop p = PropKit.use("db.properties");
		DruidPlugin dp = new DruidPlugin(p.get("jdbcUrl"), p.get("user"),p.get("password"),p.get("driver"));
		System.out.println(p.get("jdbcUrl")+"======"+p.get("user")+"============"+p.get("password")+"======="+p.get("driver"));
		WallFilter wall=new WallFilter();
		dp.addFilter(wall);
		StatFilter stat=new StatFilter();
		dp.addFilter(stat);
		me.add(dp);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
		me.add(arp);
		arp.addMapping("userInfo", UserInfo.class);
		arp.addMapping("villageResearch", VillageResearch.class);
		arp.addMapping("addinfo", AddInfo.class);
		arp.addMapping("addressinfo", AddressInfo.class);
		me.add(new Cron4jPlugin(PropKit.use("task.properties")));
	}
	public void configInterceptor(Interceptors me) {}
	public void configHandler(Handlers me) {
		
	}

}
