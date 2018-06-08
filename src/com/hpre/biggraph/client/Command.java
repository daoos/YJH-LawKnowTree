package com.hpre.biggraph.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Command {
	public CommandType type;
	public Object cmdObject;
	
	@JsonCreator
	public Command(@JsonProperty("type") CommandType type, 
			@JsonProperty("cmdObject") Object cmdObject) {
		this.type = type;
		this.cmdObject = cmdObject;
	}
}
