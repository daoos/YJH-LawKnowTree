package com.hpre.biggraph.client;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hpre.biggraph.client.enter.graph.Entity.EntityType;

/**
 * 
 * @author zhou
 *
 */
@JsonIgnoreProperties({"undefine"})
public class Batch {
	public static final String UNDEFINED = "undefine";
	public List<Command> cmds;
	
	public Batch() {
		cmds = new LinkedList<Command>();
	}
	
	@JsonCreator
	public Batch(@JsonProperty("cmds") List<Command> cmds) {
		this.cmds = cmds;
	}
	
	public void updateInsertEntity(EntityType type, String name, String identity) {
		if (identity == null || StringUtils.isBlank(identity))
			identity = UNDEFINED;
		
		Command cmd = new Command(CommandType.updateInsertVertex, 
				new _Vertex(type.toString(), name, identity));
		cmds.add(cmd);
	}
	
	public void deleteVertex(String name) {
		Command cmd = new Command(CommandType.deleteVertex, 
				new _Vertex(null, name, null));
		cmds.add(cmd);
	}
	
	public void updateInsertRelationship(String from, String to, String relationship) {
		Command cmd = new Command(CommandType.updateInsertEdge, 
				new _Edge(relationship, from, to));
		cmds.add(cmd);
	}
}
