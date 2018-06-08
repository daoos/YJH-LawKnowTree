package com.hpre.biggraph.client.enter.graph;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Edge {
	private String id;
	private String from;
	private String to;
	private String relationship;	
	
	@JsonCreator
	public Edge(@JsonProperty("from") String from, 
			@JsonProperty("to") String to, 
			@JsonProperty("relationship") String relationship) {
		setId(String.format("%s-%s-%s", from, relationship, to));
		setFrom(from);
		setTo(to);
		setRelationship(relationship);
	}
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getRelationship() {
		return relationship;
	}
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) 
			return true;
		
		if (o instanceof Entity) {
			Edge e = (Edge) o;
			
			return id.equals(e.id);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public String toString() {
		return id;
	}
	
	
	public static Edge fromJSONObject(JSONObject json) throws JSONException {
		return new Edge(json.getString("from"), 
				json.getString("to"), json.getString("relationship"));
	}
}
