package com.ezardlabs.dethsquare;

import java.util.ArrayList;

/**
 * {@link Component} that contains the position of a {@link GameObject}
 */
public final class Transform extends Component {
	public Vector2 position = new Vector2();

	private Transform parent;
	private ArrayList<Transform> children = new ArrayList<>();

	Transform() {
		position.setVector2ChangeListener((xDiff, yDiff) -> {
			for (int i = 0; i < children.size(); i++) {
				children.get(i).position.x += xDiff;
				children.get(i).position.y += yDiff;
			}
		});
	}

	public void setParent(Transform parent) {
		setParent(parent, true);
	}

	private void setParent(Transform parent, boolean addChild) {
		this.parent = parent;
		if (addChild) parent.addChild(this, false);
	}

	public Transform getParent() {
		return parent;
	}

	public void addChild(Transform child) {
		addChild(this, true);
	}

	private void addChild(Transform child, boolean setParent) {
		children.add(child);
		if (setParent) child.setParent(this, false);
	}

	public Transform getChild(int index) {
		return children.get(index);
	}

	/**
	 * Move the {@link GameObject} that this {@link Transform} is attached to. If the {@link GameObject} has a {@link Collider} attached, then the physics
	 *
	 * @param x
	 * @param y
	 */
	public void translate(float x, float y) {
		if (gameObject.isStatic) throw new Error("Static objects cannot be moved");
		if (x == 0 && y == 0) return;
		if (gameObject.collider != null) {
			gameObject.collider.move(x, y);
		} else {
			position.x += x;
			position.y += y;
		}
		for (int i = 0; i < children.size(); i++) {
			children.get(i).translate(x, y);
		}
	}
}
