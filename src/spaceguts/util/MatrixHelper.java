package spaceguts.util;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class MatrixHelper {
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
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
		
		matrixBuffer.clear();

		matrixBuffer.put(0 * 4 + 0, cotangent / aspect);
		matrixBuffer.put(1 * 4 + 1, cotangent);
		matrixBuffer.put(2 * 4 + 2, - (zFar + zNear) / deltaZ);
		matrixBuffer.put(2 * 4 + 3, -1);
		matrixBuffer.put(3 * 4 + 2, -2 * zNear * zFar / deltaZ);
		matrixBuffer.put(3 * 4 + 3, 0);
		matrixBuffer.rewind();
		
		Matrix4f multMatrix =new Matrix4f();
		multMatrix.load(matrixBuffer);
		
		Matrix4f.mul(result, multMatrix, result);
		
		return result;
	}
	
	public static Matrix4f lookAt(Vector3f eye, Vector3f center, Vector3f up){
		Matrix4f ret = new Matrix4f();
		Vector3f forward = new Vector3f(center.x - eye.x, center.y - eye.y, center.z - eye.z);
		forward.normalise();
		
		Vector3f side = new Vector3f();
		Vector3f.cross(forward, up, side);
		side.normalise();
		
		Vector3f.cross(side, forward, up);
		
		matrixBuffer.clear();
		matrixBuffer.put(0 * 4 + 0, side.x);
		matrixBuffer.put(1 * 4 + 0, side.y);
		matrixBuffer.put(2 * 4 + 0, side.z);

		matrixBuffer.put(0 * 4 + 1, up.x);
		matrixBuffer.put(1 * 4 + 1, up.y);
		matrixBuffer.put(2 * 4 + 1, up.z);

		matrixBuffer.put(0 * 4 + 2, -forward.x);
		matrixBuffer.put(1 * 4 + 2, -forward.y);
		matrixBuffer.put(2 * 4 + 2, -forward.z);
		
		matrixBuffer.rewind();
		Matrix4f mul = new Matrix4f();
		mul.load(matrixBuffer);
		
		Matrix4f.mul(ret, mul, ret);
		ret.translate(new Vector3f(-eye.x, -eye.y, -eye.z));
		
		return ret;
	}
}
