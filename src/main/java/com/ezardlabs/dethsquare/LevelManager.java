package com.ezardlabs.dethsquare;

import java.util.HashMap;

public class LevelManager {
	private static HashMap<String, Level> levels = new HashMap<>();
	private static String currentLevelName;
	private static boolean loadingLevel = false;

	public static void registerLevel(String name, Level level) {
		levels.put(name, level);
	}

	public static void loadLevel(String name) {
		if (loadingLevel) {
			throw new Error("Cannot load a level whilst another level is being loaded");
		}
		loadingLevel = true;
		currentLevelName = name;

		GameObject.destroyAll();

		Level level = levels.get(name);
		level.onLoad();

		GameObject.startAll();
		Renderer.init();
		Collider.init();
		loadingLevel = false;
	}

	public static Level getCurrentLevel() {
		if (currentLevelName == null) return null;
		else return levels.get(currentLevelName);
	}

	public static String getCurrentLevelName() {
		return currentLevelName;
	}
}
