package com.test.game.objects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Disposable;

public class GameObject extends ModelInstance implements Disposable{
	
	protected final btCollisionObject collisionObject;
	
	protected Vector3 position;
	
	public GameObject(Model model, btCollisionShape collisionShape, Vector3 position, int userValue){
		super(model, position);
		collisionObject = new btCollisionObject();
		collisionObject.setCollisionShape(collisionShape);
		collisionObject.setWorldTransform(transform);
		collisionObject.setCollisionFlags(collisionObject.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
		collisionObject.setUserValue(userValue);
	}

	public btCollisionObject getCollisionObject() {
		return collisionObject;
	}
	

	public Vector3 getPosition() {
		return position;
	}
	

	public void setPosition(Vector3 position) {
		this.position = position;
	}
	

	@Override
	public void dispose() {
		collisionObject.dispose();
	}

}
