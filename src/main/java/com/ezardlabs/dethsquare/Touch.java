package com.ezardlabs.dethsquare;

public final class Touch {
	public final int fingerId;
	public final Vector2 position;
	public final Vector2 startPosition;
	public TouchPhase phase = TouchPhase.BEGAN;
	public long lastModified = 0;

	public enum TouchPhase {
		/**
		 * A finger touched the screen
		 */
		BEGAN,
		/**
		 * A finger moved on the screen
		 */
		MOVED,
		/**
		 * A finger is touching the screen but hasn't moved
		 */
		STATIONARY,
		/**
		 * A finger was lifted from the screen. This is the final phase of a touch
		 */
		ENDED,
		/**
		 * The system cancelled tracking for the touch
		 */
		CANCELLED
	}

	public Touch(int fingerId, Vector2 position) {
		this.fingerId = fingerId;
		this.position = position;
		this.startPosition = new Vector2(position.x, position.y);
		this.lastModified = Time.frameCount;
	}

	@Override
	public String toString() {
		return "Touch(fingerId=" + fingerId + ", phase=" + phase + ")";
	}
}
