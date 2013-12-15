package opengl;

import android.opengl.GLES20;

public class GLEnemy implements GLIcon {
	private static final int VERTEX_DEGREE = 40;
	private static final int CIRCLE_VERTICES = 360 / VERTEX_DEGREE;

	private ShapeProgram program;

	private static final float[] colors = { 1f, 0f, 1f, 1f, 0f, 0f, 0.15f, 1f, 0f, 0f, 0.15f, 1f, };
	protected final GLBuffer buffer = new GLBuffer(CIRCLE_VERTICES);

	protected final GLFloatBuffer vertexBuffer;
	protected final GLFloatBuffer colorBuffer;

	public GLEnemy() {
		program = new ShapeProgram();

		for (int n = 0; n < CIRCLE_VERTICES; n++) {
			int degree = n * VERTEX_DEGREE;
			float y = (float) Math.sin(degree / 180f * Math.PI);
			float x = (float) Math.cos(degree / 180f * Math.PI);

			float xxx = x * 0.25f;
			float yyy = y * 0.25f;
			buffer.t().v(x, y, -yyy, xxx, yyy, -xxx).c(colors);
		}

		vertexBuffer = OpenGLUtil.createBuffer(buffer.getVertices());
		vertexBuffer.pushStaticBuffer();

		colorBuffer = OpenGLUtil.createBuffer(buffer.getColors());
		colorBuffer.setStepSize(4);
		colorBuffer.pushStaticBuffer();
	}

	@Override
	public void draw(float[] vpMatrix) {
		program.start(vpMatrix);

		program.staticVertexPosition(vertexBuffer);
		program.staticVertexColor(colorBuffer);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, buffer.getVertexCount());

		program.end();
	}
}
