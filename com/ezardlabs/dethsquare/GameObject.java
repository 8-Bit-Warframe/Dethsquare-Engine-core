package com.ezardlabs.dethsquare;

import com.ezardlabs.dethsquare.Collider.Collision;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Base class for all entities in the game world
 */
public final class GameObject {
	/**
	 * List of all {@link GameObject GameObjects} to be instantiated at the end of the current frame
	 */
	private static final ArrayList<GameObject> newObjects = new ArrayList<>();
	/**
	 * List of all {@link GameObject GameObjects} to be destroyed at the end of the current frame
	 */
	private static final ArrayList<GameObject> destroyedObjects = new ArrayList<>();
	/**
	 * List of all {@link GameObject GameObjects} currently in the game world
	 */
	private static final ArrayList<GameObject> objects = new ArrayList<>();
	/**
	 * List of all {@link Script Scripts} (interactive {@link Component Components}) in the
	 * game world
	 */
	private static final ArrayList<Script> scripts = new ArrayList<>();

	private static final HashMap<String, ArrayList<GameObject>> tags = new HashMap<>();

	private static final ArrayList<GameObject> objectsWithChangedComponents = new ArrayList<>();

	private static boolean startAllCalled = false;
	/**
	 * The name of the {@link GameObject}
	 */
	public String name;

	private String tag;

	private boolean initialised = false;
	/**
	 * Whether or not the {@link GameObject} is static, i.e. whether or not it will ever move.
	 * Static {@link GameObject GameObjects} can have their rendering and collision optimised
	 */
	public final boolean isStatic;
	/**
	 * List of {@link Component Components} currently attached to this object
	 */
	private final ArrayList<Component> components = new ArrayList<>();

	private final ArrayList<Component> newComponents = new ArrayList<>();
	private final ArrayList<Class<?>> removedComponents = new ArrayList<>();
	/**
	 * Fast access to this {@link GameObject}'s {@link Transform} component
	 */
	public Transform transform = new Transform();
	/**
	 * Fast access to this {@link GameObject}'s {@link Animator} component
	 */
	public Animator animator;
	/**
	 * Fast access to this {@link GameObject}'s {@link Renderer} component
	 */
	public Renderer renderer;
	/**
	 * Fast access to this {@link GameObject}'s {@link Collider} component
	 */
	public Collider collider;
	/**
	 * Fast access to this {@link GameObject}'s {@link Rigidbody} component
	 */
	public Rigidbody rigidbody;

	public GameObject() {
		this(null);
	}

	public GameObject(String name, Component... components) {
		this(name, false, components);
	}

	public GameObject(String name, boolean isStatic, Component... components) {
		this.name = name;
		this.isStatic = isStatic;
		transform.gameObject = this;
		this.components.add(transform);
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
//		for (Component c : components) {
//			if (c.getClass().equals(component.getClass())) {
//				if (c instanceof Script) {
//					scripts.remove(c);
//				}
//				components.remove(c);
//				break;
//			}
//		}
//		component.gameObject = this;
//		component.transform = transform;
//		components.add(component);
//		if (!startAllCalled) {
//			if (component instanceof Script) {
//				scripts.add((Script) component);
//			}
//			if (component instanceof Transform) {
//				transform = (Transform) component;
//			} else if (component instanceof Renderer) {
//				renderer = (Renderer) component;
//			} else if (component instanceof Animator) {
//				animator = (Animator) component;
//			} else if (component instanceof Collider) {
//				collider = (Collider) component;
//			} else if (component instanceof Rigidbody) {
//				rigidbody = (Rigidbody) component;
//			}
//		}
		newComponents.add(component);
		objectsWithChangedComponents.add(this);
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

	public <T extends Component> Component getComponentOfType(Class<T> type) {
		for (Component c : components) {
			if (type.isAssignableFrom(c.getClass())) {
				return c;
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
		removedComponents.add(component);
		objectsWithChangedComponents.add(this);
		for (Component c : components) {
			if (c.getClass().equals(component)) {
//				if (c instanceof Script) {
//					scripts.remove(c);
//				}
//				if (c instanceof Transform) {
//					transform = null;
//				} else if (c instanceof Renderer) {
//					renderer = null;
//				} else if (c instanceof Animator) {
//					animator = null;
//				} else if (c instanceof Collider) {
//					collider = null;
//				} else if (c instanceof Rigidbody) {
//					rigidbody = null;
//				}
//				components.remove(c);
				//noinspection unchecked
				return (T) c;
			}
		}
		return null;
	}

	public void setTag(String tag) {
		if (tag == null) {
			if (this.tag != null) {
				tags.get(this.tag).remove(this);
				if (tags.get(this.tag).size() == 0) {
					tags.remove(this.tag);
				}
			}
		} else {
			if (this.tag == null) {
				if (!tags.containsKey(tag)) {
					tags.put(tag, new ArrayList<GameObject>());
				}
				tags.get(tag).add(this);
			} else {
				tags.get(this.tag).remove(this);
				if (tags.get(this.tag).size() == 0) {
					tags.remove(this.tag);
				}
				if (!tags.containsKey(tag)) {
					tags.put(tag, new ArrayList<GameObject>());
				}
				tags.get(tag).add(this);
			}
		}
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	void onTriggerEnter(Collider collider) {
		for (Component component : components) {
			component.onTriggerEnter(collider);
		}
	}

	void onCollision(Collider other, Collision collision) {
		for (int i = 0; i < components.size(); i++) {
			components.get(i).onCollision(other, collision);
		}
	}

	public static GameObject instantiate(GameObject gameObject, Vector2 position) {
		gameObject.transform.position.set(position.x, position.y);
		newObjects.add(gameObject);
		return gameObject;
	}

	public static void destroy(GameObject gameObject) {
		destroyedObjects.add(gameObject);
	}

	public static void destroy(final GameObject gameObject, final long delay) {
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException ignored) {
				}
				destroyedObjects.add(gameObject);
			}
		}.start();
	}

	public static void startAll() {
		handleCreationDestruction();
//		objects.addAll(newObjects);
//		newObjects.clear();
//		for (GameObject go : objects) {
//			for (Component component : go.components) {
//				component.start();
//			}
//		}
//		startAllCalled = true;
	}

	public static void updateAll() {
		handleCreationDestruction();

		for (Script s : scripts) {
			s.update();
		}
	}

	public static GameObject[] findAllWithTag(String tag) {
		if (tags.containsKey(tag)) {
			return tags.get(tag).toArray(new GameObject[tags.get(tag).size()]);
		} else {
			return new GameObject[0];
		}
	}

	private static void handleCreationDestruction() {
		for (GameObject gameObject : destroyedObjects) {
			for (Component c : gameObject.components) {
				c.destroy();
				if (c instanceof Script) {
					scripts.remove(c);
				}
			}
		}
		objects.removeAll(destroyedObjects);
		destroyedObjects.clear();
		objects.addAll(newObjects);

		ArrayList<Component> temp = new ArrayList<>();

		for (GameObject go : objectsWithChangedComponents) {
			for (Class clazz : go.removedComponents) {
				for (Component c : go.components.toArray(new Component[go.components.size()])) {
					if (c.getClass().equals(clazz)) {
						if (c instanceof Script) {
							scripts.remove(c);
						}
						if (c instanceof Renderer) {
							go.renderer = null;
						} else if (c instanceof Animator) {
							go.animator = null;
						} else if (c instanceof Collider) {
							go.collider = null;
						} else if (c instanceof Rigidbody) {
							go.rigidbody = null;
						}
						go.components.remove(c);
					}
				}
			}
			go.removedComponents.clear();

			for (Component component : go.newComponents) {
				if (component instanceof Script) {
					scripts.add((Script) component);
				}
				if (component instanceof Transform) {
					go.transform = (Transform) component;
				} else if (component instanceof Renderer) {
					go.renderer = (Renderer) component;
				} else if (component instanceof Animator) {
					go.animator = (Animator) component;
				} else if (component instanceof Collider) {
					go.collider = (Collider) component;
				} else if (component instanceof Rigidbody) {
					go.rigidbody = (Rigidbody) component;
				}
				component.gameObject = go;
				component.transform = go.transform;
				go.components.add(component);
				temp.add(component);
			}
			go.newComponents.clear();
		}

		objectsWithChangedComponents.clear();

		for (Component component : temp) {
			component.start();
		}

		newObjects.clear();
	}
}