package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Joint extends Entity {

	private boolean inverted;
	
	public Joint(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}
	
	@Override
	public float getRotY() {
		return (inverted ? 180.0f - super.getRotY() : super.getRotY());
	}
	
}
