package spaceguts.util;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

public class MatrixHelper {
	public static Matrix4f perspective(float fovy, float aspect, float zNear, float zFar){
		Matrix4f result = new Matrix4f();
		result.setIdentity();
		
		float sine, cotangent, deltaZ;
		float radians = fovy / 2 * (float)Math.PI / 180;

		deltaZ = zFar - zNear;
		sine = (float) Math.sin(radians);

		if ((deltaZ == 0) || (sine == 0) || (aspect == 0)) {
			return result;
		}

		cotangent = (float) Math.cos(radians) / sine;
		
		FloatBuffer multMatrixBuffer = BufferUtils.createFloatBuffer(16);

		multMatrixBuffer.put(0 * 4 + 0, cotangent / aspect);
		multMatrixBuffer.put(1 * 4 + 1, cotangent);
		multMatrixBuffer.put(2 * 4 + 2, - (zFar + zNear) / deltaZ);
		multMatrixBuffer.put(2 * 4 + 3, -1);
		multMatrixBuffer.put(3 * 4 + 2, -2 * zNear * zFar / deltaZ);
		multMatrixBuffer.put(3 * 4 + 3, 0);
		
		Matrix4f multMatrix =new Matrix4f();
		multMatrix.load(multMatrixBuffer);
		
		Matrix4f.mul(result, multMatrix, result);
		
		return result;
	}
}
