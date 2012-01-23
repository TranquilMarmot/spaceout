package spaceguts.util;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

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
		multMatrixBuffer.rewind();
		
		Matrix4f multMatrix =new Matrix4f();
		multMatrix.load(multMatrixBuffer);
		
		Matrix4f.mul(result, multMatrix, result);
		
		System.out.println(result.toString());
		
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
		
		FloatBuffer values = BufferUtils.createFloatBuffer(16);
		values.put(0 * 4 + 0, side.x);
		values.put(1 * 4 + 0, side.y);
		values.put(2 * 4 + 0, side.z);

		values.put(0 * 4 + 1, up.x);
		values.put(1 * 4 + 1, up.y);
		values.put(2 * 4 + 1, up.z);

		values.put(0 * 4 + 2, -forward.x);
		values.put(1 * 4 + 2, -forward.y);
		values.put(2 * 4 + 2, -forward.z);
		
		values.rewind();
		Matrix4f mul = new Matrix4f();
		mul.load(values);
		
		Matrix4f.mul(ret, mul, ret);
		ret.translate(new Vector3f(-eye.x, -eye.y, -eye.z));
		
		return ret;
	}
}
