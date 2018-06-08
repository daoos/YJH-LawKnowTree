package com.hpre.biggraph.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class _Edge {
	public String relation;
	public String from;
	public String to;
	public String name;
	public String root;
	@JsonCreator
	public _Edge(@JsonProperty("relation") String label, 
			@JsonProperty("from") String from, 
			@JsonProperty("to") String to) {
		this.relation = label;
		this.from = from;
		this.to = to;
		this.name = from+"-"+to;
		this.root =null ;
	}
	@JsonCreator
	public _Edge(@JsonProperty("relation") String label,
				 @JsonProperty("from") String from,
				 @JsonProperty("to") String to,
				 @JsonProperty("root") String root) {
		this.relation = label;
		this.from = from;
		this.to = to;
		this.name = from+"-"+to;
		this.root = root;
	}
	@Override
	public String toString() {
		return String.format("%s/%s/%s", from, relation, to);
	}
}
