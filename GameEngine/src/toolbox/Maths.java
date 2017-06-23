package toolbox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;

public class Maths {
	
	public static boolean isShelter(Vector3f camPosition, Vector3f playerPosition, Vector3f objPosition) {
		for (int i = -10; i < 10; i++) {
			for (int j = -10; j < 10; j++) {
				for (int k = -10; k < 10; k++) {
					Vector3f objPosition_tmp = new Vector3f(objPosition.x + i, objPosition.y + j, objPosition.z + k);
					Vector3f camNormail = new Vector3f(camPosition.x, 0, camPosition.z);
					Vector3f v0 = Vector3f.sub(camNormail, camPosition, null);
					Vector3f v1 = Vector3f.sub(playerPosition, camPosition, null);
					Vector3f v2 = Vector3f.sub(objPosition_tmp, camPosition, null);
					float dot00 = Vector3f.dot(v0, v0) ;
				    float dot01 = Vector3f.dot(v0, v1) ;
				    float dot02 = Vector3f.dot(v0, v2) ;
				    float dot11 = Vector3f.dot(v1, v1) ;
				    float dot12 = Vector3f.dot(v1, v2) ;
				    float inverDeno = 1 / (dot00 * dot11 - dot01 * dot01) ;

				    float u = (dot11 * dot02 - dot01 * dot12) * inverDeno ;
				    if (u < 1 && u > 0) {
				        return true ;
				    }

				    float v = (dot00 * dot12 - dot01 * dot02) * inverDeno ;
				    // if v out of range, return directly
				    if (v < 1 && v > 0) {
				        return true ;
				    }
				    if(u + v > 1) {
				    	return true;
				    }
				}
			}
		}
	    return false;
	}
	
	/**重心插值
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param pos
	 * @return
	 */
	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();//单元矩阵
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);//x轴旋转
        Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);//y轴旋转
        Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);//z轴旋转
        Matrix4f.scale(new Vector3f(scale,scale,scale), matrix, matrix);//缩放
        return matrix;
	}
	
	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
        return viewMatrix;
	}

}
