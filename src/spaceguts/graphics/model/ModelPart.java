package spaceguts.graphics.model;

import org.lwjgl.opengl.GL11;

/**
 * Defines a section of a {@link Model} with a specific {@link Material}
 * Each model part keeps track of its material, a starting index and the number of indices it has.
 * The starting index refers to a spot in a vertex array object, which is most likely managed
 * by the {@link Model} to which the part belongs.
 * 
 * @author TranquilMarmot
 * @see Model
 * @see Material
 * @see ModelBuilder
 * @see ModelLoader
 */
public class ModelPart {
	/** {@link Material} to use for the model part*/
	private Material material;
	
	/** How to draw the model part*/
	private int startIndex, count;
	
	/**
	 * ModelPart constructor
	 * @param material Material to use
	 * @param startIndex VAO Handle containing vertices
	 * @param count Number of indices to draw with glDrawArrays
	 */
	public ModelPart(Material material, int startIndex, int count){
		this.material = material;
		this.startIndex = startIndex;
		this.count = count;
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
		// draw them triangles
		GL11.glDrawArrays(GL11.GL_TRIANGLES, startIndex, count);
	}
}
