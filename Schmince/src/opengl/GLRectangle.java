package opengl;

import android.opengl.GLES20;

/**
 * Simple OpenGL rectangle shape.
 */
public class GLRectangle {
	private LineProgram program;

	private GLFloatBuffer vertexBuffer;

	public GLRectangle() {
		program = new LineProgram();

		vertexBuffer = OpenGLUtil.createBuffer(4 * 3); //4 corners * 3 coordinates per corner
	}

	public void draw(float[] vpMatrix, float red, float green, float blue, float alpha) {
		program.start(vpMatrix);

		program.bufferVertexPosition(vertexBuffer);
		program.uniformColor(red, green, blue, alpha);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4); //number of vertices

		program.end();
	}

	public void setBounds(float x, float y, float width, float height) {
		float[] vertexData = vertexBuffer.getData();
		vertexData[0] = x;
		vertexData[1] = y + height;
		vertexData[3] = x + width;
		vertexData[4] = y + height;
		vertexData[6] = x;
		vertexData[7] = y;
		vertexData[9] = x + width;
		vertexData[10] = y;
	}

	public void drawLine(float[] vpMatrix, float red, float green, float blue, float alpha) {
		program.start(vpMatrix);

		program.bufferVertexPosition(vertexBuffer);
		program.uniformColor(red, green, blue, alpha);
		GLES20.glLineWidth(1f);
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, 4); //number of vertices

		program.end();
	}

	public void setLineBounds(float x, float y, float width, float height) {
		float[] vertexData = vertexBuffer.getData();
		vertexData[0] = x;
		vertexData[1] = y + height;
		vertexData[3] = x + width;
		vertexData[4] = y + height;
		vertexData[6] = x + width;
		vertexData[7] = y;
		vertexData[9] = x;
		vertexData[10] = y;
	}
}