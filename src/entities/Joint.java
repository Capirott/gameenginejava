package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import toolbox.Maths;

public class Joint extends Entity {

	private float rotYP;
	private Vector3f offset = new Vector3f(0.0f, 0.0f, 0.0f);
	public Joint(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
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
		return Maths.rotateYWithAnchor(Vector3f.add(super.getPosition(), offset, new Vector3f()), super.getPosition(), rotYP);
	}
	public float getRotYP() {
		return rotYP;
	}
	public void setRotYP(float rotYP) {
		this.rotYP = rotYP;
	}
}
