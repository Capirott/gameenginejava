package entities;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Joint extends Entity {

	public Joint(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
		super(model, position, rotX, rotY, rotZ, scale);
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
	
	@Override
	public Matrix4f getTransformationMatrix(){
		return transformationMatrix;
	}

	public boolean isUpdateChildren() {
		return updateChildren;
	}

	public void setUpdateChildren(boolean updateChildren) {
		this.updateChildren = updateChildren;
	}
	
}
