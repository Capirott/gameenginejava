package toolbox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;

public class Maths {
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, Vector3f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix, matrix);
		Matrix4f.scale(scale, matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
		return createTransformationMatrix(translation, rotation.x, rotation.y, rotation.z, scale);
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

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	
	public static Vector3f rotateYWithAnchor(Vector3f position, Vector3f anchor, float angle) {
		if (angle != 0 && !position.equals(anchor)) {
			System.out.println("");
		}
		Vector3f pReturn = new Vector3f();
		angle = (float) Math.toRadians(angle);
		pReturn.x = (float) (position.x * Math.cos(angle) + position.z * Math.sin(angle) -anchor.x * Math.cos(angle) - anchor.z * Math.sin(angle) + anchor.x);
		pReturn.y = position.y;
		pReturn.z = (float) (-position.x * Math.sin(angle) + position.z * Math.cos(angle) + anchor.x * Math.sin(angle) - anchor.z * Math.cos(angle) + anchor.z);
		return pReturn;		
	}
	public static Vector3f rotateXWithAnchor(Vector3f position, Vector3f anchor, float angle) {
		if (angle != 0 && !position.equals(anchor)) {
			System.out.println("");
		}
		Vector3f pReturn = new Vector3f();
		angle = (float) Math.toRadians(angle);
		pReturn.x = position.x;
		pReturn.y = (float) (position.y * Math.cos(angle) - position.z * Math.sin(angle) + anchor.y * (1 - Math.cos(angle)) + anchor.z * Math.sin(angle));
		pReturn.z = (float) (position.y * Math.sin(angle) + position.z * Math.cos(angle) + anchor.z * (1 - Math.cos(angle)) - anchor.y * Math.sin(angle));
		return pReturn;		
	}

}
