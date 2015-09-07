package com.ezardlabs.dethsquare;

public final class Rigidbody extends Script {
    public Vector2 velocity = new Vector2(0, 0.9375f);

	@Override
	public void update() {
		velocity.y += 1.25f;
		if (velocity.y > 78.125f) velocity.y = 78.125f;

		transform.translate(velocity.x, velocity.y);
	}
}
