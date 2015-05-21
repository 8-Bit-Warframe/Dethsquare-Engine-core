package com.ezardlabs.dethsquare;

import com.ezardlabs.dethsquare.Collider.CollisionLocation;

/**
 * Base class for everything attached to {@link GameObject GameObjects}
 */
public class Component {
	/**
	 * The {@link GameObject} this component is attached to. A component is always attached to a
	 * {@link GameObject}
	 */
	public GameObject gameObject;
	/**
	 * The {@link Transform} attached to the {@link GameObject} that this {@link Component} is also
	 * attached to (null if there is none attached)
	 */
	public Transform transform;

	public void start() {
	}

	public void onTriggerEnter(Collider other) {
	}

	public void onCollision(Collider other, CollisionLocation collisionLocation) {
	}

	public <T extends Component> T getComponent(Class<T> type) {
		return gameObject.getComponent(type);
	}
}
