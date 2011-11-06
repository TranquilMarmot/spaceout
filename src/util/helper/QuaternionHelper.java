package util.helper;

import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class QuaternionHelper {
	final static float PIOVER180 = ((float) Math.PI) / 180.0f;
	public static Quaternion getQuaternionFromAngles(float pitch, float yaw, float roll){
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
	
	// not sure if this one works properly
	public static float[] convertToAxisAngle(Quaternion quat){
		float scale = (float) Math.sqrt(((quat.x * quat.x) + (quat.y * quat.y) + (quat.z * quat.z)));
		
		float ax = quat.x / scale;
		float ay = quat.y / scale;
		float az = quat.z / scale;
		
		float angle = 2.0f * (float)Math.acos((double) quat.w);
		
		return new float[]{ax, ay, az, angle };
	}
	
	/**
	 * Converts a quaternion to a rotation matrix stored in a FloatBuffer
	 * @param quat The quaternion to convert
	 * @param dest The float buffer to put the rotation matrix into. MUST have a capacity of 16 and be direct
	 */
	public static void toFloatBuffer(Quaternion quat, FloatBuffer dest){
		if(!dest.isDirect()){
			System.out.println("QuaternionHelper toFloatBuffer was passed an indirect FloatBuffer!");
		} else if(dest.capacity() != 16){
			System.out.println("QuaternionHelper toFloatBuffer was passed a buffer of the incorrect size!");
		}else{
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
	
	public static Vector3f MulQuaternionVector(Quaternion quat, Vector3f vector){
		// create a normalized quaternion version of the given vector
		Vector3f normal = new Vector3f(vector.x, vector.y, vector.z);
		vector.normalise(normal);
		Quaternion vecQuat = new Quaternion(normal.x, normal.y, normal.z, 0.0f);
		
		// quaternion to multiply into
		Quaternion resQuat = new Quaternion(0.0f, 0.0f, 0.0f, 0.0f);
		
		// negate the given quaternion
		Quaternion inverseQuat = new Quaternion(-quat.x, -quat.y, -quat.z, quat.w);
		
		// multiply the vector quaternion by the inverse of the given quaternion
		Quaternion.mul(vecQuat, inverseQuat, resQuat);
		// then multiply that by the regular quaternion
		Quaternion.mul(quat, resQuat, resQuat);
		
		// return a vector representing the multiplied quaternions
		return new Vector3f(resQuat.x, resQuat.y, resQuat.z);
	}
}
