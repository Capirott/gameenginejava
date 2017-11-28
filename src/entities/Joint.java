package entities;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Joint extends Entity {

	List<Joint> childrenList = new ArrayList<Joint>();
	
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
	public Matrix4f getTransformationMatrix() {
		return super.getTransformationMatrix();
	}
	
}
