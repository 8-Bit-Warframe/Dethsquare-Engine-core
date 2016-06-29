package com.ezardlabs.dethsquare;

import java.util.HashMap;

public class LevelManager {
	private static HashMap<String, Level> levels = new HashMap<>();

	public static void registerLevel(String name, Level level) {
		levels.put(name, level);
	}

	public static void loadLevel(String name) {
		Level level = levels.get(name);
		level.onLoad();

		GameObject.startAll();
		Renderer.init();
		Collider.init();
	}
}
