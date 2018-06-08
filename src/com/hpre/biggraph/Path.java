package com.hpre.biggraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;

public class Path {
	private String start;
	private String end;
	private List<Relationship> path = new ArrayList<Relationship>();
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public void addPath(Relationship rel){
		path.add(rel);
	}
	public List<Relationship> getPath() {
		return path;
	}
	
	public boolean judge(Map<String, Node> peoples){
		if(path.size()==2){
			Relationship relationship = path.get(0);
			String from = relationship.get("from").asString();
			String to = relationship.get("to").asString();
			String center_node =from;
			if(from.equals(start) || from.equals(end)){
				center_node = to;
			}
			if(peoples.containsKey(center_node)){
				return false;
			}
		}
		
		for (Relationship relationship : path) {
			String from = relationship.get("from").asString();
			String to = relationship.get("to").asString();
			if(from.equals(start) || from.equals(end) || to.equals(start) || to.equals(end)){
				continue;
			}
			if(peoples.containsKey(from) || peoples.containsKey(to)){
				return false;
			}
			
		}
		return true;
	}
	
}
