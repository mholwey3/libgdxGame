package com.test.game.objects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public class Block extends GameObject{
	
	public static final int SIDE_LENGTH = 1;
	
	public Block(Model model, btCollisionShape collisionShape, Vector3 pos, int userValue) {
		super(model, collisionShape, pos, userValue);
		position = pos;
	}

	public Model getModel() {
		return model;
	}

	public void dispose() {
		collisionObject.dispose();
	}
	
}
