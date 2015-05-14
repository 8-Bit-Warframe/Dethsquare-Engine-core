package com.ezardlabs.dethsquare;

/**
 * {@link Component} that contains the position of a {@link GameObject}
 */
public class Transform extends Component {
	public Vector2 position = new Vector2();

	public void translate(float x, float y) {
		if (gameObject.isStatic) throw new Error("Static objects cannot be moved");
		if (gameObject.collider != null) {
			gameObject.collider.move(x, y);
		} else {
			position.x += x;
			position.y += y;
		}
	}
}
