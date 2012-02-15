package spaceguts.graphics.model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.vecmath.Point2f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import spaceout.resources.Textures;

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
public class ModelBuilder {
	/** the vertices of the model */
	private ObjectArrayList<Vector3f> vertices;

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
	
	/** max and min values for the model being built */
	public float maxX, minX, maxY, minY, maxZ, minZ = 0.0f;
	
	/**
	 * See {@link ModelPart}
	 */
	private int currentIndex = 0, count = 0;
	/** material to use for current ModelPart*/
	private Material currentMaterial;
	
	/** all the model parts */
	private ArrayList<ModelPart> modelParts;
	
	/** Whether or not we're in the middle of making a ModelPart (endModelPart hasn't been called after beginModelPart) */
	private boolean makingModelPart = false;

	/**
	 * ModelBuilder initializer
	 */
	public ModelBuilder() {
		/*
		 * We have to add a blank element to the beginning of each list, as the obj
		 * file starts referencing elements at 1, but ArrayLists start at 0
		 */
		vertices = new ObjectArrayList<Vector3f>();
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
		
		// initialize model parts array
		modelParts = new ArrayList<ModelPart>();
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
		if (indices.length == 3){
			vertexIndices.add(indices);
			count += 3;
		}
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
			
			count += 6;
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
	public Model makeModel(Textures texture) {
		if(makingModelPart){
			endModelPart();
		}
		return new Model(buildCollisionShape(), fillVertexArray(), modelParts, texture);
	}
	
	/**
	 * Fills an array buffer with the given data
	 * @return The vertex array object handle
	 */
	private int fillVertexArray(){
		// get a handle for a VAO
		int vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		
		// create buffers and fill them with data
		FloatBuffer vertBuffer = BufferUtils.createFloatBuffer(vertexIndices.size() * 9);
		FloatBuffer normBuffer = BufferUtils.createFloatBuffer(normalIndices.size() * 9);
		FloatBuffer texBuffer = BufferUtils.createFloatBuffer(textureIndices.size() * 6);
		for(int i = 0; i < vertexIndices.size(); i++){
			int[] triVerts = vertexIndices.get(i); 
			int[] triNorms = normalIndices.get(i);
			int[] triTex = textureIndices.get(i);
			
			Vector3f firstVert = vertices.get(triVerts[0]);
			vertBuffer.put(firstVert.x);
			vertBuffer.put(firstVert.y);
			vertBuffer.put(firstVert.z);
			Vector3f firstNorm = normals.get(triNorms[0]);
			normBuffer.put(firstNorm.x);
			normBuffer.put(firstNorm.y);
			normBuffer.put(firstNorm.z);
			Point2f firstTex = textureCoords.get(triTex[0]);
			texBuffer.put(firstTex.x);
			texBuffer.put(1 - firstTex.y);
			
			Vector3f secondVert = vertices.get(triVerts[1]);
			vertBuffer.put(secondVert.x);
			vertBuffer.put(secondVert.y);
			vertBuffer.put(secondVert.z);
			Vector3f secondNorm = normals.get(triNorms[1]);
			normBuffer.put(secondNorm.x);
			normBuffer.put(secondNorm.y);
			normBuffer.put(secondNorm.z);
			Point2f secondTex = textureCoords.get(triTex[1]);
			texBuffer.put(secondTex.x);
			texBuffer.put(1 - secondTex.y);
			
			
			Vector3f thirdVert = vertices.get(triVerts[2]);
			vertBuffer.put(thirdVert.x);
			vertBuffer.put(thirdVert.y);
			vertBuffer.put(thirdVert.z);
			Vector3f thirdNorm = normals.get(triNorms[2]);
			normBuffer.put(thirdNorm.x);
			normBuffer.put(thirdNorm.y);
			normBuffer.put(thirdNorm.z);
			Point2f thirdTex = textureCoords.get(triTex[2]);
			texBuffer.put(thirdTex.x);
			texBuffer.put(1 - thirdTex.y);
		}
		// be kind, please rewind()!
		vertBuffer.rewind();
		normBuffer.rewind();
		texBuffer.rewind();
		
		// handles for filling buffer objects
		IntBuffer vboHandles = BufferUtils.createIntBuffer(3);
		GL15.glGenBuffers(vboHandles);
		
		// actually fill the buffers
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(0));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(1));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(1);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboHandles.get(2));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(2);
		
		GL30.glBindVertexArray(0);
		
		return vaoHandle;
	}
	
	/**
	 * This should be called whenever a new set of vertices with a different material needs to be created.
	 * This should be called after all vertices have been added and while vertex indices are being added
	 * @param mat Material to use for incoming vertex indices
	 */
	public void startModelPart(Material mat){
		// end the current model part if we're making one
		if(isMakingModelPart())
			endModelPart();
		
		// set the current material
		currentMaterial = mat;
		// let everyone know that we're now making a model part
		makingModelPart = true;
	}
	
	/**
	 * Ends the current model part
	 */
	public void endModelPart(){
		// add the model part
		modelParts.add(new ModelPart(currentMaterial, currentIndex, count));
		
		// advance the current index and set count to 0 for the next model part
		currentIndex += count;
		count = 0;
		
		// let everyone know that we're done making the current model part
		makingModelPart = false;
	}
	
	/**
	 * @return Whether or not a model part is being made right now
	 */
	public boolean isMakingModelPart(){
		return makingModelPart;
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
}
