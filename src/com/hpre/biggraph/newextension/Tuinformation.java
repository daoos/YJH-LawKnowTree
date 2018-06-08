package com.hpre.biggraph.newextension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpre.biggraph.NewGraphDB;
import com.hpre.biggraph.client.enter.graph.Edge;
import com.hpre.biggraph.client.enter.graph.Graph;
/**
 * 新的关系图谱json
 * @author ZhouLongSi
 *
 */
public class Tuinformation {
	static String H129 = "h129";
	private static final String  BUSINESS_INFO = "businessinfo";
	public static void main(String[] args) throws Exception {
		new LoadConfigListener().contextInitialized(null);
		
		ObjectMapper mapper = new ObjectMapper();
		Tuinformation test=new Tuinformation();
		try {
			Graph g =  new NewGraphDB(null).extension("华为技术有限公司", 3);
			JSONObject object=new JSONObject(mapper.writeValueAsString(g));
			
			
			BaseMongoDAL mongo = new MyMongo(MongoConfigure.dbOnline,BUSINESS_INFO);
//			Function.printFile(object+"\n", "e:/华为json字段", true);
			System.out.println(test.getJson(object, mongo));
//			System.out.println(test.getJson(object, mongo));
			mongo.close();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 
	 * @param object 图数据的json数据
	 * @return 去除空格与重复后的数据
	 */
	public JSONObject changbaseobject(JSONObject object){
		JSONObject newobj=new JSONObject();
		try {
			String companyName=object.getString("companyName").replaceAll("\\s+", "").trim();
			newobj.put("companyName", companyName);
			JSONObject nodes=new JSONObject(); 
			JSONObject edges=new JSONObject(); 
			Iterator iter=object.getJSONObject("nodes").keys();
			while(iter.hasNext()){
				String str = (String) iter.next();
				nodes.put(str.replaceAll("\\s+", "").trim(),new JSONObject(object.getJSONObject("nodes").get(str).toString().replaceAll("\\s+", "").trim()));
			}
			Iterator iter2=object.getJSONObject("edges").keys();
			while(iter2.hasNext()){
				String str = (String) iter2.next();
				edges.put(str.replaceAll("\\s+", "").trim(),new JSONObject(object.getJSONObject("edges").get(str).toString().replaceAll("\\s+", "").trim()));
			}
			newobj.put("nodes", nodes);
			newobj.put("edges", edges);

		}catch (JSONException e) {
			e.printStackTrace();
		}
		return newobj;
		
	}
	
	
	
	/**
	 * 主入口
	 * @param obj 图数据库数据
	 * @param mongo mongo连接
	 * @return 新关系图谱json
	 */
	public JSONObject getJson(JSONObject obj,BaseMongoDAL mongo){
		if(obj==null){
			return obj;
		}
		
		JSONObject object2=new JSONObject();
		JSONObject object=changbaseobject(obj);
		try {
			String companyName=object.getString("companyName");
			object2.put("companyName",companyName);
			JSONArray nodes=new JSONArray();
			JSONArray links=new JSONArray();
			JSONObject object3=new JSONObject();
			JSONObject object4=new JSONObject();
			JSONObject basenodes=object.getJSONObject("nodes");
			JSONObject basesdges=object.getJSONObject("edges");
			Graph g=Graph.loadGraph(basesdges.toString(),basenodes.toString());
			links=getLinks(object.getJSONObject("edges"),mongo,companyName);
			nodes=getNodes(basenodes,basesdges,links,companyName,mongo,g);
			JSONObject content=getTargetbelong(links,companyName);
			object3=getTarget(companyName,mongo,content);
			object4=gettouzis(links,companyName);
			nodes.put(object3);
			object2.put("nodes", nodes);
			object2.put("links", links);
			object2.put("touzis", object4);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object2;
	}
	 
	
	/**
	 * 
	 * @param companyName 目标公司
	 * @return 目标公司全部信息
	 */
	
	public JSONObject getTarget(String companyName,BaseMongoDAL mongo,JSONObject content){
		if (!mongo.getCollectionName().equals(BUSINESS_INFO)){
			mongo.changeCollection(BUSINESS_INFO);
		}
		JSONObject information=mongo.getOneDocument(companyName);
		JSONObject object3=new JSONObject();	
		try {
			if(information!=null){
				information=information.getJSONObject("baseInformation");
				if(information!=null){
					String money=information.getString("registered_capital");
					double regmoney=0.0;
					if(!"".equals(money)){
						regmoney=transformMoney(money);
					}
					object3.put("id", companyName);
					object3.put("enterpriseName", companyName); //企业名称 
					object3.put("frName",information.getString("legal_representation"));//法人     
					object3.put("creditCode",information.getString("unified_credit_code"));//信用代码  
					object3.put("regNo", information.getString("orgnization_code"));//组织机构代码   
					object3.put("regCap",regmoney); //注册资金
					object3.put("regCapCur","万元");                                //注册资金单位
					object3.put("esDate",information.getString("date"));//注册时间    
					object3.put("openFrom",information.getString("issue_date"));//发照日期
					object3.put("openTo", information.getString("during"));//营业期限
					object3.put("enterpriseType",information.getString("type"));//公司类型
					object3.put("enterpriseStatus",information.getString("status"));//经营状态
					object3.put("address",information.getString("business_address"));//地址
					object3.put("operateScope",information.getString("scope_of_business")); //经营范围
					object3.put("regOrg", information.getString("registration_authority"));	//登记机关
					object3.put("industryName", "");
					object3.put("recCap", "");//实收资本
					object3.put("industryPhyName", "");	//行业门类别
					object3.put("flag",content.get("flag"));
					object3.put("type",content.get("type"));
					object3.put("belong",content.get("belong"));
					object3.put("belong2",content.get("belong2"));
					object3.put("position",content.get("position"));
					object3.put("level",0);
					object3.put("colorType",content.get("colorType"));		
					object3.put("djgnum","");	
				}else{
					object3.put("id", companyName);
					object3.put("enterpriseName", companyName); //企业名称 
					object3.put("frName","");//法人     
					object3.put("creditCode","");//信用代码  
					object3.put("regNo","");//组织机构代码   
					object3.put("regCap",""); //注册资金
					object3.put("regCapCur","万元");//注册资金单位
					object3.put("esDate","");//注册时间    
					object3.put("openFrom","");//发照日期
					object3.put("openTo", "");//营业期限
					object3.put("enterpriseType","");//公司类型
					object3.put("enterpriseStatus","");//经营状态
					object3.put("address","");//地址
					object3.put("operateScope",""); //经营范围
					object3.put("regOrg","");	//登记机关
					object3.put("industryName", "");
					object3.put("recCap", "");//实收资本
					object3.put("industryPhyName", "");	//行业门类别
					object3.put("flag",content.get("flag"));
					object3.put("belong",content.get("belong"));
					object3.put("belong2",content.get("belong2"));
					object3.put("type",content.get("type"));
					object3.put("position",content.get("position"));
					object3.put("level",0);
					object3.put("colorType",content.get("colorType"));		
					object3.put("djgnum","");			
				}
			}else{
				object3.put("id", companyName);
				object3.put("enterpriseName", companyName); //企业名称 
				object3.put("frName","");//法人     
				object3.put("creditCode","");//信用代码  
				object3.put("regNo","");//组织机构代码   
				object3.put("regCap",""); //注册资金
				object3.put("regCapCur","万元"); //注册资金单位
				object3.put("esDate","");//注册时间    
				object3.put("openFrom","");//发照日期
				object3.put("openTo", "");//营业期限
				object3.put("enterpriseType","");//公司类型
				object3.put("enterpriseStatus","");//经营状态
				object3.put("address","");//地址
				object3.put("operateScope",""); //经营范围
				object3.put("regOrg","");	//登记机关
				object3.put("industryName", "");
				object3.put("recCap", "");//实收资本
				object3.put("industryPhyName", "");	//行业门类别
				object3.put("flag",content.get("flag"));
				object3.put("belong",content.get("belong"));
				object3.put("belong2",content.get("belong2"));
				object3.put("position",content.get("position"));
				object3.put("type",content.get("type"));
				object3.put("level",0);
				object3.put("colorType",content.get("colorType"));		
				object3.put("djgnum","");	
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object3;

	}
	
	public JSONObject getTargetbelong(JSONArray links,String companyName){
		JSONObject content=new JSONObject();
		JSONArray belong=new JSONArray();
		JSONArray belong2=new JSONArray();
		JSONArray flag=new JSONArray();
		JSONArray position=new JSONArray();
		JSONArray type=new JSONArray();
		JSONArray colorType=new JSONArray();
		belong.put("target");
		belong2.put("target-target");
		flag.put("target");
		position.put("目标企业");
		type.put("target");
		colorType.put("0-0-target");
		try {
			for(int i=0;i<links.length();i++){
				JSONObject object=links.getJSONObject(i);
				if(!companyName.equals(object.getString("from"))&&!companyName.equals(object.getString("to"))){
					continue;
				}
				if("inv".equals(object.getString("flag"))){
					String name;
					if(companyName.equals(object.getString("from"))){
						name=object.getString("to");
					}else{
						name=object.getString("from");
					}
					belong.put(name);
					belong2.put(name+"-"+object.getString("share"));
					flag.put("inv");
					position.put(name);
					type.put(object.getString("type"));
					colorType.put("2-1-"+object.getString("type"));
				}
			}
			content.put("belong", belong);
			content.put("belong2", belong2);
			content.put("flag", flag);
			content.put("position", position);
			content.put("type", type);
			content.put("colorType", colorType);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return content;

	}
	
	
	
	
	/**
	 * 
	 * @param nodes 图数据库关系图谱的点
	 * @return 返回一个关系图谱的点集合
	 */
	public JSONArray getNodes(JSONObject nodes,JSONObject edges,JSONArray links,String companyName,BaseMongoDAL mongo,Graph g){
		Iterator iter=nodes.keys();
		JSONArray array=new JSONArray();
		while(iter.hasNext()){
			String str = (String) iter.next();
			if(companyName.equals(str)){
				continue;
			}
			try {
				JSONObject object=nodes.getJSONObject(str);
				JSONObject object2=new JSONObject();
				if("person".equals(object.getString("type"))){
					object2.put("id", object.getString("name"));
					JSONObject percont=getpersonblongs(object.getString("name"), edges);
					object2.put("flag",percont.get("flag"));
					object2.put("belong",percont.get("belong"));
					object2.put("belong2",percont.get("belong2"));
					object2.put("type",percont.get("type"));
					object2.put("position",percont.get("position"));
					object2.put("level",getLevel(companyName, object.getString("name"),g));
					object2.put("colorType",percont.get("colorType"));
					object2.put("sex","");		
				}
				if("enterprise".equals(object.getString("type"))){
					object2=getNodeobj(links,companyName,object.getString("name"),mongo,g);
				}
				array.put(object2);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return array;

	}
	
	/**
	 * 
	 * @param edges 图数据库关系图谱的边
	 * @return 返回一个关系图谱的边集合
	 */
	public JSONArray getLinks(JSONObject edges,BaseMongoDAL mongo,String companyname){
		Iterator iter=edges.keys();
		JSONArray links=new JSONArray();
		while(iter.hasNext()){
			JSONObject object=new JSONObject();
			String str = (String)iter.next();
			try {
				JSONObject object2=edges.getJSONObject(str);
				String type="";
				String flag="";
				String from=object2.getString("from");
				String to=object2.getString("to");
				String share="";
				String relationship=object2.getString("relationship");
				if(relationship.contains("监事")||relationship.contains("董事")||relationship.contains("经理")||relationship.contains("法人")){
					type="djg";
					flag="position";
					if(relationship.contains("股东")){
						flag="inv";
						if(companyname.equals(from)){
							type="djgtz";
						}else{
							type="djgrz";
						}
						share=getShare(mongo, from, to);
					}
				}
				else if(object2.getString("relationship").contains("股东")){
					if(companyname.equals(from)){
						type="dwtz";
					}else{
						type="gd";
					}
					flag="inv";
					share=getShare(mongo, from, to);

				}
				else{
					type="djg";
					flag="position";
				}
				if("djg".equals(type)){
					share="0%";
				}
				String id=from+"-"+flag+"-"+to;
				object.put("from",from);
				object.put("to",to);
				object.put("flag",flag);// 标识
				object.put("share",share); //占股比例
				object.put("type",type);// 类型
				object.put("id", id);
				links.put(object);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return links;

	}
	
//	/**
//	 * 
//	 * @return投资分类
//	 */
//	
//	public JSONObject gettouzis(JSONArray links,String companyname){
//		JSONObject touzis=new JSONObject();
//		Set dwtzs=new HashSet();
//		Set gdtzs=new HashSet();
//		Set djgtzs=new HashSet();
//		Vector<String> con=new Vector<>();
//		JSONArray dwtz=new JSONArray();//对外投资
//		JSONArray gdtz=new JSONArray();	//股东投资
//		JSONArray djgtz=new JSONArray(); //董监高投资
//		try {
//			for(int i=0;i<links.length();i++){
//				String type=links.getJSONObject(i).getString("type");
//				
//				if("dwtz".equals(type)||"djgtz".equals(type)){
//					dwtzs.add(links.getJSONObject(i).getString("to"));
//				}
//				if("gd".equals(type)||"djgrz".equals(type)||"djgtz".equals(type)){
//					if("djgtz".equals(type)){
//						gdtzs.add(links.getJSONObject(i).getString("to"));
//					}else{
//						gdtzs.add(links.getJSONObject(i).getString("from"));
//					}
//				}
//				if("djg".equals(type)||"djgrz".equals(type)||"djgtz".equals(type)){
//					if("djgtz".equals(type)){
//						djgtzs.add(links.getJSONObject(i).getString("to"));		
//					}else{
//						djgtzs.add(links.getJSONObject(i).getString("from"));
//					}
//				}
//			}	
//			for(int i=0;i<links.length();i++){
//				JSONObject object=links.getJSONObject(i);
//				for(Object str:dwtzs){
//					if(str.toString().equals(object.getString("from"))){
//						String type=object.getString("type");
//						if("gd".equals(type)||"djgrz".equals(type)||"djgtz".equals(type)){
//							con.add(links.getJSONObject(i).getString("to"));
//						}
//					}
//				}
//			}
//			
//			for(String c:con){
//				dwtzs.add(c);
//			}
//			changeSet(dwtzs,dwtz);
//			changeSet(gdtzs,gdtz);
//			changeSet(djgtzs,djgtz);
//			touzis.put("dwtz", dwtz);
//			touzis.put("gdtz", gdtz);
//			touzis.put("djgtz", djgtz);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}	
//		return touzis;
//		
//	}
//	
	/**
	 * 
	 * @return新投资分类
	 */
	
	public JSONObject gettouzis(JSONArray links,String companyname){
		JSONObject touzis=new JSONObject();
		Set dwtzs=new HashSet();
		Set gdtzs=new HashSet();
		Set djgtzs=new HashSet();
		JSONArray dwtz=new JSONArray();//对外投资
		JSONArray gdtz=new JSONArray();	//股东投资
		JSONArray djgtz=new JSONArray(); //董监高投资
		dwtzs.add(companyname);
		gdtzs.add(companyname);
		djgtzs.add(companyname);
		try {
			for(int i=0;i<links.length();i++){
				String type=links.getJSONObject(i).getString("type");
				String from=links.getJSONObject(i).getString("from");
				String to=links.getJSONObject(i).getString("to");
				//获取目标公司的股东、董监高
				if(to.equals(companyname)){
					//获取目标公司的股东
					if("gd".equals(type)||"djgrz".equals(type)||"djgtz".equals(type)){
						gdtzs.add(links.getJSONObject(i).getString("from"));
					}

					//获取目标公司董监高
					if("djg".equals(type)||"djgrz".equals(type)||"djgtz".equals(type)){
						djgtzs.add(links.getJSONObject(i).getString("from"));
					}

				}			
				else if(from.equals(companyname)){
					//获取目标公司的对外投资
					if("dwtz".equals(type)||"djgtz".equals(type)||"gd".equals(type)||"djgrz".equals(type)){
						dwtzs.add(links.getJSONObject(i).getString("to"));
					}
//					//获取董监高为目标公司的公司
//					if("djg".equals(type)||"djgrz".equals(type)||"djgtz".equals(type)){
//						djgtzs.add(links.getJSONObject(i).getString("from"));
//					}
				}
				else{
					continue;
				}
			}
			
			//获取其关联公司的股东、投资公司以及董监高
			JSONArray array=new JSONArray();
			JSONArray array2=new JSONArray();
			JSONArray array3=new JSONArray();
			//获取对外投资、股东投资以及董监高投资
			for(int i=0;i<links.length();i++){
				JSONObject object=links.getJSONObject(i);
				
				//获取目标公司投资公司的对外投资
				for(Object str:dwtzs){
					if(str.toString().equals(object.getString("from"))){
						String type=object.getString("type");
						if("gd".equals(type)||"djgrz".equals(type)||"djgtz".equals(type)){
							array.put(object.getString("to"));

						}
					}
				}
				
				//获取目标公司所有股东的投资
				for(Object str:gdtzs){
					if(str.toString().equals(object.getString("from"))){
						String type=object.getString("type");
						if("gd".equals(type)||"djgrz".equals(type)||"djgtz".equals(type)){
							array2.put(object.getString("to"));

						}
					}
				}
				
				//获取目标公司的所有董监高的投资
				for(Object str:djgtzs){
					if(str.toString().equals(object.getString("from"))){
						String type=object.getString("type");
						if("gd".equals(type)||"djgrz".equals(type)||"djgtz".equals(type)){
							array3.put(object.getString("to"));

						}
					}
				}
				
			}
			
			
			for(int i=0;i<array.length();i++){
				dwtzs.add(array.get(i));
			}
			
			for(int i=0;i<array2.length();i++){
				gdtzs.add(array2.get(i));
			}
			
			for(int i=0;i<array3.length();i++){
				djgtzs.add(array3.get(i));
			}
			
			
			changeSet(dwtzs,dwtz);
			changeSet(gdtzs,gdtz);
			changeSet(djgtzs,djgtz);
			touzis.put("dwtz", dwtz);
			touzis.put("gdtz", gdtz);
			touzis.put("djgtz", djgtz);
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		return touzis;
		
	}
	/**
	 * 
	 * @param set 集合
	 * 将set集合转化为array
	 */
	public void changeSet(Set set,JSONArray arr){
		for(Object str:set){
			arr.put(str);
		}
	}
	
	/**
	 * 
	 * @param mongo h131trade
	 * @param from 出边
	 * @param to 入边
	 * @return from所占to的比例
	 */
	public String getShare(BaseMongoDAL mongo,String from,String to){
		if (!mongo.getCollectionName().equals(BUSINESS_INFO)){
			mongo.changeCollection(BUSINESS_INFO);
		}
		JSONObject object=mongo.getOneDocument(to);
		String share="";
		if(object==null){
			return share;
		}
		try {
			JSONArray holders=object.getJSONArray("holders");
			for(int i=0;i<holders.length();i++){
				if(holders.getJSONObject(i).getString("shareholder").equals(from)){
					if(!holders.getJSONObject(i).isNull("pay_percent")){
						share=holders.getJSONObject(i).getString("pay_percent");
						break;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return share;

	}
	
	
	
	/**
	 * 
	 * @param companyName 目标公司名
	 * @param name 关联公司
	 * @param mongo 工商数据
	 * @return 关联公司相关信息
	 */
	public JSONObject getNodeobj(JSONArray links,String companyName,String name,BaseMongoDAL mongo,Graph g){
		if (!mongo.getCollectionName().equals(BUSINESS_INFO)){
			mongo.changeCollection(BUSINESS_INFO);
		}
		JSONObject information=mongo.getOneDocument(name);
		JSONObject object2=new JSONObject();
		try {
			JSONObject comcontent=getcompanyblongs(name, links);
			if(information!=null){
				information=information.getJSONObject("baseInformation");
				if(information.length()!=0){
					String money=information.getString("registered_capital");
					double regmoney=0.0;
					if(!"".equals(money)){
						regmoney=transformMoney(money);
					}
					object2.put("id", name);
					object2.put("flag",comcontent.get("flag"));
					object2.put("type",comcontent.get("type"));
					object2.put("belong",comcontent.get("belong"));
					object2.put("belong2",comcontent.get("belong2"));
					object2.put("position",comcontent.get("position"));
					object2.put("level",getLevel(companyName,name,g));
					object2.put("colorType",comcontent.get("colorType"));		
					object2.put("pripid","");
					object2.put("entName", name);
					object2.put("regNo",information.getString("register_code"));
					object2.put("entType",information.getString("type"));
					object2.put("regCap",regmoney);
					object2.put("regCapcur","万元");
					object2.put("canDate","");
					object2.put("revDate","");	
					object2.put("entStatus",information.getString("status"));
					object2.put("regOrg",information.getString("registration_authority"));
					object2.put("subConam","");
					object2.put("currency","");	
					object2.put("fundedRatio","");
					object2.put("esDate",information.getString("date"));	
					object2.put("name",information.getString("legal_representation"));
				}
				else{
					object2.put("id", name);
					object2.put("flag",comcontent.get("flag"));
					object2.put("type",comcontent.get("type"));
					object2.put("belong",comcontent.get("belong"));
					object2.put("belong2",comcontent.get("belong2"));
					object2.put("position",comcontent.get("position"));
					object2.put("level",getLevel(companyName,name,g));
					object2.put("colorType",comcontent.get("colorType"));		
					object2.put("pripid","");
					object2.put("entName", name);
					object2.put("entType","");
					object2.put("regCap","");
					object2.put("regNo","");
					object2.put("regCapcur","");
					object2.put("canDate","");
					object2.put("revDate","");	
					object2.put("entStatus","");
					object2.put("regOrg","");
					object2.put("subConam","");
					object2.put("currency","");	
					object2.put("fundedRatio","");
					object2.put("esDate","");	
					object2.put("name","");


				}
			}else{
				object2.put("id", name);
				object2.put("flag",comcontent.get("flag"));
				object2.put("type",comcontent.get("type"));
				object2.put("belong",comcontent.get("belong"));
				object2.put("belong2",comcontent.get("belong2"));
				object2.put("position",comcontent.get("position"));
				object2.put("level",getLevel(companyName,name,g));
				object2.put("colorType",comcontent.get("colorType"));		
				object2.put("pripid","");
				object2.put("entName", name);
				object2.put("entType","");
				object2.put("regCap","");
				object2.put("regCapcur","");
				object2.put("regNo","");
				object2.put("canDate","");
				object2.put("revDate","");	
				object2.put("entStatus","");
				object2.put("regOrg","");
				object2.put("subConam","");
				object2.put("currency","");	
				object2.put("fundedRatio","");
				object2.put("esDate","");	
				object2.put("name","");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object2;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * @param companyname 目标公司
	 * @param name 关联人
	 * @return 关联人与目标公司是几层关系
	 */
	public int getLevel(String companyname,String name,Graph g){	
		return handlerDegreeOfAssociation(name,g,companyname);
	}
	
	
	/**
	 * 
	 * @param company 关联公司
	 * @param links 边集合
	 * @return 关联公司在图谱中的关系
	 */
	public JSONObject getcompanyblongs(String company,JSONArray links){
		JSONObject comcontent=new JSONObject();
		JSONArray types=new JSONArray();
		JSONArray flags=new JSONArray();
		JSONArray colorTypes=new JSONArray();
		JSONArray position=new JSONArray();
		JSONArray belong=new JSONArray();
		JSONArray belong2=new JSONArray();
		try {
			for(int i=0;i<links.length();i++){
				JSONObject object=links.getJSONObject(i);
				if(!object.getString("id").contains(company)){
					continue;
				}
				String name="";
				if(object.getString("from").equals(company)){
					name=object.getString("to");
				}else{
					name=object.getString("from");
				}
				String type=object.getString("type");
				types.put(type);
				flags.put(object.getString("flag"));
				colorTypes.put("1-3-"+type);
				position.put("投资");
				belong.put(name);
				belong2.put(name+"-"+object.getString("share"));
			}
			comcontent.put("position", position);
			comcontent.put("type", types);
			comcontent.put("flag", flags);
			comcontent.put("belong", belong);
			comcontent.put("belong2", belong2);
			comcontent.put("colorType", colorTypes);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return comcontent;
		
	}
	
	/**
	 * 
	 * @param person 关联人
	 * @param edges 全部边
	 * @return 关联人在图谱中的全部关系
	 */
	public JSONObject getpersonblongs(String person,JSONObject edges){
		Iterator iter=edges.keys();
		JSONObject percontent=new JSONObject();
		JSONArray types=new JSONArray();
		JSONArray flags=new JSONArray();
		JSONArray colorTypes=new JSONArray();
		JSONArray position=new JSONArray();
		JSONArray belong=new JSONArray();
		JSONArray belong2=new JSONArray();
		try {
			while(iter.hasNext()){
				JSONObject object=new JSONObject();
				String str = (String) iter.next();
				if(!str.contains(person)){
					continue;
				}
				JSONObject object2=edges.getJSONObject(str);
				String[] relationships=object2.getString("relationship").split(",");
				String company=object2.getString("to");
				for(String relationship:relationships){
					String type="position";
					String flag="djg";
					if(relationship.contains("监事")||relationship.contains("董事")||relationship.contains("经理")||relationship.contains("法人")){
						flag="position";
						type="djg";

					}
					if(relationship.contains("股东")){
						flag="inv";
						type="gd";
					}
					String colorType="1-2-"+type;
					position.put(relationship);
					types.put(type);
					flags.put(flag);
					belong.put(company);
					belong2.put(company+"-"+relationship);
					colorTypes.put(colorType);
				}
			}
			percontent.put("position", position);
			percontent.put("type", types);
			percontent.put("flag", flags);
			percontent.put("belong", belong);
			percontent.put("belong2", belong2);
			percontent.put("colorType", colorTypes);
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		return percontent;

	}
	
	
	
	/**
	 *  
	 * @param name 关联方
	 * @param g 图谱
	 * @param companyName 目标公司
	 * @return 关联方到目标公司的最短距离
	 * @throws JSONException
	 */
	private static int  handlerDegreeOfAssociation(String name,Graph g,String companyName){
		List<String> path = DiscribeOperate.shortestDistance(g, companyName, name);
		int companyIndex = path.indexOf(companyName);
		int nameIndex=path.indexOf(name);
		int distance=companyIndex-nameIndex;
		return distance;
	}

	
	
	
	/**
	 * 
	 * @param money 注册资本
	 * @return 换算后的注册资本
	 */

    private static double transformMoney(String money){

        if(money.contains("美")){
            //换算
            return transformMoney(money,1);
        }else if(money.contains("港")){
            return transformMoney(money,3);
        }else if(money.contains("欧")){
            return transformMoney(money,2);
        }else if(money.contains("日")){
            return transformMoney(money,4);
        }else {
            return transformMoney(money,0);
        }
    }


    /**
     * 返回****元
     * @param needTransformMoney
     * @param code
     * @return
     */
    private static double transformMoney(String needTransformMoney, int code) {
        switch (code){
            case 1://美元
            {
                String register = needTransformMoney.replace("美","").replace("元","");
                return handlerMoney(register)*6.9447;
            }

            case 2://欧元
            {
                String register = needTransformMoney.replace("欧","").replace("元","");
                return handlerMoney(register)*7.2629;
            }

            case 3://港币
            {
                String register = needTransformMoney.replace("港","").replace("币","").replace("元","");
                return handlerMoney(register)*0.8945;
            }
            case 4://日元
            {
                String register = needTransformMoney.replace("日","").replace("元","");
                return  handlerMoney(register)*0.05921;

            }
            case 0:{
                return handlerMoney(needTransformMoney);

            }
            default:{
                return handlerMoney(needTransformMoney);
            }
        }

    }

    private static double handlerMoney(String payActual) {
        double pay = 0;
        payActual = payActual.replaceAll("\\s+", "").replace("（", "").replace("）", "").replace("(", "").replace(")",
                "").replace("人民币","").replace(",","").replace("\\r","").replace("&nbsp;","").replace("，","")
        .replace("-","");

        if (payActual.matches("\\d+(\\.\\d+)?(万)(元)?")) {
            payActual = payActual.replace("万", "").replace("元", "");
            pay = Double.parseDouble(payActual) * 10000;
        } else if (payActual.matches("\\d+(\\.\\d+)?元?")) {
            payActual = payActual.replace("元", "");
            pay = Double.parseDouble(payActual);
        }
        pay=pay/10000;

        return pay;
    }
	
   
}
