package texample;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import opengl.BatchTextProgram;
import android.opengl.GLES20;

class Vertices {
	final static int POSITION_CNT_2D = 2; // Number of Components in Vertex Position for 2D
	final static int POSITION_CNT_3D = 3; // Number of Components in Vertex Position for 3D
	final static int COLOR_CNT = 4; // Number of Components in Vertex Color
	final static int TEXCOORD_CNT = 2; // Number of Components in Vertex Texture Coords
	final static int NORMAL_CNT = 3; // Number of Components in Vertex Normal
	private static final int MVP_MATRIX_INDEX_CNT = 1; // Number of Components in MVP matrix index
	final static int INDEX_SIZE = Short.SIZE / 8; // Index Byte Size (Short.SIZE = bits)

	private final int positionCnt; // Number of Position Components (2=2D, 3=3D)
	private final int vertexStride; // Vertex Stride (Element Size of a Single Vertex)
	private final int vertexSize; // Bytesize of a Single Vertex
	private final IntBuffer vertices; // Vertex Buffer
	private final ShortBuffer indices; // Index Buffer
	private final int[] tmpBuffer; // Temp Buffer for Vertex Conversion

	private opengl.BatchTextProgram program;

	/**
	 * Create the vertices/indices as specified (for 2d)
	 * @param maxVertices maximum vertices allowed in buffer
	 * @param maxIndices maximum indices allowed in buffer
	 */
	public Vertices(int maxVertices, int maxIndices, BatchTextProgram program) {
		this.program = program;

		this.positionCnt = POSITION_CNT_2D; // Set Position Component Count
		this.vertexStride = this.positionCnt + TEXCOORD_CNT + MVP_MATRIX_INDEX_CNT; // Calculate Vertex Stride
		this.vertexSize = this.vertexStride * 4; // Calculate Vertex Byte Size

		ByteBuffer buffer = ByteBuffer.allocateDirect(maxVertices * vertexSize); // Allocate Buffer for Vertices (Max)
		buffer.order(ByteOrder.nativeOrder()); // Set Native Byte Order
		this.vertices = buffer.asIntBuffer(); // Save Vertex Buffer

		buffer = ByteBuffer.allocateDirect(maxIndices * INDEX_SIZE); // Allocate Buffer for Indices (MAX)
		buffer.order(ByteOrder.nativeOrder()); // Set Native Byte Order
		this.indices = buffer.asShortBuffer(); // Save Index Buffer

		this.tmpBuffer = new int[maxVertices * vertexSize / 4]; // Create Temp Buffer
	}

	/**
	 * Set the specified vertices in the vertex buffer.<br>
	 * NOTE: optimized to use integer buffer!
	 * @param vertices array of vertices (floats) to set
	 * @param offset offset to first vertex in array
	 * @param length number of floats in the vertex array (total) for easy setting use: vtx_cnt * (this.vertexSize / 4)
	 */
	public void setVertices(float[] vertices, int offset, int length) {
		this.vertices.clear(); // Remove Existing Vertices
		int last = offset + length; // Calculate Last Element
		for (int i = offset, j = 0; i < last; i++, j++)
			// FOR Each Specified Vertex
			tmpBuffer[j] = Float.floatToRawIntBits(vertices[i]); // Set Vertex as Raw Integer Bits in Buffer
		this.vertices.put(tmpBuffer, 0, length); // Set New Vertices
		this.vertices.flip(); // Flip Vertex Buffer
	}

	/**
	 * Set the specified indices in the index buffer.
	 * @param indices array of indices (shorts) to set
	 * @param offset offset to first index in array
	 * @param length number of indices in array (from offset)
	 */
	public void setIndices(short[] indices, int offset, int length) {
		this.indices.clear(); // Clear Existing Indices
		this.indices.put(indices, offset, length); // Set New Indices
		this.indices.flip(); // Flip Index Buffer
	}

	/**
	 * Perform all required binding/state changes before rendering batches.<br>
	 * USAGE: call once before calling draw() multiple times for this buffer.
	 */
	public void bind() {
		// bind vertex position pointer
		vertices.position(0); // Set Vertex Buffer to Position
		program.usePosition(positionCnt, vertexSize, vertices);

		// bind texture position pointer
		vertices.position(positionCnt); // Set Vertex Buffer to Texture Coords (NOTE: position based on whether color is also specified)
		program.useTextureCoord(TEXCOORD_CNT, vertexSize, vertices);

		// bind MVP Matrix index position handle
		vertices.position(positionCnt + TEXCOORD_CNT);
		program.useMVPMatrixIndex(MVP_MATRIX_INDEX_CNT, vertexSize, vertices);
	}

	/**
	 * Draw the currently bound vertices in the vertex/index buffers.<br>
	 * USAGE: can only be called after calling bind() for this buffer.
	 * @param primitiveType the type of primitive to draw
	 * @param offset the offset in the vertex/index buffer to start at
	 * @param numVertices the number of vertices (indices) to draw
	 */
	public void draw(int primitiveType, int offset, int numVertices) {
		indices.position(offset); // Set Index Buffer to Specified Offset
		//draw indexed
		GLES20.glDrawElements(primitiveType, numVertices, GLES20.GL_UNSIGNED_SHORT, indices);
	}
}
