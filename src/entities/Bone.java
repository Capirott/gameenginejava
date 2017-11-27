package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Bone extends Entity {

	public Bone(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

}
