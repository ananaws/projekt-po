/**
 * A type of animal.
 */
public class Leopard extends Animal implements Consumer {
	/**
	 * Parametrized constructor for leopard.
	 */
	Leopard(Position position){
		super("🐆", position, 2, 60);
		this.activeFace = "🐆";

	}
	/**
	 * Leopard-specific way of reproducing.
	 *
	 * @return An animal's "offspring".
	 */
	@Override
	public Animal reproduce(){
		this.mitosisCooldown = 60;
		this.clearStatus();
		return new Leopard(this.position);
	}
}
