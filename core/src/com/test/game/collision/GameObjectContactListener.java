package com.test.game.collision;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;

public class GameObjectContactListener extends ContactListener {
	@Override
	public boolean onContactAdded(int userValue0, int partId0, int index0, int userValue1, int partId1, int index1) {
		System.out.println("COLLISION");
		return true;
	}
}
