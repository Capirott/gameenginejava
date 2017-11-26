package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Joint extends Entity {

	private Vector3f offset;
	public Joint(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}
	public Vector3f getOffset() {
		return offset;
	}
	public void setOffset(Vector3f offset) {
		this.offset = offset;
	}	
	
	@Override
	public Vector3f getPosition() {
		if (offset != null)
			return Vector3f.add(super.getPosition(), offset, new Vector3f());
		else 
			return super.getPosition();
	}
	
}
