package entities;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import toolbox.Maths;

public class Joint extends Entity {

	List<Joint> childrenList = new ArrayList<Joint>();
	boolean updateChildren = false;
	public Joint(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
		super(model, position, rotX, rotY, rotZ, scale);
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
	
	@Override
	public void increasePosition(float dx, float dy, float dz) {
		super.increasePosition(dx, dy, dz);
		if (updateChildren) {
			for (Joint joint : childrenList) {
				joint.increasePosition(dx, dy, dz);
			}
		}
	}
	
	@Override
	public void increasePosition(Vector3f pos) {
		increasePosition(pos.x, pos.y, pos.z);
	}
	
	public void rotateAroundPoint(float dx, float dy, float dz, Vector3f anchor) {
		position = Maths.rotateXWithAnchor(position, anchor, dx);
		position = Maths.rotateYWithAnchor(position, anchor, dy);
		position = Maths.rotateZWithAnchor(position, anchor, dz);
//		rotation = Maths.rotateXWithAnchor(rotation, anchor, dx);
//		rotation = Maths.rotateYWithAnchor(rotation, anchor, dy);
//		rotation = Maths.rotateZWithAnchor(rotation, anchor, dz);
		if (updateChildren) {
			for (Joint joint : childrenList) {
				joint.rotateAroundPoint(dx, dy, dz, anchor);
			}
		}
	}
	
	@Override
	public void increaseRotation(float dx, float dy, float dz) {
		if (dx != 0.0f || dy != 0.0f || dz != 0.0f) {
			super.increaseRotation(dx, dy, dz);
			if (updateChildren) {
				for (Joint joint : childrenList) {
					joint.increaseRotation2(dx, dy, dz);
					joint.rotateAroundPoint(dx, dy, dz, position);
				}
			}
		}
	}
	
	public void increaseRotation2(float dx, float dy, float dz) {
		if (dx != 0.0f || dy != 0.0f || dz != 0.0f) {
			super.increaseRotation(dx, dy, dz);
			if (updateChildren) {
				for (Joint joint : childrenList) {
					joint.increaseRotation2(dx, dy, dz);
				}
			}
		}
	}
	
	
	
	@Override
	public Matrix4f getTransformationMatrix() {
		return super.getTransformationMatrix();
	}

	public boolean isUpdateChildren() {
		return updateChildren;
	}

	public void setUpdateChildren(boolean updateChildren) {
		this.updateChildren = updateChildren;
	}
	
}
