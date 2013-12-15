package opengl;

import android.opengl.GLES20;

public class GLPick implements GLIcon {
	private ShapeProgram program;

	protected final GLBuffer buffer = new GLBuffer(4).t()
			.v(-1.000, 0.463, -0.077, 0.994, -0.077, 0.758).c(0.473f, 0.501f, 0.489f).b().t()
			.v(-0.080, 1.000, -0.086, 0.755, 1.000, 0.410).c(0.473f, 0.501f, 0.489f).b().t()
			.v(-0.092, 0.772, -0.193, -0.963, -0.099, -1.000).c(0.510f, 0.236f, 0.000f).b().t()
			.v(-0.091, -0.937, -0.069, 0.776, -0.133, 0.784).c(0.510f, 0.236f, 0.000f).b();

	protected final GLFloatBuffer vertexBuffer;
	protected final GLFloatBuffer colorBuffer;

	public GLPick() {
		program = new ShapeProgram();

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
