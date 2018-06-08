package com.hpre.biggraph.newextension;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;






/**
 * 服务器监听器，服务器启动时加载服务器中的数据库配置文件
 * @author wfxl
 *
 */
public class LoadConfigListener implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			//服务器启动时加载mysql本地配置文件
			System.out.println("------------加载MongoDB配置文件------------");
			MongoConfigure mongoConf = new MongoConfigure();
			mongoConf.load(LoadConfigListener.class.getResourceAsStream("/com/hpre/biggraph/newextension/mongo.properties"));
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
