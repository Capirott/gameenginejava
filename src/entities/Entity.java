package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Entity {
	private TexturedModel model;
	private Vector3f position;
	private Vector3f rotation = new Vector3f();
	private Vector3f scale;
	
	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
		super();
		this.model = model;
		this.position = position;
		this.rotation.x = rotX;
		this.rotation.y = rotY;
		this.rotation.z = rotZ;
		this.scale = scale;
	}
	
	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}
	
	public void increasePosition(Vector3f pos) {
		Vector3f.add(this.position, pos, this.position);
	}

	public void increaseRotation(float dx, float dy, float dz) {
		this.rotation.x += dx;
		this.rotation.y += dy;
		this.rotation.z += dz;
	}
	
	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getRotX() {
		return rotation.x;
	}

	public void setRotX(float rotX) {
		this.rotation.y = rotX;
	}

	public float getRotY() {
		return rotation.y;
	}

	public void setRotY(float rotY) {
		this.rotation.y = rotY;
	}

	public float getRotZ() {
		return rotation.z;
	}

	public void setRotZ(float rotZ) {
		this.rotation.z = rotZ;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}	
	
	public void addScale(float x, float y, float z) {
		scale.x += x;
		scale.y += y;
		scale.z += z;
	}	
}
