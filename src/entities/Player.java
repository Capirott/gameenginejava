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
		NECK;
	    private static JType[] vals = values();
		public JType next()
	    {
	        return vals[(this.ordinal()+1) % vals.length];
	    }
	}
	private JType jType = JType.HIP;
	private static final float RUN_SPEED = 200;
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -50;
	private static final float JUMP_POWER = 30;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	private boolean isInAir = false;

	private List<Joint> joints = new ArrayList<Joint>();
	private boolean isKeyPressed;

	public Player(TexturedModel jointTexture, TexturedModel jointInverted, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale) {
		super(null, position, rotX, rotY, rotZ, scale);

		for (int i = 0; i < JType.values().length; ++i) {
			joints.add(new Joint(jointTexture, new Vector3f(position.x, position.y, position.z), 0, 0, 0, new Vector3f(scale.x, scale.y, scale.z)));        				
		}
		Joint joint = joints.get(JType.NECK.ordinal());
		joint.increasePosition(0.0f, 10.1f, 0.0f);
		joint.setRotation(new Vector3f(10.0f, 0.0f, 0.0f));
		joint.setModel(jointInverted);
		joint = joints.get(JType.CHEST.ordinal());
		joint.setModel(jointInverted);
		joint.addChildren(joints.get(JType.UPPER_L_ARM.ordinal()));
		joint.addChildren(joints.get(JType.UPPER_R_ARM.ordinal()));
		joint.addChildren(joints.get(JType.NECK.ordinal()));
		joint.increasePosition(0.0f, 7.8f, 0.0f);	
		joint = joints.get(JType.HIP.ordinal());
		joint.setModel(jointInverted);
		joint.increasePosition(0.0f, 5.5f, 0.0f);
		joint.addChildren(joints.get(JType.UPPER_L_LEG.ordinal()));
		joint.addChildren(joints.get(JType.UPPER_R_LEG.ordinal()));
		joint.addChildren(joints.get(JType.CHEST.ordinal()));
		joint = joints.get(JType.UPPER_L_ARM.ordinal());
		joint.increaseRotation(0.0f, 0.0f, 42.0f);
		joint.increasePosition(3.0f, 7.0f, 0.0f);			
		joint.addChildren(joints.get(JType.LOWER_L_ARM.ordinal()));
		joint = joints.get(JType.LOWER_L_ARM.ordinal());
		joint.increaseRotation(0.0f, 0.0f, 27.0f);
		joint.increasePosition(4.0f, 5.0f, 0.0f);			
		joint.addChildren(joints.get(JType.L_HAND.ordinal()));
		joint = joints.get(JType.L_HAND.ordinal());
		joint.increasePosition(4.0f, 3.7f, 0.0f);
		joint.setScale(new Vector3f(1.0f, 0.6f, 1.0f));
		joint = joints.get(JType.UPPER_R_ARM.ordinal());
		joint.increaseRotation(0.0f, 0.0f, -42.0f);
		joint.increasePosition(-3.0f, 7.0f, 0.0f);			
		joint.addChildren(joints.get(JType.LOWER_R_ARM.ordinal()));
		joint = joints.get(JType.LOWER_R_ARM.ordinal());
		joint.increaseRotation(0.0f, 0.0f, -27.0f);
		joint.increasePosition(-4.0f, 5.0f, 0.0f);			
		joint.addChildren(joints.get(JType.R_HAND.ordinal()));
		joint = joints.get(JType.R_HAND.ordinal());
		joint.increasePosition(-4.0f, 3.7f, 0.0f);
		joint.setScale(new Vector3f(1.0f, 0.6f, 1.0f));
		joint = joints.get(JType.UPPER_L_LEG.ordinal());
		joint.increasePosition(1.5f, 2.5f, 0.0f);	
		joint.addChildren(joints.get(JType.LOWER_L_LEG.ordinal()));
		joint = joints.get(JType.LOWER_L_LEG.ordinal());
		joint.increasePosition(1.5f, 0.5f, 0.0f);	
		joint.addChildren(joints.get(JType.L_FOOT.ordinal()));
		joint = joints.get(JType.L_FOOT.ordinal());
		joint.increasePosition(1.5f, 0.5f, 2.3f); 
		joint.increaseRotation(-90.0f, 0.0f, 0.0f);
		joint = joints.get(JType.UPPER_R_LEG.ordinal());
		joint.increasePosition(-1.5f, 2.5f, 0.0f);	
		joint.addChildren(joints.get(JType.LOWER_R_LEG.ordinal()));
		joint = joints.get(JType.LOWER_R_LEG.ordinal());
		joint.increasePosition(-1.5f, 0.5f, 0.0f);	
		joint.addChildren(joints.get(JType.R_FOOT.ordinal()));
		joint = joints.get(JType.R_FOOT.ordinal());
		joint.increasePosition(-1.5f, 0.5f, 2.3f);	
		joint.increaseRotation(-90.0f, 0.0f, 0.0f);
		for (int i = 0; i < JType.values().length; ++i) {
			joints.get(i).setUpdateChildren(true);
		}
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
		Joint joint = joints.get(JType.HIP.ordinal());
		joint.increasePosition(dx, (float)posY, dz);
		joint.increaseRotation(0, rotY, 0);
	}

	private void rotate(float x, float y, float z) {
		Joint joint = joints.get(jType.ordinal());
		joint.increaseRotation(x, y, z);
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

		if (Keyboard.isKeyDown(Keyboard.KEY_D) || (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD6) && jType == JType.HIP)) {
			this.currentTurnSpeed = -TURN_SPEED;
			return;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_A) || (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4) && jType == JType.HIP)) {
			this.currentTurnSpeed = TURN_SPEED;
			return;
		} else {
			this.currentTurnSpeed = 0;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		} else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD5) && !isKeyPressed){
			jType = jType.next();
			isKeyPressed = true;
			System.out.println("Joint changed to: " + jType.toString());
		} else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD8)){
			rotate(2.0f, 0.0f, 0.0f);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD2)){
			rotate(-2.0f, 0.0f, 0.0f);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4)){
			rotate(0.0f, 2.0f, 0.0f);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD6)){
			if (jType == JType.NECK) {
				System.out.println("X: " + joints.get(jType.ordinal()).getRotX() + "Y: " + joints.get(jType.ordinal()).getRotY() + "Z: " + joints.get(jType.ordinal()).getRotZ());
			}
			rotate(0.0f, -2.0f, 0.0f);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD9)){
			rotate(0.0f, -0.0f, 2.0f);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD3)){
			rotate(0.0f, 0.0f, -2.0f);
		} else if (!Keyboard.isKeyDown(Keyboard.KEY_NUMPAD5)) {
			isKeyPressed = false;
		}
	}

	public List<Joint> getJoints() {
		return joints;
	}

	public void setJoints(List<Joint> joints) {
		this.joints = joints;
	}
	
}
