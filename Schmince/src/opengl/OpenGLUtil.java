package opengl;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Methods to help with common OpenGL functions.
 */
public class OpenGLUtil {
	/**
	 * Create a vertex shader type (GLES20.GL_VERTEX_SHADER) or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
	 */
	public static int loadShader(int type, String shaderCode) {
		int shaderHandle = GLES20.glCreateShader(type);

		if (shaderHandle != 0) {
			GLES20.glShaderSource(shaderHandle, shaderCode);
			GLES20.glCompileShader(shaderHandle);

			// Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0) {
				Log.v(OpenGLUtil.class.getCanonicalName(),
						"Shader fail info: " + GLES20.glGetShaderInfoLog(shaderHandle));
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}

		if (shaderHandle == 0) {
			throw new RuntimeException("Error creating shader " + type);
		}

		return shaderHandle;
	}

	public static int createProgram(int vertexShaderHandle, int fragmentShaderHandle) {
		int mProgram = GLES20.glCreateProgram();

		if (mProgram != 0) {
			GLES20.glAttachShader(mProgram, vertexShaderHandle);
			GLES20.glAttachShader(mProgram, fragmentShaderHandle);

			GLES20.glLinkProgram(mProgram);

			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);

			if (linkStatus[0] == 0) {
				Log.v(OpenGLUtil.class.getCanonicalName(), GLES20.glGetProgramInfoLog(mProgram));
				GLES20.glDeleteProgram(mProgram);
				mProgram = 0;
			}
		}

		if (mProgram == 0) {
			throw new RuntimeException("Error creating program.");
		}

		return mProgram;
	}

	/**
	 * Create a buffer with the number of elements provided.
	 */
	public static GLFloatBuffer createBuffer(int count) {
		int[] buffers = new int[1];
		GLES20.glGenBuffers(buffers.length, buffers, 0);
		return new GLFloatBuffer(buffers[0], count, null);
	}

	public static GLFloatBuffer createBuffer(float[] data) {
		int[] buffers = new int[1];
		GLES20.glGenBuffers(buffers.length, buffers, 0);
		return new GLFloatBuffer(buffers[0], data.length, data);
	}
}
