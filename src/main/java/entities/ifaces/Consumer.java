public interface Consumer {
	void setTargetEntity(Edible obj);
	boolean hasTargetEntity();
	void clearTargetEntity();
	Edible getTargetEntity();
	void eat(Edible obj);
}
