package com.test.game.objects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.test.game.Gameplay;

public class Player extends GameObject {
	
	private Quaternion rotation;
	private Vector3 velocity;

	private static final int ROTATE_CLOCKWISE = -1;
	private static final int ROTATE_COUNTER_CLOCKWISE = 1;
	private static final float WIDTH = 0.75f;
	private static final float HEIGHT = 1f;
	private static final float DEPTH = 0.75f;
	
	private final float MAX_MOVEMENT_SPEED = 10.0f;
	private final float ACCELERATION = 10.0f;
	private final float DECELERATION = 5.0f;
	private final float ROTATION_SPEED = 200f;
	
	public Player(Model model, btCollisionShape collisionShape, Vector3 pos, float mass) {
		super(model, collisionShape, pos, mass);
		rotation = new Quaternion();
		velocity = Vector3.Zero;
	}
	
	public Quaternion getRotation() {
		return rotation;
	}
	
	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
	}
	
	public Vector3 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector3 velocity) {
		this.velocity = velocity;
	}

	public static int getROTATE_CLOCKWISE() {
		return ROTATE_CLOCKWISE;
	}

	public static int getROTATE_COUNTER_CLOCKWISE() {
		return ROTATE_COUNTER_CLOCKWISE;
	}

	public static float getWidth() {
		return WIDTH;
	}

	public static float getHeight() {
		return HEIGHT;
	}

	public static float getDepth() {
		return DEPTH;
	}

	public float getMAX_MOVEMENT_SPEED() {
		return MAX_MOVEMENT_SPEED;
	}

	public float getACCELERATION() {
		return ACCELERATION;
	}

	public float getDECELERATION() {
		return DECELERATION;
	}

	public float getROTATION_SPEED() {
		return ROTATION_SPEED;
	}

	public void accelerate(float delta) {
		Vector3 direction = new Vector3();
		direction.x = (float) Math.cos(rotation.getAngleAroundRad(Vector3.Z) + (Math.PI / 2));
		direction.y = (float) Math.sin(rotation.getAngleAroundRad(Vector3.Z) + (Math.PI / 2));
		direction.nor();
		float addX = direction.x * ACCELERATION * delta;
		float addY = direction.y * ACCELERATION * delta;
		velocity.add(addX, addY, 0f);
	}
	
	public void decelerate(float delta) {
		Vector3 normalizeVelocity = velocity.cpy().nor();
		float subX = normalizeVelocity.x * DECELERATION * delta;
		float subY = normalizeVelocity.y * DECELERATION * delta;
		velocity.sub(subX, subY, 0f);
	}
	
	/**
	 * Moves the player forward according to the direction they are facing
	 * NOTE: Quaternion.getAngleAroundRad() gets the angle based on the unit circle - pi/2
	 * So we need to compensate for this when we are setting our x and y directional vector components
	 * @param delta
	 */
	public void move(float delta) {
		velocity.clamp(0f, MAX_MOVEMENT_SPEED);
		float x = velocity.x * delta;
		float y = velocity.y * delta;
		transform.trn(x, y, 0f);
		transform.getTranslation(position);
		rigidBody.setWorldTransform(transform);
		Gameplay.dynamicsWorld.stepSimulation(delta, 5, 1f/60f);
		//System.out.println("position: " + position);
	}
	
	/**
	 * Rotates the player left or right around the Z axis
	 * @param delta
	 * @param clockwise
	 */
	public void rotate(float delta, int clockwise) {
		transform.rotate(Vector3.Z, clockwise * ROTATION_SPEED * delta);
		transform.getRotation(rotation);
		rigidBody.setWorldTransform(transform);
		Gameplay.dynamicsWorld.stepSimulation(delta, 5, 1f/60f);
		//System.out.println("rotation: " + ((int)(rotation.getAngleAround(Vector3.Z) + 90)) % 360);
	}
}
