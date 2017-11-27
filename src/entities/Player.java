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
	private List<Bone> bones = new ArrayList<Bone>();

	public Player(TexturedModel jointTexture, TexturedModel boneTexture, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
		super(null, position, rotX, rotY, rotZ, scale);

		for (int i = 0; i < JType.values().length; ++i) {
			joints.add(new Joint(jointTexture, position, 0, 0, 0, new Vector3f(scale.x / 5.0f, scale.y / 5.0f , scale.z / 5.0f)));        				
		}
		joints.get(JType.HEAD.ordinal()).setOffset(new Vector3f(0.0f, 14.0f, 0.0f));
		joints.get(JType.NECK.ordinal()).setOffset(new Vector3f(0.0f, 11.0f, 0.0f));			
		joints.get(JType.UPPER_L_ARM.ordinal()).setOffset(new Vector3f(2.0f, 9.0f, 0.0f));			
		joints.get(JType.LOWER_L_ARM.ordinal()).setOffset(new Vector3f(3.0f, 7.0f, 0.0f));			
		joints.get(JType.L_HAND.ordinal()).setOffset(new Vector3f(4.0f, 6.0f, 0.0f));
		joints.get(JType.UPPER_R_ARM.ordinal()).setOffset(new Vector3f(-2.0f, 9.0f, 0.0f));	
		joints.get(JType.LOWER_R_ARM.ordinal()).setOffset(new Vector3f(-3.0f, 7.0f, 0.0f));	
		joints.get(JType.R_HAND.ordinal()).setOffset(new Vector3f(-4.0f, 6.0f, 0.0f));;
		joints.get(JType.CHEST.ordinal()).setOffset(new Vector3f(0.0f, 8.0f, 0.0f));		
		joints.get(JType.HIP.ordinal()).setOffset(new Vector3f(0.0f, 6.0f, 0.0f));
		joints.get(JType.UPPER_L_LEG.ordinal()).setOffset(new Vector3f(1.5f, 6.0f, 0.0f));	
		joints.get(JType.LOWER_L_LEG.ordinal()).setOffset(new Vector3f(1.5f, 3.0f, 0.0f));	
		joints.get(JType.L_FOOT.ordinal()).setOffset(new Vector3f(1.5f, 0.0f, 0.0f));       	
		joints.get(JType.UPPER_R_LEG.ordinal()).setOffset(new Vector3f(-1.5f, 6.0f, 0.0f));	
		joints.get(JType.LOWER_R_LEG.ordinal()).setOffset(new Vector3f(-1.5f, 3.0f, 0.0f));	
		joints.get(JType.R_FOOT.ordinal()).setOffset(new Vector3f(-1.5f, 0.0f, 0.0f));			
				
		for (int i = 0; i < BType.values().length; ++i) {
			bones.add(new Bone(boneTexture, new Vector3f(0.0f, 0.0f, 0.0f), rotX, rotY, rotZ,  new Vector3f(scale.x, scale.y, scale.z)));
		}
		Bone bone = bones.get(BType.HEAD_NECK.ordinal());
		bone.addScale(1.0f, 0.7f, 0.3f);
		bone = bones.get(BType.NECK_CHEST.ordinal());
		bone.addScale(0.0f, 0.7f, 0.0f);
		bone = bones.get(BType.CHEST_HIP.ordinal());
		bone.addScale(0.0f, 0.21f, 0.0f);
		bone = bones.get(BType.UL_L_LEG.ordinal());
		bone.addScale(0.0f, 0.7f, 0.0f);
		bone = bones.get(BType.UL_R_LEG.ordinal());
		bone.addScale(0.0f, 0.7f, 0.0f);
		bone = bones.get(BType.L_L_FOOT.ordinal());
		bone.addScale(0.0f, 0.7f, 0.0f);
		bone = bones.get(BType.L_R_FOOT.ordinal());
		bone.addScale(0.0f, 0.7f, 0.0f);
		bone = bones.get(BType.NECK_UL_ARM.ordinal());
		bone.addScale(0.0f, 0.5f, 0.0f);
		bone = bones.get(BType.NECK_UR_ARM.ordinal());
		bone.addScale(0.0f, 0.5f, 0.0f);
		bone = bones.get(BType.UL_L_ARM.ordinal());
		bone.addScale(0.0f, 0.21f, 0.4f);
		bone = bones.get(BType.UL_R_ARM.ordinal());
		bone.addScale(0.0f, 0.21f, 0.4f);
		bone = bones.get(BType.L_L_HAND.ordinal());
		bone.addScale(0.4f, -0.1f, 2.0f);
		bone = bones.get(BType.L_R_HAND.ordinal());
		bone.addScale(0.0f, -0.1f, 1.0f);
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
//			joint.setRotX(getRotX());
//			joint.setRotY(getRotY() + 10);
			joint.setRotYP(getRotY());
//			joint.setRotZ(getRotZ());
		}
		
		for (Bone bone : bones) {
//			bone.setRotX(getRotX());
			bone.setRotY(getRotY());
//			bone.setRotZ(getRotZ());
		}
		bones.get(BType.HEAD_NECK.ordinal()).setPosition(joints.get(JType.NECK.ordinal()).getPosition());
		bones.get(BType.NECK_CHEST.ordinal()).setPosition(joints.get(JType.CHEST.ordinal()).getPosition());
		bones.get(BType.CHEST_HIP.ordinal()).setPosition(joints.get(JType.HIP.ordinal()).getPosition());
		bones.get(BType.UL_L_LEG.ordinal()).setPosition(joints.get(JType.LOWER_L_LEG.ordinal()).getPosition());
		bones.get(BType.UL_R_LEG.ordinal()).setPosition(joints.get(JType.LOWER_R_LEG.ordinal()).getPosition());
		bones.get(BType.L_L_FOOT.ordinal()).setPosition(joints.get(JType.L_FOOT.ordinal()).getPosition());
		bones.get(BType.L_R_FOOT.ordinal()).setPosition(joints.get(JType.R_FOOT.ordinal()).getPosition());
		bones.get(BType.NECK_UL_ARM.ordinal()).setPosition(joints.get(JType.UPPER_L_ARM.ordinal()).getPosition());		
		bones.get(BType.NECK_UL_ARM.ordinal()).setRotZ(42);
		bones.get(BType.NECK_UR_ARM.ordinal()).setPosition(joints.get(JType.UPPER_R_ARM.ordinal()).getPosition());
		bones.get(BType.NECK_UR_ARM.ordinal()).setRotZ(-42);
		bones.get(BType.UL_L_ARM.ordinal()).setPosition(joints.get(JType.LOWER_L_ARM.ordinal()).getPosition());
		bones.get(BType.UL_L_ARM.ordinal()).setRotZ(25);
		bones.get(BType.UL_R_ARM.ordinal()).setPosition(joints.get(JType.LOWER_R_ARM.ordinal()).getPosition());
		bones.get(BType.UL_R_ARM.ordinal()).setRotZ(-25);
		bones.get(BType.L_L_HAND.ordinal()).setPosition(joints.get(JType.L_HAND.ordinal()).getPosition());
		bones.get(BType.L_L_HAND.ordinal()).setRotZ(40);
		bones.get(BType.L_R_HAND.ordinal()).setPosition(joints.get(JType.R_HAND.ordinal()).getPosition());
		bones.get(BType.L_R_HAND.ordinal()).setRotZ(-40);
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

	public List<Bone> getBones() {
		return bones;
	}

	public void setBones(List<Bone> bones) {
		this.bones = bones;
	}	
	
}
