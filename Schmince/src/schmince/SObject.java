package schmince;

public abstract class SObject {
	public int x;
	public int y;

	abstract void update(SchminceRenderer render);

	abstract void draw(SchminceRenderer render);
}
