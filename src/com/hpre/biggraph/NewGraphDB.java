package com.hpre.biggraph;

import static org.neo4j.driver.v1.Values.parameters;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpre.biggraph.client.Batch;
import com.hpre.biggraph.client.Command;
import com.hpre.biggraph.client._Edge;
import com.hpre.biggraph.client._Vertex;
import com.hpre.biggraph.client.enter.graph.Entity;
import com.hpre.biggraph.client.enter.graph.Graph;
import com.hpre.biggraph.client.enter.graph.Entity.EntityType;

public class NewGraphDB {
	public static final Log LOG = LogFactory.getLog(NewGraphDB.class);
	
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
	public static PreparedStatement prestm =  null;
	private Driver driver;
	
	
	
	
	public NewGraphDB(String properties) {
			try {
//				conn = DriverManager.getConnection("jdbc:neo4j://h129:7474/","sjcjb","hpre&-*123");
//				driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "star" ) );
//				driver = GraphDatabase.driver( "bolt://h129:7687", AuthTokens.basic( "sjcjb", "hpre&-*123" ) );
				driver = GraphDatabase.driver( MyConnConfigure.url, AuthTokens.basic( MyConnConfigure.name, MyConnConfigure.pass ) );
//				conn = DriverManager.getConnection("jdbc:neo4j://localhost:7474/","neo4j","star");
//				conn = DriverManager.getConnection(MyConnConfigure.url,MyConnConfigure.name,MyConnConfigure.pass);
		
//				stmt = conn.createStatement();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public void close() {
		driver.close();
	}
	
	/**
	 * 批量执行cql语句
	 * @param CQLS
	 * @throws Exception
	 */
	public void batchExecute(List<String> CQLS) throws Exception{
		 if (CQLS.isEmpty()) 
				return;
		 try ( Session session = driver.session() )
			{
				for (String cql : CQLS) {
					try ( Transaction tx = session.beginTransaction() )
					{
						tx.run(cql);
						tx.success();
					}
				}
			}
	 }
	
	
	
	
	/**
	 * 获取插入节点的cql语句
	 * @param _Vertex
	 * @return
	 */
	private String getNodeCql(_Vertex _Vertex){
		String sql = "merge (n:law {name:'"+_Vertex.name+"', root:'"+_Vertex.root+"' }) on create set n.type='"+_Vertex.type+"',n.identity='"+_Vertex.identity+"',n.root='"+_Vertex.root+"' on match set n.type='"+_Vertex.type+"',n.identity='"+_Vertex.identity+"',n.root='"+_Vertex.root+"' return n";
		return sql;
	}
	/**
	 * 插入一个节点
	 * @param _Vertex
	 * @return
	 * @throws Exception
	 */
	public boolean createVertexesOrUpdate(_Vertex _Vertex) throws Exception{
		if(_Vertex==null)
			return false;
		List<_Vertex> _vertexes = new ArrayList<_Vertex>();
		_vertexes.add(_Vertex);
		batchUpdateInsertVertexes(_vertexes);
		return true;
	}
	
	
	/**
	 * 批量插入节点
	 * @param _vertexes
	 * @throws Exception
	 */
	public void batchUpdateInsertVertexes(List<_Vertex> _vertexes) throws Exception {
		if (_vertexes.isEmpty())
			return;
		List<String> CQLS = new ArrayList<String>();
		for (_Vertex _Vertex : _vertexes) {
			CQLS.add(getNodeCql(_Vertex));
		}
		batchExecute(CQLS);
	}
	
	
	/**
	 * 获取删除节点及其关系的cql
	 * @param name
	 * @return
	 */
	private String getDeleteVertexesCQL(String name){
//		MATCH (cc: CreditCard)-[rel]-(c:Customer) DELETE cc,c,rel
		String sql = "MATCH (e:law)-[r]-(relateNode) where e.name='"+name+"' DELETE e,r";
//		System.out.println(sql);
		return sql;
	}
	
	
	/**
	 * 批量删除边节点
	 * @param _vertexes
	 * @throws Exception
	 */
	public void batchDeleteVertexes(List<_Vertex> _vertexes) throws Exception {
		if (_vertexes.isEmpty())
			return;
		List<String> CQLS = new ArrayList<String>();
		for (_Vertex _Vertex : _vertexes) {
			CQLS.add(getDeleteVertexesCQL(_Vertex.name));
		}
		batchExecute(CQLS);
	}
	
	
	
	/**
	 * 获取新增边关系的cql
	 * @param _Edge
	 * @return
	 */
	private String getInsertEdecql(_Edge _Edge){
		String sql = "MATCH (m:law { name:'"+_Edge.from+"' , root:'"+_Edge.root+"' }) MATCH (p:law { name:'"+_Edge.to+"', root:'"+_Edge.root+"' }) "
				+ "MERGE (p)-[r:gra]-(m) ON "
				+ "CREATE SET r.relation ='"+_Edge.relation+"',r.name='"+_Edge.name+"',r.from='"+_Edge.from+"',r.to='"+_Edge.to+"',r.root='"+_Edge.root+"' "
						+ "on match set r.relation ='"+_Edge.relation+"',r.name='"+_Edge.name+"',r.from='"+_Edge.from+"',r.to='"+_Edge.to+"',r.root='"+_Edge.root+"' RETURN p,r,m ;";
//		System.out.println(sql);
		return sql;
	}
	
	/**
	 * 插入一条边关系
	 * @param _Edge
	 * @return
	 * @throws Exception
	 */
	public boolean updateInsertEdges(_Edge _Edge) throws Exception {
		if(_Edge==null)
			return false;
		
		List<_Edge> _edges = new ArrayList<_Edge>();
		_edges.add(_Edge);
		batchUpdateInsertEdges(_edges);
		return true;
	}
	
	/**
	 * 批量插入边关系
	 * @param _edges
	 * @throws Exception
	 */
	public void batchUpdateInsertEdges(List<_Edge> _edges) throws Exception {
		if (_edges.isEmpty()) 
			return;
		List<String> CQLS = new ArrayList<String>();
		for (_Edge _Edge : _edges) {
			if(_Edge.from.equals(_Edge.to))
				continue;
			CQLS.add(getInsertEdecql(_Edge));
		}
		batchExecute(CQLS);
	}
	
	
	
	
	/**
	 * 批量删除边关系
	 * @param _edges
	 * @throws Exception
	 */
	 public void batchDeleteEdges(List<_Edge> _edges) throws Exception{
		 if (_edges.isEmpty()) 
				return;
		 List<String> CQLS = new ArrayList<String>();
		 
		 for (_Edge _Edge : _edges) {
			 CQLS.add(getdeleteEdgeCQl(_Edge));
		 }
		 batchExecute(CQLS);
	 }
	
	private String getdeleteEdgeCQl(_Edge _Edge){
		String sql = "MATCH (a:law)-[r:gra]->(b:law) WHERE a.name = '"+_Edge.to+"' AND b.name = '"+_Edge.from+"' DELETE r;";
		return sql;
	}
	public boolean deleteEdge(_Edge _Edge) throws Exception {
		if(_Edge==null)
			return false;
		List<_Edge> _edges = new ArrayList<_Edge>();
		_edges.add(_Edge);
		batchDeleteEdges(_edges);
		return true;
	}
	
	

	public boolean deleteVertex(String name) throws Exception {
		List<_Vertex> _vertexes = new ArrayList<_Vertex>();
		_vertexes.add(new _Vertex("", name, ""));
		batchDeleteVertexes(_vertexes);
		return true;
	}
	
	
	
	public Graph extension(String name, int layer) throws Exception {
		if(layer<0){
			layer = 0;
		}else if(layer>3){			
			layer = 3;
		}
		String sql = "MATCH (n:law{name:'"+name+"'})-[r:gra*1.."+layer+"]-(relateNode) return r,relateNode,n";
		Graph graph = new Graph();
		graph.companyName = name;
		Map<String, Node> peoples = new HashMap<String, Node>();
		Map<String, Node> allNodes = new HashMap<String, Node>();
		List<Path> edges =  new ArrayList<Path>();
		try ( Session session = driver.session())
		{
			 try ( Transaction tx = session.beginTransaction() )
			    {
//				 System.out.println(sql);
			        StatementResult result = tx.run(sql);
			        while ( result.hasNext() )
			        {
			            Record record = result.next();
			            List<Object> rels =  record.get( "r" ).asList();
			            Path path = new Path();
			            for (Object rel : rels) {
			            	Relationship one_gra = (Relationship) rel;
//			            	graph.addRelationship(one_gra.get("from").asString(), one_gra.get("to").asString(), one_gra.get("relation").asString());
			            	
			            	path.addPath(one_gra);
			            }
			            
			            Node node = record.get( "relateNode" ).asNode();
//			            graph.addEntity(node.get("type").asString(), node.get("name").asString(), node.get("identity").asString());
			            
			            path.setEnd(node.get("name").asString());
			            allNodes.put(node.get("name").asString(), node);
//			            System.out.println(node.get("type").asString());
			            if(node.get("type").asString().equals("person")){
			            	peoples.put(node.get("name").asString(), node);
			            }
			            
			            Node root_node = record.get( "n" ).asNode();
			            graph.addEntity(root_node.get("type").asString(), root_node.get("name").asString(), root_node.get("identity").asString());
			            
			            
			            path.setStart(root_node.get("name").asString());
			            allNodes.put(root_node.get("name").asString(), root_node);
			            
			            
			            edges.add(path);
			        }
			    }
		}
		if(!allNodes.containsKey(name)){
			graph.addEntity(EntityType.enterprise, name, "");
		}
		traversal(graph,allNodes,peoples,edges);
		
		
		
		
		return graph;
	}
	
	
	
	private void traversal(Graph graph, Map<String, Node> allNodes,
			Map<String, Node> peoples, List<Path> edges) {
		for (Path path : edges) {
			if(path.judge(peoples)){
				List<Relationship> rels = path.getPath();
				for (Relationship relationship : rels) {
					String from = relationship.get("from").asString();
					String to = relationship.get("to").asString();
					if(from.equals(to)){
						continue;
					}
					if(allNodes.containsKey(from) && allNodes.containsKey(to)){
						Node from_node = allNodes.get( from );
			            graph.addEntity(from_node.get("type").asString(), from_node.get("name").asString(), from_node.get("identity").asString());
			            
			            Node to_node = allNodes.get( to );
			            graph.addEntity(to_node.get("type").asString(), to_node.get("name").asString(), to_node.get("identity").asString());
			            
			            graph.addRelationship(relationship.get("from").asString(), relationship.get("to").asString(), relationship.get("relation").asString());
		            	
					}
				}
			}
		}
		
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
		NewGraphDB graphDB = new NewGraphDB("/conf/titan-cassandra-es.properties");
		System.out.println("用时" + ((System.currentTimeMillis() - start)));
		List<_Vertex> _Vertexs = new ArrayList<_Vertex>();
		for (int i = 0; i < 10; i++) {
			_Vertex v = new _Vertex(""+i, ""+i, ""+i);
			_Vertexs.add(v);
		}
		
		
//		graphDB.batchUpdateInsertVertexes(_Vertexs);
//		graphDB.batchDeleteVertexes(_Vertexs);
//		graphDB.deleteVertex("1");
		
		
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
