package opengl;

import android.opengl.GLES20;

public class GLFlare implements GLIcon {
	private ShapeProgram program;

	protected final GLBuffer buffer = new GLBuffer(8).t()
			.v(0.095, 0.329, -1.000, -0.734, -0.742, -0.979).c(0.968f, 0.000f, 0.000f).b().t()
			.v(0.095, 0.340, -0.741, -1.000, 0.454, 0.149).c(0.968f, 0.000f, 0.000f).b().t()
			.v(0.166, 0.262, 0.692, 0.738, 0.283, 0.155).c(0.460f, 0.458f, 0.450f).b().t()
			.v(0.019, 0.244, 0.104, 0.340, 0.550, 0.088).c(0.983f, 0.465f, 0.000f).b().t()
			.v(-0.992, -0.727, -0.692, -0.993, -0.853, -0.632).c(0.983f, 0.465f, 0.000f).b().t()
			.v(0.661, 0.665, 0.528, 0.969, 0.717, 0.687).c(0.983f, 0.992f, 0.000f).b().t()
			.v(0.703, 0.714, 0.940, 1.000, 0.674, 0.648).c(0.983f, 0.992f, 0.000f).b().t()
			.v(0.711, 0.657, 1.000, 0.679, 0.674, 0.611).c(0.983f, 0.992f, 0.000f).b();

	protected final GLFloatBuffer vertexBuffer;
	protected final GLFloatBuffer colorBuffer;

	public GLFlare() {
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
