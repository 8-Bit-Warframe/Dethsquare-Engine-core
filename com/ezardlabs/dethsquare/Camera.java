package com.ezardlabs.dethsquare;

public class Camera extends Script {
	public final RectF bounds = new RectF();
	public static Camera main;

	public Camera(boolean main) {
		if (main) Camera.main = this;
	}

	public void update() {
		bounds.left = transform.position.x;
		bounds.top = transform.position.y;
		bounds.right = bounds.left + Screen.width / Screen.scale;
		bounds.bottom = bounds.top + Screen.height / Screen.scale;
	}
}
