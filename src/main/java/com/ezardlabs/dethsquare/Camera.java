package com.ezardlabs.dethsquare;

public final class Camera extends Script {
	public final RectF bounds = new RectF();
	public static Camera main;
	private boolean isMain = false;

	public Camera(boolean main) {
		isMain = main;
	}

	@Override
	public void start() {
		if (isMain) main = this;
	}

	public void update() {
		bounds.left = transform.position.x;
		bounds.top = transform.position.y;
		bounds.right = bounds.left + Screen.width / Screen.scale;
		bounds.bottom = bounds.top + Screen.height / Screen.scale;
	}
}
