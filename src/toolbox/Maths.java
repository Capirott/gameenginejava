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

	public static Vector3f rotateXWithAnchor(Vector3f position, Vector3f anchor, float angle) {
		Matrix4f matrix = getTransMatrixX(anchor, angle);
		Vector4f vec = new Vector4f(position.x, position.y, position.z, 1);
		vec = Matrix4f.transform(matrix, vec, vec);
		return new Vector3f(vec.x, vec.y, vec.z);		
	}

	public static Matrix4f getTransMatrixX(Vector3f anchor, float angle) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		matrix.translate(anchor);	
		angle = (float) Math.toRadians(angle);
		matrix.rotate(angle, new Vector3f(1, 0, 0));
		matrix.translate(anchor.negate(new Vector3f()));
		return matrix;
	}

	public static Matrix4f getTransMatrixY(Vector3f anchor, float angle) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		matrix.translate(anchor);	
		angle = (float) Math.toRadians(angle);
		matrix.rotate(angle, new Vector3f(0, 1, 0));
		matrix.translate(anchor.negate(new Vector3f()));
		return matrix;
	}
	
	public static Vector3f rotateYWithAnchor(Vector3f position, Vector3f anchor, float angle) {
		Matrix4f matrix = getTransMatrixY(anchor, angle);
		Vector4f vec = new Vector4f(position.x, position.y, position.z, 1);
		vec = Matrix4f.transform(matrix, vec, vec);
		return new Vector3f(vec.x, vec.y, vec.z);			
	}

	public static Matrix4f getTransMatrixZ(Vector3f anchor, float angle) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		matrix.translate(anchor);	
		angle = (float) Math.toRadians(angle);
		matrix.rotate(angle, new Vector3f(0, 0, 1));
		matrix.translate(anchor.negate(new Vector3f()));
		return matrix;
	}

	public static Vector3f rotateZWithAnchor(Vector3f position, Vector3f anchor, float angle) {
		Matrix4f matrix = getTransMatrixZ(anchor, angle);
		Vector4f vec = new Vector4f(position.x, position.y, position.z, 1);
		vec = Matrix4f.transform(matrix, vec, vec);
		return new Vector3f(vec.x, vec.y, vec.z);		
	}

}
