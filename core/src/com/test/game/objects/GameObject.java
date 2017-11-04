package com.test.game.objects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import com.test.game.collision.GameObjectMotionState;

public class GameObject extends ModelInstance implements Disposable{
	
	protected final Model model;
	protected final btCollisionShape collisionShape;
	protected final btRigidBody rigidBody;	
	protected Vector3 position;
	protected final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
	protected static Vector3 localInertia = new Vector3();
	
	protected final GameObjectMotionState motionState;
	
	public GameObject(Model model, btCollisionShape collisionShape, Vector3 position, float mass){
		super(model, position);
		this.model = model;
		this.collisionShape = collisionShape;
		this.position = position;
		if(mass > 0f) {
			collisionShape.calculateLocalInertia(mass, localInertia);
		} else {
			localInertia.set(0, 0, 0);
		}
		constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, collisionShape, localInertia);
		
		motionState = new GameObjectMotionState();
		motionState.setTransform(transform);
		rigidBody = new btRigidBody(constructionInfo);
		rigidBody.setMotionState(motionState);
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
		collisionShape.dispose();
		rigidBody.dispose();
		constructionInfo.dispose();
		motionState.dispose();
	}
}
