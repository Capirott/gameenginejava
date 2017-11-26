package entities;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity {

	enum JType {
		HIP_JOINT,
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
	
	private static final float RUN_SPEED = 20;
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -50;
	private static final float JUMP_POWER = 30;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	private boolean isInAir = false;

	private List<Joint> joints = new ArrayList<Joint>();

	public Player(TexturedModel joint, TexturedModel bones, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(null, position, rotX, rotY, rotZ, scale);

		for (int i = 0; i < JType.values().length; ++i) {
			joints.add(new Joint(joint, position, rotX, rotY, rotZ, scale / 5.0f));        				
		}
		
		joints.get(JType.HEAD.ordinal()).setOffset(new Vector3f(0.0f, 17.0f, 0.0f));
		joints.get(JType.CHEST.ordinal()).setOffset(new Vector3f(0.0f, 12.0f, 0.0f));		
		joints.get(JType.UPPER_L_ARM.ordinal()).setOffset(new Vector3f(3.0f, 10.0f, 0.0f));			
		joints.get(JType.LOWER_L_ARM.ordinal()).setOffset(new Vector3f(5.0f, 8.0f, 0.0f));			
//		joints.get(JType.UPPER_R_ARM.ordinal()).setOffset(new Vector3f(3.0f, 10.0f, 0.0f));	
		joints.get(JType.R_HAND.ordinal());
		joints.get(JType.L_HAND.ordinal());
		joints.get(JType.HIP_JOINT.ordinal());
		joints.get(JType.UPPER_L_LEG.ordinal());	
		joints.get(JType.LOWER_L_LEG.ordinal());	
		joints.get(JType.L_FOOT.ordinal());       	
		joints.get(JType.UPPER_R_LEG.ordinal());	
		joints.get(JType.LOWER_R_LEG.ordinal());	
		joints.get(JType.R_FOOT.ordinal());       	
		joints.get(JType.LOWER_R_ARM.ordinal());	
	    joints.get(JType.NECK.ordinal());			
	
	}                                                                          
                                                                                 
                                                                                 
                                                                                 
	public void move(Terrain terrain) {
		checkInputs();
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if (super.getPosition().y < terrainHeight) {
			upwardsSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}		
	
		for (Joint joint : joints) {
			joint.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
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
