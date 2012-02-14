package spaceguts.graphics.model;

import java.util.ArrayList;

import org.lwjgl.opengl.GL30;

import spaceguts.graphics.render.Render3D;
import spaceout.resources.Textures;

import com.bulletphysics.collision.shapes.CollisionShape;

/**
 * This class defines a 3D model. A model is a set of vertices to draw, what texture to use when drawing them,
 * and a collision shape to use with JBullet
 * @author TranquilMarmot
 *
 */
public class Model {
	/** Handle corresponding to the model's vertex array */
	private int vaoHandle;
	/** Collision shape used for physics sim */
	private CollisionShape collisionShape;
	/** Texture to use for rendering */
	private Textures texture;
	/** All the parts of the model (each has a different material) */
	private ArrayList<ModelPart> parts;
	
	/**
	 * Create a model
	 * @param collisionShape Collision shape to use for the model
	 * @param vaoHandle VAO Handle for drawing model
	 * @param parts Parts of the model
	 * @param texture Texture that the model uses
	 */
	public Model(CollisionShape collisionShape, int vaoHandle, ArrayList<ModelPart> parts, Textures texture){
		// these all come from the model loader
		this.vaoHandle = vaoHandle;
		this.collisionShape = collisionShape;
		this.texture = texture;
		this.parts = parts;
	}
	
	/**
	 * @return Collision shape for this model
	 */
	public CollisionShape getCollisionShape(){
		return collisionShape;
	}
	
	/**
	 * Renders the model.
	 * To render the model, we bind its vertex array and then draw all of its parts,
	 * after setting the right material for rendering
	 */
	public void render(){
		GL30.glBindVertexArray(vaoHandle);
		for(ModelPart p : parts){
			Material mat = p.getMaterial();
			Render3D.setCurrentMaterial(mat.getKd(), mat.getKa(), mat.getKs(), mat.getShininess());
			p.draw();
		}
	}

	/**
	 * @return Texture to use to render model
	 */
	public Textures getTexture() {
		return texture;
	}	
}
