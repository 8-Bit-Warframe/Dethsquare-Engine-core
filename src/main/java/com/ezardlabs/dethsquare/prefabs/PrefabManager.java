package com.ezardlabs.dethsquare.prefabs;

import com.ezardlabs.dethsquare.GameObject;

import java.util.HashMap;

public class PrefabManager {
	private static HashMap<String, PrefabCreator> prefabs = new HashMap<>();


	/**
	 * Register a prefabricated {@link GameObject}
	 *
	 * @param name   The name to identify the prefab with
	 * @param prefab The {@link PrefabCreator} used to create the GameObject
	 */
	public static void addPrefab(String name, PrefabCreator prefab) {
		if (name.endsWith("_other")) {
			throw new IllegalArgumentException("Prefab name cannot end with \"_other\"");
		} else {
			prefabs.put(name, prefab);
		}
	}

	/**
	 * Register a prefabricated {@link GameObject} that can be instantiated across clients
	 *
	 * @param name        The name to identify the prefab with
	 * @param prefab      The {@link PrefabCreator} used to create the GameObject
	 * @param otherPrefab An alternate {@link PrefabCreator} to be used for instantiation on
	 *                    every client except the caller - for example, you could have a
	 *                    prefab for a player object that has a script that accepts input on
	 *                    one client, but not on other clients. Can be null, in which case the
	 *                    same {@link PrefabCreator} will be used on all clients
	 */
	public static void addPrefab(String name, PrefabCreator prefab, PrefabCreator otherPrefab) {
		if (name.endsWith("_other")) {
			throw new IllegalArgumentException("Prefab name cannot end with '_other'");
		} else {
			prefabs.put(name, prefab);
			prefabs.put(name + "_other", otherPrefab);
		}
	}

	/**
	 * Check whether or not a prefab exists
	 *
	 * @param name The name of the prefab
	 * @return Whether or not a prefab with the given name exists
	 */
	public static boolean prefabExists(String name) {
		return prefabs.containsKey(name);
	}

	/**
	 * Load the prefab with the given name
	 *
	 * @param name The name of the prefab to load
	 * @return The {@link GameObject} prefab with the given name
	 */
	public static GameObject loadPrefab(String name) {
		PrefabCreator prefab = prefabs.get(name);
		if (prefab == null) {
			throw new PrefabNotFoundException("Prefab '" + name + "' not found");
		} else {
			return prefab.create();
		}
	}

	/**
	 * Unregisters the prefab with the given name
	 *
	 * @param name The name of the prefab to unregister
	 */
	public static void removePrefab(String name) {
		prefabs.remove(name);
	}

	private static class PrefabNotFoundException extends RuntimeException {

		private PrefabNotFoundException(String prefabName) {
			super("Prefab '" + prefabName + "' not found");
		}
	}
}
