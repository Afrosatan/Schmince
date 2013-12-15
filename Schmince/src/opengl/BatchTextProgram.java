package opengl;

import java.nio.IntBuffer;

import android.opengl.GLES20;

/**
 * Program for text drawing, uses odd mvp matrices, pulls color data from a text and multiplies by a uniform color.
 */
public class BatchTextProgram extends OpenGLProgram {
	private static final String vertexShaderCode = "uniform mat4 u_MVPMatrix[24];" // An array representing the combined model/view/projection matrices for each sprite
			+ "attribute float a_MVPMatrixIndex; " // The index of the MVPMatrix of the particular sprite
			+ "attribute vec4 a_Position; " // Per-vertex position information we will pass in.
			+ "attribute vec2 a_TexCoordinate; " // Per-vertex texture coordinate information we will pass in
			+ "varying vec2 v_TexCoordinate; " // This will be passed into the fragment shader.
			+ "varying vec4 v_Position; " // 
			+ "void main() { " // The entry point for our vertex shader.
			+ "	  int mvpMatrixIndex = int(a_MVPMatrixIndex); " //
			+ "   v_TexCoordinate = a_TexCoordinate; " //
			+ "   v_Position = a_Position; " //
			+ "   gl_Position = u_MVPMatrix[mvpMatrixIndex] " // gl_Position is a special variable used to store the final position.
			+ "               * a_Position; " // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
			+ "}";
	private static final String fragmentShaderCode = "uniform sampler2D u_Texture; " // The input texture.
			+ "precision mediump float; " // Set the default precision to medium. We don't need as high of a precision in the fragment shader.
			+ "uniform vec4 u_Color; " //uniform color of the text
			+ "varying vec2 v_TexCoordinate; " // Interpolated texture coordinate per fragment.
			+ "uniform float u_clipMinX; " //
			+ "uniform float u_clipMinY; " //
			+ "uniform float u_clipMaxX; " //
			+ "uniform float u_clipMaxY; " //
			+ "varying vec4 v_Position; " // 
			+ "void main() { " // The entry point for our fragment shader.
			+ "   if(v_Position.x < u_clipMinX || v_Position.x > u_clipMaxX || v_Position.y < u_clipMinY || v_Position.y > u_clipMaxY) {" //outside of clip bounds
			+ "      gl_FragColor = vec4(0, 0, 0, 0); " //transparent
			+ "   } else {" //
			+ "      gl_FragColor = texture2D(u_Texture, v_TexCoordinate).w * u_Color; " // texture is grayscale so take only grayscale value from it when computing color output (otherwise font is always black)
			+ "   }" //
			+ "}";

	final int gl_u_MVPMatrix;
	final int gl_u_Color;
	final int gl_u_Texture;

	final int gl_a_MVPMatrixIndex;
	final int gl_a_TexCoordinate;
	final int gl_a_Position;

	final int gl_u_clipMinX;
	final int gl_u_clipMinY;
	final int gl_u_clipMaxX;
	final int gl_u_clipMaxY;

	public BatchTextProgram() {
		super(vertexShaderCode, fragmentShaderCode);

		gl_u_MVPMatrix = GLES20.glGetUniformLocation(program, "u_MVPMatrix");
		gl_u_Color = GLES20.glGetUniformLocation(program, "u_Color");
		gl_u_Texture = GLES20.glGetUniformLocation(program, "u_Texture");

		gl_a_MVPMatrixIndex = GLES20.glGetAttribLocation(program, "a_MVPMatrixIndex");
		gl_a_TexCoordinate = GLES20.glGetAttribLocation(program, "a_TexCoordinate");
		gl_a_Position = GLES20.glGetAttribLocation(program, "a_Position");

		gl_u_clipMinX = GLES20.glGetUniformLocation(program, "u_clipMinX");
		gl_u_clipMinY = GLES20.glGetUniformLocation(program, "u_clipMinY");
		gl_u_clipMaxX = GLES20.glGetUniformLocation(program, "u_clipMaxX");
		gl_u_clipMaxY = GLES20.glGetUniformLocation(program, "u_clipMaxY");
	}

	@Override
	public void start(float[] vpMatrix) {
		// Add program to OpenGL ES environment
		GLES20.glUseProgram(program);
	}

	public void useMVPMatrices(int numSprites, float[] uMVPMatrices) {
		// bind MVP matrices array to shader
		GLES20.glUniformMatrix4fv(gl_u_MVPMatrix, numSprites, false, uMVPMatrices, 0);
		GLES20.glEnableVertexAttribArray(gl_u_MVPMatrix);
	}

	public void useColor(float red, float green, float blue, float alpha) {
		GLES20.glUniform4f(gl_u_Color, red, green, blue, alpha);
		GLES20.glEnableVertexAttribArray(gl_u_Color);
	}

	public void useTexture(int textureId) {
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0); // Set the active texture unit to texture unit 0
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId); // Bind the texture to this unit
		// Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0
		GLES20.glUniform1i(gl_u_Texture, 0);
	}

	public void useMVPMatrixIndex(int mvpMatrixIndexCnt, int vertexSize, IntBuffer vertices) {
		GLES20.glVertexAttribPointer(gl_a_MVPMatrixIndex, mvpMatrixIndexCnt, GLES20.GL_FLOAT,
				false, vertexSize, vertices);
		GLES20.glEnableVertexAttribArray(gl_a_MVPMatrixIndex);
	}

	public void useTextureCoord(int texcoordCnt, int vertexSize, IntBuffer vertices) {
		GLES20.glVertexAttribPointer(gl_a_TexCoordinate, texcoordCnt, GLES20.GL_FLOAT, false,
				vertexSize, vertices);
		GLES20.glEnableVertexAttribArray(gl_a_TexCoordinate);
	}

	public void usePosition(int positionCnt, int vertexSize, IntBuffer vertices) {
		GLES20.glVertexAttribPointer(gl_a_Position, positionCnt, GLES20.GL_FLOAT, false,
				vertexSize, vertices);
		GLES20.glEnableVertexAttribArray(gl_a_Position);
	}

	@Override
	public void end() {
		GLES20.glDisableVertexAttribArray(gl_u_MVPMatrix);
		GLES20.glDisableVertexAttribArray(gl_u_Color);
		GLES20.glDisableVertexAttribArray(gl_u_Texture);

		GLES20.glDisableVertexAttribArray(gl_a_MVPMatrixIndex);
		GLES20.glDisableVertexAttribArray(gl_a_TexCoordinate);
		GLES20.glDisableVertexAttribArray(gl_a_Position);

		GLES20.glDisableVertexAttribArray(gl_u_clipMinX);
		GLES20.glDisableVertexAttribArray(gl_u_clipMinY);
		GLES20.glDisableVertexAttribArray(gl_u_clipMaxX);
		GLES20.glDisableVertexAttribArray(gl_u_clipMaxY);
	}

	public void setClipBounds(float minx, float miny, float maxx, float maxy) {
		GLES20.glUniform1f(gl_u_clipMinX, minx);
		GLES20.glUniform1f(gl_u_clipMinY, miny);
		GLES20.glUniform1f(gl_u_clipMaxX, maxx);
		GLES20.glUniform1f(gl_u_clipMaxY, maxy);
		GLES20.glEnableVertexAttribArray(gl_u_clipMinX);
		GLES20.glEnableVertexAttribArray(gl_u_clipMinY);
		GLES20.glEnableVertexAttribArray(gl_u_clipMaxX);
		GLES20.glEnableVertexAttribArray(gl_u_clipMaxY);
	}
}
