package com.bitwaffle.spaceguts.util;

import java.nio.FloatBuffer;

import javax.vecmath.Quat4f;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class QuaternionHelper {
	final static float PIOVER180 = ((float) Math.PI) / 180.0f;

	/**
	 * Converts angles to a quaternion
	 * @param pitch X axis rotation
	 * @param yaw Y axis rotation
	 * @param roll Z axis rotation
	 * @return Quaternion representing angles
	 */
	public static Quaternion getQuaternionFromAngles(float pitch, float yaw,
			float roll) {
		Quaternion quat;

		float p = pitch * PIOVER180 / 2.0f;
		float y = yaw * PIOVER180 / 2.0f;
		float r = roll * PIOVER180 / 2.0f;

		float sinp = (float) Math.sin(p);
		float siny = (float) Math.sin(y);
		float sinr = (float) Math.sin(r);
		float cosp = (float) Math.cos(p);
		float cosy = (float) Math.cos(y);
		float cosr = (float) Math.cos(r);

		float quatX = sinr * cosp * cosy - cosr * sinp * siny;
		float quatY = cosr * sinp * cosy + sinr * cosp * siny;
		float quatZ = cosr * cosp * siny - sinr * sinp * cosy;
		float quatW = cosr * cosp * cosy + sinr * sinp * siny;

		quat = new Quaternion(quatX, quatY, quatZ, quatW);
		Quaternion retQuat = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		Quaternion.normalise(quat, retQuat);

		return retQuat;
	}

	/**
	 * Not sure if this works
	 * @param quat Quaternion to convert
	 * @return float array with 4 elements- x, y, z, and angle
	 */
	public static float[] convertToAxisAngle(Quaternion quat) {
		float scale = (float) Math
				.sqrt(((quat.x * quat.x) + (quat.y * quat.y) + (quat.z * quat.z)));

		float ax = quat.x / scale;
		float ay = quat.y / scale;
		float az = quat.z / scale;

		float angle = 2.0f * (float) Math.acos((double) quat.w);

		return new float[] { ax, ay, az, angle };
	}

	/**
	 * Converts a quaternion to a rotation matrix stored in a FloatBuffer
	 * 
	 * @param quat
	 *            The quaternion to convert
	 * @param dest
	 *            The float buffer to put the rotation matrix into. MUST have a
	 *            capacity of 16 and be direct
	 */
	public static void toFloatBuffer(Quaternion quat, FloatBuffer dest) {
		if (!dest.isDirect()) {
			System.out
					.println("QuaternionHelper toFloatBuffer was passed an indirect FloatBuffer!");
		} else if (dest.capacity() != 16) {
			System.out
					.println("QuaternionHelper toFloatBuffer was passed a buffer of the incorrect size!");
		} else {
			dest.clear();

			float x = quat.x;
			float y = quat.y;
			float z = quat.z;
			float w = quat.w;

			float x2 = x * x;
			float y2 = y * y;
			float z2 = z * z;
			float xy = x * y;
			float xz = x * z;
			float yz = y * z;
			float wx = w * x;
			float wy = w * y;
			float wz = w * z;

			dest.put(1.0f - 2.0f * (y2 + z2));
			dest.put(2.0f * (xy - wz));
			dest.put(2.0f * (xz + wy));
			dest.put(0.0f);
			dest.put(2.0f * (xy + wz));
			dest.put(1.0f - 2.0f * (x2 + z2));
			dest.put(2.0f * (yz - wx));
			dest.put(0.0f);
			dest.put(2.0f * (xz - wy));
			dest.put(2.0f * (yz + wx));
			dest.put(1.0f - 2.0f * (x2 + y2));
			dest.put(0.0f);
			dest.put(0.0f);
			dest.put(0.0f);
			dest.put(0.0f);
			dest.put(1.0f);

			dest.rewind();
		}
	}

	/**
	 * Rotates a vector by a quaternion
	 * 
	 * @param vector
	 *            The vector to rotate
	 * @param quat
	 *            The quaternion to rotate the vector by
	 * @return Rotate vector
	 */
	public static Vector3f rotateVectorByQuaternion(Vector3f vector,
			Quaternion quat) {
		Quaternion vecQuat = new Quaternion(vector.x, vector.y, vector.z, 0.0f);

		Quaternion quatNegate = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		quat.negate(quatNegate);

		Quaternion resQuat = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		Quaternion.mul(vecQuat, quatNegate, resQuat);
		Quaternion.mul(quat, resQuat, resQuat);

		return new Vector3f(resQuat.x, resQuat.y, resQuat.z);
	}
	
	/**
	 * Rotates a vector by a quaternion
	 * 
	 * @param vector
	 *            The vector to rotate
	 * @param quat
	 *            The quaternion to rotate the vector by
	 * @return Rotate vector
	 */
	public static Vector3f rotateVectorByQuaternion(javax.vecmath.Vector3f vector,
			Quaternion quat) {
		Quaternion vecQuat = new Quaternion(vector.x, vector.y, vector.z, 0.0f);

		Quaternion quatNegate = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		quat.negate(quatNegate);

		Quaternion resQuat = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		Quaternion.mul(vecQuat, quatNegate, resQuat);
		Quaternion.mul(quat, resQuat, resQuat);

		return new Vector3f(resQuat.x, resQuat.y, resQuat.z);
	}
	
	/**
	 * Rotates a vector by a quaternion
	 * 
	 * @param vector
	 *            The vector to rotate
	 * @param quat
	 *            The quaternion to rotate the vector by
	 * @return Rotate vector
	 */
	public static Vector3f rotateVectorByQuaternion(javax.vecmath.Vector3f vector,
			Quat4f quat) {
		Quaternion vecQuat = new Quaternion(vector.x, vector.y, vector.z, 0.0f);

		Quat4f quatNegate = new Quat4f(0.0f, 0.0f, 0.0f, 1.0f);
		quat.negate(quatNegate);

		Quaternion resQuat = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		Quaternion.mul(vecQuat, new Quaternion(quatNegate.x, quatNegate.y, quatNegate.z, quatNegate.w), resQuat);
		Quaternion.mul(new Quaternion(quat.x, quat.y, quat.z, quat.w), resQuat, resQuat);

		return new Vector3f(resQuat.x, resQuat.y, resQuat.z);
	}

	/**
	 * Converts a quaternion to good ol' euler angles
	 * 
	 * @param quat
	 *            The quaternion to convert
	 * @return A vector containing the three euler angles
	 */
	public static Vector3f getEulerAnglesFromQuaternion(Quaternion quat) {
		float xn = (2 * ((quat.x * quat.y) + (quat.z * quat.w)))
				/ (1 - (2 * ((quat.y * quat.y) + (quat.z * quat.z))));
		float x = (float) (Math.atan((double) xn));

		float yn = 2 * ((quat.x * quat.z) - (quat.w * quat.y));
		float y = (float) (Math.asin((double) yn));

		float zn = (2 * ((quat.x * quat.w) + (quat.y * quat.z)))
				/ (1 - (2 * ((quat.z * quat.z) + (quat.w * quat.w))));
		float z = (float) (Math.atan((double) zn));

		x = x * (180.0f / (float) Math.PI);
		y = y * (180.0f / (float) Math.PI);
		z = z * (180.0f / (float) Math.PI);

		return new Vector3f(x, y, z);
	}
	
	/**
	 * Rotate a quaternion by a vector
	 * @param quat Quaternion to rotate
	 * @param amount Amount to rotate quaternion by
	 * @return Rotated quaternion
	 */
	public static Quaternion rotate(Quaternion quat, Vector3f amount){
		Quaternion ret = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		
		ret = rotateX(quat, amount.x);
		ret = rotateY(ret, amount.y);
		ret = rotateZ(ret, amount.z);
		
		return ret;
	}

	/**
	 * Rotate a quaternion along it's x axis a certain amount
	 * 
	 * @param amount
	 *            Amount to rotate the quaternion
	 * @return Rotated quaternion
	 */
	public static Quaternion rotateX(Quaternion quat, float amount) {
		Quaternion ret = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		double radHalfAngle = Math.toRadians((double) amount) / 2.0;
		float sinVal = (float) Math.sin(radHalfAngle);
		float cosVal = (float) Math.cos(radHalfAngle);
		Quaternion rot = new Quaternion(sinVal, 0.0f, 0.0f, cosVal);
		Quaternion.mul(quat, rot, ret);
		return ret;
	}

	/**
	 * Rotate a quaternion along it's y axis a certain amount
	 * 
	 * @param amount
	 *            Amount to rotate the quaternion
	 * @return Rotated quaternion
	 */
	public static Quaternion rotateY(Quaternion quat, float amount) {
		Quaternion ret = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		double radHalfAngle = Math.toRadians((double) amount) / 2.0;
		float sinVal = (float) Math.sin(radHalfAngle);
		float cosVal = (float) Math.cos(radHalfAngle);
		Quaternion rot = new Quaternion(0.0f, sinVal, 0.0f, cosVal);
		Quaternion.mul(quat, rot, ret);
		return ret;
	}

	/**
	 * Rotate a quaternion along it's z axis a certain amount
	 * 
	 * @param amount
	 *            Amount to rotate the quaternion
	 * @return Rotated quaternion
	 */
	public static Quaternion rotateZ(Quaternion quat, float amount) {
		Quaternion ret = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		double radHalfAngle = Math.toRadians((double) amount) / 2.0;
		float sinVal = (float) Math.sin(radHalfAngle);
		float cosVal = (float) Math.cos(radHalfAngle);
		Quaternion rot = new Quaternion(0.0f, 0.0f, sinVal, cosVal);
		Quaternion.mul(quat, rot, ret);
		return ret;
	}

	/**
	 * Moves the given vector a certain amount along the rotation of the given
	 * quaternion
	 * 
	 * @param quat
	 *            Rotation to use for movement
	 * @param vec
	 *            Vector to add to
	 * @param amount
	 *            Amount to add to the vector
	 */
	public static Vector3f moveX(Quaternion quat, Vector3f vec, float amount) {
		Vector3f ret = new Vector3f(0.0f, 0.0f, 0.0f);
		Vector3f multi = rotateVectorByQuaternion(new Vector3f(amount, 0.0f,
				0.0f), quat);
		Vector3f.add(vec, multi, ret);
		return ret;
	}

	/**
	 * Moves the given vector a certain amount along the rotation of the given
	 * quaternion
	 * 
	 * @param quat
	 *            Rotation to use for movement
	 * @param vec
	 *            Vector to add to
	 * @param amount
	 *            Amount to add to the vector
	 */
	public static Vector3f moveY(Quaternion quat, Vector3f vec, float amount) {
		Vector3f ret = new Vector3f(0.0f, 0.0f, 0.0f);
		Vector3f multi = rotateVectorByQuaternion(new Vector3f(0.0f, amount,
				0.0f), quat);
		Vector3f.add(vec, multi, ret);
		return ret;
	}

	/**
	 * Moves the given vector a certain amount along the rotation of the given
	 * quaternion
	 * 
	 * @param quat
	 *            Rotation to use for movement
	 * @param vec
	 *            Vector to add to
	 * @param amount
	 *            Amount to add to the vector
	 */
	public static Vector3f moveZ(Quaternion quat, Vector3f vec, float amount) {
		Vector3f ret = new Vector3f(0.0f, 0.0f, 0.0f);
		Vector3f multi = rotateVectorByQuaternion(new Vector3f(0.0f, 0.0f,
				amount), quat);
		Vector3f.add(vec, multi, ret);
		return ret;
	}
	
	/**
	 * Converts a quaternion to a rotation matrix
	 * @param quat Quaternion to convert
	 * @return Rotation matrix representing given quaternion
	 */
	public static Matrix4f toMatrix(Quaternion quat){
		float x2 = quat.x * quat.x;
		float y2 = quat.y * quat.y;
		float z2 = quat.z * quat.z;
		float xy = quat.x * quat.y;
		float xz = quat.x * quat.z;
		float yz = quat.y * quat.z;
		float wx = quat.w * quat.x;
		float wy = quat.w * quat.y;
		float wz = quat.w * quat.z;
		
		Matrix4f ret = new Matrix4f();


		ret.m00 = (1.0f - 2.0f * (y2 + z2));
		ret.m10 = (2.0f * (xy - wz));
		ret.m20 = (2.0f * (xz + wy));
		ret.m30 = (0.0f);
		
		ret.m01 = (2.0f * (xy + wz));
		ret.m11 = (1.0f - 2.0f * (x2 + z2));
		ret.m21 = (2.0f * (yz - wx));
		ret.m31 = (0.0f);
		
		ret.m02 = (2.0f * (xz - wy));
		ret.m12 = (2.0f * (yz + wx));
		ret.m22 = (1.0f - 2.0f * (x2 + y2));
		ret.m32 = (0.0f);
		
		ret.m03 = (0.0f);
		ret.m13 = (0.0f);
		ret.m23 = (0.0f);
		ret.m33 = (1.0f);
		
		return ret;
	}
	
	/**
	 * Finds the quaternion between two vectors. Assumes that the vectors are NOT unit length.
	 * @param vec1 First vector
	 * @param vec2 Second vector
	 * @return Quaternion between vectors
	 */
	public static Quaternion quaternionBetweenVectors(Vector3f vec1, Vector3f vec2){
		Vector3f c = new Vector3f();
		Vector3f.cross(vec1, vec2, c);
		
		double v1squr = (double)vec1.lengthSquared();
		double v2squr = (double)vec2.lengthSquared();
		double angle = Math.sqrt(v1squr * v2squr) + (double)Vector3f.dot(vec1, vec2);
		
		Quaternion q = new Quaternion(c.x, c.y, c.z, (float) angle);
		q.normalise(q);
		return q;
	}
	
	/**
	 * Converts an axis and an angle to a quaternion
	 * @param axis Axis
	 * @param angle Angle
	 * @return Quaternion representing rotation
	 */
	public static Quaternion quaternionFromAxisAngle(Vector3f axis, double angle){
		if(Math.abs(angle) < 1e-6)
			return new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		
		double halfAngle = angle * 0.5;
		
		float s = (float)Math.sin(halfAngle);
		
		float x = axis.x * s;
		float y = axis.y * s;
		float z = axis.z * s;
		float w = (float)Math.cos(halfAngle);
		
		return new Quaternion(x, y, z, w);
	}
	
	// O is your object's position
	// P is the position of the object to face
	// U is the nominal "up" vector (typically Vector3.Y)
	// Note: this does not work when O is straight below or straight above P
	/*
	Matrix RotateToFace(Vector3 O, Vector3 P, Vector3 U)
	{
	  Vector3 D = (O - P);
	  Vector3 Right = Vector3.Cross(U, D);
	  Vector3.Normalize(ref Right, out Right);
	  Vector3 Backwards = Vector3.Cross(Right, U);
	  Vector3.Normalize(ref Backwards, out Backwards);
	  Vector3 Up = Vector3.Cross(Backwards, Right);
	  Matrix rot = new Matrix(Right.X, Right.Y, Right.Z, 0, Up.X, Up.Y, Up.Z, 0, Backwards.X, Backwards.Y, Backwards.Z, 0, 0, 0, 0, 1);
	  return rot;
	}*/
	
	public static Matrix4f rotateToFace(Vector3f O, Vector3f P, Vector3f U){
		Vector3f D = new Vector3f();
		Vector3f.sub(O, P, D);
		
		Vector3f Right = new Vector3f();
		Vector3f.cross(U, D, Right);
		
		Vector3f Backwards = new Vector3f();
		Vector3f.cross(Right, U, Backwards);
		
		Backwards.normalise(Backwards);
		
		Vector3f Up = new Vector3f();
		Vector3f.cross(Backwards, Right, Up);
		
		Matrix4f rot = new Matrix4f();
		

		rot.m00 = Right.x;
		rot.m10 = Right.y;
		rot.m20 = Right.z;
		rot.m30 = 0.0f;
		
		rot.m03 = Up.x;
		rot.m11 = Up.y;
		rot.m21 = Up.z;
		rot.m31 = 0.0f;
		
		rot.m02 = Backwards.x;
		rot.m12 = Backwards.y;
		rot.m22 = Backwards.z;
		rot.m32 = 0.0f;
		
		rot.m03 = 0.0f;
		rot.m13 = 0.0f;
		rot.m23 = 0.0f;
		rot.m33 = 1.0f;
		
		/*
		rot.m00 = Right.x;
		rot.m01 = Right.y;
		rot.m02 = Right.z;
		rot.m03 = 0.0f;
		
		rot.m10 = Up.x;
		rot.m11 = Up.y;
		rot.m12 = Up.z;
		rot.m13 = 0.0f;
		
		rot.m20 = Backwards.x;
		rot.m21 = Backwards.y;
		rot.m22 = Backwards.z;
		rot.m23 = 0.0f;
		
		rot.m30 = 0.0f;
		rot.m31 = 0.0f;
		rot.m32 = 0.0f;
		rot.m33 = 1.0f;
		*/
		
		return rot;
	}
	
	public static Quaternion quaternionFromMatrix(Matrix4f m){
		float tr = m.m00 + m.m11 + m.m22;
		float qw, qx, qy, qz;
		
		if (tr > 0) { 
		  float S = (float) (Math.sqrt(tr+1.0) * 2); // S=4*qw 
		  qw = 0.25f * S;
		  qx = (m.m21 - m.m12) / S;
		  qy = (m.m02 - m.m20) / S; 
		  qz = (m.m10 - m.m01) / S; 
		} else if ((m.m00 > m.m11)&(m.m00 > m.m22)) { 
		  float S = (float) (Math.sqrt(1.0f + m.m00 - m.m11 - m.m22) * 2); // S=4*qx 
		  qw = (m.m21 - m.m12) / S;
		  qx = 0.25f * S;
		  qy = (m.m01 + m.m10) / S; 
		  qz = (m.m02 + m.m20) / S; 
		} else if (m.m11 > m.m22) { 
		  float S = (float) (Math.sqrt(1.0f + m.m11 - m.m00 - m.m22) * 2.0f); // S=4*qy
		  qw = (m.m02 - m.m20) / S;
		  qx = (m.m01 + m.m10) / S; 
		  qy = 0.25f * S;
		  qz = (m.m12 + m.m21) / S; 
		} else { 
		  float S = (float) (Math.sqrt(1.0f + m.m22 - m.m00 - m.m11) * 2.0f); // S=4*qz
		  qw = (m.m10 - m.m01) / S;
		  qx = (m.m02 + m.m20) / S;
		  qy = (m.m12 + m.m21) / S;
		  qz = 0.25f * S;
		}
		
		return new Quaternion(qx, qy, qz, qw);
	}
	
	public static Quaternion lookAt(Vector3f from, Vector3f to){
		/*
		template <class T, class U>
		Quaternion<decltype(T()*U())> TurnFromTo(Vec3<T> a, Vec3<U>b){
		        Normalize(a);
		        Normalize(b);
		        Vec3<decltype(T()*U())> c=Normalized(a+b);
		        Vec3<decltype(T()*U())> axis=CrossProd(a,c);
		        return Quaternion<decltype(T()*U())>(DotProd(a,c) , axis.x, axis.y, axis.z);
		}*/
		
		Vector3f a = new Vector3f(), b = new Vector3f(), c = new Vector3f();
		from.normalise(a);
		to.normalise(b);
		Vector3f.add(a, b, c);
		c.normalise(c);
		Vector3f axis = new Vector3f();
		Vector3f.cross(c, a, axis);
		
		return new Quaternion(axis.x, axis.y, axis.z, Vector3f.dot(a, c));
	}
}
