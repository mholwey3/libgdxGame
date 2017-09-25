package com.test.game;

import com.badlogic.gdx.Game;
import com.test.game.screens.GameplayScreen;

public class TestGame extends Game {
	@Override
	public void create () {
		setScreen(new GameplayScreen(this));
	}
}
