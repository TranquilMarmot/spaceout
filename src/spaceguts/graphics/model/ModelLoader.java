package spaceguts.graphics.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.vecmath.Point2f;
import javax.vecmath.Vector3f;

import org.lwjgl.util.vector.Quaternion;

import spaceguts.util.QuaternionHelper;
import spaceout.resources.Textures;

/**
 * This class loads in an obj file using a {@link ModelBuilder} and returns a {@link Model}.
 * There's a ton of different ways to call loadObjFile, you only need to use one that gives you the
 * options you need.
 * 
 * The 'directory' String passed in to loadObjFile should point to a directory that contains
 * a .obj file and a .mtl file. It's assumed that the .obj and .mtl files have the same name
 * and that name matches the name of the directory.
 * 
 * So, for example, a directory look like:
 * 	model/
 * 		model.obj
 * 		model.mtl
 * 		model.png
 * 
 * @author TranquilMarmot
 * @see Model
 * @see ModelBuilder
 * @see ModelPart
 *
 */
public class ModelLoader {
	public static Model loadObjFile(String directory, Textures texture){
		return loadObjFile(directory, new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), texture);
	}
	
	public static Model loadObjFile(String directory, float scale, Textures texture){
		return loadObjFile(directory, new Vector3f(scale, scale, scale), new Vector3f(0.0f, 0.0f, 0.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), texture);
	}
	
	public static Model loadObjFile(String directory, Quaternion rotation, Textures texture){
		return loadObjFile(directory, new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.0f, 0.0f, 0.0f), rotation, texture);
	}
	
	public static Model loadObjFile(String directory, Vector3f scale, Textures texture){
		return loadObjFile(directory, scale, new Vector3f(0.0f, 0.0f, 0.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), texture);
	}
	
	public static Model loadObjFile(String directory, float scale, Quaternion rotation, Textures texture){
		return loadObjFile(directory, new Vector3f(scale, scale, scale), new Vector3f(0.0f, 0.0f, 0.0f), rotation, texture);
	}
	
	/**
	 * Get a model from an obj file
	 * @param directory Directory to load the model in from. The directory must contain a .obj and a .mtl file with the same name as the directory.
	 * @param scale Scale to load the model in at
	 * @param offset Location offset to give each vertex being loaded
	 * @param rotation Rotation offset to give each vertex being loaded
	 * @param texture The texture from {@link Textures} to use for the model
	 * @return A model loaded from the file
	 */
	public static Model loadObjFile(String directory, Vector3f scale, Vector3f offset, Quaternion rotation, Textures texture){
		// our model
		Model model = null;
		
		// find out the name of the directory
		int lastSlash = 0;
		char[] chars = directory.toCharArray();
		for(int i = chars.length - 1; i >= 0; i--){
			if(chars[i] == '/'){
				lastSlash = i;
				break;
			}
		}
		
		// get the name of the directory
		String name = directory.substring(lastSlash);
		
		// get a list of materials
		MaterialList materials = loadMaterialList(directory + name + ".mtl");
		
		try{
			// open the .obj file
			BufferedReader reader = new BufferedReader(new FileReader(directory + name + ".obj"));
			
			// current line
			String line;
			
			// our model builder
			ModelBuilder builder = new ModelBuilder();
			
			while((line = reader.readLine()) != null){
				// split the line up at spaces
				StringTokenizer toker = new StringTokenizer(line, " ");
				// grab the line's type
				String lineType = toker.nextToken();
				
				// object name
				if (lineType.equals("o")) {
					//System.out.println("Loading " + toker.nextToken());
				}

				// vertex
				if (lineType.equals("v")) {
					// grab the coordinates
					float x = (Float.parseFloat(toker.nextToken()) + offset.x) * scale.x;
					float y = (Float.parseFloat(toker.nextToken()) + offset.y) * scale.y;
					float z = (Float.parseFloat(toker.nextToken()) + offset.z) * scale.z;
					
					//org.lwjgl.util.vector.Vector3f rotated = QuaternionHelper.rotateVectorByQuaternion(new org.lwjgl.util.vector.Vector3f(x, y, z), rotation);

					//builder.addVertex(new Vector3f(rotated.x, rotated.y, rotated.z));
					builder.addVertex(new Vector3f(x, y, z));
				}

				// normal
				if (lineType.equals("vn")) {
					// grab the coordinates
					float x = Float.parseFloat(toker.nextToken());
					float y = Float.parseFloat(toker.nextToken());
					float z = Float.parseFloat(toker.nextToken());
					
					org.lwjgl.util.vector.Vector3f rotated = QuaternionHelper.rotateVectorByQuaternion(new org.lwjgl.util.vector.Vector3f(x, y, z), rotation);

					builder.addNormal(new Vector3f(rotated.x, rotated.y, rotated.z));
				}

				// texture coord
				if (line.startsWith("vt")) {
					float u = Float.parseFloat(toker.nextToken());
					float v = Float.parseFloat(toker.nextToken());

					builder.addTextureCoords(new Point2f(u, v));
				}
				
				// new material
				if(line.startsWith("usemtl")){
					// end the current material if we're on one
					//if(builder.isMakingModelPart())
					//	builder.endModelPart();
					
					String mat = toker.nextToken();
					builder.startModelPart(materials.getMaterial(mat));
				}

				// face
				if (line.startsWith("f")) {
					// to see if we're dealing with a triangle or a quad (the ModelBuilder automaticall splits quads into triangles)
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
			
			model = builder.makeModel(texture);
			
		} catch(IOException e){
			e.printStackTrace();
		}
		
		return model;
	}
	
	/**
	 * Get a material list for an obj file
	 * @param file .obj file to load .mtl file for (string should contain ".obj" at the end)
	 * @return List of materials from .mtl file
	 */
	private static MaterialList loadMaterialList(String file){
		// material list
		MaterialList list = new MaterialList();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			// current line
			String line;
			
			// name of the material (important!)
			String name = "NULL";
			// vectors for ambient, diffuse, specular
			org.lwjgl.util.vector.Vector3f Ka = null, Kd = null, Ks = null;
			// shininess
			float Shininess = -1.0f;
			
			// whether or not we're loading a material right now
			boolean loadingMaterial = false;
			// whether or not a material has been loaded (ready to be added to list)
			boolean materialLoaded = false;
			
			// go through the whole .mtl file
			while((line = reader.readLine()) != null){
				// new material
				if(line.startsWith("newmtl")){
					name = line.substring(line.indexOf("newmtl") + 7, line.length());
					Ka = null;
					Kd = null;
					Ks = null;
					Shininess = -1.0f;
					
					loadingMaterial = true;
				}
				
				else if(loadingMaterial){
					// grab variable
					if(line.startsWith("Ns")){
						StringTokenizer toker = new StringTokenizer(line, " ");
						toker.nextToken();
						Shininess = Float.parseFloat(toker.nextToken());
					} else if(line.startsWith("Ka")){
						Ka = getColor(line);
					} else if(line.startsWith("Kd")){
						Kd = getColor(line);
					} else if(line.startsWith("Ks")){
						Ks = getColor(line);
					}
					
					// if we have all the necessary variables, we're done loading this material and it can be added to the list
					if(Shininess != -1.0f && Ka != null && Ks != null && Kd != null){
						materialLoaded = true;
					}
				}
				
				// add material to list if it's loaded
				if(materialLoaded){
					Material mat = new Material(Ka, Kd, Ks, Shininess);
					list.addMaterial(name, mat);
				}
			}
		} catch(IOException e){
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * Get a vector representing a color from a string
	 * @param line Line to get color from
	 * @return Color from string
	 */
	private static org.lwjgl.util.vector.Vector3f getColor(String line){
		StringTokenizer toker = new StringTokenizer(line, " " );
		toker.nextToken();
		float x = Float.parseFloat(toker.nextToken());
		float y = Float.parseFloat(toker.nextToken());
		float z = Float.parseFloat(toker.nextToken());
		return new org.lwjgl.util.vector.Vector3f(x, y, z);
	}
}
