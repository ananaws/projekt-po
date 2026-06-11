public abstract class Item extends Entity {
	EItemStates status;
	Item(String face, Position position){
		super(face, position);
		this.status = EItemStates.NORMAL;
	}
}
