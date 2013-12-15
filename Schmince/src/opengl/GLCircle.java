package opengl;

import android.opengl.GLES20;

/**
 * Simple OpenGL circle shape.
 */
public class GLCircle {
	private static final int VERTEX_DEGREE = 4;
	private static final int VERTICES = 360 / VERTEX_DEGREE;

	private LineProgram program;

	private GLFloatBuffer lineVertexBuffer;
	private GLFloatBuffer fillVertexBuffer;

	public GLCircle() {
		program = new LineProgram();

		float[] vertexData = new float[VERTICES * 3]; //3 floats per vertex
		float[] fillVertexData = new float[VERTICES * 3 + 3 + 3]; //3 floats per vertex + hub since it's triangle fan + copy first vertex to last position
		for (int n = 0; n < VERTICES; n++) {
			int degree = n * VERTEX_DEGREE;
			float y = (float) Math.sin(degree / 180f * Math.PI);
			float x = (float) Math.cos(degree / 180f * Math.PI);

			vertexData[n * 3] = x;
			vertexData[n * 3 + 1] = y;
			vertexData[n * 3 + 2] = 0f;

			fillVertexData[3 + n * 3] = x;
			fillVertexData[3 + n * 3 + 1] = y;
			fillVertexData[3 + n * 3 + 2] = 0f;
			if (n == 0) {
				fillVertexData[VERTICES * 3 + 3] = x;
				fillVertexData[VERTICES * 3 + 4] = y;
				fillVertexData[VERTICES * 3 + 5] = 0f;
			}
		}

		lineVertexBuffer = OpenGLUtil.createBuffer(vertexData);
		lineVertexBuffer.pushStaticBuffer();

		fillVertexBuffer = OpenGLUtil.createBuffer(fillVertexData);
		fillVertexBuffer.pushStaticBuffer();
	}

	public void drawLine(float[] vpMatrix, float lineWidth, float red, float green, float blue,
			float alpha) {
		program.start(vpMatrix);

		program.staticVertexPosition(lineVertexBuffer);
		program.uniformColor(red, green, blue, alpha);

		GLES20.glLineWidth(lineWidth);
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, VERTICES); //number of vertices

		program.end();
	}

	public void drawFill(float[] vpMatrix, float red, float green, float blue, float alpha) {
		program.start(vpMatrix);

		program.staticVertexPosition(fillVertexBuffer);
		program.uniformColor(red, green, blue, alpha);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, VERTICES + 2); //number of vertices

		program.end();
	}
}
