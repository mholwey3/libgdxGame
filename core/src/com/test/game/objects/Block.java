package com.test.game.objects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public class Block extends GameObject{
	
	private Vector3 position;
	public btCollisionObject collisionObject;
	
	public static final int SIDE_LENGTH = 1;
	
	public Block(Model model, btCollisionShape collisionShape, Vector3 pos) {
		super(model, collisionShape, pos);
		position = pos;
		
		collisionObject = new btCollisionObject();
		collisionObject.setCollisionShape(collisionShape);
	}

	public Model getModel() {
		return model;
	}

	public Vector3 getPosition() {
		return position;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}
	
	public btCollisionObject getCollisionObject() {
		return collisionObject;
	}

	public void setCollisionObject(btCollisionObject collisionObject) {
		this.collisionObject = collisionObject;
	}

	public void dispose() {
		collisionObject.dispose();
	}
	
}
