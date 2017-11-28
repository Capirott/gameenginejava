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

	public Player(TexturedModel jointTexture, TexturedModel jointInverted, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
		super(null, position, rotX, rotY, rotZ, scale);

		for (int i = 0; i < JType.values().length; ++i) {
			joints.add(new Joint(jointTexture, new Vector3f(position.x, position.y, position.z), 0, 0, 0, new Vector3f(scale.x, scale.y, scale.z)));        				
		}
		Joint joint = joints.get(JType.NECK.ordinal());
		joint.increasePosition(new Vector3f(0.0f, 13.3f, 0.0f));
		joint.setRotation(new Vector3f(10.0f, 0.0f, 0.0f));
		joint.setModel(jointInverted);
		joint = joints.get(JType.CHEST.ordinal());
		joint.setModel(jointInverted);
		joint.increasePosition(new Vector3f(0.0f, 11.0f, 0.0f));	
		joint = joints.get(JType.HIP.ordinal());
		joint.setModel(jointInverted);
		joint.increasePosition(new Vector3f(0.0f, 9.0f, 0.0f));
		joint = joints.get(JType.UPPER_L_ARM.ordinal());
		joint.increasePosition(new Vector3f(2.0f, 9.0f, 0.0f));			
		joint = joints.get(JType.LOWER_L_ARM.ordinal());
		joint.increasePosition(new Vector3f(3.0f, 7.0f, 0.0f));			
		joint = joints.get(JType.L_HAND.ordinal());
		joint.increasePosition(new Vector3f(4.0f, 6.0f, 0.0f));
		joint = joints.get(JType.UPPER_R_ARM.ordinal());
		joint.increasePosition(new Vector3f(-2.0f, 9.0f, 0.0f));	
		joint = joints.get(JType.LOWER_R_ARM.ordinal());
		joint.increasePosition(new Vector3f(-3.0f, 7.0f, 0.0f));	
		joint = joints.get(JType.R_HAND.ordinal());
		joint.increasePosition(new Vector3f(-4.0f, 6.0f, 0.0f));;
		joint = joints.get(JType.UPPER_L_LEG.ordinal());
		joint.increasePosition(new Vector3f(1.5f, 6.0f, 0.0f));	
		joint = joints.get(JType.LOWER_L_LEG.ordinal());
		joint.increasePosition(new Vector3f(1.5f, 3.0f, 0.0f));	
		joint = joints.get(JType.L_FOOT.ordinal());
		joint.increasePosition(new Vector3f(1.5f, 0.0f, 0.0f));       	
		joint = joints.get(JType.UPPER_R_LEG.ordinal());
		joint.increasePosition(new Vector3f(-1.5f, 6.0f, 0.0f));	
		joint = joints.get(JType.LOWER_R_LEG.ordinal());
		joint.increasePosition(new Vector3f(-1.5f, 3.0f, 0.0f));	
		joint = joints.get(JType.R_FOOT.ordinal());
		joint.increasePosition(new Vector3f(-1.5f, 0.0f, 0.0f));			
	}
	
	public void move(Terrain terrain) {
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
			joint.increasePosition(dx, (float)posY, dz);
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
