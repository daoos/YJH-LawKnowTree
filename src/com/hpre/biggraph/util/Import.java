package com.hpre.biggraph.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hpre.biggraph.LoadConfigListener;
import com.hpre.biggraph.MyConnConfigure;
import org.json.JSONObject;

import com.hpre.biggraph.GraphDB;
import com.hpre.biggraph.NewGraphDB;
import com.hpre.biggraph.client._Edge;
import com.hpre.biggraph.client._Vertex;
import com.hpre.biggraph.client.enter.graph.Edge;

public class Import {
	public static void main(String[] args) throws Exception {
		LoadConfigListener loadConfigListener=new LoadConfigListener();
		loadConfigListener.contextInitialized(null);
		importNode("/home/hadoop/wnd/usr/leagal/logs/vertex.txt");
		importEdge("/home/hadoop/wnd/usr/leagal/logs/edges.txt");
	}
	
	public static void importNode(String filePath) throws Exception{
		FileReader re =  new FileReader(filePath);
		BufferedReader read = new BufferedReader(re );
		String str = null;
		NewGraphDB db = new NewGraphDB(null);
		int i = 0;
		List<_Vertex> _Vertexs = new ArrayList<_Vertex>();
		while((str=read.readLine())!=null){
			try {
				System.out.println(str);
				JSONObject obj = new JSONObject(str);
				_Vertex v = new _Vertex( obj.getString("type"), obj.getString("name"),obj.getString("identity"),obj.getString("root"));
				_Vertexs.add(v);
				i++;
				if(i>20){
					i=0;
					db.batchUpdateInsertVertexes(_Vertexs);
					_Vertexs.clear();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		db.batchUpdateInsertVertexes(_Vertexs);
		db.close();
		read.close();
	}
	
	
	public static void importEdge(String filePath) throws Exception{
		FileReader re =  new FileReader(filePath);
		BufferedReader read = new BufferedReader(re );
		String str = null;
		NewGraphDB db = new NewGraphDB(null);
		int i = 0;
		List<_Edge> _edges = new ArrayList<_Edge>();
		while((str=read.readLine())!=null){
			try {
				System.out.println(str);
				JSONObject obj = new JSONObject(str);
				_Edge edge = new _Edge(obj.getString("relation").replace("\\", "、"), obj.getString("from").replace("\\", "、"),obj.getString("to").replace("\\", "、"),obj.getString("root"));
				_edges.add(edge);
				i++;
				if(i>20){
					i=0;
					db.batchUpdateInsertEdges(_edges);
					_edges.clear();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		db.batchUpdateInsertEdges(_edges);
		db.close();
		read.close();
	}
}
