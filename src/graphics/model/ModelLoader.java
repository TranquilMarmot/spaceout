package graphics.model;

import graphics.model.geom.Face;
import graphics.model.geom.Quad;
import graphics.model.geom.Triangle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

import com.sun.istack.internal.Builder;

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

	// TODO finish this!!!
	public static Model loadObjFile(String file, float scale) {
		Model m = null;
		/*
		 * while(vertex) addVertex
		 * 
		 * while(normal) addNormal
		 * 
		 * while(face) if(faceIsTriangle) addTriangle else if(faceIsQuad)
		 * addQuad
		 * 
		 * makeModel
		 */

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			String line;

			ModelBuilder builder = new ModelBuilder();

			String name;

			// go through the whooole file
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("o")) {
					name = line.substring(2);
				}

				if (line.startsWith("v") && !line.startsWith("vn")) {
					while (line.startsWith("v") && !line.startsWith("vn")) {
						StringTokenizer toker = new StringTokenizer(line);
						// skip the "v"
						toker.nextToken();

						// grab the coordinates
						float x = Float.parseFloat(toker.nextToken());
						float y = Float.parseFloat(toker.nextToken());
						float z = Float.parseFloat(toker.nextToken());

						builder.addVertex(new Vector3f(x, y, z));

						line = reader.readLine();
					}
				}

				if (line.startsWith("vn")) {
					while (line.startsWith("vn")) {
						StringTokenizer toker = new StringTokenizer(line);
						// skip the "vn"
						toker.nextToken();

						// grab the coordinates
						float x = Float.parseFloat(toker.nextToken());
						float y = Float.parseFloat(toker.nextToken());
						float z = Float.parseFloat(toker.nextToken());

						builder.addNormal(new Vector3f(x, y, z));

						line = reader.readLine();
					}
				}
				if (line.startsWith("f")) {
					while (line != null && line.startsWith("f")) {
						StringTokenizer toker = new StringTokenizer(line);
						// skip the "f"
						toker.nextToken();

						// to see if we're dealing with a triangle or a quad
						int numVertices = toker.countTokens();

						int[] vertexIndices = new int[numVertices];
						int[] normalIndices = new int[numVertices];

						for (int i = 0; i < numVertices; i++) {
							String indices = toker.nextToken();

							// split it at the "//", thats how the obj file
							// separates the normal from the vertex
							StringTokenizer splitter = new StringTokenizer(
									indices, "//");
							// grab the vertex and the normal indices
							int vertex = Integer.parseInt(splitter.nextToken());
							int normal = Integer.parseInt(splitter.nextToken());

							// add them to their respective int[]
							vertexIndices[i] = vertex;
							normalIndices[i] = normal;
						}

						// add the indices to the model builder
						builder.addVertexIndices(vertexIndices);
						builder.addNormalIndices(normalIndices);
						
						line = reader.readLine();
					}
				}
			}
			
			m = builder.makeModel(scale);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out
					.println("Oh no, NullPointer in ModelLoader! Maybe there wasn't an 'o' defined in the file?");
			e.printStackTrace();
		}
		
		return m;
	}

	// TODO finish this!!!
	public static Model loadObjFileOld(String file, float scale) {
		Model m = null;
		/*
		 * while(vertex) addVertex
		 * 
		 * while(normal) addNormal
		 * 
		 * while(face) if(faceIsTriangle) addTriangle else if(faceIsQuad)
		 * addQuad
		 * 
		 * makeModel
		 */

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			String line;

			// go through the whooole file
			while ((line = reader.readLine()) != null) {
				// go until we reach an o, which denotes an object
				if (line.startsWith("o")) {
					/*
					 * Grab the name of the object This is usually put in by
					 * Blender, and as such is normally "Cube" or "Sphere" or
					 * whatever mesh you satrted the object with. It can easily
					 * be changed in the obj file, next to the "o", although
					 * it's not really used for anything, I just threw it in
					 * there for completion.
					 */
					String name = line.substring(2);

					// the ModelBuilder we'll use to make our model
					ModelBuilder builder = new ModelBuilder();

					// grab all the lines that start with "v" (vertices)
					System.out.println("******getting vertices******");
					line = reader.readLine();
					while (line.startsWith("v") && !(line.startsWith("vn"))) {
						line = reader.readLine();
						if (line.startsWith("vn"))
							break;
						System.out.println(line);
						StringTokenizer toker = new StringTokenizer(line);
						// skip the "v"
						toker.nextToken();

						// grab the coordinates
						float x = Float.parseFloat(toker.nextToken());
						float y = Float.parseFloat(toker.nextToken());
						float z = Float.parseFloat(toker.nextToken());

						builder.addVertex(new Vector3f(x, y, z));
					}

					// grab all the lines that start with "vn" (normals)
					System.out.println("******getting normals******");
					line = reader.readLine();
					while (line.startsWith("vn")) {
						line = reader.readLine();
						if (!line.startsWith("vn"))
							break;
						System.out.println(line);
						StringTokenizer toker = new StringTokenizer(line);
						// skip the "vn"
						toker.nextToken();

						// grab the coordinates
						float x = Float.parseFloat(toker.nextToken());
						float y = Float.parseFloat(toker.nextToken());
						float z = Float.parseFloat(toker.nextToken());

						builder.addNormal(new Vector3f(x, y, z));
					}

					// skip extra lines
					while (!line.startsWith("f"))
						line = reader.readLine();

					// grab all the lines that start with "f" (faces)
					System.out.println("******getting faces******");
					while (line != null && line.startsWith("f")) {
						line = reader.readLine();
						if (line != null) {
							System.out.println(line);
							StringTokenizer toker = new StringTokenizer(line);
							// skip the "f"
							toker.nextToken();

							// to see if we're dealing with a triangle or a quad
							int numVertices = toker.countTokens();

							int[] vertexIndices = new int[numVertices];
							int[] normalIndices = new int[numVertices];

							for (int i = 0; i < numVertices; i++) {
								String indices = toker.nextToken();

								// split it at the "//", thats how the obj file
								// separates the normal from the vertex
								StringTokenizer splitter = new StringTokenizer(
										indices, "//");
								// grab the vertex and the normal indices
								int vertex = Integer.parseInt(splitter
										.nextToken());
								int normal = Integer.parseInt(splitter
										.nextToken());

								// add them to their respective int[]
								vertexIndices[i] = vertex;
								normalIndices[i] = normal;
							}

							// add the indices to the model builder
							builder.addVertexIndices(vertexIndices);
							builder.addNormalIndices(normalIndices);
						}
					}

					m = builder.makeModel(scale);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out
					.println("Oh no, NullPointer in ModelLoader! Maybe there wasn't an 'o' defined in the file?");
			e.printStackTrace();
		}

		return m;
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
