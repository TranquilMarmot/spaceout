package spaceguts.graphics.model;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * Defines a section of a {@link Model} with a specific {@link Material}
 * @author TranquilMarmot
 * @see Model
 * @see Material
 * @see ModelBuilder
 * @see ModelLoader
 */
public class ModelPart {
	/** {@link Material} to use for this model part*/
	private Material material;
	
	/** How to draw this model part*/
	private int vaoHandle, numIndices;
	
	/**
	 * 
	 * @param material Material to use
	 * @param vaoHandle VAO Handle containing vertices
	 * @param numIndices Number of indices to draw with glDrawArrays
	 */
	public ModelPart(Material material, int vaoHandle, int numIndices){
		this.material = material;
		this.vaoHandle = vaoHandle;
		this.numIndices = numIndices;
	}
	
	/**
	 * @return Material to use
	 */
	public Material getMaterial(){
		return material;
	}
	
	/**
	 * Draw the model part
	 */
	public void draw(){
		GL30.glBindVertexArray(vaoHandle);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, numIndices);
	}
}
