package spaceguts.graphics.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.vecmath.Point2f;
import javax.vecmath.Vector3f;

import org.lwjgl.util.vector.Quaternion;

import spaceguts.util.QuaternionHelper;
import spaceguts.util.resources.Textures;

public class ModelLoader {
	public static Model loadObjFile(String file, Textures texture){
		return loadObjFile(file, new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), texture);
	}
	
	public static Model loadObjFile(String file, float scale, Textures texture){
		return loadObjFile(file, new Vector3f(scale, scale, scale), new Vector3f(0.0f, 0.0f, 0.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), texture);
	}
	
	public static Model loadObjFile(String file, Quaternion rotation, Textures texture){
		return loadObjFile(file, new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.0f, 0.0f, 0.0f), rotation, texture);
	}
	
	public static Model loadObjFile(String file, Vector3f scale, Textures texture){
		return loadObjFile(file, scale, new Vector3f(0.0f, 0.0f, 0.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), texture);
	}
	
	public static Model loadObjFile(String file, float scale, Quaternion rotation, Textures texture){
		return loadObjFile(file, new Vector3f(scale, scale, scale), new Vector3f(0.0f, 0.0f, 0.0f), rotation, texture);
	}
	
	public static Model loadObjFileOld(String file, Vector3f scale, Vector3f offset, Quaternion rotation, Textures texture){
		Model model = null;
		loadMaterialList(file);
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String line;
			
			ModelBuilder builder = new ModelBuilder();
			
			while((line = reader.readLine()) != null){
				// split the line up at spaces
				StringTokenizer toker = new StringTokenizer(line, " ");
				// grab the line's type
				String lineType = toker.nextToken();
				
				if (lineType.equals("o")) {
					//System.out.println("Loading " + toker.nextToken().substring(2));
				}

				if (lineType.equals("v")) {
					// grab the coordinates
					float x = (Float.parseFloat(toker.nextToken()) + offset.x) * scale.x;
					float y = (Float.parseFloat(toker.nextToken()) + offset.y) * scale.y;
					float z = (Float.parseFloat(toker.nextToken()) + offset.z) * scale.z;
					
					//org.lwjgl.util.vector.Vector3f rotated = QuaternionHelper.rotateVectorByQuaternion(new org.lwjgl.util.vector.Vector3f(x, y, z), rotation);

					//builder.addVertex(new Vector3f(rotated.x, rotated.y, rotated.z));
					builder.addVertex(new Vector3f(x, y, z));
				}

				if (lineType.equals("vn")) {
					// grab the coordinates
					float x = Float.parseFloat(toker.nextToken());
					float y = Float.parseFloat(toker.nextToken());
					float z = Float.parseFloat(toker.nextToken());
					
					org.lwjgl.util.vector.Vector3f rotated = QuaternionHelper.rotateVectorByQuaternion(new org.lwjgl.util.vector.Vector3f(x, y, z), rotation);

					builder.addNormal(new Vector3f(rotated.x, rotated.y, rotated.z));
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
			
			model = builder.makeModel(texture);
			
		} catch(IOException e){
			e.printStackTrace();
		}
		
		return model;
	}
	
	
	public static Model loadObjFile(String file, Vector3f scale, Vector3f offset, Quaternion rotation, Textures texture){
		Model model = null;
		MaterialList materials = loadMaterialList(file);
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String line;
			
			ModelBuilder builder = new ModelBuilder();
			
			while((line = reader.readLine()) != null){
				// split the line up at spaces
				StringTokenizer toker = new StringTokenizer(line, " ");
				// grab the line's type
				String lineType = toker.nextToken();
				
				if (lineType.equals("o")) {
					//System.out.println("Loading " + toker.nextToken().substring(2));
				}

				if (lineType.equals("v")) {
					// grab the coordinates
					float x = (Float.parseFloat(toker.nextToken()) + offset.x) * scale.x;
					float y = (Float.parseFloat(toker.nextToken()) + offset.y) * scale.y;
					float z = (Float.parseFloat(toker.nextToken()) + offset.z) * scale.z;
					
					//org.lwjgl.util.vector.Vector3f rotated = QuaternionHelper.rotateVectorByQuaternion(new org.lwjgl.util.vector.Vector3f(x, y, z), rotation);

					//builder.addVertex(new Vector3f(rotated.x, rotated.y, rotated.z));
					builder.addVertex(new Vector3f(x, y, z));
				}

				if (lineType.equals("vn")) {
					// grab the coordinates
					float x = Float.parseFloat(toker.nextToken());
					float y = Float.parseFloat(toker.nextToken());
					float z = Float.parseFloat(toker.nextToken());
					
					org.lwjgl.util.vector.Vector3f rotated = QuaternionHelper.rotateVectorByQuaternion(new org.lwjgl.util.vector.Vector3f(x, y, z), rotation);

					builder.addNormal(new Vector3f(rotated.x, rotated.y, rotated.z));
				}

				if (line.startsWith("vt")) {
					float u = Float.parseFloat(toker.nextToken());
					float v = Float.parseFloat(toker.nextToken());

					builder.addTextureCoords(new Point2f(u, v));
				}
				
				if(line.startsWith("usemtl")){
					if(builder.isMakingModelPart())
						builder.endMaterial();
					
					String mat = toker.nextToken();
					builder.startMaterial(materials.getMaterial(mat));
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
			
			model = builder.makeModel(texture);
			
		} catch(IOException e){
			e.printStackTrace();
		}
		
		return model;
	}
	
	private static MaterialList loadMaterialList(String file){
		StringTokenizer toke = new StringTokenizer(file, ".");
		file = toke.nextToken() + ".mtl";
		
		
		MaterialList list = new MaterialList();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String line;
			String name = "NULL";
			org.lwjgl.util.vector.Vector3f Ka = null, Kd = null, Ks = null;
			float Shininess = -1.0f;
			
			boolean loadingMaterial = false;
			boolean materialLoaded = false;
			
			
			while((line = reader.readLine()) != null){
				if(line.startsWith("newmtl")){
					name = line.substring(line.indexOf("newmtl") + 7, line.length());
					Ka = null;
					Kd = null;
					Ks = null;
					Shininess = -1.0f;
					
					loadingMaterial = true;
				}
				
				else if(loadingMaterial){
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
					
					if(Shininess != -1.0f && Ka != null && Ks != null && Kd != null){
						materialLoaded = true;
					}
				}
				
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
	
	private static org.lwjgl.util.vector.Vector3f getColor(String line){
		StringTokenizer toker = new StringTokenizer(line, " " );
		toker.nextToken();
		float x = Float.parseFloat(toker.nextToken());
		float y = Float.parseFloat(toker.nextToken());
		float z = Float.parseFloat(toker.nextToken());
		return new org.lwjgl.util.vector.Vector3f(x, y, z);
	}
	
	private Model loadModel(MaterialList materials){
		return null;
	}
}
