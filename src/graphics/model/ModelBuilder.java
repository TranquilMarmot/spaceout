package graphics.model;

import java.util.ArrayList;

import javax.vecmath.Point2f;
import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;


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

	/** for rendering the model */
	private int callList;

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
		vertexIndices.add(indices);
	}

	public void addNormal(Vector3f vertex) {
		normals.add(vertex);
	}

	public void addNormalIndices(int[] indices) {
		normalIndices.add(indices);
	}
	
	public void addTextureCoords(Point2f point){
		textureCoords.add(point);
	}

	public void addTetxureIndices(int[] indices) {
		textureIndices.add(indices);
	}

	/**
	 * This method should be called after all the vertices, faces, and normals
	 * and their respective indices have been added.
	 */
	public Model makeModel(float scale) {
		buildCallList();

		return new Model(callList);
	}

	private void buildCallList() {
		callList = GL11.glGenLists(1);

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

	public int getCallList() {
		return callList;
	}

	public String getName() {
		return name;
	}
}
