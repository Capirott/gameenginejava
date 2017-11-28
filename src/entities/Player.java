package entities;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;
import toolbox.Maths;

public class Player extends Entity {

	enum JType {
		HIP,
		UPPER_L_LEG,
		LOWER_L_LEG,
		L_FOOT,
		UPPER_R_LEG,
		LOWER_R_LEG,
		R_FOOT,
		CHEST,
		UPPER_L_ARM,
		LOWER_L_ARM,
		L_HAND,
		UPPER_R_ARM,
		LOWER_R_ARM,
		R_HAND,
		NECK,
		HEAD
	}
	
	enum BType {
		HEAD_NECK,
		NECK_CHEST,
		CHEST_HIP,
		UL_L_LEG,
		UL_R_LEG,
		L_L_FOOT,
		L_R_FOOT,
		NECK_UR_ARM,
		NECK_UL_ARM,
		UL_L_ARM,
		UL_R_ARM,
		L_L_HAND,
		L_R_HAND
	}
	
	private static final float RUN_SPEED = 200;
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -50;
	private static final float JUMP_POWER = 30;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	private boolean isInAir = false;

	private List<Joint> joints = new ArrayList<Joint>();

	public Player(TexturedModel jointTexture, TexturedModel boneTexture, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
		super(null, position, rotX, rotY, rotZ, scale);

		for (int i = 0; i < JType.values().length; ++i) {
			joints.add(new Joint(jointTexture, new Vector3f(position.x, position.y, position.z), 0, 0, 0, new Vector3f(scale.x / 5.0f, scale.y / 5.0f , scale.z / 5.0f)));        				
		}
		joints.get(JType.HEAD.ordinal()).increasePosition(new Vector3f(0.0f, 14.0f, 0.0f));
		joints.get(JType.NECK.ordinal()).increasePosition(new Vector3f(0.0f, 11.0f, 0.0f));			
		joints.get(JType.UPPER_L_ARM.ordinal()).increasePosition(new Vector3f(2.0f, 9.0f, 0.0f));			
		joints.get(JType.LOWER_L_ARM.ordinal()).increasePosition(new Vector3f(3.0f, 7.0f, 0.0f));			
		joints.get(JType.L_HAND.ordinal()).increasePosition(new Vector3f(4.0f, 6.0f, 0.0f));
		joints.get(JType.UPPER_R_ARM.ordinal()).increasePosition(new Vector3f(-2.0f, 9.0f, 0.0f));	
		joints.get(JType.LOWER_R_ARM.ordinal()).increasePosition(new Vector3f(-3.0f, 7.0f, 0.0f));	
		joints.get(JType.R_HAND.ordinal()).increasePosition(new Vector3f(-4.0f, 6.0f, 0.0f));;
		joints.get(JType.CHEST.ordinal()).increasePosition(new Vector3f(0.0f, 8.0f, 0.0f));		
		joints.get(JType.HIP.ordinal()).increasePosition(new Vector3f(0.0f, 6.0f, 0.0f));
		joints.get(JType.UPPER_L_LEG.ordinal()).increasePosition(new Vector3f(1.5f, 6.0f, 0.0f));	
		joints.get(JType.LOWER_L_LEG.ordinal()).increasePosition(new Vector3f(1.5f, 3.0f, 0.0f));	
		joints.get(JType.L_FOOT.ordinal()).increasePosition(new Vector3f(1.5f, 0.0f, 0.0f));       	
		joints.get(JType.UPPER_R_LEG.ordinal()).increasePosition(new Vector3f(-1.5f, 6.0f, 0.0f));	
		joints.get(JType.LOWER_R_LEG.ordinal()).increasePosition(new Vector3f(-1.5f, 3.0f, 0.0f));	
		joints.get(JType.R_FOOT.ordinal()).increasePosition(new Vector3f(-1.5f, 0.0f, 0.0f));			
	}
	
	public void move(Terrain terrain) {
		System.out.println(getRotY());
		checkInputs();
		float rotY = currentTurnSpeed * DisplayManager.getFrameTimeSeconds();
		super.increaseRotation(0, rotY, 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		double posY = getPosition().y;
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if (super.getPosition().y < terrainHeight) {
			upwardsSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}
		posY = super.getPosition().y - posY;
		for (Joint joint : joints) {
//			joint.setRotX(getRotX());
//			joint.setRotY(getRotY() + 10);	
			joint.increasePosition(dx, (float)posY, dz);
			joint.setPosition(Maths.rotateYWithAnchor(joint.getPosition(), getPosition(), rotY));
//			joint.setRotZ(getRotZ());
		}
		
	}


	private void jump() {
		if (!isInAir) {
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}

	private void checkInputs() {
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.currentSpeed = RUN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.currentSpeed = -RUN_SPEED;
		} else {
			this.currentSpeed = 0;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.currentTurnSpeed = -TURN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.currentTurnSpeed = TURN_SPEED;
		} else {
			this.currentTurnSpeed = 0;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}
	}

	public List<Joint> getJoints() {
		return joints;
	}

	public void setJoints(List<Joint> joints) {
		this.joints = joints;
	}
	
}
