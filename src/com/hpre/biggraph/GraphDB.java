package com.hpre.biggraph;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpre.biggraph.client.Batch;
import com.hpre.biggraph.client.Command;
import com.hpre.biggraph.client._Edge;
import com.hpre.biggraph.client._Vertex;
import com.hpre.biggraph.client.enter.graph.Graph;
import com.hpre.biggraph.client.enter.graph.Entity.EntityType;

public class GraphDB {
	public static final Log LOG = LogFactory.getLog(GraphDB.class);
	
	public static final String DEFAULT_VERTEX_LABEL = "defaultV";
	public static final String DEFAULT_EDGE_LABEL = "defaultE";
	
	public static final String DIRECT = ">";
	
	public static final String VID = "vid";
	public static final String EID = "eid";
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String IDENTITY = "identity";
	public static final String RELATION = "relation";
	
	public static final String VID_IDX = "vid_idx";
	public static final String EID_IDX = "eid_idx";
	public static final String NAME_IDX = "name_idx";
	public static final String REL_IDX = "rel_idx";
	
	public static final String MIXED_INDEXER_NAME = "search";
	public static Connection conn = null;
	public static Statement stmt = null;
	public static PreparedStatement prestm =  null;
	static{
		try {
			Class.forName("org.neo4j.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public GraphDB(String properties) {
			try {
				conn = DriverManager.getConnection("jdbc:neo4j://h129:7474/","sjcjb","hpre&-*123");
				
//				conn = DriverManager.getConnection("jdbc:neo4j://localhost:7474/","neo4j","star");
//				conn = DriverManager.getConnection(MyConnConfigure.url,MyConnConfigure.name,MyConnConfigure.pass);
		
//				stmt = conn.createStatement();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	public void close() {
		try {
			if(stmt!=null)
				stmt.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		try {
			if(conn!=null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void batchUpdateInsertVertexes(List<_Vertex> _vertexes) throws Exception {
		if (_vertexes.isEmpty())
			return;
		System.out.println(conn);
		conn.setAutoCommit(false);
		PreparedStatement prestm = conn.prepareStatement("merge (n:stu {name:?}) on create set n.type=?,n.identity=? on match set n.type=?,n.identity=?");
		for (_Vertex _Vertex : _vertexes) {
//			createVertexesOrUpdate(_Vertex);
			prestm.setString(1, _Vertex.name);
			prestm.setString(2, _Vertex.type);
			prestm.setString(3, _Vertex.identity);
			prestm.setString(4, _Vertex.type);
			prestm.setString(5, _Vertex.identity);
			
//			String sql = getNodeCql(_Vertex);
//			System.out.println(sql);

			prestm.addBatch();
		}
		System.out.println(prestm.execute());
//		int[] arr = prestm.execute();
//		System.out.println(arr.length);
//		for (int i : arr) {
//			System.out.println(i);
//		}
		conn.commit();
//		conn.commit();
	}
	public String getNodeCql(_Vertex _Vertex){
		String sql = "merge (n:stu {name:'"+_Vertex.name+"'}) on create set n.type='"+_Vertex.type+"',n.identity='"+_Vertex.identity+"' on match set n.type='"+_Vertex.type+"',n.identity='"+_Vertex.identity+"' return n";
//		System.out.println(sql);
		return sql;
	}
	
	public boolean createVertexesOrUpdate(_Vertex _Vertex) throws Exception{
//		String sql = "merge (n:stu {name:'"+_Vertex.name+"'}) on create set n.type='"+_Vertex.type+"',n.identity='"+_Vertex.identity+"' return n";
		
		String sql = getNodeCql(_Vertex);
		return stmt.execute(sql);
	}
	
	
	public void batchDeleteVertexes(List<_Vertex> _vertexes) throws Exception {
		if (_vertexes.isEmpty())
			return;
		for (_Vertex _Vertex : _vertexes) {
			deleteVertex(_Vertex.name);
		}
	}
	
	public void batchUpdateInsertEdges(List<_Edge> _edges) throws Exception {
		if (_edges.isEmpty()) 
			return;
		conn.setAutoCommit(false);
		for (_Edge _Edge : _edges) {
			System.out.println(updateInsertEdges(_Edge));
		}
		conn.commit();
	}
	 public void batchDeleteEdges(List<_Edge> _edges) throws Exception{
		 if (_edges.isEmpty()) 
				return;
		for (_Edge _Edge : _edges) {
			System.out.println(deleteEdge(_Edge));
		}
	 }
	
	
	private boolean deleteEdge(_Edge _Edge) throws Exception {
		String sql = "MATCH (a:stu)-[r:gra]->(b:stu) WHERE a.name = '"+_Edge.to+"' AND b.name = '"+_Edge.from+"' DELETE r;";
		return stmt.execute(sql);
	}

	private boolean updateInsertEdges(_Edge _Edge) throws Exception {
		String sql = "MATCH (m:stu { name:'"+_Edge.from+"' }) MATCH (p:stu { name:'"+_Edge.to+"' }) "
				+ "MERGE (p)-[r:gra]-(m) ON "
				+ "CREATE SET r.relation ='"+_Edge.relation+"',r.name='"+_Edge.name+"',r.from='"+_Edge.from+"',r.to='"+_Edge.to+"' "
						+ "on match set r.relation ='"+_Edge.relation+"',r.name='"+_Edge.name+"',r.from='"+_Edge.from+"',r.to='"+_Edge.to+"' RETURN p,r,m ;";
		
//		String sql = "merge (p:stu{name:'"+_Edge.from+"'})-[r:gra]-(m:stu{name:'"+_Edge.to+"'}) on create set  r.relation ='"+_Edge.relation+"',r.name='"+_Edge.name+"',r.from='"+_Edge.from+"',r.to='"+_Edge.to+"' on match set r.relation ='"+_Edge.relation+"',r.name='"+_Edge.name+"',r.from='"+_Edge.from+"',r.to='"+_Edge.to+"' return p,r,m";
		
		return stmt.execute(sql);
	}
	

	public boolean deleteVertex(String name) throws Exception {
		String sql = "MATCH (e:stu) where e.name='"+name+"' DELETE e";
		return stmt.execute(sql);
	}
	
	public Graph extension(String name, int layer) throws Exception {
		String sql = "MATCH (n:stu{name:'"+name+"'})-[r:gra*1.."+layer+"]-(relateNode) return r,relateNode,n";
		ResultSet rs = stmt.executeQuery(sql);
		Graph graph = new Graph();
		graph.companyName = name;
		while(rs.next()){
//			System.out.println(rs.getString(1));
//			System.out.println(rs.getString(2));
//			System.out.println(rs.getString(3));
//			System.out.println();
			
			//关系
			JSONArray arr = new JSONArray(rs.getString(1));
			for (int i = 0; i < arr.length(); i++) {
				JSONObject one_gra =  arr.getJSONObject(i);
				graph.addRelationship(one_gra.getString("from"), one_gra.getString("to"), one_gra.getString("relation"));
			}
			
			//节点
			JSONObject node =  new JSONObject(rs.getString(2));
			graph.addEntity(node.getString("type"), node.getString("name"), node.getString("identity"));
			
			//主节点
			JSONObject root_node =  new JSONObject(rs.getString(3));
			graph.addEntity(root_node.getString("type"), root_node.getString("name"), root_node.getString("identity"));
			
		}
		rs.close();
		return graph;
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public void batchExec(Batch batch) throws Exception {
		List<_Vertex> updateV = new LinkedList<_Vertex>();
		List<_Vertex> deleteV = new LinkedList<_Vertex>();
		List<_Edge> insertE = new LinkedList<_Edge>();
		
		for (Command cmd: batch.cmds) {
			// ת�����ֲ�json��ʧ�������Ϣ
			if (cmd.cmdObject instanceof LinkedHashMap) {
				LinkedHashMap map = (LinkedHashMap)cmd.cmdObject;
				
				switch (cmd.type) {
				case updateInsertVertex:
				case deleteVertex: {
					_Vertex _v = new _Vertex((String) map.get(TYPE), 
							(String) map.get(NAME), 
							(String) map.get(IDENTITY));
					cmd.cmdObject = _v;
					break;
					}
				case updateInsertEdge: {
					_Edge _e = new _Edge((String) map.get(RELATION), 
							(String) map.get("from"), 
							(String) map.get("to"));
					cmd.cmdObject = _e;
					break;
					}
				default: break;
				}
			}
			
			switch (cmd.type) {
			case updateInsertVertex:
				updateV.add((_Vertex)cmd.cmdObject);
				break;
			case deleteVertex:
				deleteV.add((_Vertex)cmd.cmdObject);
				break;
			case updateInsertEdge:
				insertE.add((_Edge)cmd.cmdObject);
				break;
			default:
				break;
			}
		}
		
		batchUpdateInsertVertexes(updateV);
		batchDeleteVertexes(deleteV);
		batchUpdateInsertEdges(insertE);
	}
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		GraphDB graphDB = new GraphDB("/conf/titan-cassandra-es.properties");
		System.out.println("用时" + ((System.currentTimeMillis() - start)));
		List<_Vertex> _Vertexs = new ArrayList<_Vertex>();
		for (int i = 0; i < 10; i++) {
			_Vertex v = new _Vertex(""+i, ""+i, ""+i);
			_Vertexs.add(v);
		}


//		graphDB.batchUpdateInsertVertexes(_Vertexs);
//		graphDB.batchDeleteVertexes(_Vertexs);

		List<_Edge> _edges = new ArrayList<_Edge>();
		for (int i = 0; i < 9; i++) {
			_Edge d = new _Edge("主要人员",""+i , ""+(i+1));
			_edges.add(d);
		}
//		graphDB.batchUpdateInsertEdges(_edges);

//		graphDB.batchDeleteEdges(_edges);

		Graph graph = graphDB.extension("华为技术有限公司", 2);
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writeValueAsString(graph));
//		ObjectMapper mapper = new ObjectMapper();
//
//		graphDB.deleteVertex("A");
//		graphDB.deleteVertex("B");
//		graphDB.deleteVertex("C");
//		graphDB.deleteVertex("COM1");
//		graphDB.deleteVertex("COM2");
//		graphDB.deleteVertex("COM3");

//		Batch batch = new Batch();
//		batch.updateInsertEntity(EntityType.person, "A", "�ɶ�");
//		batch.updateInsertEntity(EntityType.person, "B", "�ɶ�");
//		batch.updateInsertEntity(EntityType.person, "C", "�߹�");
//		batch.updateInsertEntity(EntityType.enterprise, "COM1", "");
//		batch.updateInsertEntity(EntityType.enterprise, "COM2", "");
//		batch.updateInsertEntity(EntityType.enterprise, "COM3", "");
//		batch.updateInsertRelationship("A", "COM1", "Ͷ��");
//		batch.updateInsertRelationship("B", "COM1", "Ͷ��");
//		batch.updateInsertRelationship("B", "COM2", "Ͷ��");
//		batch.updateInsertRelationship("COM2", "COM1", "Ͷ��");
//		batch.updateInsertRelationship("COM1", "COM3", "Ͷ��");
//		batch.updateInsertRelationship("C", "COM2", "Ͷ��");
//		batch.updateInsertEntity(EntityType.enterprise, "COM1", "�Ǻ�");
//		batch.updateInsertEntity(EntityType.person, "A", "����");
//		batch.updateInsertEntity(EntityType.person, "D", "����");
//		batch.deleteVertex("D");
//		batch.deleteVertex("B");
//		graphDB.batchExec(batch);

//		graphDB.insertEdge("Ͷ��2", "A", "COM1");

//		System.err.println(mapper.writeValueAsString(graphDB.extension("COM1", Integer.MAX_VALUE)));
//
//		graphDB.graph.close();
	}
}
