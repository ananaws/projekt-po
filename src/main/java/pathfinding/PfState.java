import java.util.*;
class PfState implements Pathfinder {
	private int xLim;
	private int yLim;
	private ArrayList<Node> nodeList;
	private ArrayList<Block> blockList;
	PfState(int xLim, int yLim){
		this.xLim = xLim;
		this.yLim = yLim;
	}
	public void buildPathfindingGrid(ArrayList<Block> blockList){
		this.nodeList = generateNodeList(this.xLim, this.yLim);
		populateNeighbours(this.nodeList, blockList);
		this.blockList = blockList;
	}

	public ArrayList<Position> findRoute(Position src, Position dst, boolean allowIllegalPaths){
		if (nodeList == null || nodeList.isEmpty()) return null;
		return findRouteByNode(this.nodeList.get(getNodeIndexByPosition(src)), this.nodeList.get(getNodeIndexByPosition(dst)), allowIllegalPaths);
	}

	public ArrayList<Position> plotRandomPath(Position src, int range){
		if(range > xLim / 2 - 1 || range > yLim / 2 - 1){
			return null;
		}
		Position dst;
		loop:
		while(true){
			int rdX = this.xLim;
			int rdY = this.yLim;
			while ( src.x + rdX > this.xLim - 1 || src.x + rdX < 0 || src.y + rdY > this.yLim - 1 || src.y + rdY < 0 ){
				rdX = (int) (randomizeSign() * Math.random() * range);
				rdY = (int) (randomizeSign() * Math.random() * range);
			}	
			dst = new Position(src.x + rdX, src.y + rdY);
			for(Block blk : this.blockList){
				if(dst.equals(blk.getPosition())) continue loop;
			}
			break;
		}
		System.out.println(dst.toString());
		return findRoute(src, dst, false);
	}
	
	private int randomizeSign(){
		double decider = Math.random();
		if(decider > 0.5){
			return 1;
		} else {
			return -1;
		}
	}
	
	private ArrayList<Position> findRouteByNode(Node src, Node dst, boolean allowIllegalPaths){
		return aStar.aStarWrapper(src, dst, allowIllegalPaths);
	}

	
	private ArrayList<Node> generateNodeList(int xLim, int yLim){
	// Input: Board dimensions 
	// Output: ArrayList of Node
		ArrayList<Node> nodes = new ArrayList<>();
		for(int y = 0; y < yLim; ++y){
			for(int x = 0; x < xLim; ++x){
				// x + y * xLim will increment by one each iteration
				Node obj = new Node(x + y * xLim, x, y);
				nodes.add(obj);
			}
		}		
		return nodes;
	}

	private void populateNeighbours(ArrayList<Node> nodeList, ArrayList<Block> blockList){
	// This function populates all nodes of nodeList with Node.Edge objects that represent nodes adjacent to the origin.
		Node neighbour;
		for(Node node : nodeList){
			ArrayList<Node.Edge> neighbouringNodes = new ArrayList<>();
		//	if (node.x > 0 && node.y < yDim - 1){ 		// left and down
		//		neighbour = nodeList.get(Util.getNodeIndexByPosition(new Position(node.x - 1, node.y + 1), xDim));
		//		neighbouringNodes.add(new Node.Edge(Util.calculateMovementCost(node.getPosition(), neighbour.getPosition(), this.blockList), neighbour));
		//	}
		//	if (node.x > 0 && node.y > 0){ 			// left and up
		//		neighbour = nodeList.get(Util.getNodeIndexByPosition(new Position(node.x - 1, node.y - 1), xDim));
		//		neighbouringNodes.add(new Node.Edge(Util.calculateMovementCost(node.getPosition(), neighbour.getPosition(), this.blockList), neighbour));
		//	}
		//	if (node.x < xDim - 1 && node.y > 0){		// right and up
		//		neighbour = nodeList.get(Util.getNodeIndexByPosition(new Position(node.x + 1, node.y - 1), xDim));
		//		neighbouringNodes.add(new Node.Edge(Util.calculateMovementCost(node.getPosition(), neighbour.getPosition(), this.blockList), neighbour));
		//	}
		//	if (node.x < xDim - 1 && node.y < yDim - 1){	// right and down
		//		neighbour = nodeList.get(Util.getNodeIndexByPosition(new Position(node.x + 1, node.y + 1), xDim));
		//		neighbouringNodes.add(new Node.Edge(Util.calculateMovementCost(node.getPosition(), neighbour.getPosition(), this.blockList), neighbour));
		//	}
			if (node.x < this.xLim - 1){		// right	
				neighbour = nodeList.get(getNodeIndexByPosition(new Position(node.x + 1, node.y - 0)));
				neighbouringNodes.add(new Node.Edge(calculateMovementCost(node.getPosition(), neighbour.getPosition(), blockList), neighbour));
			}
			if (node.y < this.yLim - 1){				// down
				neighbour = nodeList.get(getNodeIndexByPosition(new Position(node.x - 0, node.y + 1)));
				neighbouringNodes.add(new Node.Edge(calculateMovementCost(node.getPosition(), neighbour.getPosition(), blockList), neighbour));
			}
			if (node.x > 0){ 				// left
				neighbour = nodeList.get(getNodeIndexByPosition(new Position(node.x - 1, node.y - 0)));
				neighbouringNodes.add(new Node.Edge(calculateMovementCost(node.getPosition(), neighbour.getPosition(), blockList), neighbour));
			}
			if (node.y > 0){ 				// up
				neighbour = nodeList.get(getNodeIndexByPosition(new Position(node.x - 0, node.y - 1)));
				neighbouringNodes.add(new Node.Edge(calculateMovementCost(node.getPosition(), neighbour.getPosition(), blockList), neighbour));
			}
			node.neighbouringNodes = neighbouringNodes;	
		}
	}

	private int getNodeIndexByPosition(Position pos){
		return pos.x + pos.y * this.xLim;
	}

	private Position getNodePositionByIndex(int index){
		return new Position(index % this.xLim, index / this.xLim);
	}

	private double calculateMovementCost(Position posFrom, Position posTo, ArrayList<Block> blockList){
	if(posFrom == posTo) return 0;	
	for(Block obj : blockList){
		if( !obj.isPassable() && (obj.getPosition().equals(posTo) || obj.getPosition().equals(posFrom)) ) {
			return 1000000; // one million
		} else if( obj.isPassable() && (obj.getPosition().equals(posTo) || obj.getPosition().equals(posFrom))){
			return obj.getMovementDifficulty();
		}
	}
	if(Math.abs(posTo.x - posFrom.x + posTo.y - posFrom.x) % 2 == 0) return 1.4;
	return 1;
	}
}
