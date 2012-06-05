package com.bitwaffle.spaceguts.util;

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
		
		result.m00 = cotangent / aspect;
		result.m01 = 0.0f;
		result.m02 = 0.0f;
		result.m03 = 0.0f;
		
		result.m10 = 0.0f;
		result.m11 = cotangent;
		result.m12 = 0.0f;
		result.m13 = 0.0f;
		
		result.m20 = 0.0f;
		result.m21 = 0.0f;
		result.m22 = -(zFar + zNear) / deltaZ;
		result.m23 = -1.0f;
		
		result.m30 = 0.0f;
		result.m31 = 0.0f;
		result.m32 = -2.0f * zNear * zFar / deltaZ;
		result.m33 = 0.0f;
		
		return result;
	}
	
	public static Matrix4f lookAt(Vector3f eye, Vector3f center, Vector3f up){
		Matrix4f result = new Matrix4f();
		Vector3f forward = new Vector3f(center.x - eye.x, center.y - eye.y, center.z - eye.z);
		forward.normalise();
		
		Vector3f side = new Vector3f();
		Vector3f.cross(forward, up, side);
		side.normalise();
		
		Vector3f.cross(side, forward, up);
		
		result.m00 = side.x;
		result.m01 = up.x;
		result.m02 = -forward.x;
		result.m03 = 0.0f;
		
		result.m10 = side.y;
		result.m11 = up.y;
		result.m12 = -forward.y;
		result.m13 = 0.0f;
		
		result.m20 = side.z;
		result.m21 = up.z;
		result.m22 = -forward.z;
		result.m23 = 0.0f;
		
		result.m30 = 0.0f;
		result.m31 = 0.0f;
		result.m32 = 0.0f;
		result.m33 = 0.0f;
		
		result.translate(new Vector3f(-eye.x, -eye.y, -eye.z));
		
		return result;
	}
}
