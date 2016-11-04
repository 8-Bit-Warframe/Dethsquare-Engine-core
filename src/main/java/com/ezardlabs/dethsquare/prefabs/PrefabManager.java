package com.ezardlabs.dethsquare.prefabs;

import com.ezardlabs.dethsquare.GameObject;

import java.util.HashMap;

public class PrefabManager {
	private static HashMap<String, PrefabCreator> prefabs = new HashMap<>();

	public static void addPrefab(String name, PrefabCreator prefab) {
		prefabs.put(name, prefab);
	}

	public static GameObject loadPrefab(String name) {
		return prefabs.get(name).create();
	}

	public static void removePrefab(String name, PrefabCreator prefab) {
		prefabs.remove(name);
	}
}
