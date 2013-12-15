package opengl;

import android.opengl.GLES20;

public class GLLocator implements GLIcon {
	private ShapeProgram program;

	protected final GLBuffer buffer = new GLBuffer(14).t()
			.v(-0.399, 0.839, -0.356, 0.977, 0.583, 0.731).c(0.000f, 0.000f, 0.000f).b().t()
			.v(0.397, 0.757, 0.594, 0.736, 0.963, 0.030).c(0.000f, 0.000f, 0.000f).b().t()
			.v(0.849, 0.129, 1.000, -0.010, 0.601, -0.835).c(0.000f, 0.000f, 0.000f).b().t()
			.v(0.605, -0.854, 0.640, -0.618, -0.001, -1.000).c(0.000f, 0.000f, 0.000f).b().t()
			.v(-0.005, -0.997, 0.199, -0.856, -0.605, -0.871).c(0.000f, 0.000f, 0.000f).b().t()
			.v(-0.561, -0.899, -0.371, -0.926, -1.000, -0.332).c(0.000f, 0.000f, 0.000f).b().t()
			.v(-0.985, -0.300, -0.854, -0.431, -0.905, 0.246).c(0.000f, 0.000f, 0.000f).b().t()
			.v(-0.898, 0.117, -0.946, 0.298, -0.330, 1.000).c(0.000f, 0.000f, 0.000f).b().t()
			.v(0.141, 0.183, 0.220, 0.380, 0.350, 0.155).c(0.957f, 0.000f, 0.000f).b().t()
			.v(-0.547, -0.444, -0.541, -0.207, -0.353, -0.454).c(0.000f, 0.000f, 0.996f).b().t()
			.v(0.016, -0.516, 0.009, -0.252, 0.312, -0.481).c(0.961f, 0.992f, 0.000f).b().t()
			.v(-0.515, 0.035, -0.356, 0.345, -0.216, 0.006).c(0.000f, 0.992f, 0.992f).b().t()
			.v(0.316, -0.231, 0.391, -0.050, 0.535, -0.301).c(0.000f, 0.992f, 0.000f).b().t()
			.v(-0.325, -0.659, -0.190, -0.468, -0.076, -0.682).c(0.994f, 0.000f, 0.985f).b();

	protected final GLFloatBuffer vertexBuffer;
	protected final GLFloatBuffer colorBuffer;

	public GLLocator() {
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
