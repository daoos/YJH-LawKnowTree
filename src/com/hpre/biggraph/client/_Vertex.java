package com.hpre.biggraph.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class _Vertex {
	public String type;
	public String name;
	public String identity;
	public String root;
	@JsonCreator
	public _Vertex(@JsonProperty("type") String label, 
			@JsonProperty("name") String name, 
			@JsonProperty("identity") String identity) {
		this.type = label;
		this.name = name;
		this.identity = identity;
		this.root = null;
	}
	@JsonCreator
	public _Vertex(@JsonProperty("type") String label,
				   @JsonProperty("name") String name,
				   @JsonProperty("identity") String identity,
				   @JsonProperty("root") String root) {
		this.type = label;
		this.name = name;
		this.identity = identity;
		this.root =root;
	}
	
	@Override
	public String toString() {
		return String.format("%s/%s/%s", type, name, identity);
	}
}
