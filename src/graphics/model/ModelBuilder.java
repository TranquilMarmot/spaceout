package graphics.model;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.vecmath.Point2f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.ScalarType;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;


/*
 * NOTENOTENOTE
 * Given indices 0,1,2,3 a quad can be split into two triangles:
 * 0,1,2 and 2,3,0
 */


public class ModelBuilder {
	/** the name of the model */
	public String name;

	/** the vertices of the model */
	private ArrayList<Vector3f> vertices;

	/** the normals of the model */
	private ArrayList<Vector3f> normals;

	/** the texture coordinates of the model */
	private ArrayList<Point2f> textureCoords;

	/** which vertices to call */
	private ArrayList<int[]> vertexIndices;

	/** which normals to call */
	private ArrayList<int[]> normalIndices;

	/** which texture coordinates to call */
	private ArrayList<int[]> textureIndices;

	public ModelBuilder() {
		/*
		 * Have to add a blank element to the beginning of each list, as the obj
		 * file starts referencing elements at 1, but ArrayLists start at 0
		 */
		vertices = new ArrayList<Vector3f>();
		vertices.add(new Vector3f(0.0f, 0.0f, 0.0f));

		normals = new ArrayList<Vector3f>();
		normals.add(new Vector3f(0.0f, 0.0f, 0.0f));

		textureCoords = new ArrayList<Point2f>();
		textureCoords.add(new Point2f(0.0f, 0.0f));

		// these just store which vertices to grab, don't need to add a blank
		// element to them
		vertexIndices = new ArrayList<int[]>();
		normalIndices = new ArrayList<int[]>();
		textureIndices = new ArrayList<int[]>();
	}

	public void addVertex(Vector3f vertex) {
		vertices.add(vertex);
	}

	public void addVertexIndices(int[] indices) {
		if(indices.length == 3)
			vertexIndices.add(indices);
		else if(indices.length == 4){
			int[] tri1 = new int[3];
			tri1[0] = indices[0];
			tri1[1] = indices[1];
			tri1[2] = indices[2];
			
			vertexIndices.add(tri1);
			
			int[] tri2 = new int[3];
			tri2[0] = indices[2];
			tri2[1] = indices[3];
			tri2[2] = indices[0];
			
			vertexIndices.add(tri2);
		} else{
			System.out.println("Error! Array not a triangle or a quad! (ModelBuilder)");
		}
	}

	public void addNormal(Vector3f vertex) {
		normals.add(vertex);
	}

	public void addNormalIndices(int[] indices) {
		if(indices.length == 3)
			normalIndices.add(indices);
		else if(indices.length == 4){
			int[] tri1 = new int[3];
			tri1[0] = indices[0];
			tri1[1] = indices[1];
			tri1[2] = indices[2];
			
			normalIndices.add(tri1);
			
			int[] tri2 = new int[3];
			tri2[0] = indices[2];
			tri2[1] = indices[3];
			tri2[2] = indices[0];
			
			normalIndices.add(tri2);
		} else{
			System.out.println("Error! Array not a triangle or a quad! (ModelBuilder)");
		}
	}
	
	public void addTextureCoords(Point2f point){
		textureCoords.add(point);
	}

	public void addTetxureIndices(int[] indices) {
		if(indices.length == 3)
			textureIndices.add(indices);
		else if(indices.length == 4){
			int[] tri1 = new int[3];
			tri1[0] = indices[0];
			tri1[1] = indices[1];
			tri1[2] = indices[2];
			
			textureIndices.add(tri1);
			
			int[] tri2 = new int[3];
			tri2[0] = indices[2];
			tri2[1] = indices[3];
			tri2[2] = indices[0];
			
			textureIndices.add(tri2);
		} else{
			System.out.println("Error! Array not a triangle or a quad! (ModelBuilder)");
		}
	}

	/**
	 * This method should be called after all the vertices, faces, and normals
	 * and their respective indices have been added.
	 */
	public Model makeModel(float scale) {
		//return new Model(buildCallList());
		return new Model(buildCollisionShape(), buildCallList());
	}
	
	private CollisionShape buildCollisionShape(){
		/*
		 *  Each Vector3f in vertices has 3 floats, and each float is 4 bytes so we need
		 *  (number of vertices * 3 floats per vertex * 4 bytes per float) bytes
		 */
		ByteBuffer vertexBase = ByteBuffer.allocate((vertices.size() - 1) * 3 * 4);
		//ByteBuffer vertexBase = BufferUtils.createByteBuffer(vertices.size() * 3 * 4);
		for(int i = 1; i < vertices.size(); i++){
			Vector3f vec = vertices.get(i);
			vertexBase.putFloat(vec.x);
			vertexBase.putFloat(vec.y);
			vertexBase.putFloat(vec.z);
		}
		
		vertexBase.rewind();
		
		/*
		 *  Each int[] in vertexIndices has 3 ints, and each int is 4 bytes so we need
		 *  (number of arrays * 3 ints per array * 4 bytes per int) bytes
		 */
		ByteBuffer triangleIndexBase = ByteBuffer.allocate(vertexIndices.size() * 3 * 4);
		//ByteBuffer triangleIndexBase = BufferUtils.createByteBuffer(vertexIndices.size() * 3 * 4);
		for(int i = 0; i < vertexIndices.size(); i++){
			int[] triangleIndices = vertexIndices.get(i);
			for(int j : triangleIndices)
				triangleIndexBase.putInt(j);
		}
		
		triangleIndexBase.rewind();
		
		IndexedMesh imesh = new IndexedMesh();
		imesh.triangleIndexBase = triangleIndexBase;
		imesh.numTriangles = vertexIndices.size();
		// each int is 4 bytes
		imesh.triangleIndexStride = 1;
		
		imesh.vertexBase = vertexBase;
		// size - 1 because first element is a dummy
		imesh.numVertices = vertices.size() - 1;
		// each float is 4 bytes
		imesh.vertexStride = 1;
		
		TriangleIndexVertexArray vertArr = new TriangleIndexVertexArray();
		
		vertArr.addIndexedMesh(imesh);
		
		return new BvhTriangleMeshShape(vertArr, true);
	}

	private int buildCallList() {
		int callList = GL11.glGenLists(1);

		GL11.glNewList(callList, GL11.GL_COMPILE);
		{
			for(int i = 0; i < vertexIndices.size(); i++){
				int[] verts = vertexIndices.get(i);
				int[] norms = normalIndices.get(i);
				int[] texts = textureIndices.get(i);
				
				// triangle
				if(verts.length == 3 && norms.length == 3 && texts.length == 3){
					GL11.glBegin(GL11.GL_TRIANGLES);{
						drawArrays(verts, norms, texts);
					}GL11.glEnd();
				}
				//quad
				else if(verts.length == 4 && norms.length == 4 && texts.length == 4){
					GL11.glBegin(GL11.GL_QUADS);{
						drawArrays(verts, norms, texts);
					}GL11.glEnd();
				} else{
					System.out.println("Error! There's either not the right amount of indices for something, or there's not that same amount of geom, normal, and texture coordinates (ModelBuilder)");
				}
			}
		}
		GL11.glEndList();
		
		return callList;
	}
	
	private void drawArrays(int[] verts, int[] norms, int[] texCoords){
		for(int i = 0; i < verts.length; i++){
			Vector3f vertex = vertices.get(verts[i]);
			Vector3f normal = normals.get(norms[i]);
			Point2f coord = textureCoords.get(texCoords[i]);
			
			GL11.glTexCoord2f(coord.x, 1 - coord.y);
			GL11.glVertex3f(vertex.x, vertex.y, vertex.z);
			GL11.glNormal3f(normal.x, normal.y, normal.z);
		}
	}

	public String getName() {
		return name;
	}
}
