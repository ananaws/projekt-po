import java.util.*;
/*
 * #field  creatureId	Uniquely identifies an animal.
 * #field  energy	Energy level of the animal. Used for moving. Does not decrement passively each cycle.
 * #field  food	Food level of the animal. If food reaches 0, the animal dies. Food decrements by foodDrainValue passively with each cycle.
 * #field  age	Age of the animal. If the age exceeds maxAge, the animal dies. Age increments passively with each cycle.
 * #field  foodCritValue	Value at which an animal starts looking for food (enters the CHASING state). 
 * #field  foodDrainValue	Amount to decrement the "food" field each cycle.
 * #field  nutritionValue	Amount of the consumer's food to increment were this animal to be eaten.
 * #field  mitosisCooldown 	Reproduction cooldown.
 * #field  targetObject	An entity the animal would pursue if its status were "CHASING"
 * #field  activeFace	A string representing an alternative look for the animal. Unused.
 * #field  status	An EAnimalStates field denoting the current activity.
 */

/**
 * Represents an animal. Extends entity and implements Lifetime.
 */
public abstract class Animal extends Entity implements Lifetime {
	ArrayList<Position> moveList;
	int energy;
	int food;
	int age;
	int maxAge;
	int foodCritValue;
	int foodDrainValue;
	int nutritionValue;
	int mitosisCooldown;
	static int counter = 0;
	int creatureId;
	Edible targetObject;
	String activeFace;
	EAnimalStates status;

	@Override public String toString(){
		return this.getClass().getSimpleName() + " " + this.creatureId +  " : " + status.name(); 
	}

	@Override
	public boolean equals(Object obj){
		if (this == obj) return true;
		if(!(obj instanceof Animal)) return false;
		Animal anm = (Animal) obj;
		if(this.creatureId == anm.creatureId) return true;
		return false;
	}
	/**
	 * Constructor of Animal.
	 *
	 * @param face The string representing an animal. Although it is not enforced, it should be a single character.
	 * @param foodDrainValue How much to decrement "food" each cycle.
 	 * @param foodCritValue	Value at which an animal starts looking for food (enters the CHASING state). 
	 */
	Animal(String face, Position position, int foodDrainValue, int foodCritValue){
		super(face, position);
		this.energy = 100;
		this.food = 100 + (int) (Math.random() * 100);
		this.age = 0;
		this.maxAge = 200;
		this.nutritionValue = 150;
		this.foodDrainValue = foodDrainValue;
		this.foodCritValue = foodCritValue;
		this.creatureId = counter++;
		this.mitosisCooldown = 40;
		this.status = EAnimalStates.NONE;
		this.moveList = new ArrayList<>();
	}

	/**
	 * The "brain" of the animal.
	 * With each invocation increments age and lowers food, as well as selects a new status if necessary.
	 */
	public void tick(){
		this.age += 1;
		this.food -= foodDrainValue;
		this.selectNewState();
		this.mitosisCooldown -= 1;
		assert(status != EAnimalStates.NONE);
		switch(this.status) {
			case SLEEPING:
				if(energy >= 99){
					this.clearStatus();
				} else {
					this.energy += 20;
				}
				break;
			case CHASING:
				advanceTowardsMoveTarget();
				break;
			case MOVING:
				advanceTowardsMoveTarget();
				break;
			case IDLE:
				break;
			case DEAD:
				break;
		}
	}
	/**
	 * Deicdes on what action the animal should switch to.
	 */

	private void selectNewState(){
		// add this so animals can't "un-die"
		if(status == EAnimalStates.DEAD) return;

		if(this.age > this.maxAge){
		       	this.status = EAnimalStates.DEAD;
			return;
		}

		if(this.food < 0){ 
			this.status = EAnimalStates.DEAD;
			return;
		}

		if(this.energy <= 0){
			this.setStatusSleeping();
			return;
		}
	
		if(this.isHungry()){
			this.setStatusChasing();
			return;
		}
		if(food >= (foodCritValue * 5)/6 && energy > 50 && mitosisCooldown < 1){
			this.setStatusMitosis();
		}	
		
		if(this.status == EAnimalStates.NONE || this.status == EAnimalStates.IDLE){
			this.setStatusMoving();
			return;
		}
	}

	public abstract Animal reproduce();

	/**
	 * Self-explanatory.
	 *
	 * @return boolean Whether the animal has a target entity.
	 */
	public boolean hasTargetEntity(){
		if(this.targetObject== null){
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @return Position The target tile of an animal
	 */

	public Position getMoveTarget(){
		if(this.hasMoveTarget()){
			return moveList.get(moveList.size() - 1);
		} else {
			return this.getPosition();
		}
	}

	/**
	 * @return boolean Whether an animal has a move target
	 */
	public boolean hasMoveTarget(){
		if(this.moveList == null){
			return false;
		} else if (this.moveList.isEmpty()){
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Makes the animal take a step towards its destination.
	 */
	private void advanceTowardsMoveTarget(){
		if(moveList == null) return;
		if(moveList.isEmpty()){
			return;
		} else {
			if(energy >= 5){
				this.position = moveList.get(0);
				moveList.remove(this.getPosition());
				this.energy -= 5;
				if(moveList.isEmpty()){
					this.clearStatus();
				}
			}
		}
	}
	
	/**
	 * Checks if an animal is within a radius of a given position.
	 * 
	 * @param dst The tile to compare this animal's position to.
	 * @param n The "radius", actually an amount of steps.
	 * @return boolean Whether the animal is in the specified radius
	 */
	public boolean isWithinTiles(Position dst, int n){
		if(aStar.calculateDistance(this.getPosition(), dst) <= n) return true;
		return false;	
	}

	/**
	 * What do you expect?
	 */
	public void eat(Edible obj){
		this.food += obj.getNutritionValue();
		obj.getEaten();
		this.clearStatus();
	}

	/**
	 * A getter for the activeFace of an animal.
	 */
	public String getActiveFace(){
		return this.activeFace;
	}

	/**
	 * A getter for the status of an animal.
	 */
	public EAnimalStates getStatus(){
		return this.status;
	}

	/**
	 * A getter for the creatureId of an animal.
	 */
	public int getCreatureId(){
		return this.creatureId;
	}

	/**
	 * Checks if the animal is eligible for "cleanup".
	 * This is used by the board's main loop to determine whether an animal should be deleted from the board.
	 *
	 * @return boolean Whether the animal is ready to be deleted from the board.
	 */
	public boolean hasLifeCycleFinished(){
		if(this.status == EAnimalStates.DEAD) return true;
		return false;
	}
	
	/**
	 * Checks if the animal is ready to split in two.
	 *
	 * @return boolean Whether the animal is ready to undergo mitosis
	 */
	public boolean wantsToUndergoMitosis(){
		if(this.status == EAnimalStates.MITOSIS) return true;
		return false;
	}
	
	/**
	 * Checks if the animal is hungry.
	 *
	 * @return boolean Whether the animal is hungry (duh)	
	 */
	public boolean isHungry(){
		if(this.food <= this.foodCritValue) return true;
		return false;
	}
	
	/**
	 * Checks if the animal is moving.
	 *
	 * @return boolean Whether the animal is moving.
	 */
	public boolean isMoving(){
		if(this.status == EAnimalStates.MOVING) return true;
		return false;
	}

	/**
	 * Checks if the animal is chasing
	 *
	 * @return boolean Whether the animal is chasing.
	 */
	public boolean isChasing(){
		if(this.status == EAnimalStates.CHASING) return true;
		return false;
	}
	
	/**
	 * A getter for energy of the animal.
	 *
	 * @return int The animal's energy level.
	 */
	public int getEnergy(){
		return this.energy;
	}
	
	/**
	 * Sets the status to dead. Such is the way of life. Or death.
	 */
	public void getEaten(){
		this.status = EAnimalStates.DEAD;
	}

	/**
	 * Sets the status to mitosis.
	 */
	public void setStatusMitosis(){
		this.status = EAnimalStates.MITOSIS;
	}

	/**
	 * Sets the status to sleeping.
	 */
	private void setStatusSleeping(){
		this.status = EAnimalStates.SLEEPING;
	}
	
	/**
	 * Sets the status to moving.
	 */
	private void setStatusMoving(){
		this.status = EAnimalStates.MOVING;
	}

	/**
	 * Sets the status to idle.
	 */	
	private void setStatusIdle(){
		this.status = EAnimalStates.IDLE;
	}
	
	/**
	 * Sets the status to chasing.
	 */
	private void setStatusChasing(){
		this.status = EAnimalStates.CHASING;
	}
	
	/**
	 * Clears the status.
	 */	
	protected void clearStatus(){
		this.status = EAnimalStates.NONE;
	}
	
	/**
	 * A getter for the nutritionValue of an animal.
	 *
	 * @return int nutritionValue
	 */
	public int getNutritionValue(){
		return this.nutritionValue;
	}
	
	/**
	 * Sets the object to chase. This is different from the moveTarget since it stores an entity, not a list of positions. 
	 * This allows recomputing a path mid-chase to the target even if it has moved.
	 *
	 * @param obj An Edible object to chase
	 */
	public void setTargetEntity(Edible obj){
		this.targetObject = obj; 
	}

	/**
	 * Clears the target entity.
	 */
	public void clearTargetEntity(){
		this.targetObject = null;
	}

	/**
	 * A getter for the targetEntity of an animal.
	 */
	public Edible getTargetEntity(){
		return this.targetObject;
	}

	/**
	 * Gives an animal a list of moves to take. An animal will blindly follow these moves and they are its only way of knowing what the map looks like.
	 *
	 * @param moveList An arraylist of positions to move to.
	 */
	public void setMoveTarget(ArrayList<Position> moveList){
		this.moveList = moveList;
	}
	/**
	 * A getter for the moveList of an animal.
	 *
	 * @return moveList
	 */
	public ArrayList<Position> getMoveList(){
		return this.moveList;
	}
	
	/**
	 * @param n Amount of moves to look ahead
	 * @return The requested position
	 */
	public Position getPositionInMoves(int n){
		return this.moveList.get(n);
	}
	
	/**
	 * @param pos The position to compare with.
	 * @return The amount of moves until a position is reached 
	 */
	public int getMovesUntilPosition(Position pos){
		int res = moveList.indexOf(pos);
		return res;
	}
}
