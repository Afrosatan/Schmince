package opengl;

import android.opengl.GLES20;

public class GLArmor implements GLIcon {
	private ShapeProgram program;

	protected final GLBuffer buffer = new GLBuffer(6).t()
			.v(-0.630, -0.997, 0.483, -0.997, 0.385, 0.680).c(0.473f, 0.501f, 0.489f).b().t()
			.v(-0.646, -1.000, -0.499, 0.765, 0.413, 0.681).c(0.473f, 0.501f, 0.489f).b().t()
			.v(0.667, -0.010, 1.000, 0.452, -0.096, 0.730).c(0.473f, 0.501f, 0.489f).b().t()
			.v(-0.336, 0.690, -0.337, 1.000, 0.166, 0.957).c(0.473f, 0.501f, 0.489f).b().t()
			.v(0.139, 0.953, 0.102, 0.575, -0.348, 0.666).c(0.473f, 0.501f, 0.489f).b().t()
			.v(0.026, 0.873, -1.000, 0.521, -0.780, 0.024).c(0.473f, 0.501f, 0.489f).b();

	protected final GLFloatBuffer vertexBuffer;
	protected final GLFloatBuffer colorBuffer;

	public GLArmor() {
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
