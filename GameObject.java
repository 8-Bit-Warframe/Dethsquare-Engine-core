package com.ezardlabs.dethsquare;

import java.util.ArrayList;

/**
 * Base class for all entities in the game world
 */
public class GameObject {
	/**
	 * List of all {@link GameObject GameObjects} to be instantiated at the end of the current frame
	 */
	private static ArrayList<GameObject> newObjects = new ArrayList<>();
	/**
	 * List of all {@link GameObject GameObjects} to be destroyed at the end of the current frame
	 */
	private static ArrayList<GameObject> destroyedObjects = new ArrayList<>();
	/**
	 * List of all {@link GameObject GameObjects} currently in the game world
	 */
	private static ArrayList<GameObject> objects = new ArrayList<>();
	/**
	 * List of all {@link Script Scripts} (interactive {@link Component Components}) in the
	 * game world
	 */
	private static ArrayList<Script> scripts = new ArrayList<>();

	/**
	 * The name of the {@link GameObject}
	 */
	public String name;
	/**
	 * Whether or not the {@link GameObject} is static, i.e. whether or not it will ever move.
	 * Static {@link GameObject GameObjects} can have their rendering and collision optimised
	 */
	public final boolean isStatic;
	/**
	 * List of {@link Component Components} currently attached to this object
	 */
	private ArrayList<Component> components = new ArrayList<>();
	/**
	 * Fast access to this {@link GameObject}'s {@link Transform} component
	 */
	public Transform transform;
	/**
	 * Fast access to this {@link GameObject}'s {@link Collider} component
	 */
	public Collider collider;

	public GameObject() {
		this(null);
	}

	public GameObject(String name, Component... components) {
		this(name, false, components);
	}

	public GameObject(String name, boolean isStatic, Component... components) {
		this.name = name;
		this.isStatic = isStatic;
		addComponent(new Transform());
		for (Component c : components) {
			addComponent(c);
		}
	}

	/**
	 * Attaches a {@link Component} to this {@link GameObject}. If a {@link Component} of the same
	 * type is already present, then it is automatically replaced
	 *
	 * @param component The {@link Component} to attach to this {@link GameObject}
	 * @param <T>       The type of the {@link Component}
	 * @return The {@link Component} that has just been attached
	 */
	public <T extends Component> T addComponent(T component) {
		for (Component c : components) {
			if (c.getClass().equals(component.getClass())) {
				if (c instanceof Script) {
					scripts.remove(c);
				}
				components.remove(c);
				break;
			}
		}
		component.gameObject = this;
		component.transform = transform;
		components.add(component);
		if (component instanceof Script) {
			scripts.add((Script) component);
		} else if (component instanceof Transform) {
			transform = (Transform) component;
		} else if (component instanceof Collider) {
			collider = (Collider) component;
		}
		return component;
	}

	/**
	 * Returns the {@link Component} of the given type if the {@link GameObject} has one
	 * attached, null if it doesn't
	 *
	 * @param type The type of {@link Component} to retrieve
	 */
	public <T extends Component> T getComponent(Class<T> type) {
		for (Component c : components) {
			if (c.getClass().equals(type)) {
				return type.cast(c);
			}
		}
		return null;
	}

	/**
	 * Removes the {@link Component} of the given type from this {@link GameObject} if it exists.
	 * If it doesn't exist then nothing happens
	 *
	 * @param component The type of {@link Component} to remove from this {@link GameObject}
	 * @param <T>
	 * @return The removed {@link Component} if it existed; otherwise null
	 */
	public <T extends Component> T removeComponent(Class<T> component) {
		if (component.equals(Transform.class)) {
			throw new Error("Cannot remove the Transform component of a GameObject");
		}
		for (Component c : components) {
			if (c.getClass().equals(component)) {
				if (c instanceof Script) {
					scripts.remove(c);
				}
				components.remove(c);
				break;
			}
		}
		return null;
	}

	void onTriggerEnter(Collider collider) {
		for (Component component : components) {
			component.onTriggerEnter(collider);
		}
	}

	public static GameObject instantiate(GameObject gameObject, Vector2 position) {
		gameObject.transform.position.set(position.x, position.y);
		newObjects.add(gameObject);
		return gameObject;
	}

	public static void startAll() {
		updateAll();
		for (GameObject gameObject : objects) {
			for (Component component : gameObject.components) {
				component.start();
			}
		}
	}

	public static void updateAll() {
		objects.addAll(newObjects);
		newObjects.clear();
		for (Script s : scripts) {
			s.update();
		}
	}
}
