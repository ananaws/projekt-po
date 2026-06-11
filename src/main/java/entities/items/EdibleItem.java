public abstract class EdibleItem extends Item implements Edible, Lifetime {
	int nutritionValue;
	EdibleItem(String face, Position position, int nutritionValue){
		super(face, position);
		this.nutritionValue = nutritionValue;
	}

	public void getEaten(){
		this.status = EItemStates.EATEN;
	}
	public int getNutritionValue(){
		return this.nutritionValue;
	}
	public boolean hasLifeCycleFinished(){
		if(status == EItemStates.EATEN) return true;
		return false;
	}

}
