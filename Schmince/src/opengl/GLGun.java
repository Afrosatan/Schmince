package opengl;

import android.opengl.GLES20;

public class GLGun implements GLIcon {
	private ShapeProgram program;

	protected final GLBuffer buffer = new GLBuffer(13).t()
			.v(-0.258, 0.155, 0.989, 0.128, -0.259, 0.697).c(0.500f, 0.500f, 0.500f).b().t()
			.v(1.000, 0.095, -0.260, 0.703, 0.996, 0.706).c(0.500f, 0.500f, 0.500f).b().t()
			.v(-0.269, 0.722, -0.667, -1.000, -1.000, -0.798).c(0.500f, 0.500f, 0.500f).b().t()
			.v(-0.278, 0.751, -0.419, 0.101, -0.227, 0.149).c(0.500f, 0.500f, 0.500f).b().t()
			.v(-0.306, 0.222, -0.319, -0.153, -0.207, 0.233).c(0.287f, 0.253f, 0.267f).b().t()
			.v(-0.504, -0.347, -0.473, -0.208, -0.173, -0.350).c(0.287f, 0.253f, 0.267f).b().t()
			.v(-0.097, 0.145, -0.196, -0.346, 0.018, 0.199).c(0.287f, 0.253f, 0.267f).b().t()
			.v(-0.120, 0.334, 0.891, 0.306, 0.910, 0.355).c(0.147f, 0.159f, 0.118f).b().t()
			.v(-0.108, 0.457, -0.087, 0.518, 0.874, 0.473).c(0.147f, 0.159f, 0.118f).b().t()
			.v(-0.077, 0.249, -0.108, 0.291, 0.928, 0.225).c(0.147f, 0.159f, 0.118f).b().t()
			.v(0.855, 0.525, 0.855, 0.603, -0.061, 0.593).c(0.147f, 0.159f, 0.118f).b().t()
			.v(-0.861, -0.816, -0.686, -0.868, -0.440, 0.229).c(0.000f, 0.000f, 0.000f).b().t()
			.v(-0.364, 0.487, -0.396, 1.000, -0.206, 0.565).c(0.272f, 0.252f, 0.244f).b();

	protected final GLFloatBuffer vertexBuffer;
	protected final GLFloatBuffer colorBuffer;

	public GLGun() {
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
