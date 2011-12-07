package graphics.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.vecmath.Point2f;
import javax.vecmath.Vector3f;

/**
 * Handles loading models from external files.
 * @author TranquilMarmot
 *
 */
public class ModelLoader {
	public static Model loadObjFile(String file){
		return loadObjFile(file, new Vector3f(0.0f, 0.0f, 0.0f), 1.0f);
	}
	
	public static Model loadObjFile(String file, float scale){
		return loadObjFile(file, new Vector3f(0.0f, 0.0f, 0.0f), scale);
	}
	
	/**
	 * Loads a wavefront .obj file. See <a href="http://en.wikipedia.org/wiki/Wavefront_.obj_file">the wikipedia page on obj files</a> for more info.
	 * @param file File to load model from
	 * @param offset Offset for the model's center
	 * @param scale The scale to create the model at
	 * @return A model representing the given file
	 */
	public static Model loadObjFile(String file, Vector3f offset, float scale) {
		Model m = null;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			String line;

			ModelBuilder builder = new ModelBuilder();

			// go through the whooole file
			while ((line = reader.readLine()) != null) {
				// split the line up at spaces
				StringTokenizer toker = new StringTokenizer(line, " ");
				// grab the line's type
				String lineType = toker.nextToken();
				
				if (lineType.equals("o")) {
					builder.name = toker.nextToken().substring(2);
				}

				if (lineType.equals("v")) {
					// grab the coordinates
					float x = (Float.parseFloat(toker.nextToken()) + offset.x) * scale;
					float y = (Float.parseFloat(toker.nextToken()) + offset.y) * scale;
					float z = (Float.parseFloat(toker.nextToken()) + offset.z) * scale;

					builder.addVertex(new Vector3f(x, y, z));
				}

				if (lineType.equals("vn")) {
					// grab the coordinates
					float x = Float.parseFloat(toker.nextToken());
					float y = Float.parseFloat(toker.nextToken());
					float z = Float.parseFloat(toker.nextToken());

					builder.addNormal(new Vector3f(x, y, z));
				}

				if (line.startsWith("vt")) {
					float u = Float.parseFloat(toker.nextToken());
					float v = Float.parseFloat(toker.nextToken());

					builder.addTextureCoords(new Point2f(u, v));
				}

				if (line.startsWith("f")) {
					// to see if we're dealing with a triangle or a quad
					int numVertices = toker.countTokens();

					int[] vertexIndices = new int[numVertices];
					int[] normalIndices = new int[numVertices];
					int[] textureIndices = new int[numVertices];
					
					for(int i = 0; i < numVertices; i++){
						String indices = toker.nextToken();
						StringTokenizer split = new StringTokenizer(indices, "/");
						// the obj file goes vertex/texture-coordinate/normal
						vertexIndices[i] = Integer.parseInt(split.nextToken());
						textureIndices[i] = Integer.parseInt(split.nextToken());
						normalIndices[i] = Integer.parseInt(split.nextToken());
					}
					
					
					// add the indices to the model builder
					builder.addVertexIndices(vertexIndices);
					builder.addNormalIndices(normalIndices);
					builder.addTetxureIndices(textureIndices);
				}
			}

			m = builder.makeModel(scale);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out
					.println("Fuck fuck fuck fuck!");
			e.printStackTrace();
		}

		return m;
	}
}
