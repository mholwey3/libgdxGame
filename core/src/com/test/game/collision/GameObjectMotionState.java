package com.test.game.collision;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

public class GameObjectMotionState extends btMotionState {
	Matrix4 transform;
	
    public Matrix4 getTransform() {
		return transform;
	}
    
	public void setTransform(Matrix4 transform) {
		this.transform = transform;
	}
	
	@Override
    public void getWorldTransform (Matrix4 worldTrans) {
        worldTrans.set(transform);
    }
    @Override
    public void setWorldTransform (Matrix4 worldTrans) {
        transform.set(worldTrans);
    }
}