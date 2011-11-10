package graphics.model;

import java.util.ArrayList;

import javax.vecmath.Point2f;
import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

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
		 * FIXME can either do this, or offset each index by 1 at some point 
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
		if (indices.length == 3) {
			vertexIndices.add(indices);
		} else if (indices.length == 4) {
			// first triangle from the quad
			int[] tri1 = new int[3];
			tri1[0] = indices[0];
			tri1[1] = indices[1];
			tri1[2] = indices[2];
			vertexIndices.add(tri1);

			// second triangle from the quad
			int[] tri2 = new int[3];
			tri2[0] = indices[2];
			tri2[1] = indices[3];
			tri2[2] = indices[0];
			vertexIndices.add(tri2);
		} else {
			System.out.println("Vertex indices not a triangle or a quad!");
		}
	}

	public void addNormal(Vector3f vertex) {
		normals.add(vertex);
	}

	public void addNormalIndices(int[] indices) {
		if (indices.length == 3) {
			normalIndices.add(indices);
		} else if (indices.length == 4) {
			// first triangle from the quad
			int[] tri1 = new int[3];
			tri1[0] = indices[0];
			tri1[1] = indices[1];
			tri1[2] = indices[2];
			normalIndices.add(tri1);

			// second triangle from the quad
			int[] tri2 = new int[3];
			tri2[0] = indices[2];
			tri2[1] = indices[3];
			tri2[2] = indices[0];
			normalIndices.add(tri2);
		} else {
			System.out.println("Normal indices not a triangle or a quad!");
		}
	}
	
	public void addTextureCoords(Point2f point){
		textureCoords.add(point);
	}

	public void addTetxureIndices(int[] indices) {
		if (indices.length == 3) {
			textureIndices.add(indices);
		} else if (indices.length == 4) {
			// first triangle from the quad
			int[] tri1 = new int[3];
			tri1[0] = indices[0];
			tri1[1] = indices[1];
			tri1[2] = indices[2];
			textureIndices.add(tri1);

			// second triangle from the quad
			int[] tri2 = new int[3];
			tri2[0] = indices[2];
			tri2[1] = indices[3];
			tri2[2] = indices[0];
			textureIndices.add(tri2);
		} else {
			System.out.println("Texture indices not a triangle or a quad!");
		}
	}

	/**
	 * This method should be called after all the vertices, faces, and normals
	 * and their respective indices have been added.
	 */
	public Model makeModel(float scale) {
		checkForCompleteness();

		buildCallList();

		return new Model(callList);
	}

	private void buildCallList() {
		callList = GL11.glGenLists(1);

		GL11.glNewList(callList, GL11.GL_COMPILE);
		{
			GL11.glBegin(GL11.GL_TRIANGLES);
			{
				for (int i = 0; i < vertexIndices.size(); i++) {
					int[] verts = vertexIndices.get(i);
					Vector3f vertex1 = vertices.get(verts[0]);
					Vector3f vertex2 = vertices.get(verts[1]);
					Vector3f vertex3 = vertices.get(verts[2]);

					// only grab normal indices if they exist
					int[] norms = null;
					Vector3f normal1 = null;
					Vector3f normal2 = null;
					Vector3f normal3 = null;
					if (normalIndices.size() != 0) {
						norms = normalIndices.get(i);
						normal1 = normals.get(norms[0]);
						normal2 = normals.get(norms[1]);
						normal3 = normals.get(norms[2]);
					}

					// only grab texture indices if they exist
					int[] texts = null;
					Point2f texture1 = null;
					Point2f texture2 = null;
					Point2f texture3 = null;
					if (textureIndices.size() != 0) {
						texts = textureIndices.get(i);
						texture1 = textureCoords.get(texts[0]);
						texture2 = textureCoords.get(texts[1]);
						texture3 = textureCoords.get(texts[2]);
					}

					GL11.glVertex3f(vertex1.x, vertex1.y, vertex1.z);
					if (texture1 != null)
						GL11.glTexCoord2f(texture1.x, texture1.y);
					if (normal1 != null)
						GL11.glNormal3f(normal1.x, normal1.y, normal1.z);

					GL11.glVertex3f(vertex2.x, vertex2.y, vertex2.z);
					if (texture2 != null)
						GL11.glTexCoord2f(texture2.x, texture2.y);
					if (normal2 != null)
						GL11.glNormal3f(normal2.x, normal2.y, normal2.z);

					GL11.glVertex3f(vertex3.x, vertex3.y, vertex3.z);
					if (texture3 != null)
						GL11.glTexCoord2f(texture3.x, texture3.y);
					if (normal3 != null)
						GL11.glNormal3f(normal3.x, normal3.y, normal3.z);
				}
			}
			GL11.glEnd();
		}
		GL11.glEndList();
	}

	/**
	 * Makes sure that there are at least some vertices. If there are normals it
	 * makes sure that there's one for every vertex. Does the same for texture
	 * coordinates.
	 */
	private void checkForCompleteness() {
		// FIXME This should probably throw exceptions (maybe create a
		// ModelBuilderException?)
		if (vertices.size() == 0 || vertexIndices.size() == 0) {
			System.out
					.println("There aren't any vertices! You sure you want to make this model?");
		}

		if (normalIndices.size() != 0) {
			if (normalIndices.size() != vertexIndices.size())
				System.out
						.println("Different amount of normal and vertex indices!");
		}

		if (textureIndices.size() != 0) {
			if (textureIndices.size() != vertexIndices.size())
				System.out
						.println("Different amount of texture indices and vertex indices!");
		}
	}

	public int getCallList() {
		return callList;
	}

	public String getName() {
		return name;
	}
}
