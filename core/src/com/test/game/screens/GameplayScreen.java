package com.test.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.test.game.Map;
import com.test.game.Gameplay;

public class GameplayScreen extends GenericScreen{
	
	private Map map;
	private Gameplay gameplay;

	public GameplayScreen(Game game) {
		super(game);
	}
	
	@Override
	public void show() {
		map = new Map("./maps/testLevelPixmap.png");
		gameplay = new Gameplay(map);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        
		gameplay.render(delta);
	}
	
	@Override
	public void dispose() {
		gameplay.dispose();
	}
}
