import java.util.*;
public class Node extends Position implements Comparable<Node>{
	// This class represent a node on the pathfinding grid. Edge subclass represents a connected node and the cost of moving to it.
	public Node parentNode = null;
	public int nodePfIndex;
	double costFunction = Double.MAX_VALUE;
	double costSoFar = Double.MAX_VALUE;
	public ArrayList<Edge> neighbouringNodes;

	Node(int nodePfIndex, int x, int y){
		super(x, y);
		this.nodePfIndex = nodePfIndex;
		this.neighbouringNodes = new ArrayList<Edge>();
	}

	public static class Edge{
		double movementCost;
		public Node node;
		Edge(double movementCost, Node node){
			this.movementCost = movementCost;
			this.node = node;
		}
		@Override public String toString(){
			return "Edge pfIndex: " + node.getPfIndex() + " @ [" + node.x + ", " + node.y + "], cost: " + movementCost;
		}
	}

	public int getPfIndex(){
		return this.nodePfIndex;
	}
	@Override public String toString(){
		return "pfIndex: " + this.getPfIndex() + " @ [" + this.x + ", " + this.y + "], f: " + this.costFunction; 
	}
	public String toStringExtended(){
		String tmp = new String();
		for(Edge edge : this.neighbouringNodes){
			tmp += edge.toString() + "\n";
		}
		return "Node pfIndex: " + this.getPfIndex() + " [" + this.x + ", " + this.y + "]:\n\n" + tmp;
	}
	@Override public int compareTo(Node obj){
		return Double.compare(this.costFunction, obj.costFunction);
	}

}
