import java.util.*;
abstract class aStar {
	public static int calculateDistance(Position src, Position dst){
	// Heuristic function 
		int dx = Math.abs(src.x - dst.x);
		int dy = Math.abs(src.y - dst.y);
		// Chebyshev distance
		return (dx + dy) - 1 * Math.min(dx, dy); 
	}

	public static ArrayList<Position> aStarWrapper(Node src, Node dst, boolean allowIllegalPaths){
		Node solution = aStar(src, dst);
		if(solution == null) return null;
		return unwindAStar(solution, allowIllegalPaths);

	}
	private static Node aStar(Node src, Node dst){
		PriorityQueue<Node> openList = new PriorityQueue<>();
		PriorityQueue<Node> closedList = new PriorityQueue<>();
		src.costSoFar = 0; // costSoFar = move function, distance from src
		src.costFunction = src.costSoFar; // cost = heuristic + move
		openList.add(src);

		while(!openList.isEmpty()){
			Node currentNode = openList.peek(); // <-- where picking a new node happens
		//	for(Node nd : openList){
		//		System.out.printf("%s, ", nd.toString());
		//	}
		//	System.out.println("chosen : " + currentNode.toStringExtended());
			if(currentNode == dst){
				return currentNode;
			}

			for(Node.Edge neighbourEdge : currentNode.neighbouringNodes){ // for every edge surrounding current node
				Node neighbourNode = neighbourEdge.node; // select one of the neighbours and put it in neighbourNode
				double totalCost = currentNode.costSoFar + neighbourEdge.movementCost;

				if(!openList.contains(neighbourNode) && !closedList.contains(neighbourNode)){
					neighbourNode.parentNode = currentNode;
					neighbourNode.costSoFar  = totalCost;
					neighbourNode.costFunction = neighbourNode.costSoFar + calculateDistance(neighbourNode, dst);
					openList.add(neighbourNode);
				} else {
					if(totalCost < neighbourNode.costSoFar){
						neighbourNode.parentNode = currentNode;
						neighbourNode.costSoFar = totalCost;
						neighbourNode.costFunction = neighbourNode.costSoFar + calculateDistance(neighbourNode, dst);
						if(closedList.contains(neighbourNode)){
							closedList.remove(neighbourNode);
							openList.add(neighbourNode);
						}
					}
				}
			}
			openList.remove(currentNode);
			closedList.add(currentNode);
		}

		return null; // no solution found
	}

	private static ArrayList<Position> unwindAStar(Node solution, boolean allowIllegalPaths){
		if(solution.costFunction > 1000000 && !allowIllegalPaths) return null; 
		ArrayList<Position> positions = new ArrayList<>();	
		Node dst = solution;
		if(dst == null) return null;
		while(dst.parentNode != null){
			positions.add(new Position(dst.x, dst.y));
			dst = dst.parentNode;
		}
		Collections.reverse(positions);
		return positions;
	}
}
