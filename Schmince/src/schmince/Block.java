package schmince;

public class Block {
	private float health = -1;

	private SObject object = null;

	public void setHealth(float health) {
		this.health = health;
	}

	public void setObject(SObject object) {
		this.object = object;
	}

	public boolean isOccupied() {
		return object != null;
	}

	public boolean isColored() {
		return health > 0;
	}

	public float getHealth() {
		return health;
	}

	public void mine(int mineValue) {
		health -= mineValue * 0.05f;
	}

	public SObject getObject() {
		return object;
	}
}
