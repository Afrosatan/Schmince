package texample;

/**
 * Calculates 4 floats from texture size and 
 */
class TextureRegion {
	/**
	 * Top/Left U,V Coordinates
	 */
	public final float u1, v1;
	/**
	 * Bottom/Right U,V Coordinates
	 */
	public final float u2, v2;

	/**
	 * Calculate U,V coordinates from specified texture coordinates
	 * @param texWidth the width of the texture the region is for
	 * @param texHeight the height of the texture the region is for
	 * @param x the left of the region on the texture (in pixels)
	 * @param y the top of the region on the texture (in pixels)
	 * @param width the width of the region on the texture (in pixels)
	 * @param height the height of the region on the texture (in pixels)
	 */
	public TextureRegion(float texWidth, float texHeight, float x, float y, float width,
			float height) {
		this.u1 = x / texWidth; // Calculate U1
		this.v1 = y / texHeight; // Calculate V1
		this.u2 = this.u1 + (width / texWidth); // Calculate U2
		this.v2 = this.v1 + (height / texHeight); // Calculate V2
	}
}