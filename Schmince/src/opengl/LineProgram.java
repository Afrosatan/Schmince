package opengl;

import android.opengl.GLES20;

/**
 * Program with a uniform color, and a normal vertex position.
 */
public class LineProgram extends OpenGLProgram {
	private static final String vertexShaderCode = "attribute vec3 vertexPosition; uniform mat4 vpMatrix; "
			+ "void main() { gl_Position = vpMatrix * vec4(vertexPosition,1); }";
	private static final String fragmentShaderCode = "precision lowp float; uniform vec4 fragmentColor; "
			+ "void main() { gl_FragColor = fragmentColor; }";

	private int gl_vpMatrix;
	private int gl_vertexPosition;
	private int gl_fragmentColor;

	public LineProgram() {
		super(vertexShaderCode, fragmentShaderCode);

		gl_vpMatrix = GLES20.glGetUniformLocation(program, "vpMatrix");
		gl_vertexPosition = GLES20.glGetAttribLocation(program, "vertexPosition");
		gl_fragmentColor = GLES20.glGetUniformLocation(program, "fragmentColor");
	}

	@Override
	public void start(float[] vpMatrix) {
		// Add program to OpenGL ES environment
		GLES20.glUseProgram(program);

		// Apply the transformation  
		GLES20.glUniformMatrix4fv(gl_vpMatrix, 1, false, vpMatrix, 0);
	}

	public void bufferVertexPosition(GLFloatBuffer vertexBuffer) {
		bufferData(gl_vertexPosition, vertexBuffer);
	}

	public void staticVertexPosition(GLFloatBuffer vertexBuffer) {
		staticData(gl_vertexPosition, vertexBuffer);
	}

	public void uniformColor(float red, float green, float blue, float alpha) {
		GLES20.glUniform4f(gl_fragmentColor, red, green, blue, alpha);
	}

	@Override
	public void end() {
		GLES20.glDisableVertexAttribArray(gl_vertexPosition);
	}
}
