/**
 * A type of animal.
 */
public class Fish extends Animal implements MovingEdible {

	/**
	 * Parametrized constructor for fish.
	 */
	Fish(Position position){
		super("🐟", position, 1, 20);
		this.activeFace = "🐟";
	}
	/**
	 * Fish-specific way of reproducing.
	 *
	 * @return An animal's "offspring".
	 */
	@Override
	public Animal reproduce(){
		this.mitosisCooldown = 40;
		this.clearStatus();
		return new Fish(this.position);
	}
}
