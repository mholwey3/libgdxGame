package com.test.game.objects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;

public class GameObject extends ModelInstance implements Disposable{
	
	protected final btRigidBody rigidBody;	
	protected Vector3 position;
	protected final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
	protected static Vector3 localInertia = new Vector3();
	
	public GameObject(Model model, btCollisionShape collisionShape, Vector3 position, float mass){
		super(model, position);
		if(mass > 0f) {
			collisionShape.calculateLocalInertia(mass, localInertia);
		} else {
			localInertia.set(0, 0, 0);
		}
		this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, collisionShape, localInertia);
		rigidBody = new btRigidBody(constructionInfo);
	}

	public btRigidBody getRigidBody() {
		return rigidBody;
	}
	

	public Vector3 getPosition() {
		return position;
	}
	

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	@Override
	public void dispose() {
		rigidBody.dispose();
		constructionInfo.dispose();
	}

}
