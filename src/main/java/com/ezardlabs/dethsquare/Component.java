package com.ezardlabs.dethsquare;

import com.ezardlabs.dethsquare.Collider.Collision;

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

    /**
     * Called when this {@link Component} is first created
     */
	public void start() {
	}

    /**
     * Called when this {@link Component} is destroyed
     */
    protected void destroy() {
    }

	/**
	 * Called when the {@link Collider} other enters the trigger
	 *
	 * @param other The other {@link Collider} involved in the collision
	 */
	public void onTriggerEnter(Collider other) {
	}

	/**
	 * Called when this {@link Component}'s {@link Collider} collides with another {@link Collider}
	 * @param collision the {@link Collision} data associated with this collision
	 */
	public void onCollision(Collision collision) {
	}
}
