package spaceguts.graphics.model;

import java.util.ArrayList;

import spaceguts.graphics.render.Render3D;
import spaceguts.util.resources.Textures;

import com.bulletphysics.collision.shapes.CollisionShape;

/**
 * This class defines a 3D model. A model is a set of vertices to draw, what texture to use when drawing them,
 * and a collision shape to use with JBullet
 * @author TranquilMarmot
 *
 */
public class Model {
	//private int vaoHandle, numIndices;
	private CollisionShape collisionShape;
	private Textures texture;
	private ArrayList<ModelPart> parts;
	
	public Model(CollisionShape collisionShape, int vaoHandle, int numIndices, Textures texture){
		// these all come from the model loader
		//this.vaoHandle = vaoHandle;
		//this.numIndices = numIndices;
		this.collisionShape = collisionShape;
		this.texture = texture;
		//this.parts = parts;
	}
	
	public CollisionShape getCollisionShape(){
		return collisionShape;
	}
	
	public void render(){
		for(ModelPart p : parts){
			Material mat = p.getMaterial();
			Render3D.setCurrentMaterial(mat.getKd(), mat.getKa(), mat.getKs(), mat.getShininess());
			p.draw();
		}
		
		//GL30.glBindVertexArray(vaoHandle);
		//GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, numIndices);
	}

	public Textures getTexture() {
		return texture;
	}	
}
