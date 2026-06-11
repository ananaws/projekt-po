/**
 * Represents a position in 2D space. An extension of this class is a Node, which represents a position on the pathfinding grid.
 * Getters and setters for x and y are not used to improve readability.
 */
class Position{
	public int x;
	public int y;
	Position(int x, int y){
		this.x = x;
		this.y = y;
	}
	Position(Position obj){
		this.x = obj.x;
		this.y = obj.y;
	}
	@Override public boolean equals(Object obj){
		if (this == obj) return true;
		if(!(obj instanceof Position)) return false;
		Position pos = (Position) obj;
		if(this.x == pos.x && this.y == pos.y) return true;
		return false;
	}
	@Override public String toString(){
		return "[" + this.x + ", " + this.y + "]";
	}
	@Override public int hashCode(){
        	return this.x * 31 + (this.x * 31) * this.y; 
	}
	public Position getPosition(){
		return this;
	}
}
