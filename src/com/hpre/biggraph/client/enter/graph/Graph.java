package com.hpre.biggraph.client.enter.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hpre.biggraph.client.enter.graph.Entity.EntityType;

public class Graph {
	@JsonProperty("nodes")
	public Map<String, Entity> entityMap;
	@JsonProperty("edges")
	public Map<String, Edge> edgeMap;
	public String companyName;
	
	public Graph() {
		entityMap = new HashMap<String, Entity>();
		edgeMap = new HashMap<String, Edge>();
	}
	
	public void addEntity(EntityType type, String name, String identity) {
		entityMap.put(name, new Entity(type, name, identity));
	}
	
	
	public void addEntity(String type_name, String name, String identity) {
		EntityType type = EntityType.other;
		switch (type_name) {
			case "person":
				type = EntityType.person;
				break;
			case "enterprise":
				type = EntityType.enterprise;
				break;
			default:
				break;
		}
		
		entityMap.put(name, new Entity(type, name, identity));
	}
	
	public void removeEntity(String name) {
		entityMap.remove(name);
	}
	
	public void addRemarkForEntity(Remark remark, String name) {
		Set<Remark> set = entityMap.get(name).getRemark();
		set.add(remark);
	}
	
	public void removeRemarkFromEntity(Remark remark, String name) {
		Set<Remark> set = entityMap.get(name).getRemark();
		if (set.contains(remark))
			set.remove(remark);
	}
	
	public void addRelationship(String from, String to, String relationship) {
		Edge edge = new Edge(from, to, relationship);
		edgeMap.put(edge.getId(), edge);
	}
	
	public void removeRelationship(String from, String to, String relationship) {
		Edge edge = new Edge(from, to, relationship);
		edgeMap.remove(edge.getId());
	}
	
	
	public static Graph loadGraph(String edgeString, String entityString) throws JSONException {
//		ObjectMapper mapper = new ObjectMapper();
		Graph graph = new Graph();
		
//		graph.edgeMap = mapper.readValue(edgeString, 
//				new TypeReference<Map<String, Edge>>() {});
//		graph.entityMap = mapper.readValue(entityString, 
//				new TypeReference<Map<String, Entity>>() {});
		JSONObject edges = new JSONObject(edgeString);
		Iterator it = edges.keys();
		while (it.hasNext()) {
			String key = (String) it.next();
			graph.edgeMap.put(key, 
					Edge.fromJSONObject(edges.getJSONObject(key)));
		}
		
		JSONObject entities = new JSONObject(entityString);
		it = entities.keys();
		while (it.hasNext()) {
			String key = (String) it.next();
			graph.entityMap.put(key, 
					Entity.fromJSONObject(entities.getJSONObject(key)));
		}
		
		return graph;
	}

}
