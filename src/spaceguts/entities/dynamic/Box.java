package spaceguts.entities.dynamic;

import spaceguts.graphics.model.Model;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import spaceguts.physics.CollisionTypes;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;

import spaceguts.util.manager.TextureManager;

public class Box extends DynamicEntity{
	final static short collisionGroup = CollisionTypes.WALL;
	final static short collidesWith = (short)(CollisionTypes.SHIP | CollisionTypes.PLANET);

	public Box(Vector3f location, Quaternion rotation, Vector3f size, float mass,
			float restitution) {
		super(location, rotation, makeModel(size), mass, restitution, collisionGroup, collidesWith);
		this.type = "Ground";
	}
	
	private static Model makeModel(Vector3f size){
		float xSize = size.x;
		float ySize = size.y;
		float zSize = size.z;
		
		CollisionShape groundShape = new BoxShape(
				new javax.vecmath.Vector3f(xSize, ySize, zSize));
		
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
		
		int groundTexture = TextureManager.CHECKERS;
		
		return new Model(groundShape, groundCallList, groundTexture);
	}

}
