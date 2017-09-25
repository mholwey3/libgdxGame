package com.test.game.desktop;

import java.awt.Toolkit;
import java.awt.Dimension;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.test.game.TestGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		// Get the application config (the viewport) and set its size
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		config.width = (int)screenSize.getWidth();
		config.height = (int)screenSize.getHeight();
		
		// Creates a new application using the TestGame class and the config we created above
		new LwjglApplication(new TestGame(), config);
	}
}
