package spaceguts.graphics.glsl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.vecmath.Point2f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import spaceguts.util.model.Model;
import spaceguts.util.model.ModelLoader;
import spaceguts.util.resources.Textures;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.collision.shapes.ShapeHull;
import com.bulletphysics.util.ObjectArrayList;

/**
 * Used to build a model being read in from a file. One of these should be
 * created for every model being created, and the ultimate outcome is getting a
 * {@link Model} from calling <code>makeModel</code>
 * 
 * @author TranquilMarmot
 * @see ModelLoader
 */
public class GLSLModelBuilder {
	/** the vertices of the model */
	private ObjectArrayList<Vector3f> vertices;

	/** the normals of the model */
	private ObjectArrayList<Vector3f> normals;

	/** the texture coordinates of the model */
	private ObjectArrayList<Point2f> textureCoords;

	/** which vertices to call */
	private ObjectArrayList<int[]> vertexIndices;

	/** which normals to call */
	private ObjectArrayList<int[]> normalIndices;

	/** which texture coordinates to call */
	private ObjectArrayList<int[]> textureIndices;
	
	/** max and min values for the model being built (for general use, like bounding boxes) */
	public float maxX, minX, maxY, minY, maxZ, minZ = 0.0f;

	/**
	 * ModelBuilder initializer
	 */
	public GLSLModelBuilder() {
		/*
		 * We have to add a blank element to the beginning of each list, as the obj
		 * file starts referencing elements at 1, but ArrayLists start at 0
		 */
		vertices = new ObjectArrayList<Vector3f>();
		vertices.add(new Vector3f(0.0f, 0.0f, 0.0f));

		normals = new ObjectArrayList<Vector3f>();
		normals.add(new Vector3f(0.0f, 0.0f, 0.0f));

		textureCoords = new ObjectArrayList<Point2f>();
		textureCoords.add(new Point2f(0.0f, 0.0f));

		// these just store which vertices to grab, don't need to add a blank
		// element to them
		vertexIndices = new ObjectArrayList<int[]>();
		normalIndices = new ObjectArrayList<int[]>();
		textureIndices = new ObjectArrayList<int[]>();
	}

	/**
	 * Add a vertex to the model being built
	 * 
	 * @param vertex
	 *            The vertex to add
	 */
	public void addVertex(Vector3f vertex) {
		// check for max and min values
		if(vertex.x > maxX)
			maxX = vertex.x;
		if(vertex.x < minX)
			minX = vertex.x;
		
		if(vertex.y > maxY)
			maxY = vertex.y;
		if(vertex.y < minY)
			minY = vertex.y;
		
		if(vertex.z > maxZ)
			maxZ = vertex.z;
		if(vertex.z < minZ)
			minZ = vertex.z;
		
		vertices.add(vertex);
	}

	/**
	 * Add vertex indices to the model being built. Automatically splits quads
	 * into triangles for simplicity.
	 * 
	 * @param indices The indices to add
	 */
	public void addVertexIndices(int[] indices) {
		// add if it's just a triangle
		if (indices.length == 3)
			vertexIndices.add(indices);
		// else split the quad into two triangles
		else if (indices.length == 4) {
			/*
			 * Given indices 0,1,2,3 a quad can be split into two triangles:
			 * 0,1,2 and 2,3,0
			 */
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
		} else {
			System.out
					.println("Error! Array not a triangle or a quad! (ModelBuilder)");
		}
	}

	/**
	 * Add a normal to the model being built.
	 * 
	 * @param vertex
	 *            The vertex to add
	 */
	public void addNormal(Vector3f vertex) {
		normals.add(vertex);
	}

	/**
	 * Add normal indices to the model being built. Automatically splits quads
	 * into triangles for simplicity.
	 * 
	 * @param indices The indices to add
	 */
	public void addNormalIndices(int[] indices) {
		// add if it's just a triangle
		if (indices.length == 3)
			normalIndices.add(indices);
		// else split the quad into two triangles
		else if (indices.length == 4) {
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
		} else {
			System.out
					.println("Error! Array not a triangle or a quad! (ModelBuilder)");
		}
	}

	/**
	 * Add texture coordinates to the model being built. Only 2D texture coordinates are supported right now.
	 * @param point The texture coordinates to add
	 */
	public void addTextureCoords(Point2f point) {
		textureCoords.add(point);
	}

	/**
	 * Add texture coordinate indices to the model being built. Automatically splits quads
	 * into triangles for simplicity.
	 * @param indices The indices to add
	 */
	public void addTetxureIndices(int[] indices) {
		if (indices.length == 3)
			textureIndices.add(indices);
		else if (indices.length == 4) {
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
		} else {
			System.out
					.println("Error! Array not a triangle or a quad! (ModelBuilder)");
		}
	}

	/**
	 * This method should be called after all the vertices, faces, and normals
	 * and their respective indices have been added.
	 * @return A model built using the current indices
	 */
	public GLSLModel makeModel(Textures texture) {
		FloatBuffer verticesBuf = BufferUtils.createFloatBuffer(vertices.size() * 3);
		/*
		for(Vector3f v : vertices){
			verticesBuf.put(v.x);
			verticesBuf.put(v.y);
			verticesBuf.put(v.z);
		}
		*/
		
		FloatBuffer normalsBuf = BufferUtils.createFloatBuffer(normals.size() * 3);
		/*
		for(Vector3f n : normals){
			normalsBuf.put(n.x);
			normalsBuf.put(n.y);
			normalsBuf.put(n.z);
		}
		*/
		
		FloatBuffer texCoordBuf = BufferUtils.createFloatBuffer(textureCoords.size() * 2);
		/*
		for(Point2f p : textureCoords){
			texCoordBuf.put(p.x);
			texCoordBuf.put(p.y);
		}
		*/
		
		IntBuffer indices = BufferUtils.createIntBuffer(vertexIndices.size());
		for(int i[] : vertexIndices){
			indices.put(i[0]);
			indices.put(i[1]);
			indices.put(i[2]);
		}
		
		int vertIndex = 0, txIndex = 0;
		for(int i = 0; i < vertices.size(); i++){
			Vector3f vert = vertices.get(i);
			verticesBuf.put(vertIndex, vert.x);
			verticesBuf.put(vertIndex + 1, vert.y);
			verticesBuf.put(vertIndex + 2, vert.z);
			
			Vector3f norm = normals.get(i);
			normalsBuf.put(vertIndex, norm.x);
			normalsBuf.put(vertIndex + 1, norm.y);
			normalsBuf.put(vertIndex + 2, norm.z);
			
			vertIndex += 3;
			
			Point2f tex = textureCoords.get(i);
			texCoordBuf.put(txIndex, tex.x);
			texCoordBuf.put(txIndex + 1, tex.x);
			
			txIndex += 2;
		}
		
		verticesBuf.rewind();
		normalsBuf.rewind();
		texCoordBuf.rewind();
		indices.rewind();
		
		// TODO make this
		return new GLSLModel(buildCollisionShape(), verticesBuf, normalsBuf, texCoordBuf, indices);
	}
	
	/**
	 * Builds a collision shape for this model
	 * @return A convex hull collision shape representing this model
	 */
	private CollisionShape buildCollisionShape(){
		ConvexShape originalConvexShape = new ConvexHullShape(vertices);
		
		// create a hull based on the vertices
		ShapeHull hull = new ShapeHull(originalConvexShape);
		float margin = originalConvexShape.getMargin();
		hull.buildHull(margin);
		
		return new ConvexHullShape(hull.getVertexPointer());
	}

	/**
	 * Builds a call list for drawing the model.
	 * @return The call list to call to draw the model
	 */
	private int buildCallList() {
		int callList = GL11.glGenLists(1);

		GL11.glNewList(callList, GL11.GL_COMPILE_AND_EXECUTE);
		{
			for (int i = 0; i < vertexIndices.size(); i++) {
				int[] verts = vertexIndices.get(i);
				int[] norms = normalIndices.get(i);
				int[] texts = textureIndices.get(i);

				// triangle
				if (verts.length == 3 && norms.length == 3 && texts.length == 3) {
					GL11.glBegin(GL11.GL_TRIANGLES);
					{
						drawArrays(verts, norms, texts);
					}
					GL11.glEnd();
				}
				// quad
				else if (verts.length == 4 && norms.length == 4
						&& texts.length == 4) {
					GL11.glBegin(GL11.GL_QUADS);
					{
						drawArrays(verts, norms, texts);
					}
					GL11.glEnd();
				} else {
					System.out
							.println("Error! There's either not the right amount of indices for something, or there's not that same amount of geom, normal, and texture coordinates (ModelBuilder)");
				}
			}
		}
		GL11.glEndList();

		return callList;
	}

	/**
	 * Draws the arrays given.
	 * @param verts Vertices to draw
	 * @param norms Normals for the vertices
	 * @param texCoords Texture coordinates for the vertices
	 */
	private void drawArrays(int[] verts, int[] norms, int[] texCoords) {
		for (int i = 0; i < verts.length; i++) {
			Vector3f vertex = vertices.get(verts[i]);
			Vector3f normal = normals.get(norms[i]);
			Point2f coord = textureCoords.get(texCoords[i]);

			GL11.glTexCoord2f(coord.x, 1 - coord.y);
			GL11.glNormal3f(normal.x, normal.y, normal.z);
			GL11.glVertex3f(vertex.x, vertex.y, vertex.z);
		}
	}
}
