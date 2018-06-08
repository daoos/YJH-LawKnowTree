package com.hpre.biggraph;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpre.biggraph.client.CommandType;
import com.hpre.biggraph.client.enter.graph.Graph;
import com.hpre.biggraph.newextension.BaseMongoDAL;
import com.hpre.biggraph.newextension.MongoConfigure;
import com.hpre.biggraph.newextension.MyMongo;
import com.hpre.biggraph.newextension.Tuinformation;

public class ExtensionService extends HttpServlet {
private ObjectMapper mapper = null;
	
	@Override 
	public void init() {
		mapper = new ObjectMapper();
	}
	/**
	 * Constructor of the object.
	 */
	public ExtensionService() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		CommandType cmd = CommandType.valueOf(request.getHeader("X-Command"));
		String name = URLDecoder.decode(
				request.getParameter("name"), "UTF-8");
		String layer = request.getParameter("layer");
		
		NewGraphDB gradb = null;
		BaseMongoDAL mongo = null;
		JSONObject result=null;
		Graph g;
		try {
			gradb =  new NewGraphDB(null);
			g = gradb.extension(name, Integer.parseInt(layer));
			JSONObject object=new JSONObject(mapper.writeValueAsString(g));
			
			mongo = new MyMongo(MongoConfigure.dbOnline,"businessinfo");
			Tuinformation test=new Tuinformation();
			result =test.getJson(object, mongo);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(gradb!=null)
				gradb.close();
			if(mongo!=null)
				mongo.close();
		}
		
		out.print(result);
		out.flush();
		out.close();
	}

	

}
