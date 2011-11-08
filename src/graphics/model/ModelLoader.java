package graphics.model;

import graphics.model.geom.Face;
import graphics.model.geom.Quad;
import graphics.model.geom.Triangle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.lwjgl.opengl.GL11;


public class ModelLoader {
	/**
	 * Loads in vertices from an external file to an Model, which has a display
	 * list for the vertices
	 * 
	 * @param file
	 *            The file to load the Model from
	 * @return An ArrayList containing all the Models found in the file
	 */
	public static ArrayList<Integer> loadPlyFile(String file) {
		return loadPlyFile(file, 1.0f);
	}

	/**
	 * Loads in vertices from an external file to an Model, which has a display
	 * list for the vertices
	 * 
	 * @param file
	 *            The file to load the Model from
	 * @param scale
	 *            The scale to load the model in at
	 * @return An ArrayList containing all the Models found in the file
	 */
	public static ArrayList<Integer> loadPlyFile(String file, float scale) {
		ArrayList<Integer> models = new ArrayList<Integer>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			String line;

			int numVertices = 0;
			int numFaces = 0;

			// read until there's nothing left to read
			while ((line = reader.readLine()) != null || line != null) {
				if (line.startsWith("element")) {
					StringTokenizer toker = new StringTokenizer(line, " ");
					// skip 'element'
					toker.nextElement();

					// figure out whether we're getting number of vertices or
					// number of faces
					String type = toker.nextToken();
					if (type.equals("vertex")) {
						numVertices = Integer.parseInt(toker.nextToken());
					} else if (type.equals("face")) {
						numFaces = Integer.parseInt(toker.nextToken());
					}
				}

				// these ArrayLists hold all the vertices and normals (there is
				// a normal for every vertex)
				float[][] vertices = new float[numVertices][3];
				float[][] normals = new float[numVertices][3];

				// this array holds all the faces
				Face[] faces = new Face[numFaces];

				// skip the rest of the header
				if (!line.startsWith("end_header"))
					continue;

				// now, loop through all the vertices
				for (int i = 0; i < numVertices; i++) {
					line = reader.readLine();
					StringTokenizer toker = new StringTokenizer(line, " ");

					// get the x, y, and z for the vertex
					float x = Float.parseFloat(toker.nextToken());
					float y = Float.parseFloat(toker.nextToken());
					float z = Float.parseFloat(toker.nextToken());

					x *= scale;
					y *= scale;
					z *= scale;

					float[] vertex = { x, y, z };
					vertices[i] = vertex;

					// get the normal for the vertex
					float nx = Float.parseFloat(toker.nextToken());
					float ny = Float.parseFloat(toker.nextToken());
					float nz = Float.parseFloat(toker.nextToken());

					nx *= scale;
					ny *= scale;
					nz *= scale;

					float[] normal = { nx, ny, nz };
					normals[i] = normal;
				}

				// now, loop through all the faces
				for (int i = 0; i < numFaces; i++) {
					line = reader.readLine();

					StringTokenizer toker = new StringTokenizer(line, " ");

					// grab the number of vertices
					int numFaceVertices = Integer.parseInt(toker.nextToken());

					Face f = null;

					// create a triangle or a quad based on the number of
					// vertices
					if (numFaceVertices == 3)
						f = new Triangle();
					else if (numFaceVertices == 4)
						f = new Quad();
					else
						System.out
								.println("Error reading in number of face vertices!");

					// loop through all the face's vertices
					for (int j = 0; j < numFaceVertices; j++) {
						// grab which vertex we need
						int v = Integer.parseInt(toker.nextToken());

						// set the vertex and normal for the face
						f.vertices[j] = vertices[v];
						f.normals[j] = normals[v];
					}

					faces[i] = f;
				}

				// create the Model for all those faces; the display list is
				// created with the Model
				int list = GL11.glGenLists(1);

				// fill up the display list with all the instructions for
				// drawing the faces
				GL11.glNewList(list, GL11.GL_COMPILE);
				{
					for (Face f : faces) {
						f.draw();
					}
				}
				GL11.glEndList();

				models.add(list);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return models;
	}
	
	public static Model loadObjFile(String file, String name){
		//Model m = new Model();
		
		return null;
	}

	public static ArrayList<Integer> loadObjFileOld(String file) {
		// TODO this is TOTALLY incomplete, should be finished
		ArrayList<Integer> Models = new ArrayList<Integer>();

		ArrayList<float[]> vertices = new ArrayList<float[]>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			String line;

			// read until there's nothing left to read
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("o")) {
					int list = GL11.glGenLists(1);

					Models.add(list);

					// grab all the vertices for the current Model
					while ((line = reader.readLine()).startsWith("v")) {
						StringTokenizer toker = new StringTokenizer(line, " ");
						// skip the v
						toker.nextToken();

						float x = Float.parseFloat(toker.nextToken());
						float y = Float.parseFloat(toker.nextToken());
						float z = Float.parseFloat(toker.nextToken());

						float[] vertex = { x, y, z };
						vertices.add(vertex);
					}

					// now grab all the faces
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return Models;
	}
}
