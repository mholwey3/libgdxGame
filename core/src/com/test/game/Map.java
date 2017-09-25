package com.test.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;

public class Map {
	
	static final int EMPTY = 0;
	static final int SPAWN = 0x22B14C;
	static final int BLOCK = 0xFFFFFF;
	
	private int[][] blocks;
	
	public int[][] getBlocks() {
		return blocks;
	}

	public void setBlocks(int[][] tiles) {
		this.blocks = tiles;
	}

	public Map(String mapPath) {
		loadMap(mapPath);
	}
	
	private void loadMap(String mapPath) {
		Pixmap pixmap = new Pixmap(Gdx.files.internal(mapPath));
		blocks = new int[pixmap.getWidth()][pixmap.getHeight()];
		
		for(int y = 0; y < pixmap.getHeight(); y++) {
			for(int x = 0; x < pixmap.getWidth(); x++) {
				int pixel = (pixmap.getPixel(x, y) >>> 8) & 0xFFFFFF;
				if (pixel == SPAWN || pixel == BLOCK) {
					blocks[x][y] = pixel;
				}
			}
		}
	}
}
