package com.ezardlabs.dethsquare;

/**
 * Representation of 2D vectors and points
 */
public class Vector2 {
	public float x;
	public float y;

	/**
	 * Shorthand for writing Vector2(0, 0)
	 */
	public Vector2() {

	}

	/**
	 * Creates a new vector with the given x and y components
	 */
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void set(float x, float y) {
		this.x = x ;
		this.y = y;
	}
}