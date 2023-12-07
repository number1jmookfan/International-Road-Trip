import java.util.*;
public class WorldGraph {
	
	//our graph is contained in here, the first vertex is represented with the first string and the second vertex is represented with a pair of the destination country and the edge weight as an integer
	private HashMap<String, HashMap<String, Integer>> graph;
	
	WorldGraph(){
		graph = new HashMap<String, HashMap<String, Integer>>();
	}
	
	public void addEdge(String country, String adjacent, int distance) {
		//if there is nothing adjacent, we add a floating node to the graph
		if (adjacent == "") {
			graph.put(country, new HashMap<String, Integer>());
		}
		else {
			//if the graph already has the first vertex, just put the edge in
			if (graph.containsKey(country)) {
				graph.get(country).put(adjacent, distance);
			}
			//if it doesnt, make the first vertex and then put the edge in
			else {
				graph.put(country, new HashMap<String,Integer>());
				graph.get(country).put(adjacent, distance);
			}
		}
	}
	//for the below methods, you have to run nameChanger.alias() before you input the strings
	//checks if two countries are adjacent
	public boolean areAdjacent(String country1, String country2) {
		boolean adjacent = false;
		if(graph.containsKey(country1)) {
			if (graph.get(country1).containsKey(country2)){
				adjacent = true;
			}
		}
		return adjacent;
	}
	//gets the edge weight of two different countries, must check to see if countries are adjacent before using this method
	public int edgeWeight(String country1, String country2) {
		return graph.get(country1).get(country2);
	}
	//get all adjacent countries to a set and then return it, if the country does not exist or the vertex has no adjacent vertices, then return an empty set
	public Set<String> getAdjacents(String country){
		if(graph.containsKey(country) && !graph.get(country).isEmpty()) {
			return graph.get(country).keySet();
		}
		return new HashSet<String>();
	}
	//returns true if the graph contains the country, false if not
	public boolean containsCountry(String country) {
		return graph.containsKey(country);
	}
	//dijkstra's algorithm
	public HashMap<String, String> dijkstra(String sourceCountry, String finalCountry){
		//only runs the algorithm if the two countries are in the graph, must run namechanger before calling this method
		if(this.containsCountry(sourceCountry) && this.containsCountry(finalCountry)) {
	    	//add finalized nodes here
	    	HashMap<String, String> finalized = new HashMap<String,String>();
	    	//min heap to store the nodes
	    	PriorityQueue<NodeDistance> nodeHeap = new PriorityQueue<NodeDistance>();
	    	//initialize last country variable
			NodeDistance lastCountry = null;
			//add source to heap
			nodeHeap.add(new NodeDistance(sourceCountry, 0, sourceCountry));
	    	//while the node for country 2 is not finalized
			while(!finalized.containsKey(finalCountry) && !nodeHeap.isEmpty()) {
	        	boolean isFinalized = false;
	        	//poll top of min heap until we get a variable that needs to be finalized or the heap is empty
	        	while(!isFinalized && !nodeHeap.isEmpty()) {
		        	NodeDistance smallestNode = nodeHeap.poll();
		        	if (!finalized.containsKey(smallestNode.getName())) {
		        		finalized.put(smallestNode.getName(), smallestNode.getPrev());
		        		lastCountry = smallestNode;
		        		isFinalized = true;
		        	}
	        	}
	        	//if we finalized a node, we can add its neighbors to the heap
	    		if(isFinalized) {
		        	//add adjacent vertexes to the heap
		        	for(String adjacentCountry : this.getAdjacents(lastCountry.getName())) {
		        			int distance = this.edgeWeight(adjacentCountry, lastCountry.getName()) + lastCountry.getDistance();
		            		NodeDistance currentNode = new NodeDistance(adjacentCountry, distance , lastCountry.getName());
		            		nodeHeap.add(currentNode);
		        	}
	    		}
	    	}
			return finalized;
		}
		//if the two countries are not in the graph, return an empty hashmap
		else {
			return new HashMap<String, String>();
		}
	}	
}
