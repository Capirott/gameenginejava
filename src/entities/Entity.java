package entities;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import toolbox.Maths;

public class Entity {
	protected TexturedModel model;
	protected Vector3f position = new Vector3f();
	protected Vector3f rotation = new Vector3f();
	protected Matrix4f transformationMatrix = new Matrix4f();
	List<Joint> childrenList = new ArrayList<Joint>();
	boolean updateChildren = false;
	
	
	
	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
		super();
		this.position = position;
		this.model = model;
		this.rotation.x = rotX;
		this.rotation.y = rotY;
		this.rotation.z = rotZ;
		transformationMatrix.setIdentity();
		transformationMatrix.translate(position);
		transformationMatrix.scale(scale);
		rotateAroundX(rotX, null);
		rotateAroundY(rotY, null);
		rotateAroundZ(rotZ, null);
	}
	
	public void increasePosition(float dx, float dy, float dz) {
		Matrix4f trans = new Matrix4f();
		trans.setIdentity();
		trans.translate(new Vector3f(dx, dy, dz));
		Matrix4f.mul(trans, transformationMatrix, transformationMatrix);
		this.position.translate(dx, dy, dz);
	}
	
	public void increasePosition(Vector3f pos) {
		increasePosition(pos);
	}
	
	public List<Joint> getChildren() {
		return childrenList;
	}

	public void setChildren(List<Joint> children) {
		this.childrenList = children;
	}
	
	public void addChildren(Joint children) {
		this.childrenList.add(children);
	}
	
	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public float getRotX() {
		return rotation.x;
	}

	public float getRotY() {
		return rotation.y;
	}

	public float getRotZ() {
		return rotation.z;
	}

	
	public void addScale(float x, float y, float z) {
		transformationMatrix.scale(new Vector3f(x, y, z));
	}
	
	public Matrix4f getTransformationMatrix() {
		return transformationMatrix;
	}
	
	public void rotateAroundX(float angle, Vector3f anchor) {
		rotation.x += angle;
		if (anchor == null) {
			anchor = position;
			Matrix4f.mul(Maths.getTransMatrixX(position, angle), transformationMatrix, transformationMatrix);
		} else {
			Matrix4f trans = Maths.getTransMatrixX(anchor, angle);
			Matrix4f.mul(trans, transformationMatrix, transformationMatrix);

		}
		position = Maths.rotateXWithAnchor(position, anchor, angle);
		if (updateChildren) {
			for (Joint joint : childrenList) {
				joint.rotateAroundX(angle, anchor);
			}
		}
	}
	
	public void rotateAroundY(float angle, Vector3f anchor) {
		rotation.y += angle;
		if (anchor == null) {
			anchor = position;
			Matrix4f.mul(Maths.getTransMatrixY(position, angle), transformationMatrix, transformationMatrix);
		} else {

			Matrix4f trans = Maths.getTransMatrixY(anchor, angle);
			Matrix4f.mul(trans, transformationMatrix, transformationMatrix);
		}
		position = Maths.rotateYWithAnchor(position, anchor, angle);
		if (updateChildren) {
			for (Joint joint : childrenList) {
				joint.rotateAroundY(angle, anchor);
			}
		}
	}
	
	public void rotateAroundZ(float angle, Vector3f anchor) {
		rotation.z += angle;
		if (anchor == null) {
			anchor = position;
			Matrix4f.mul(Maths.getTransMatrixZ(position, angle), transformationMatrix, transformationMatrix);
		} else {
			Matrix4f trans = Maths.getTransMatrixZ(anchor, angle);
			Matrix4f.mul(trans, transformationMatrix, transformationMatrix);
		}
		position = Maths.rotateZWithAnchor(position, anchor, angle);
		if (updateChildren) {
			for (Joint joint : childrenList) {
				joint.rotateAroundZ(angle, anchor);
			}
		}
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
}
