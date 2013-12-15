package opengl;

import android.opengl.GLES20;

/**
 * Program with varying color (full opacity) and normal vertex position. 
 */
public class ShapeProgram extends OpenGLProgram {
	private static final String vertexShaderCode = "attribute vec3 vertexPosition; attribute vec4 vertexColor; "
			+ "varying vec4 fragmentColor; uniform mat4 vpMatrix; "
			+ "void main() { gl_Position = vpMatrix * vec4(vertexPosition,1); "
			+ "fragmentColor = vertexColor; }";
	private static final String fragmentShaderCode = "precision lowp float; varying vec4 fragmentColor; "
			+ "void main() { gl_FragColor = fragmentColor; }";

	final int gl_vpMatrix;
	final int gl_vertexPosition;
	final int gl_vertexColor;

	public ShapeProgram() {
		super(vertexShaderCode, fragmentShaderCode);

		gl_vpMatrix = GLES20.glGetUniformLocation(program, "vpMatrix");
		gl_vertexPosition = GLES20.glGetAttribLocation(program, "vertexPosition");
		gl_vertexColor = GLES20.glGetAttribLocation(program, "vertexColor");
	}

	@Override
	public void start(float[] vpMatrix) {
		// Add program to OpenGL ES environment
		GLES20.glUseProgram(program);

		// Apply the transformation  
		GLES20.glUniformMatrix4fv(gl_vpMatrix, 1, false, vpMatrix, 0);
	}

	public void staticVertexPosition(GLFloatBuffer vertexBuffer) {
		staticData(gl_vertexPosition, vertexBuffer);
	}

	public void bufferVertexPosition(GLFloatBuffer vertexBuffer) {
		bufferData(gl_vertexPosition, vertexBuffer);
	}

	public void staticVertexColor(GLFloatBuffer colorBuffer) {
		staticData(gl_vertexColor, colorBuffer);
	}

	public void bufferVertexColor(GLFloatBuffer colorBuffer) {
		bufferData(gl_vertexColor, colorBuffer);
	}

	@Override
	public void end() {
		GLES20.glDisableVertexAttribArray(gl_vertexPosition);
		GLES20.glDisableVertexAttribArray(gl_vertexColor);
	}
}
