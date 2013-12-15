package util;

/**
 * RGBA color object.
 */
public class SColor {
	public float Red = 1f;
	public float Green = 1f;
	public float Blue = 1f;
	public float Alpha = 1f;

	public SColor() {
	}

	public SColor(float r, float g, float b) {
		this.Red = r;
		this.Green = g;
		this.Blue = b;
	}

	public SColor(float r, float g, float b, float a) {
		this.Red = r;
		this.Green = g;
		this.Blue = b;
		this.Alpha = a;
	}

	public void set(float r, float g, float b) {
		this.Red = r;
		this.Green = g;
		this.Blue = b;
	}

	public void set(float r, float g, float b, float a) {
		this.Red = r;
		this.Green = g;
		this.Blue = b;
		this.Alpha = a;
	}

	public void set(SColor color) {
		this.Red = color.Red;
		this.Green = color.Green;
		this.Blue = color.Blue;
	}
}
