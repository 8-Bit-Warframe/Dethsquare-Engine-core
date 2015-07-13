package com.ezardlabs.dethsquare;

public final class Rigidbody extends Script {
	public float gravity = 0.9375f;

	@Override
	public void update() {
		gravity += 1.25f;
		if (gravity > 78.125f) gravity = 78.125f;

		transform.translate(0, gravity);
	}
}
