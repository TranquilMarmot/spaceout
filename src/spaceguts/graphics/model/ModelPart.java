package spaceguts.graphics.model;

import org.lwjgl.opengl.GL11;

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
	private int startIndex, endIndex;
	
	/**
	 * 
	 * @param material Material to use
	 * @param startIndex VAO Handle containing vertices
	 * @param endIndex Number of indices to draw with glDrawArrays
	 */
	public ModelPart(Material material, int startIndex, int endIndex){
		this.material = material;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
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
		GL11.glDrawArrays(GL11.GL_TRIANGLES, startIndex, endIndex);
	}
}
