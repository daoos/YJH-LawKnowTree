package com.hpre.biggraph;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
/**
 * neo4j配置
 * @author wfxl
 *
 */
public class MyConnConfigure extends Properties {
	public static String driver;
	public static String url;
	public static String name;
	public static String pass;
	public static List<String> receivers;
	@Override
	public synchronized void load(InputStream inStream) throws IOException {
		super.load(inStream);
		driver = this.getProperty("driver");
		url = this.getProperty("url");
		name = this.getProperty("name");
		pass = this.getProperty("pass");
		
		receivers = new ArrayList<String>();
		receivers.add("1162916411@qq.com");
		receivers.add("1049852196@qq.com");
	}
}
