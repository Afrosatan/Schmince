package util;

public class Rectangle {
	public float x;
	public float y;
	public float w;
	public float h;

	public Rectangle() {
	}

	public Rectangle(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public Rectangle(Rectangle rect) {
		this.set(rect);
	}

	public float right() {
		return x + w;
	}

	public float bottom() {
		return y + h;
	}

	public boolean intersects(Rectangle location) {
		if (x < location.right() && location.x < right())
			if (y < location.bottom() && location.y < bottom())
				return true;
		return false;
	}

	public boolean contains(Rectangle location) {
		if (x < location.x && right() > location.right())
			if (y < location.y && bottom() > location.bottom())
				return true;
		return false;
	}

	public float centerX() {
		return x + w / 2f;
	}

	public float centerY() {
		return y + h / 2f;
	}

	public boolean contains(float xx, float yy) {
		return xx >= x && xx < right() && yy >= y && yy < bottom();
	}

	public float size() {
		return w * h;
	}

	public void set(Rectangle rect) {
		this.x = rect.x;
		this.y = rect.y;
		this.w = rect.w;
		this.h = rect.h;
	}

	public void set(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
}