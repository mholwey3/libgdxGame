package com.test.game.objects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public class Player extends GameObject {
	public static final int ROTATE_CLOCKWISE = -1;
	public static final int ROTATE_COUNTER_CLOCKWISE = 1;
	
	private Quaternion rotation;
	private float acceleration;
	private float movementSpeed;
	private float rotationSpeed;

	private final float MAX_MOVEMENT_SPEED = 10.0f;
	public static final float DIAMETER = 0.75f;
	
	public Player(Model model, btCollisionShape collisionShape, Vector3 startPos, int userValue) {
		super(model, collisionShape, startPos, userValue);
		position = startPos;
		rotation = new Quaternion();
		
		acceleration = 5f;
		movementSpeed = 0f;
		rotationSpeed = 150f;
	}
	
	// Begin Getters and Setters
	
	public Quaternion getRotation() {
		return rotation;
	}
	
	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
	}

	public float getAcceleration() {
		return acceleration;
	}
	
	public void setAcceleration(float acceleration) {
		this.acceleration = acceleration;
	}
	
	public float getMovementSpeed() {
		return movementSpeed;
	}
	
	public void setMovementSpeed(float movementSpeed) {
		this.movementSpeed = movementSpeed;
	}
	
	public float getRotationSpeed() {
		return rotationSpeed;
	}
	
	public void setRotationSpeed(float rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}
	// End Getters and Setters
	
	public void accelerate(float delta) {
		if(movementSpeed < MAX_MOVEMENT_SPEED) {
			movementSpeed += acceleration * delta;
		} else {
			movementSpeed = MAX_MOVEMENT_SPEED;
		}
	}
	
	public void decelerate(float delta) {
		if(movementSpeed > 0f) {
			movementSpeed -= delta;
		} else {
			movementSpeed = 0f;
		}
	}
	
	/**
	 * Moves the player forward according to the direction they are facing
	 * NOTE: Quaternion.getAngleAroundRad() gets the angle based on the unit circle - pi/2
	 * So we need to compensate for this when we are setting our x and y directional vector components
	 * @param delta
	 */
	public void move(float delta) {
		Vector3 direction = new Vector3();
		direction.x = (float) Math.cos(rotation.getAngleAroundRad(Vector3.Z) + (Math.PI / 2));
		direction.y = (float) Math.sin(rotation.getAngleAroundRad(Vector3.Z) + (Math.PI / 2));
		float x = direction.x * movementSpeed * delta;
		float y = direction.y * movementSpeed * delta;
		transform.trn(x, y, 0f);
		collisionObject.setWorldTransform(transform);
		transform.getTranslation(position);
		//System.out.println("position: " + position);
	}
	
	/**
	 * Rotates the player left or right around the Z axis
	 * @param delta
	 * @param clockwise
	 */
	public void rotate(float delta, int clockwise) {
		transform.rotate(Vector3.Z, clockwise * rotationSpeed * delta);
		transform.getRotation(rotation);
		//System.out.println("rotation: " + ((int)(rotation.getAngleAround(Vector3.Z) + 90)) % 360);
	}
	
	public void dispose() {
		collisionObject.dispose();
	}
}
