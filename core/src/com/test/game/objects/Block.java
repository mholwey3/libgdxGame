package com.test.game.objects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public class Block extends GameObject{
	
	public static final float SIDE_LENGTH = 1f;
	
	public Block(Model model, btCollisionShape collisionShape, Vector3 pos, float mass) {
		super(model, collisionShape, pos, mass);
		position = pos;
	}

	public Model getModel() {
		return model;
	}
}
