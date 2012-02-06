package spaceout.entities.dynamic;

import javax.vecmath.Vector3f;

import org.lwjgl.util.vector.Quaternion;

import spaceguts.entities.DynamicEntity;
import spaceguts.graphics.model.Model;
import spaceguts.graphics.model.ModelBuilder;
import spaceguts.physics.CollisionTypes;
import spaceguts.util.resources.Textures;

public class Box extends DynamicEntity{
	final static short COL_GROUP = CollisionTypes.WALL;
	final static short COL_WITH = (short)(CollisionTypes.SHIP | CollisionTypes.PLANET);

	public Box(org.lwjgl.util.vector.Vector3f location, Quaternion rotation, Vector3f size, float mass,
			float restitution) {
		super(location, rotation, makeModel(size), mass, restitution, COL_GROUP, COL_WITH);
		this.type = "Ground";
	}
	
	private static Model makeModel(Vector3f size){
		float xSize = size.x;
		float ySize = size.y;
		float zSize = size.z;
		
		//CollisionShape groundShape = new BoxShape(
		//		new javax.vecmath.Vector3f(xSize, ySize, zSize));
		
		ModelBuilder builder = new ModelBuilder();
		
		float[] vertices = {
				xSize, ySize, zSize,  -xSize, ySize, zSize,  -xSize, -ySize, zSize,  xSize, -ySize, zSize,
				xSize, ySize, zSize,  xSize, -ySize, zSize,  xSize, -ySize, -zSize,  xSize, ySize, -zSize,
				xSize, ySize, zSize,  xSize, ySize, -zSize,  -xSize, ySize, zSize,  -xSize, ySize, zSize,
				-xSize, ySize, zSize,  -xSize, ySize, -zSize,  -xSize, -ySize, -zSize,  -xSize, -ySize, zSize,
				-xSize, -ySize, -zSize,  xSize, -ySize, -zSize,  xSize, -ySize, zSize,  -xSize, -ySize, zSize,
				xSize, -ySize, -zSize,  -xSize, -ySize, -zSize,  -xSize, ySize, -zSize,  xSize, ySize, -zSize
		};
		
		// normal array
		float[] normals =   {0,0,1,  0,0,1,  0,0,1,  0,0,1,             // v0-v1-v2-v3
		                     1,0,0,  1,0,0,  1,0,0, 1,0,0,              // v0-v3-v4-v5
		                     0,1,0,  0,1,0,  0,1,0, 0,1,0,              // v0-v5-v6-v1
		                     -1,0,0,  -1,0,0, -1,0,0,  -1,0,0,          // v1-v6-v7-v2
		                     0,-1,0,  0,-1,0,  0,-1,0,  0,-1,0,         // v7-v4-v3-v2
		                     0,0,-1,  0,0,-1,  0,0,-1,  0,0,-1};        // v4-v7-v6-v5
				
		int[] indices = {
				0,1,2,  2,3,0,
				1,2,3,  2,3,0,
				4,5,6,  6,7,4,
				8,9,10, 10,11,8,
				12,13,14, 14,15,12,
				16,17,18, 18,19,16,
				20,21,22, 22,23,20
		};
		
		for(int i = 0; i < vertices.length; i += 3){
			Vector3f vec = new Vector3f(vertices[i], vertices[i + 1], vertices[i + 2]);
			builder.addVertex(vec);
		}
		
		for(int i = 0; i < normals.length; i++){
			Vector3f norm = new Vector3f(normals[i], normals[i + 1], normals[i + 2]);
			builder.addNormal(norm);
		}
		
		for(int i = 0; i < indices.length; i++){
			int[] ind = { indices[i], indices[i + 1], indices[i + 2] };
			builder.addVertexIndices(ind);
			builder.addNormalIndices(ind);
		}
		
		/*
		int groundCallList = GL11.glGenLists(1);
		GL11.glNewList(groundCallList, GL11.GL_COMPILE);
		{
			GL11.glBegin(GL11.GL_QUADS);
			{
			    // Bottom Face
			    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-xSize, -ySize, -zSize);  // Top Right Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( xSize, -ySize, -zSize);  // Top Left Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( xSize, -ySize,  zSize);  // Bottom Left Of The Texture and Quad
			    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-xSize, -ySize,  zSize);  // Bottom Right Of The Texture and Quad
			    // Front Face
			    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-xSize, -ySize,  zSize);  // Bottom Left Of The Texture and Quad
			    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f( xSize, -ySize,  zSize);  // Bottom Right Of The Texture and Quad
			    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f( xSize,  ySize,  zSize);  // Top Right Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-xSize,  ySize,  zSize);  // Top Left Of The Texture and Quad
			    // Back Face
			    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-xSize, -ySize, -zSize);  // Bottom Right Of The Texture and Quad
			    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-xSize,  ySize, -zSize);  // Top Right Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( xSize,  ySize, -zSize);  // Top Left Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( xSize, -ySize, -zSize);  // Bottom Left Of The Texture and Quad
			    // Right face
			    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f( xSize, -ySize, -zSize);  // Bottom Right Of The Texture and Quad
			    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f( xSize,  ySize, -zSize);  // Top Right Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( xSize,  ySize,  zSize);  // Top Left Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( xSize, -ySize,  zSize);  // Bottom Left Of The Texture and Quad
			    // Left Face
			    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-xSize, -ySize, -zSize);  // Bottom Left Of The Texture and Quad
			    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-xSize, -ySize,  zSize);  // Bottom Right Of The Texture and Quad
			    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-xSize,  ySize,  zSize);  // Top Right Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-xSize,  ySize, -zSize);  // Top Left Of The Texture and Quad
			    // Top Face
			    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-xSize, ySize, -zSize);  // Top Right Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( xSize, ySize, -zSize);  // Top Left Of The Texture and Quad
			    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( xSize, ySize,  zSize);  // Bottom Left Of The Texture and Quad
			    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-xSize, ySize,  zSize);  // Bottom Right Of The Texture and Quad
			}
			GL11.glEnd();
		}
		GL11.glEndList();
		*/
		
		Textures groundTexture = Textures.CHECKERS;
		
		
		return builder.makeModel(groundTexture);
	}

}
