package spaceguts.graphics.glsl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import spaceguts.util.model.ModelBuilder;

public class VBOTorus {
	private int vaoHandle;
	private int faces, rings, sides;
	
	private float[] verts, norms, tex;
	private int[] el;
	
	public VBOTorus(float outerRadius, float innerRadius, int sides, int rings){
		this.rings = rings;
		this.sides = sides;
		
		// generate vertices
		generateVerts(outerRadius, innerRadius);
		
		FloatBuffer v = BufferUtils.createFloatBuffer(verts.length);
		v.put(verts);
		v.rewind();
		
		FloatBuffer n = BufferUtils.createFloatBuffer(norms.length);
		n.put(norms);
		n.rewind();
		
		FloatBuffer t = BufferUtils.createFloatBuffer(tex.length);
		t.put(tex);
		t.rewind();
		
		IntBuffer ele = BufferUtils.createIntBuffer(el.length);
		ele.put(el);
		ele.rewind();
		
		vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		
		// create and populate buffer objects
		IntBuffer handle = BufferUtils.createIntBuffer(3);
		GL15.glGenBuffers(handle);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle.get(0));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, v, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(0); // vertex position
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle.get(1));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, n, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(1); // vertex normal
		
		//GL20.glEnableVertexAttribArray(2); // texture coords
		//GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle.get(2));
		//GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0L);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, handle.get(2));
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, ele, GL15.GL_STATIC_DRAW);
		
		GL30.glBindVertexArray(0);
	}
	
	public void render(){
		GL30.glBindVertexArray(vaoHandle);
		GL11.glDrawElements(GL11.GL_TRIANGLES, faces * 6, GL11.GL_UNSIGNED_INT, 0L);
	}
	
	private void generateVerts(float outerRadius, float innerRadius){
		faces = sides * rings;
		int nVerts = sides * (rings + 1); //one extra to duplicate first ring
		
		verts = new float[nVerts * 3];
		norms = new float[nVerts * 3];
		tex = new float[nVerts * 2];
		el = new int[faces * 6];
		
		float ringFactor = (float) (Math.PI * 2.0 / (double)rings);
		float sideFactor = (float) (Math.PI * 2.0 / (double)sides);
		
		// create vertices, normals and tex coords
		int idx = 0, tidx = 0;
		for(int ring = 0; ring <= rings; ring++){
			double u = (double)(ring * ringFactor);
			float cu = (float)(Math.cos(u));
			float su = (float)(Math.sin(u));
			
			for(int side = 0; side < sides; side++){
				double v = (double)(side * sideFactor);
				float cv = (float)(Math.cos(v));
				float sv = (float)(Math.sin(v));
				float r = (outerRadius + innerRadius * cv);
	            verts[idx] = r * cu;
	            verts[idx + 1] = r * su;
	            verts[idx + 2] = innerRadius * sv;
	            norms[idx] = cv * cu * r;
	            norms[idx + 1] = cv * su * r;
	            norms[idx + 2] = sv * r;
	            tex[tidx] = (float)(u / Math.PI * 2.0);
	            tex[tidx+1] = (float)(v / Math.PI * 2.0);
	            tidx += 2;
	            // Normalize
	            float len = (float)(Math.sqrt(
	            		 (double)(
	            				 norms[idx] * norms[idx] + 
	            				 norms[idx+1] * norms[idx+1] + 
	            				 norms[idx+2] * norms[idx+2] )
	            				 ));
	            norms[idx] /= len;
	            norms[idx+1] /= len;
	            norms[idx+2] /= len;
	            idx += 3;
			}
		}
		
		// create indices
		idx = 0;
		for(int ring = 0; ring < rings; ring++){
			int ringStart = ring * sides;
			int nextRingStart = (ring + 1) * sides;
			for(int side = 0; side < sides; side++){
	            int nextSide = (side+1) % sides;
	            // The quad
	            el[idx] = (ringStart + side);
	            el[idx+1] = (nextRingStart + side);
	            el[idx+2] = (nextRingStart + nextSide);
	            el[idx+3] = ringStart + side;
	            el[idx+4] = nextRingStart + nextSide;
	            el[idx+5] = (ringStart + nextSide);
	            idx += 6;
			}
		}
	}
}

