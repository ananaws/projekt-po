public class Block extends Entity {
	private Boolean passable;
	public double movementDifficulty;
	Block(Position pos){
		super("X", pos);
		this.passable = false;
		this.movementDifficulty = Double.MAX_VALUE;
	}
	Block(String face, Position pos, double movementDifficulty){
		super(face, pos);
		this.movementDifficulty = movementDifficulty;
		this.passable = true;
	}
	public Boolean isPassable(){
		return passable;
	}
	public double getMovementDifficulty(){
		return this.movementDifficulty;
	}
}

