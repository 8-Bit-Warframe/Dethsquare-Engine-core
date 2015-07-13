package com.ezardlabs.dethsquare;

/**
 * Representation of 2D vectors and points
 */
public final class Vector2 {
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

	/**
	 * Sets the x and y components of the vector
	 *
	 * @param x the new x component
	 * @param y the new y component
	 */
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public static double distance(Vector2 a, Vector2 b) {
		return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
	}

	public static double distance(Vector2 vector, float x, float y) {
		return Math.sqrt(Math.pow(x - vector.x, 2) + Math.pow(y - vector.y, 2));
	}

	@Override
	public String toString() {
		return "Vector2(" + x + ", " + y + ")";
	}
}