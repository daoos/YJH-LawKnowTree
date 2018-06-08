package com.hpre.biggraph.client.enter.graph;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Remark {
	public static enum RemarkType {
		info,
		warning
	}
	
	private String content;
	private String attach;
	private RemarkType type;
	
	public Remark(String remark, RemarkType type) {
		this(remark, type, null);
	}
	
	@JsonCreator
	public Remark(@JsonProperty("content") String remark,
			@JsonProperty("type") RemarkType type, 
			@JsonProperty("attach") String attach) {
		setContent(remark);
		setType(type);
		setAttach(attach);
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) 
			return true;
		
		if (o instanceof Remark) {
			Remark r = (Remark) o;
			
			return content.equals(r.content);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return content.hashCode();
	}
	
	@Override
	public String toString() {
		return content;
	}

	public RemarkType getType() {
		return type;
	}

	public void setType(RemarkType type) {
		this.type = type;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}
	
	/*****************************************/
	/**
	 * json.jar compatible
	 * @return
	 */
	public JSONObject toJSONObject() throws JSONException {
		JSONObject remark = new JSONObject();
		
		remark.put("attach", attach == null? "": attach);
		remark.put("content", content);
		remark.put("type", type);

		return remark;
	}
	
	public static Remark fromJSONObject(JSONObject json) throws JSONException {
		
		return new Remark(json.getString("content"), 
				RemarkType.valueOf(json.getString("type")), json.getString("attach"));
	}
}
