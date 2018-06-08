package com.hpre.biggraph;

public class CopyOfGraphDB {
//public class CopyOfGraphDB {
//	public static final Log LOG = LogFactory.getLog(CopyOfGraphDB.class);
//	
//	public static final String DEFAULT_VERTEX_LABEL = "defaultV";
//	public static final String DEFAULT_EDGE_LABEL = "defaultE";
//	
//	public static final String DIRECT = ">";
//	
//	public static final String VID = "vid";
//	public static final String EID = "eid";
//	public static final String NAME = "name";
//	public static final String TYPE = "type";
//	public static final String IDENTITY = "identity";
//	public static final String RELATION = "relation";
//	
//	public static final String VID_IDX = "vid_idx";
//	public static final String EID_IDX = "eid_idx";
//	public static final String NAME_IDX = "name_idx";
//	public static final String REL_IDX = "rel_idx";
//	
//	public static final String MIXED_INDEXER_NAME = "search";
//	
//	private TitanGraph graph = null;
//	
//	public CopyOfGraphDB(String properties) {
//		graph = TitanFactory.open(properties);
//		constructVertexAndEdge();
//	}
//	
//	public void close() {
//		graph.close();
//	}
//	
//	/**
//	 * ���㣺{label:defaultV, vid, name, type, identity}, CompositeIdx: {vid_idx:uniqe}
//	 * �ߣ�{label:defaultE, eid, relation}, CompositeIdx: {eid_idx}, MixedIdx: {REL_IDX:TEXTSTRING} 
//	 */
//	public void constructVertexAndEdge() {
//		TitanManagement mgmt = graph.openManagement();
//		
//		PropertyKey vid = getOrCreatePropertyKey(mgmt, VID, String.class);
//		PropertyKey eid = getOrCreatePropertyKey(mgmt, EID, String.class);
//		PropertyKey name = getOrCreatePropertyKey(mgmt, NAME, String.class);
//		getOrCreatePropertyKey(mgmt, IDENTITY, String.class);
//		getOrCreatePropertyKey(mgmt, TYPE, String.class);
//		PropertyKey relation = getOrCreatePropertyKey(mgmt, RELATION, String.class);
//		
//		// Composite Index
//		if (!mgmt.containsGraphIndex(VID_IDX)) {
//			TitanGraphIndex index = mgmt.buildIndex(VID_IDX, Vertex.class)
//										.addKey(vid)
//										.unique()
//										.buildCompositeIndex();
//			
//			mgmt.setConsistency(vid, ConsistencyModifier.LOCK); // Ensures only one vid per vertex
//			mgmt.setConsistency(index, ConsistencyModifier.LOCK); // Ensures vid uniqueness in the graph
//		}
//		if (!mgmt.containsGraphIndex(NAME_IDX)) {
//			mgmt.buildIndex(NAME_IDX, Vertex.class)
//				.addKey(name)
//				.buildCompositeIndex();
//		}
//		if (!mgmt.containsGraphIndex(EID_IDX)) {
//			mgmt.buildIndex(EID_IDX, Edge.class)
//					.addKey(eid)
//					.buildCompositeIndex();
//		}
//		
//		// Mixed Index
//		if (!mgmt.containsGraphIndex(REL_IDX)) {
//			mgmt.buildIndex(REL_IDX, Edge.class)
//					.addKey(relation, Mapping.TEXTSTRING.asParameter())
//					.buildMixedIndex(MIXED_INDEXER_NAME);
//		}
//		
//		// Make Label
//		if (!mgmt.containsVertexLabel(DEFAULT_VERTEX_LABEL))
//			mgmt.makeVertexLabel(DEFAULT_VERTEX_LABEL);
//		if (!mgmt.containsEdgeLabel(DEFAULT_EDGE_LABEL))
//			mgmt.makeEdgeLabel(DEFAULT_EDGE_LABEL);
//		mgmt.commit();
//	}
//	
//	private static PropertyKey getOrCreatePropertyKey(TitanManagement mgmt, 
//			String keyName, Class<?> clasz) {
//		PropertyKey key = null;
//		if (!mgmt.containsPropertyKey(keyName))
//			key = mgmt.makePropertyKey(keyName).dataType(clasz).make();
//		else 
//			key = mgmt.getPropertyKey(keyName);
//		
//		return key;
//	}
//	
//	/**
//	 * ��������ڵ�
//	 * 
//	 * _VertexΪ��ʱ�洢��
//	 */
//	public void batchInsertVetexes(List<_Vertex> _vertexes) {
//		if (_vertexes.isEmpty())
//			return;
//		// ���붥��
//		TitanTransaction tx = graph.newTransaction();
//		for (_Vertex v: _vertexes) {
//			try {
//				tx.addVertex(T.label, DEFAULT_VERTEX_LABEL, VID, v.name,
//						NAME, v.name, TYPE, v.type, IDENTITY, v.identity);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		tx.commit();
//		tx.close();
//	}
//	
//	public void insertVertex(String name, String type, String identity) {
//		TitanTransaction tx = graph.newTransaction();
//		tx.addVertex(T.label, DEFAULT_VERTEX_LABEL, VID, name,
//				NAME, name, TYPE, type, IDENTITY, identity);
//		tx.commit();
//		tx.close();
//	}
//	
//	public void batchUpdateInsertVertexes(List<_Vertex> _vertexes) {
//		if (_vertexes.isEmpty())
//			return;
//		
//		TitanTransaction tx = graph.newTransaction();
//		for (_Vertex _v: _vertexes) {
//			GraphTraversalSource traversalSource = tx.traversal();
//			if (!traversalSource.V().has(NAME, _v.name).hasNext())
//				tx.addVertex(T.label, DEFAULT_VERTEX_LABEL, VID, _v.name,
//						NAME, _v.name, TYPE, _v.type, IDENTITY, _v.identity);
//			else {
//				Vertex v = traversalSource.V().has(NAME, _v.name).next();
//				v.property(TYPE, _v.type);
//				v.property(IDENTITY, _v.identity);
//			}
//		}
//		tx.commit();
//		tx.close();
//	}
//	
//	public void updateInsertVertex(String name, String type, String identity) {
//		TitanTransaction tx = graph.newTransaction();
//		GraphTraversalSource traversalSource = tx.traversal();
//		if (!traversalSource.V().has(NAME, name).hasNext())
//			tx.addVertex(T.label, DEFAULT_VERTEX_LABEL, VID, name,
//					NAME, name, TYPE, type, IDENTITY, identity);
//		else {
//			Vertex v = traversalSource.V().has(NAME, name).next();
//			v.property(TYPE, type);
//			v.property(IDENTITY, identity);
//		}
//		
//		tx.commit();
//		tx.close();
//	}
//	
//	public void batchDeleteVertexes(List<_Vertex> _vertexes) {
//		if (_vertexes.isEmpty())
//			return;
//		
//		TitanTransaction tx = graph.newTransaction();
//		
//		for (_Vertex v: _vertexes) {
//			GraphTraversalSource traversalSource = tx.traversal();
//			
//			while (traversalSource.V().has(NAME, v.name).hasNext())
//				traversalSource.V().has(NAME, v.name).next().remove();
//		}
//		
//		tx.commit();
//		tx.close();
//	}
//	
//	public void deleteVertex(String name) {
//		TitanTransaction tx = graph.newTransaction();
//		GraphTraversalSource traversalSource = tx.traversal();
//		
//		while (traversalSource.V().has(NAME, name).hasNext())
//			traversalSource.V().has(NAME, name).next().remove();
//		
//		tx.commit();
//		tx.close();
//	}
//	
//	private static String getEdgeId(String from, String to) {
//		return String.format("%s-%s-%s", from, DIRECT, to);
//	}
//	
//	public void batchInsertEdges(List<_Edge> _edges) {
//		if (_edges.isEmpty()) 
//			return;
//		
//		TitanTransaction tx = graph.newTransaction();
//		for (_Edge e: _edges) {
//			String eid = getEdgeId(e.from, e.to);
//			GraphTraversalSource traversalSource = tx.traversal();
//			
//			// �߲��ܽ�Ψһ�������Բ��ò�ѯ��ʽ��֤Ψһ��
//			if (traversalSource.E().has(EID, eid).hasNext()) {
//				continue;
//			}
//			
//			Vertex fromV = null;
//			if (traversalSource.V().has(NAME, e.from).hasNext())
//				fromV = traversalSource.V().has(NAME, e.from).next();
//			Vertex toV = null;
//			if (traversalSource.V().has(NAME, e.to).hasNext())
//				toV = traversalSource.V().has(NAME, e.to).next();
//			
//			if (fromV == null || toV == null) {
//				continue;
//			}
//
//			fromV.addEdge(DEFAULT_EDGE_LABEL, toV, EID, 
//						eid, RELATION, e.relation);
//		}
//		tx.commit();
//		tx.close();
//	}
//	
//	public void batchUpdateInsertEdges(List<_Edge> _edges) {
//		if (_edges.isEmpty()) 
//			return;
//		
//		TitanTransaction tx = graph.newTransaction();
//		for (_Edge _e: _edges) {
//			GraphTraversalSource traversalSource = tx.traversal();
//			String eid = getEdgeId(_e.from, _e.to);
//			
//			if (traversalSource.E().has(EID, eid).hasNext()) {
//				Edge edge = traversalSource.E().has(EID, eid).next();
//				
//				edge.property(RELATION, _e.relation);
//				continue;
//			}
//			
//			Vertex fromV = null;
//			if (traversalSource.V().has(NAME, _e.from).hasNext())
//				fromV = traversalSource.V().has(NAME, _e.from).next();
//			Vertex toV = null;
//			if (traversalSource.V().has(NAME, _e.to).hasNext())
//				toV = traversalSource.V().has(NAME, _e.to).next();
//			
//			if (fromV == null || toV == null) {
//				continue;
//			}
//
//			fromV.addEdge(DEFAULT_EDGE_LABEL, toV, EID, 
//						eid, RELATION, _e.relation);
//		}
//		tx.commit();
//		tx.close();
//	}
//	
//	public void insertEdge(String relation, String from, String to) {
//		String eid = getEdgeId(from, to);
//		TitanTransaction tx = graph.newTransaction();
//		GraphTraversalSource traversalSource = tx.traversal();
//		
//		if (traversalSource.E().has(EID, eid).hasNext()) {
//			return;
//		}
//		
//		Vertex fromV = null;
//		if (traversalSource.V().has(NAME, from).hasNext())
//			fromV = traversalSource.V().has(NAME, from).next();
//		Vertex toV = null;
//		if (traversalSource.V().has(NAME, to).hasNext())
//			toV = traversalSource.V().has(NAME, to).next();
//		
//		if (fromV == null || toV == null) {
//			return;
//		}
//		
//		fromV.addEdge(DEFAULT_EDGE_LABEL, toV, EID, eid, RELATION, relation);
//		tx.commit();
//		tx.close();
//	}
//	
//	
//	
//	
//	public Graph extension(String name, int layer) {
//		Graph retG = new Graph();
//		retG.companyName = name;
//		
//		try {
//			LOG.info("L1");
//			
//			TitanTransaction tx = graph.newTransaction();
//			GraphTraversalSource traversalSource = tx.traversal();
//			
//			LOG.info("L2");
//			if (!traversalSource.V().has(NAME, name).hasNext()) {
//				tx.rollback();
//				tx.close();
//				LOG.info("Lxret");
//				return retG;
//			}
//			LOG.info("L3");
//			
//			Set<String> visited = new HashSet<String>();
//			Vertex v = traversalSource.V().has(NAME, name).next();
//			LOG.info("L4");
//			LOG.info(String.format("Extension: %s, Type: %s, Identity: %s", 
//					v.value(NAME), v.value(TYPE), v.value(IDENTITY)));
//			
//			retG.addEntity(EntityType.valueOf((String)v.value(TYPE)), 
//					(String)v.value(NAME), (String)v.value(IDENTITY));
//			visited.add((String)v.value(NAME));
//			traversal(retG, v, layer, visited);
//			
//			tx.commit();
//			tx.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return retG;
//	}
//	
//	private void traversal(Graph g, Vertex v, int layer, Set<String> visited) {
//		if (layer <= 0)
//			return;
//		/**
//		 * ��ʱ���?����ڵ㲻��չ
//		 */
//		LOG.info(String.format("Traversal: %s, Type: %s", v.value(NAME), v.value(TYPE)));
//		if (((String)v.value(TYPE)).equals("person")) {
//			LOG.debug("Person pass");
//			return;
//		}
//		LOG.info("X1");
//		
//		// ����ڵ����
//		Iterator<Edge> it = v.edges(Direction.OUT);
//		String from = (String)v.value(NAME);
//		LOG.info("X2");
//		while (it.hasNext()) {
//			Edge edge = it.next();
//			LOG.info("OUT Edge: " + edge.value(EID));
//			Vertex toV = edge.inVertex();
//			String t = (String)toV.value(NAME);
//			
//			if (visited.contains(t))
//				continue;
//			else
//				visited.add(t);
//			
//			LOG.info("XY2");
//			g.addEntity(EntityType.valueOf( (String) toV.value(TYPE)), 
//					t, (String) toV.value(IDENTITY));
//			g.addRelationship(from, t, (String) edge.value(RELATION));
//			LOG.info("XY3");
//			traversal(g, toV, layer - 1, visited);
//		}
//		
//		// ����ڵ����
//		it = v.edges(Direction.IN);
//		String to = (String)v.value(NAME);
//		LOG.info("X3");
//		while (it.hasNext()) {
//			Edge edge = it.next();
//			Vertex fromV = edge.outVertex();
//			LOG.info("In Edge: " + edge.value(EID));
//			String f = (String)fromV.value(NAME);
//			LOG.info("From: " + f);
//			if (visited.contains(f))
//				continue;
//			else
//				visited.add(f);
//			
//			LOG.info("XX3");
//			g.addEntity(EntityType.valueOf( (String) fromV.value(TYPE)), 
//					f, (String) fromV.value(IDENTITY));
//			g.addRelationship(f, to, (String) edge.value(RELATION));
//			LOG.info("XX4");
//			try {
//				traversal(g, fromV, layer - 1, visited);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	@SuppressWarnings("rawtypes")
//	public void batchExec(Batch batch) {
//		List<_Vertex> updateV = new LinkedList<_Vertex>();
//		List<_Vertex> deleteV = new LinkedList<_Vertex>();
//		List<_Edge> insertE = new LinkedList<_Edge>();
//		
//		for (Command cmd: batch.cmds) {
//			// ת�����ֲ�json��ʧ�������Ϣ
//			if (cmd.cmdObject instanceof LinkedHashMap) {
//				LinkedHashMap map = (LinkedHashMap)cmd.cmdObject;
//				
//				switch (cmd.type) {
//				case updateInsertVertex:
//				case deleteVertex: {
//					_Vertex _v = new _Vertex((String) map.get(TYPE), 
//							(String) map.get(NAME), 
//							(String) map.get(IDENTITY));
//					cmd.cmdObject = _v;
//					break;
//					}
//				case updateInsertEdge: {
//					_Edge _e = new _Edge((String) map.get(RELATION), 
//							(String) map.get("from"), 
//							(String) map.get("to"));
//					cmd.cmdObject = _e;
//					break;
//					}
//				default: break;
//				}
//			}
//			
//			switch (cmd.type) {
//			case updateInsertVertex:
//				updateV.add((_Vertex)cmd.cmdObject);
//				break;
//			case deleteVertex:
//				deleteV.add((_Vertex)cmd.cmdObject);
//				break;
//			case updateInsertEdge:
//				insertE.add((_Edge)cmd.cmdObject);
//				break;
//			default:
//				break;
//			}
//		}
//		
//		batchUpdateInsertVertexes(updateV);
//		batchDeleteVertexes(deleteV);
//		batchUpdateInsertEdges(insertE);
//	}
//	
//	public static void main(String[] args) throws JsonProcessingException {
//		long start = System.currentTimeMillis();
//		CopyOfGraphDB graphDB = new CopyOfGraphDB("/conf/titan-cassandra-es.properties");
//		System.out.println("����ͼ��ʱ��" + ((System.currentTimeMillis() - start)));
//		
//		ObjectMapper mapper = new ObjectMapper();
//		
//		graphDB.deleteVertex("A");
//		graphDB.deleteVertex("B");
//		graphDB.deleteVertex("C");
//		graphDB.deleteVertex("COM1");
//		graphDB.deleteVertex("COM2");
//		graphDB.deleteVertex("COM3");
//		
////		Batch batch = new Batch();
////		batch.updateInsertEntity(EntityType.person, "A", "�ɶ�");
////		batch.updateInsertEntity(EntityType.person, "B", "�ɶ�");
////		batch.updateInsertEntity(EntityType.person, "C", "�߹�");
////		batch.updateInsertEntity(EntityType.enterprise, "COM1", "");
////		batch.updateInsertEntity(EntityType.enterprise, "COM2", "");
////		batch.updateInsertEntity(EntityType.enterprise, "COM3", "");
////		batch.updateInsertRelationship("A", "COM1", "Ͷ��");
////		batch.updateInsertRelationship("B", "COM1", "Ͷ��");
////		batch.updateInsertRelationship("B", "COM2", "Ͷ��");
////		batch.updateInsertRelationship("COM2", "COM1", "Ͷ��");
////		batch.updateInsertRelationship("COM1", "COM3", "Ͷ��");
////		batch.updateInsertRelationship("C", "COM2", "Ͷ��");
////		batch.updateInsertEntity(EntityType.enterprise, "COM1", "�Ǻ�");
////		batch.updateInsertEntity(EntityType.person, "A", "����");
////		batch.updateInsertEntity(EntityType.person, "D", "����");
////		batch.deleteVertex("D");
////		batch.deleteVertex("B");
////		graphDB.batchExec(batch);
//		
////		graphDB.insertEdge("Ͷ��2", "A", "COM1");
//
//		System.err.println(mapper.writeValueAsString(graphDB.extension("COM1", Integer.MAX_VALUE)));
//
//		graphDB.graph.close();
//	}
}
