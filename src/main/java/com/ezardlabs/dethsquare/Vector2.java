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
	 * Creates a new vector that has the same x and y values as the source vector
	 */
	public Vector2(Vector2 source) {
		this.x = source.x;
		this.y = source.y;
	}

	/**
	 * Sets the x and y components of the vector
	 *
	 * @param x the new x component
	 * @param y the new y component
	 */
	public void set(float x, float y) {
		if (listener != null) listener.onVector2Changed(x - this.x, y - this.y);
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns a new {@link Vector2} that has been offset by the given parameters
	 * @param x The amount to offset the x coordinate by
	 * @param y The amount to offset the y coordinate by
	 * @return The offset {@link Vector2}
	 */
	public Vector2 offset(float x, float y) {
		return new Vector2(this.x + x, this.y + y);
	}

	/**
	 * Calculates the distance between 2 vector points
	 * @param a the first point
	 * @param b the second point
	 * @return The distance between the 2 points
	 */
	public static double distance(Vector2 a, Vector2 b) {
		return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
	}

	@Override
	public String toString() {
		return "Vector2(" + x + ", " + y + ")";
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Vector2 && ((Vector2) o).x == x && ((Vector2) o).y == y;
	}

	private Vector2ChangeListener listener;

	void setVector2ChangeListener(Vector2ChangeListener listener) {
		this.listener = listener;
	}

	interface Vector2ChangeListener {
		void onVector2Changed(float xDiff, float yDiff);
	}
}