package opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

/**
 * Wrapper around all of the ByteBuffer/FloatBuffer crap.
 */
public class GLFloatBuffer {
	private static final int BYTES_PER_FLOAT = 4;

	private int bufferId;
	private int stepSize = 3;
	private float[] data;
	private ByteBuffer byteBuffer;
	private FloatBuffer floatBuffer;

	/**
	 * Normally this should only be accessed via {@link OpenGLUtil#createBuffer(int)} or {@link OpenGLUtil#createBuffer(float[])}.
	 */
	public GLFloatBuffer(int id, int count, float[] dataparam) {
		this.bufferId = id;

		if (dataparam != null) {
			data = dataparam;
		} else {
			data = new float[count];
		}
		byteBuffer = ByteBuffer.allocateDirect(data.length * BYTES_PER_FLOAT).order(
				ByteOrder.nativeOrder());
		floatBuffer = byteBuffer.asFloatBuffer();
	}

	public float[] getData() {
		return data;
	}

	public void update() {
		floatBuffer.put(data);
		floatBuffer.position(0);
	}

	public ByteBuffer getByteBuffer() {
		return byteBuffer;
	}

	public int getBufferId() {
		return bufferId;
	}

	public void setStepSize(int stepSize) {
		this.stepSize = stepSize;
	}

	public int getStepSize() {
		return stepSize;
	}

	public void pushStaticBuffer() {
		update();
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferId);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, byteBuffer.capacity(), byteBuffer,
				GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}
}
