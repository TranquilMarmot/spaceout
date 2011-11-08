package graphics.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;

public class ModelBuilder {
	/** the name of the model */
	private String name;

	/** the indexed mesh, used to create the ship for JBullet */
	public IndexedMesh mesh;
	private ByteBuffer triangleIndexBase;
	private ByteBuffer vertexBase;

	private ArrayList<Vector3f> vertices;
	private ArrayList<Vector3f> normals;
	private ArrayList<Integer> triangleIndices;
	private ArrayList<Integer> normalIndices;

	/** for rendering the model */
	private int callList;

	public ModelBuilder(String modelName) {
		mesh = new IndexedMesh();

		// each float is 4 bytes
		mesh.triangleIndexStride = 4;
		// an int is also 4 bytes
		mesh.vertexStride = 4;
	}

	public void addNormal(Vector3f vertex) {
		normals.add(vertex);
	}
	
	public void addTriangleNormalIndices(int[] indices){
		if(indices.length != 3)
			System.out.println("Didn't get 3 indices for a triangle normals! Adding anyway...");
		normalIndices.add(indices[0]);
		normalIndices.add(indices[1]);
		normalIndices.add(indices[2]);
	}
	
	public void addQuadNormalIndices(int[] indices){
		if(indices.length != 4)
			System.out.println("Didn't get 4 indices for a quad normals! Adding anyway...");
		// TODO have this split the indices into two triangles
	}
	
	
	public void addVertex(Vector3f vertex) {
		vertices.add(vertex);
	}

	public void addTriangleVertexIndices(int[] indices) {
		if(indices.length != 3)
			System.out.println("Didn't get 3 indices for a triangle vertices! Adding anyway...");
		triangleIndices.add(indices[0]);
		triangleIndices.add(indices[1]);
		triangleIndices.add(indices[2]);
	}

	public void addQuadVertexIndices(int[] indices) {
		if(indices.length != 4)
			System.out.println("Didn't get 4 indices for a quad vertices! Adding anyway...");
		// TODO have this split the quad into two triangles ([top left, bottom
		// left, bottom right] and [top left, top right, bottom right])
	}

	/**
	 * This method should be called after all the vertices, faces, and normals
	 * and their respective indices have been added.
	 */
	public Model makeModel(float scale) {
			try {
				// the indices are stored as singular ints, in groups of threes (three per triangle)
				mesh.numTriangles = triangleIndices.size() / 3;
				// vertices are stored as Vector3f objects, so the size of the list is the number of vertices
				mesh.numVertices = vertices.size();
				
				/*
				 * Vertices are stored in the vertices ArrayList as Vector3f objects.
				 * This means that for every index in 'vertices', we have three floats
				 * representing the vertex. So we multiply the size by three, 
				 * as we only want single floats in our ByteBuffer.
				 * We multiply that by the vertexStride (number of bytes per float; 4)
				 * to get the total number of bytes for the buffer.
				 */
				vertexBase = BufferUtils.createByteBuffer(vertices.size() * 3
						* mesh.vertexStride);
				for (Vector3f vect : vertices) {
					vertexBase.putFloat(vect.x * scale);
					vertexBase.putFloat(vect.y * scale);
					vertexBase.putFloat(vect.z * scale);
				}

				/*
				 * Indices are stored in the faceIndices ArrayList as ints.
				 * So we want number of indices * stride (number of bytes per int; 4)
				 * bytes for the buffer.
				 */
				triangleIndexBase = BufferUtils.createByteBuffer(triangleIndices
						.size() * mesh.triangleIndexStride);
				for (int index : triangleIndices) {
					triangleIndexBase.putInt(index);
				}
				
				// set the mesh's bases
				mesh.vertexBase = vertexBase;
				mesh.triangleIndexBase = triangleIndexBase;
				
				// create a TriangleIndexVertxArray and the collision shape
				TriangleIndexVertexArray triangleArray = new TriangleIndexVertexArray();
				triangleArray.addIndexedMesh(mesh);
				CollisionShape collisionShape = new BvhTriangleMeshShape(triangleArray, true);

				/* BEGIN CALL LIST BUILDING */
				callList = GL11.glGenLists(1);
				
				if(triangleIndices.size() != normalIndices.size() && normalIndices.size() > 0)
					System.out.println("triangleIndices size is not equal to normalIndices size!!!");

				GL11.glNewList(callList, GL11.GL_COMPILE);
				{
					GL11.glBegin(GL11.GL_TRIANGLES);
					{
						for (int i = 0; i < triangleIndices.size(); i += 3){
							// grab the three vertices
							int v1 = triangleIndices.get(i);
							int v2 = triangleIndices.get(i + 1);
							int v3 = triangleIndices.get(i + 2);
							
							// FIXME this needs to only grab/use normals if they exist, otherwise is just does the vertices
							int n1 = normalIndices.get(i);
							int n2 = normalIndices.get(i + 1);
							int n3 = normalIndices.get(i + 2);
							
							// grab the normals
							Vector3f normal1 = normals.get(n1);
							Vector3f normal2 = normals.get(n2);
							Vector3f normal3 = normals.get(n3);
							
							// grab the vertices
							Vector3f vertex1 = vertices.get(v1);
							Vector3f vertex2 = vertices.get(v2);
							Vector3f vertex3 = vertices.get(v3);
							
							// draw the first normal/vertex
							GL11.glNormal3f(normal1.x * scale, normal1.y * scale, normal1.z * scale);
							GL11.glVertex3f(vertex1.x * scale, vertex1.y * scale, vertex1.z * scale);
							
							// draw the second normal/vertex
							GL11.glNormal3f(normal2.x * scale, normal2.y * scale, normal2.z * scale);
							GL11.glVertex3f(vertex2.x * scale, vertex2.y * scale, vertex2.z * scale);
							
							// draw the third normal/vertex
							GL11.glNormal3f(normal3.x * scale, normal3.y * scale, normal3.z * scale);
							GL11.glVertex3f(vertex3.x * scale, vertex3.y * scale, vertex3.z * scale);
						}
					}
					GL11.glEnd();
				}
				GL11.glEndList();
				
				/* END CALL LIST BUILDING */
				
				return new Model(collisionShape, callList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		return  null;
	}
	public int getCallList() {
		return callList;
	}

	public String getName() {
		return name;
	}
}
