package com.hpre.biggraph;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;



/**
 * ------------加载neo4j的配置------------监听器
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
			//------------加载neo4j的配置------------
			System.out.println("------------加载neo4j的配置------------");
			MyConnConfigure myConnConfigure = new MyConnConfigure();
			//System.out.println(LoadConfigListener.class.getResource("/com/wfxl/common/myconfig.properties"));
			myConnConfigure.load(LoadConfigListener.class.getResourceAsStream("/com/hpre/biggraph/neo4j.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
