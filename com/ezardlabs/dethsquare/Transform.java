package com.ezardlabs.dethsquare;

/**
 * {@link Component} that contains the position of a {@link GameObject}
 */
public final class Transform extends Component {
	public Vector2 position = new Vector2();

	/**
	 * Move the {@link GameObject} that this {@link Transform} is attached to. If the {@link GameObject} has a {@link Collider} attached, then the physics
	 *
	 * @param x
	 * @param y
	 */
	public void translate(float x, float y) {
		if (gameObject.isStatic) throw new Error("Static objects cannot be moved");
		if (x == 0 && y == 0) return;
		if (position.x + x < 0) x = -position.x;
		if (position.y + y < 0) y = -position.y;
		if (gameObject.collider != null) {
			gameObject.collider.move(x, y);
		} else {
			position.x += x;
			position.y += y;
		}
	}
}
