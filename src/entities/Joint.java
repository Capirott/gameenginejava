package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Joint extends Entity {

	private boolean inverted;
	
	public Joint(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		this.inverted = false;
	}

	public boolean isInverted() {
		return inverted;
	}
	
	@Override
	public float getRotY() {
		return (inverted ? -super.getRotY() : super.getRotY());
	}

	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}	
	
}
