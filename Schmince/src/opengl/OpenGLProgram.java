package opengl;

import java.nio.ByteBuffer;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Wrapper around an OpenGL ES2 program with common functions/setup for setting up shaders, and buffering data.
 */
public abstract class OpenGLProgram {
	final int program;
	final int vertexShader;
	final int fragmentShader;

	OpenGLProgram(String vertexShaderCode, String fragmentShaderCode) {
		vertexShader = OpenGLUtil.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		fragmentShader = OpenGLUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		program = OpenGLUtil.createProgram(vertexShader, fragmentShader);
	}

	/**
	 * Prepare to start drawing with this program. This should be matched with a call to {@link #end()}<br>
	 * Implementations must call {@link GLES20#glUseProgram(program)}.
	 */
	public abstract void start(float[] vpMatrix);

	/**
	 * Clean up after a round of drawing with the program.<br>
	 * Implementations should call glDisableVertexAttribArray on applicable attribute handles.
	 */
	public abstract void end();

	void bufferData(int attribHandle, GLFloatBuffer floatBuffer) {
		floatBuffer.update();
		ByteBuffer bytes = floatBuffer.getByteBuffer();

		GLES20.glEnableVertexAttribArray(attribHandle);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, floatBuffer.getBufferId());
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, bytes.capacity(), bytes, GLES20.GL_DYNAMIC_DRAW);
		GLES20.glVertexAttribPointer(attribHandle, floatBuffer.getStepSize(), GLES20.GL_FLOAT,
				false, 0, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}

	void staticData(int attribHandle, GLFloatBuffer floatBuffer) {
		GLES20.glEnableVertexAttribArray(attribHandle);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, floatBuffer.getBufferId());
		GLES20.glVertexAttribPointer(attribHandle, floatBuffer.getStepSize(), GLES20.GL_FLOAT,
				false, 0, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}

	/**
	 * If you suspect the shaders are broken, call this and logcat might tell you why.
	 */
	void logShaderErrors() {
		Log.e(getClass().getName(), GLES20.glGetShaderInfoLog(vertexShader));
		Log.e(getClass().getName(), GLES20.glGetShaderInfoLog(fragmentShader));
	}

	/**
	 * Delete program and shaders, you can call this when you are done with the program if you really want to...
	 */
	public void delete() {
		GLES20.glDeleteShader(vertexShader);
		GLES20.glDeleteShader(fragmentShader);
		GLES20.glDeleteProgram(program);
	}
}
