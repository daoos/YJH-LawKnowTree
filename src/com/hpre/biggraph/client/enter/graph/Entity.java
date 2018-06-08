package com.hpre.biggraph.client.enter.graph;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"infor"})
public class Entity {
	public static enum EntityType {
		person,
		enterprise,
		other
	}
	
	private EntityType type;
	private String name;
	private String identity;
	private Set<Remark> remark;
	public JSONObject getInfor() {
		return infor;
	}


	private JSONObject infor;
	
	public Entity(EntityType type, String name, String identity) {
		this(type, name, identity, null);
	}
	
	@JsonCreator
	public Entity(@JsonProperty("type") EntityType type, 
			@JsonProperty("name") String name, 
			@JsonProperty("remark") Set<Remark> remark,
			JSONObject infor) {
		setType(type);
		setName(name);
		if (remark == null) 
			remark = new HashSet<Remark>();
		setRemark(remark);
		
		/**
		 * json.jar
		 */
		if (infor == null)
			infor = new JSONObject();
		setInfor(infor);
	}
	
	
	@JsonCreator
	public Entity(@JsonProperty("type") EntityType type, 
			@JsonProperty("name") String name, 
			@JsonProperty("identity") String identity,
			@JsonProperty("remark") Set<Remark> remark) {
		setType(type);
		setName(name);
		setIdentity(identity);
		if (remark == null) 
			remark = new HashSet<Remark>();
		setRemark(remark);
	}
	
	public EntityType getType() {
		return type;
	}
	public void setType(EntityType type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<Remark> getRemark() {
		return remark;
	}
	public void setRemark(Set<Remark> remark) {
		this.remark = remark;
	}
	public void setInfor(JSONObject infor) {
		this.infor = infor;
	}
	@Override
	public boolean equals(Object o) {
		if (o == this) 
			return true;
		
		if (o instanceof Entity) {
			Entity e = (Entity) o;
			
			return name.equals(e.name);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public String toString() {
		return name;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}
	
	
	public static Entity fromJSONObject(JSONObject json) throws JSONException {
		Set<Remark> remark = new HashSet<Remark>();
		JSONArray array = json.getJSONArray("remark");
		
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			remark.add(Remark.fromJSONObject(obj));
		}
		
		JSONObject info = new JSONObject();
		if (json.has("infor"))
			info = json.getJSONObject("infor");
		
		return new Entity(EntityType.valueOf(json.getString("type")), 
				json.getString("name"), remark, info);
	}
}
