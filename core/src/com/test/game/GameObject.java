package com.test.game;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Disposable;

public class GameObject extends ModelInstance implements Disposable{
	
	public final btCollisionObject collisionObject;
	
	public Vector3 position;
	
	public GameObject(Model model, btCollisionShape collisionShape, Vector3 position){
		super(model, position);
		collisionObject = new btCollisionObject();
		collisionObject.setCollisionShape(collisionShape);
		collisionObject.setWorldTransform(transform);
	}

	@Override
	public void dispose() {
		collisionObject.dispose();
	}

}
