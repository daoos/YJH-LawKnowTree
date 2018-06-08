package com.hpre.biggraph;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpre.biggraph.client.Batch;
import com.hpre.biggraph.client.CommandType;
import com.hpre.biggraph.client.enter.graph.Graph;
import com.qidian.email.EmailMonitor;


public class GraphService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ObjectMapper mapper = null;
	
	@Override 
	public void init() {
		mapper = new ObjectMapper();
	}
	
	






	@Override
	public void destroy() {
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter output = response.getWriter();
		NewGraphDB graphDB = new NewGraphDB(null);
		CommandType cmd = CommandType.valueOf(request.getHeader("X-Command"));
		switch (cmd) {
//		case insertVertex: {
//			String type = request.getParameter("type");
//			String name = URLDecoder.decode(
//					request.getParameter("name"), "UTF-8");
//			String identity = URLDecoder.decode(
//					request.getParameter("identity"), "UTF-8");
//				
//			graphDB.insertVertex(name, type, identity);
//			break;
//		}
//		
//		case insertEdge: {
//			String from = URLDecoder.decode(
//					request.getParameter("from"), "UTF-8");
//			String to = URLDecoder.decode(
//					request.getParameter("to"), "UTF-8");
//			String relationship = URLDecoder.decode(
//					request.getParameter("relationship"), "UTF-8");
//			
//			graphDB.insertEdge(relationship, from, to);
//			break;
//		}
//		
//		case deleteVertex: {
//			String name = URLDecoder.decode(
//					request.getParameter("name"), "UTF-8");
//			
//			graphDB.deleteVertex(name);
//			break;
//		}
		
			case extension: {
				String name = URLDecoder.decode(
						request.getParameter("name"), "UTF-8");
				String layer = request.getParameter("layer");
				
				Graph g;
				try {
					g = graphDB.extension(name, Integer.parseInt(layer));
					output.print(mapper.writeValueAsString(g));
				} catch (Exception e) {
//					EmailMonitor.sendEmail("图数据库查询出错--"+name, "图数据库查询出错--"+name, MyConnConfigure.receivers);
					e.printStackTrace();
				}
				break;
			}
			
			case batchCmd: {
				String batchCmds = URLDecoder.decode(
						request.getParameter("cmds"), "UTF-8");
				Batch batch = mapper.readValue(batchCmds, Batch.class);
				try {
					graphDB.batchExec(batch);
				} catch (Exception e) {
//					EmailMonitor.sendEmail("图数据库批量操作出错--", batchCmds, MyConnConfigure.receivers);
					e.printStackTrace();
				}
				break;
			}
			default: break;
		}
		graphDB.close();
		output.close();
	}
}
