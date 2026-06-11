public abstract class Entity {
	private String face;
	protected Position position;
	Entity(String face, Position position){
		this.position = position;
		this.face = face;
	}
	public String getFace(){
		return this.face;
	}	
	public Position getPosition(){
		return this.position;
	}
}
