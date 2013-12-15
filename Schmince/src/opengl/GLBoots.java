package opengl;

import android.opengl.GLES20;

public class GLBoots implements GLIcon {
	private ShapeProgram program;

	protected final GLBuffer buffer = new GLBuffer(14).t()
			.v(-0.999, -0.113, -0.952, -0.594, -0.207, -0.659).c(0.504f, 0.528f, 0.531f).b().t()
			.v(-0.186, -0.663, -0.225, 1.000, -0.654, 0.939).c(0.462f, 0.473f, 0.473f).b().t()
			.v(-0.169, -0.720, -1.000, -0.124, -0.356, 0.083).c(0.504f, 0.528f, 0.531f).b().t()
			.v(-0.962, -0.623, -0.168, -0.743, -0.177, -0.573).c(0.196f, 0.188f, 0.189f).b().t()
			.v(-0.982, -0.641, -0.710, -0.703, -0.894, -0.869).c(0.000f, 0.984f, 0.989f).b().t()
			.v(-0.659, -0.726, -0.555, -0.962, -0.444, -0.733).c(0.000f, 0.984f, 0.989f).b().t()
			.v(-0.356, -0.739, -0.254, -0.958, -0.165, -0.723).c(0.000f, 0.984f, 0.989f).b().t()
			.v(0.108, 0.938, 0.090, -0.684, 0.462, 0.903).c(0.462f, 0.473f, 0.473f).b().t()
			.v(0.291, 0.140, 0.084, -0.718, 1.000, -0.061).c(0.504f, 0.528f, 0.531f).b().t()
			.v(0.984, -0.041, 0.947, -0.703, 0.071, -0.667).c(0.504f, 0.528f, 0.531f).b().t()
			.v(0.099, -0.691, 0.949, -0.705, 0.098, -0.818).c(0.196f, 0.188f, 0.189f).b().t()
			.v(0.106, -0.848, 0.349, -0.817, 0.248, -0.987).c(0.000f, 0.984f, 0.989f).b().t()
			.v(0.407, -0.820, 0.605, -0.766, 0.539, -1.000).c(0.000f, 0.984f, 0.989f).b().t()
			.v(0.725, -0.767, 0.955, -0.715, 0.831, -0.967).c(0.000f, 0.984f, 0.989f).b();

	protected final GLFloatBuffer vertexBuffer;
	protected final GLFloatBuffer colorBuffer;

	public GLBoots() {
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
