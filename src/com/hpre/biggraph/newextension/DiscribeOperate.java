package com.hpre.biggraph.newextension;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.hpre.biggraph.client.enter.graph.Edge;
import com.hpre.biggraph.client.enter.graph.Graph;
import com.mongodb.Mongo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class DiscribeOperate {
	public static void main(String[] args) {
//		Properties header = new Properties();
//		HttpClient client = new HttpClient("http://s137:5000/relation");
//		String param = get_file("e:\\6-2\\深圳市中金创展融资担保股份有限公司.json");
//		String content = "";
//		try {
//			content = client.query(param, header);
//			client.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		
//		HttpClient client = new HttpClient("http://172.16.40.1:5000/community");
//		HttpClient client = new HttpClient("http://172.16.40.1:5000/gaoguan");
		
//		header.put("X-Command", Command.insertVertex);	
//		System.out.println("插入关系节点："+name);
//		System.out.println("content:"+content);
		
//		try {
//			System.out.println(getDiscribe("河南万里运输集团有限公司"));
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	private static Map<String, Integer> handlerDegreeOfAssociation(JSONArray names_nodes,Graph g,String companyName) throws JSONException {
		Map<String,Integer> map = new HashMap<>();

		for (int i = 0; i < names_nodes.length(); i++) {

			List<String> path = DiscribeOperate.shortestDistance(g, companyName, names_nodes.getString(i));

			int companyIndex = path.indexOf(companyName);
			for (int j = 0 ; j< path.size() ; j++){
				int distance = Math.abs(j - companyIndex);//获取距离目标公司的距离
				if( distance ==1){
					map.put(path.get(j),70);
				}else if(distance ==2){
					map.put(path.get(j),40);
				}else if(distance >= 3){
					map.put(path.get(j),10);
				}
			}

		}
		return map;
	}




	public static boolean inArray(JSONArray path,String name) throws JSONException{
		for (int i = 0; i < path.length(); i++) {
			if(path.getString(i).equals(name))
				return true;
		}
		return false;
	}
	public static String get_file(String filepath) {
		File file = new File(filepath);
		String content = "";
		String tmp;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			try {
				while ((tmp = reader.readLine()) != null) {
					content = content + tmp;
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public static List<String> shortestDistance(Graph graph, String name1, String name2) {
		List<String> path = new LinkedList<String>();
		Map<Integer, String> nodes = new HashMap<Integer, String>();
		Map<String, Integer> nodesInverse = new HashMap<String, Integer>();
		
		int size = graph.entityMap.size();
		int[][] disMatrix = new int[size][size];
		int index = 0;
		
		for (String key: graph.entityMap.keySet()) {
			nodes.put(index, key);
			nodesInverse.put(key, index);
			index += 1;
		}
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (i == j)
					disMatrix[i][j] = 0;
				else
					disMatrix[i][j] = Integer.MAX_VALUE;
			}
		}
		
		for (Edge edge: graph.edgeMap.values()) {
			int i = nodesInverse.get(edge.getFrom());
			int j = nodesInverse.get(edge.getTo());
			disMatrix[i][j] = 1;
			disMatrix[j][i] = 1;
		}
		
		int source = nodesInverse.get(name1);
		int target = nodesInverse.get(name2);
		int[] prev = dijkstra(disMatrix, source, target);
		
		if (prev[target] == -1)
			return path;
		
		path.add(nodes.get(target));
		int p = target;
		while (prev[p] != -1) {
			path.add(nodes.get(prev[p]));
			p = prev[p];
		}
		
		return path;
	}
	
	public static class State implements Comparable<State> {
		int index;
		int distance;
		
		public State(int i, int d) {
			index = i;
			distance = d;
		}
		
		@Override
		public int compareTo(State s) {
			return Integer.compare(distance, s.distance);
		}
	}
	
	public static int[] dijkstra(int[][] disMatrix, int source, int target) {
		int size = disMatrix[0].length;
		int[] dist = new int[size];
		int[] prev = new int[size];
		
		Arrays.fill(dist, Integer.MAX_VALUE);
		Arrays.fill(prev, -1);
		
		PriorityQueue<State> que = new PriorityQueue<State>();
		dist[source] = 0;
		
		for (int i = 0; i < size; i++) {
			
			que.add(new State(i, disMatrix[source][i]));
		}
		
		while (!que.isEmpty()) {
			State s = que.poll();
			if (s.index == target)
				break;
			
			for (int i = 0; i < size; i++) {
				if ((long)dist[s.index] + disMatrix[i][s.index] < (long)dist[i]) {
					dist[i] = dist[s.index] + disMatrix[i][s.index];
					prev[i] = s.index;
					que.add(new State(i, dist[i]));
				}
			}
		}
		
		return prev;
	}
}
