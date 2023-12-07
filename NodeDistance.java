
public class NodeDistance implements Comparable<NodeDistance>{
	
	//class used in dijkstras used to link a vertex name, the vertex it came from, and the distance from the source, utilizes comparable so we can put it in a min heap
	
	private String name;
	private int distance;
	private String predecessor;
	
	public NodeDistance(String nm, int dist, String prevNode) {
		name = nm;
		distance = dist;
		predecessor = prevNode;
	}
	
	public String getName() {
		return name;
	}
	
	public int getDistance() {
		return distance;
	}
	
	public String getPrev() {
		return predecessor;
	}
	
	public int compareTo(NodeDistance nd) {
		return Integer.compare(this.getDistance(), nd.getDistance());
	}
}
