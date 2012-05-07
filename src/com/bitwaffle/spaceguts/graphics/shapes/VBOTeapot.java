package com.bitwaffle.spaceguts.graphics.shapes;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class VBOTeapot {
	static int[][] patchdata = {
	    /* rim */
		  {102, 103, 104, 105, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
		    /* body */
		  {12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27},
		  {24, 25, 26, 27, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40},
		    /* lid */
		  {96, 96, 96, 96, 97, 98, 99, 100, 101, 101, 101, 101, 0, 1, 2, 3,},
		  {0, 1, 2, 3, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117},
		    /* bottom */
		  {118, 118, 118, 118, 124, 122, 119, 121, 123, 126, 125, 120, 40, 39, 38, 37},
		    /* handle */
		  {41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56},
		  {53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 28, 65, 66, 67},
		    /* spout */
		  {68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83},
		  {80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95}
	};
	
	static float[][] cpdata = {
		   {0.2f, 0.0f, 2.7f},
		    {0.2f, -0.112f, 2.7f},
		    {0.112f, -0.2f, 2.7f},
		    {0.0f, -0.2f, 2.7f},
		    {1.3375f, 0.0f, 2.53125f},
		    {1.3375f, -0.749f, 2.53125f},
		    {0.749f, -1.3375f, 2.53125f},
		    {0.0f, -1.3375f, 2.53125f},
		    {1.4375f, 0.0f, 2.53125f},
		    {1.4375f, -0.805f, 2.53125f},
		    {0.805f, -1.4375f, 2.53125f},
		    {0.0f, -1.4375f, 2.53125f},
		    {1.5f, 0.0f, 2.4f},
		    {1.5f, -0.84f, 2.4f},
		    {0.84f, -1.5f, 2.4f},
		    {0.0f, -1.5f, 2.4f},
		    {1.75f, 0.0f, 1.875f},
		    {1.75f, -0.98f, 1.875f},
		    {0.98f, -1.75f, 1.875f},
		    {0.0f, -1.75f, 1.875f},
		    {2.0f, 0.0f, 1.35f},
		    {2.0f, -1.12f, 1.35f},
		    {1.12f, -2.0f, 1.35f},
		    {0.0f, -2.0f, 1.35f},
		    {2.0f, 0.0f, 0.9f},
		    {2.0f, -1.12f, 0.9f},
		    {1.12f, -2.0f, 0.9f},
		    {0.0f, -2.0f, 0.9f},
		    {-2.0f, 0.0f, 0.9f},
		    {2.0f, 0.0f, 0.45f},
		    {2.0f, -1.12f, 0.45f},
		    {1.12f, -2.0f, 0.45f},
		    {0.0f, -2.0f, 0.45f},
		    {1.5f, 0.0f, 0.225f},
		    {1.5f, -0.84f, 0.225f},
		    {0.84f, -1.5f, 0.225f},
		    {0.0f, -1.5f, 0.225f},
		    {1.5f, 0.0f, 0.15f},
		    {1.5f, -0.84f, 0.15f},
		    {0.84f, -1.5f, 0.15f},
		    {0.0f, -1.5f, 0.15f},
		    {-1.6f, 0.0f, 2.025f},
		    {-1.6f, -0.3f, 2.025f},
		    {-1.5f, -0.3f, 2.25f},
		    {-1.5f, 0.0f, 2.25f},
		    {-2.3f, 0.0f, 2.025f},
		    {-2.3f, -0.3f, 2.025f},
		    {-2.5f, -0.3f, 2.25f},
		    {-2.5f, 0.0f, 2.25f},
		    {-2.7f, 0.0f, 2.025f},
		    {-2.7f, -0.3f, 2.025f},
		    {-3.0f, -0.3f, 2.25f},
		    {-3.0f, 0.0f, 2.25f},
		    {-2.7f, 0.0f, 1.8f},
		    {-2.7f, -0.3f, 1.8f},
		    {-3.0f, -0.3f, 1.8f},
		    {-3.0f, 0.0f, 1.8f},
		    {-2.7f, 0.0f, 1.575f},
		    {-2.7f, -0.3f, 1.575f},
		    {-3.0f, -0.3f, 1.35f},
		    {-3.0f, 0.0f, 1.35f},
		    {-2.5f, 0.0f, 1.125f},
		    {-2.5f, -0.3f, 1.125f},
		    {-2.65f, -0.3f, 0.9375f},
		    {-2.65f, 0.0f, 0.9375f},
		    {-2.0f, -0.3f, 0.9f},
		    {-1.9f, -0.3f, 0.6f},
		    {-1.9f, 0.0f, 0.6f},
		    {1.7f, 0.0f, 1.425f},
		    {1.7f, -0.66f, 1.425f},
		    {1.7f, -0.66f, 0.6f},
		    {1.7f, 0.0f, 0.6f},
		    {2.6f, 0.0f, 1.425f},
		    {2.6f, -0.66f, 1.425f},
		    {3.1f, -0.66f, 0.825f},
		    {3.1f, 0.0f, 0.825f},
		    {2.3f, 0.0f, 2.1f},
		    {2.3f, -0.25f, 2.1f},
		    {2.4f, -0.25f, 2.025f},
		    {2.4f, 0.0f, 2.025f},
		    {2.7f, 0.0f, 2.4f},
		    {2.7f, -0.25f, 2.4f},
		    {3.3f, -0.25f, 2.4f},
		    {3.3f, 0.0f, 2.4f},
		    {2.8f, 0.0f, 2.475f},
		    {2.8f, -0.25f, 2.475f},
		    {3.525f, -0.25f, 2.49375f},
		    {3.525f, 0.0f, 2.49375f},
		    {2.9f, 0.0f, 2.475f},
		    {2.9f, -0.15f, 2.475f},
		    {3.45f, -0.15f, 2.5125f},
		    {3.45f, 0.0f, 2.5125f},
		    {2.8f, 0.0f, 2.4f},
		    {2.8f, -0.15f, 2.4f},
		    {3.2f, -0.15f, 2.4f},
		    {3.2f, 0.0f, 2.4f},
		    {0.0f, 0.0f, 3.15f},
		    {0.8f, 0.0f, 3.15f},
		    {0.8f, -0.45f, 3.15f},
		    {0.45f, -0.8f, 3.15f},
		    {0.0f, -0.8f, 3.15f},
		    {0.0f, 0.0f, 2.85f},
		    {1.4f, 0.0f, 2.4f},
		    {1.4f, -0.784f, 2.4f},
		    {0.784f, -1.4f, 2.4f},
		    {0.0f, -1.4f, 2.4f},
		    {0.4f, 0.0f, 2.55f},
		    {0.4f, -0.224f, 2.55f},
		    {0.224f, -0.4f, 2.55f},
		    {0.0f, -0.4f, 2.55f},
		    {1.3f, 0.0f, 2.55f},
		    {1.3f, -0.728f, 2.55f},
		    {0.728f, -1.3f, 2.55f},
		    {0.0f, -1.3f, 2.55f},
		    {1.3f, 0.0f, 2.4f},
		    {1.3f, -0.728f, 2.4f},
		    {0.728f, -1.3f, 2.4f},
		    {0.0f, -1.3f, 2.4f},
		    {0.0f, 0.0f, 0.0f},
		    {1.425f, -0.798f, 0.0f},
		    {1.5f, 0.0f, 0.075f},
		    {1.425f, 0.0f, 0.0f},
		    {0.798f, -1.425f, 0.0f},
		    {0.0f, -1.5f, 0.075f},
		    {0.0f, -1.425f, 0.0f},
		    {1.5f, -0.84f, 0.075f},
		    {0.84f, -1.5f, 0.075f}
	};
	
	static float tex[][][] = {
		{ {0, 0}, {1, 0}},
		{ {0, 1}, {1, 1}}
	};
	
	int faces, vaoHandle;
	
	VBOTeapot(int grid, Matrix4f lidTransform){
		int verts = 32 * (grid + 1) * (grid + 1);
		faces = grid * grid * 32;
		float[] v = new float[verts * 3];
		float[] n = new float[verts * 3];
		float[] tc = new float[verts * 2];
		int[] el = new int[faces * 6];
		
		vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		
		IntBuffer handle = BufferUtils.createIntBuffer(4);
		GL15.glGenBuffers(handle);
		
		generatePatches(v, n, tc, el, grid);
		moveLid(grid,v, lidTransform);
		
		FloatBuffer vBuf = BufferUtils.createFloatBuffer(verts * 3);
		vBuf.put(v);
		vBuf.rewind();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle.get(0));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vBuf, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(0);
		
		FloatBuffer nBuf = BufferUtils.createFloatBuffer(verts * 3);
		nBuf.put(n);
		nBuf.rewind();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle.get(1));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, nBuf, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(1);
		
		FloatBuffer tcBuf = BufferUtils.createFloatBuffer(verts * 2);
		tcBuf.put(tc);
		tcBuf.rewind();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle.get(2));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tcBuf, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0L);
		GL20.glEnableVertexAttribArray(2);
		
		IntBuffer elBuf = BufferUtils.createIntBuffer(faces * 6);
		elBuf.put(el);
		elBuf.rewind();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, handle.get(3));
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elBuf, GL15.GL_STATIC_DRAW);
		
		GL30.glBindVertexArray(0);
	}
	
	
	void generatePatches(float[] v, float[] n, float[] tc, int[] el, int grid){
		float[] B = new float[4 * (grid + 1)];
		float[] dB = new float[4 * (grid + 1)];
		
		int idx = 0, elIndex = 0, tcIndex = 0;
		
		// pre-computer the basis funtions (bernstein polynomials)
		// and their derivatives
		computeBasisFunctions(B, dB, grid);
		
		// Build each patch
		// The rim
	    // Build each patch
	    // The rim
	    buildPatchReflect(0, B, dB, v, n, tc, el, idx, elIndex, tcIndex, grid, true, true);
	    // The body
	    buildPatchReflect(1, B, dB, v, n, tc, el, idx, elIndex, tcIndex, grid, true, true);
	    buildPatchReflect(2, B, dB, v, n, tc, el, idx, elIndex, tcIndex, grid, true, true);
	    // The lid
	    buildPatchReflect(3, B, dB, v, n, tc, el, idx, elIndex, tcIndex, grid, true, true);
	    buildPatchReflect(4, B, dB, v, n, tc, el, idx, elIndex, tcIndex, grid, true, true);
	    // The bottom
	    buildPatchReflect(5, B, dB, v, n, tc, el, idx, elIndex, tcIndex, grid, true, true);
	    // The handle
	    buildPatchReflect(6, B, dB, v, n, tc, el, idx, elIndex, tcIndex, grid, false, true);
	    buildPatchReflect(7, B, dB, v, n, tc, el, idx, elIndex, tcIndex, grid, false, true);
	    // The spout
	    buildPatchReflect(8, B, dB, v, n, tc, el, idx, elIndex, tcIndex, grid, false, true);
	    buildPatchReflect(9, B, dB, v, n, tc, el, idx, elIndex, tcIndex, grid, false, true);
	}
	
	void moveLid(int grid, float[] v, Matrix4f lidTransform){
	    int start = 3 * 12 * (grid+1) * (grid+1);
	    int end = 3 * 20 * (grid+1) * (grid+1);

	    for( int i = start; i < end; i+=3 )
	    {
	        Vector4f vert = new Vector4f(v[i], v[i+1], v[i+2], 1.0f );
	        vert.x = vert.x * lidTransform.m00;
	        vert.y = vert.y * lidTransform.m11;
	        vert.z = vert.z * lidTransform.m22;
	        vert.w = vert.w * lidTransform.m33;
	        v[i] = vert.x;
	        v[i+1] = vert.y;
	        v[i+2] = vert.z;
	    }
	}
	
	void buildPatchReflect(int patchNum, float[] B, float[] dB, float[] v, float[] n, float[] tc, int[] el, int index, int elIndex, int tcIndex, int grid, boolean reflectX, boolean reflectY){
		Vector3f[][] patch = new Vector3f[4][4];
		Vector3f[][] patchRevV = new Vector3f[4][4];
		
		getPatch(patchNum, patch, false);
		getPatch(patchNum, patchRevV, true);
		
		// patch without modification
		buildPatch(patch, B, dB, v, n, tc, el, index, elIndex, tcIndex, grid, new Matrix3f(), true);
		
		// patch reflected in x
		if(reflectX){
			Matrix3f xReflect = new Matrix3f();
			xReflect.m00 = -1.0f;
			xReflect.m11 = 1.0f;
			xReflect.m22 = 1.0f;
			
			buildPatch(patchRevV, B, dB, v, n, tc, el, index, elIndex, tcIndex, grid, xReflect, false);
		}
		
		// patch reflected in y
		if(reflectY){
			Matrix3f yReflect = new Matrix3f();
			yReflect.m00 = 1.0f;
			yReflect.m11 = -1.0f;
			yReflect.m22 = 1.0f;
			
			buildPatch(patchRevV, B, dB, v, n, tc, el, index, elIndex, tcIndex, grid, yReflect, false);
		}
		
		if(reflectX && reflectY){
			Matrix3f xyReflect = new Matrix3f();
			xyReflect.m00 = -1.0f;
			xyReflect.m11 = -1.0f;
			xyReflect.m22 = 1.0f;
			
			buildPatch(patchRevV, B, dB, v, n, tc, el, index, elIndex, tcIndex, grid, xyReflect, false);
		}
		
	}
	
	
	
	void buildPatch(Vector3f[][] patch, float[] B, float[] dB, float[] v, float[] n, float[] tc, int[] el, int index, int elIndex, int tcIndex, int grid, Matrix3f reflect, boolean invertNormal){
		int startIndex = index / 3;
		float tcFactor = 1.0f / grid;
		
		for(int i = 0; i <= grid; i++){
			for(int j = 0; j <= grid; j++){
				Vector3f eval = evaluate(i, j, B, patch);
				Vector3f pt = new Vector3f();
				pt.x = eval.x * reflect.m00;
				pt.y = eval.y * reflect.m11;
				pt.z = eval.z * reflect.m22;
				
				eval = evaluateNormal(i,j,B,dB,patch);
				Vector3f norm = new Vector3f();
				norm.x = eval.x * reflect.m00;
				norm.y = eval.y * reflect.m11;
				norm.z = eval.z * reflect.m22;
				if(invertNormal)
					norm.negate();
				
	            n[index] = norm.x;
	            n[index+1] = norm.y;
	            n[index+2] = norm.z;

	            tc[tcIndex] = i * tcFactor;
	            tc[tcIndex+1] = j * tcFactor;

	            index += 3;
	            tcIndex += 2;
			}
		}
		
		for(int i = 0; i < grid; i++){
			int iStart = i * (grid + 1) + startIndex;
			int nextiStart = (i + 1) * (grid + 1) + startIndex;
			for(int j = 0; j < grid; j++){
	            el[elIndex] = iStart + j;
	            el[elIndex+1] = nextiStart + j + 1;
	            el[elIndex+2] = nextiStart + j;

	            el[elIndex+3] = iStart + j;
	            el[elIndex+4] = iStart + j + 1;
	            el[elIndex+5] = nextiStart + j + 1;

	            elIndex += 6;
			}
		}
	}
	
	
	void getPatch(int patchNum, Vector3f[][] patch, boolean reverseV){
		for(int u = 0; u < 4; u++){
			for(int v = 0; v < 4; v++){
				if(reverseV){
					patch[u][v] = new Vector3f(
							cpdata[patchdata[patchNum] [u * 4 + (3 - v)]][0],
							cpdata[patchdata[patchNum] [u * 4 + (3 - v)]][1],
							cpdata[patchdata[patchNum] [u * 4 + (3 - v)]][2]
							);
				} else{
					patch[u][v] = new Vector3f(
							cpdata[patchdata[patchNum] [u * 4 + v]][0],
							cpdata[patchdata[patchNum] [u * 4 + v]][1],
							cpdata[patchdata[patchNum] [u * 4 + v]][2]
							);
				}
			}
		}
	}
	
	
	void computeBasisFunctions(float[] B, float[] dB, int grid){
		float inc = 1.0f / grid;
		for(int i = 0; i <= grid; i++){
			float t = i * inc;
			float tSqr = t * t;
			float oneMinusT = (1.0f - t);
			float oneMinusT2 = oneMinusT * oneMinusT;
			
	        B[i * 4 + 0] = oneMinusT * oneMinusT2;
	        B[i * 4 + 1] = 3.0f * oneMinusT2 * t;
	        B[i * 4 + 2] = 3.0f * oneMinusT * tSqr;
	        B[i * 4 + 3] = t * tSqr;
			
	        dB[i * 4 + 0] = -3.0f * oneMinusT2;
	        dB[i * 4 + 1] = -6.0f * t * oneMinusT + 3.0f * oneMinusT2;
	        dB[i * 4 + 2] = -3.0f * tSqr + 6.0f * t * oneMinusT;
	        dB[i * 4 + 3] = 3.0f * tSqr;
		}
	}
	
	
	Vector3f evaluate (int gridU, int gridV, float[] B, Vector3f[][] patch){
		Vector3f p = new Vector3f(0.0f, 0.0f, 0.0f);
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				float BUval = B[gridU * 4 + i];
				float BVval = B[gridV * 4 + j];
				Vector3f pat = patch[i][j];
				pat.x = pat.x * BUval * BVval;
				pat.y = pat.y * BUval * BVval;
				pat.z = pat.z * BUval * BVval;
				
				Vector3f.add(p, pat, p);
			}
		}
		
		return p;
	}
	
	
	
	Vector3f evaluateNormal(int gridU, int gridV, float[] B, float[] dB, Vector3f[][] patch){
		Vector3f du = new Vector3f(0.0f, 0.0f, 0.0f);
		Vector3f dv = new Vector3f(0.0f, 0.0f, 0.0f);
		
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				Vector3f pat = patch[i][j];
				
				Vector3f duAdd = new Vector3f(pat.x, pat.y, pat.z);
				float dBval = dB[gridU * 4 + i];
				float Bval = B[gridV * 4 + j];
				duAdd.x = duAdd.x * dBval * Bval;
				duAdd.y = duAdd.y * dBval * Bval;
				duAdd.z = duAdd.z * dBval * Bval;
				Vector3f.add(du, duAdd, du);
				
				Vector3f dvAdd = new Vector3f(pat.x, pat.y, pat.z);
				Bval = B[gridU * 4 + i];
				dBval = dB[gridV * 4 + j];
				dvAdd.x = dvAdd.x * Bval * dBval;
				dvAdd.y = dvAdd.y * Bval * dBval;
				dvAdd.z = dvAdd.z * Bval * dBval;
				Vector3f.add(dv, dvAdd, dv);
			}
		}
		
		Vector3f result = new Vector3f();
		Vector3f.cross(du, dv, result);
		result.normalise();
		
		return result;
	}
	
	void render(){
		GL30.glBindVertexArray(vaoHandle);
		GL11.glDrawElements(GL11.GL_TRIANGLES, 6 * faces, GL11.GL_UNSIGNED_INT, 0L);
	}
}
