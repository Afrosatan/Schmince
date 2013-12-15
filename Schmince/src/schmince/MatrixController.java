package schmince;

import android.opengl.Matrix;

/**
 * Maintains matrices - size 16 float arrays used as 4x4 matrices - for OpenGL.
 */
public class MatrixController {
	private float[] projMatrix = new float[16];
	private float[] viewMatrix = new float[16];
	private float[] viewOrthoMatrix = new float[16];
	private float[] vpMatrix = new float[16]; //projMatrix * viewMatrix
	private float[] vpOrthoMatrix = new float[16]; //basic frustrum * viewOrthoMatrix
	private float[] tempMatrix = new float[16]; //temporary matrix to reuse (Eg. array copy and pass as parameter)

	private float eyeZ = 20.0f;
	private int screenWidth;
	private int screenHeight;
	private float hex;
	private float hey;

	private float minZ = 10.0f;
	private float maxZ = 25.0f;

	public MatrixController() {
		setEyeZ(eyeZ);
	}

	public void setMaxZ(float maxZ) {
		this.maxZ = maxZ;
		setEyeZ(eyeZ);
	}

	public void setMinZ(float minZ) {
		this.minZ = minZ;
		setEyeZ(eyeZ);
	}

	public void setEyeZ(float distance) {
		eyeZ = Math.min(Math.max(distance, minZ), maxZ);
		Matrix.setLookAtM(viewMatrix, 0, 0, 0, eyeZ, 0f, 0f, 0f, 0.0f, 1.0f, 0.0f);
	}

	public float getEyeZ() {
		return eyeZ;
	}

	public void setScreenSize(int width, int height) {
		if (screenWidth == width && screenHeight == height)
			return;
		this.screenWidth = width;
		this.screenHeight = height;

		//calculate projection matrix so that we don't get squished shapes
		//if we are wider than taller, we want to show the extra stuff on the sides of the screen
		//	and if we are taller, then show extra stuff on the top and bottom
		if (width > height) {
			float ratio = (float) width / height;
			Matrix.frustumM(projMatrix, 0, -ratio, ratio, -1, 1, 1, 100);
		} else {
			float ratio = (float) height / width;
			Matrix.frustumM(projMatrix, 0, -1, 1, -ratio, ratio, 1, 100);
		}

		//project matrix for GUI will be projecting to the basic -1->1 box and we let the view matrix handle the interesting stuff
		Matrix.frustumM(tempMatrix, 0, -1, 1, 1, -1, 1, 100);
		Matrix.orthoM(viewOrthoMatrix, 0, 0, width, height, 0, 0.5f, 100f);
		Matrix.multiplyMM(vpOrthoMatrix, 0, tempMatrix, 0, viewOrthoMatrix, 0);

		vpOrthoMatrix[8] = 0;
		vpOrthoMatrix[9] = 0;
		vpOrthoMatrix[10] = 0;
		vpOrthoMatrix[11] = 0;

		vpOrthoMatrix[12] = -1;
		vpOrthoMatrix[13] = -1;
		vpOrthoMatrix[14] = -1;
		vpOrthoMatrix[15] = 1;
	}

	/**
	 * Parameters should be the world coordinates of the location the camera is looking at.
	 */
	public void updateVPMatrix(float hex, float hey) {
		this.hex = hex;
		this.hey = hey;
		// Calculate the projection and view transformation
		Matrix.multiplyMM(vpMatrix, 0, projMatrix, 0, viewMatrix, 0);
		Matrix.translateM(vpMatrix, 0, -hex, -hey, 0);
	}

	/**
	 * Copy the combine view/projection matrix into the provided matrix and returns that array.
	 */
	public float[] getVPMatrix(float[] tempMatrix) {
		System.arraycopy(vpMatrix, 0, tempMatrix, 0, 16);
		return tempMatrix;
	}

	/**
	 * Copy the orthographic projection matrix into the provided matrix and returns that array.
	 */
	public float[] getVPOrthoMatrix(float[] tempMatrix) {
		System.arraycopy(vpOrthoMatrix, 0, tempMatrix, 0, 16);
		return tempMatrix;
	}

	//don't allocate new arrays in the deviceToWorld function - premature optimization for the win!!!
	private float[] d2wworld = new float[4];
	private float[] d2whomog = new float[4];
	private float[] d2wMatrix = new float[16];

	/**
	 * The difference between device coordinates and orthographic coordinates is that the y is flipped (also, orthographic coordinates are shifted by the navigation bar).
	 */
	public float deviceToOrthoY(float y) {
		y = (-y + screenHeight);
		return y;
	}

	/**
	 * Convert the given x, y coordinates in orthographic coordinates to world coordinates on the z = 0 plane.<br>
	 * Orthographic coordinates is the same as android device coordinates but the y has to be flipped, see {@link MatrixController#deviceToOrtho(float)}. 
	 * @return length 2 array in world coordinates - [x, y]
	 */
	public float[] orthoToWorld(float x, float y) {
		d2wworld[0] = x;
		d2wworld[1] = y;
		d2wworld[2] = 0;
		d2wworld[3] = 1; //i don't have any idea why the last element has to be 1, but the calculation gets offset if you don't

		Matrix.multiplyMV(d2whomog, 0, vpOrthoMatrix, 0, d2wworld, 0); //convert the orthographic coordinates to homogenous coordinates
		System.arraycopy(vpMatrix, 0, d2wMatrix, 0, 16); //create a copy of the vpMatrix so we can undo the eye translation
		Matrix.translateM(d2wMatrix, 0, hex, hey, 0); //undo the eye translation
		Matrix.invertM(d2wMatrix, 0, d2wMatrix, 0); //invert the vpMatrix so that we can reverse from homogenous to world coordinates
		Matrix.multiplyMV(d2wworld, 0, d2wMatrix, 0, d2whomog, 0); //convert homogenous coordinates to world coordinates

		//project the vector from the camera to the world coordinates out to the z = 0 plane 
		x = 0.0f;
		y = 0.0f;
		d2whomog[0] = (eyeZ - d2wworld[2]);
		if (d2wworld[0] != 0) {
			d2whomog[1] = d2whomog[0] / d2wworld[0];
			if (d2whomog[1] != 0)
				x = eyeZ / d2whomog[1];
		}
		if (d2wworld[1] != 0) {
			d2whomog[1] = d2whomog[0] / d2wworld[1];
			if (d2whomog[1] != 0)
				y = eyeZ / d2whomog[1];
		}

		//translate back to the eye location
		x += hex;
		y += hey;

		return new float[] { x, y };
	}
}
