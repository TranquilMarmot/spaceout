package graphics.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;

import com.bulletphysics.collision.shapes.IndexedMesh;

public class Model {
	/** the name of the model */
	private String name;

	/** the indexed mesh, used to create the ship for JBullet */
	public IndexedMesh mesh;
	private ByteBuffer triangleIndexBase;
	private ByteBuffer vertexBase;
	
	private ArrayList<Vector3f> vertices;
	private ArrayList<Vector3f> normals;
	private ArrayList<Integer> faces;
	
	/** for rendering the model */
	private int callList;

	public Model(String name, int numTriangles, int numVertices) {
		mesh = new IndexedMesh();
		mesh.numTriangles = numTriangles;
		mesh.numVertices = numVertices;
		// each float is 4 bytes
		mesh.triangleIndexStride = 4;
		// an int is also 4 bytes
		mesh.vertexStride = 4;
		
		mesh.triangleIndexBase = triangleIndexBase;
		mesh.vertexBase = vertexBase;
	}
	
	public void addVertex(Vector3f vertex){
		vertices.add(vertex);
	}
	
	public void addNormal(Vector3f vertex){
		normals.add(vertex);
	}
	
	public void addTriangle(int[] vertices){
		faces.add(vertices[0]);
		faces.add(vertices[1]);
		faces.add(vertices[2]);
	}
	
	public void finalize(){
		/*
		 *  each triangle has 3 vertices, and each vertex is 4 bytes
		 *  so we need (numTriangles * 3 vertices/triangle * n bytes/vertex) bytes
		 */
		// FIXME is this correct?
		triangleIndexBase = BufferUtils.createByteBuffer(vertices.size() * 3 * mesh.triangleIndexStride );
		vertexBase = BufferUtils.createByteBuffer(faces.size() * 3 * mesh.vertexStride);
		
		// TODO have this fill up the ByteBuffers with the vertices and faces
		// TODO then got through all of them and build a call list
	}
	
	public int getCallList(){
		return callList;
	}

	public String getName() {
		return name;
	}
}
