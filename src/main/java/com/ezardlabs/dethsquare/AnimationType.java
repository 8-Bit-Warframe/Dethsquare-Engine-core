package com.ezardlabs.dethsquare;

public abstract class AnimationType implements Cloneable {
	/**
	 * An animation that runs a single time, from start to end
	 */
	public static final AnimationType ONE_SHOT = new AnimationType() {
		@Override
		public int update(int currentFrame, int numFrames) {
			if (currentFrame < numFrames - 1) return currentFrame + 1;
			else return -1;
		}
	};
	/**
	 * An animation that oscillates between its start and end points
	 */
	public static final AnimationType OSCILLATE = new AnimationType() {
		private int direction = 1;

		@Override
		public int update(int currentFrame, int numFrames) {
			int frame = currentFrame + direction;
			if (frame == 0 || frame == numFrames - 1) {
				direction *= -1;
			}
			return frame;
		}
	};
	/**
	 * An animation that plays through from start to end, restarting when finished
	 */
	public static final AnimationType LOOP = new AnimationType() {
		@Override
		public int update(int currentFrame, int numFrames) {
			int frame = currentFrame + 1;
			if (frame == numFrames) frame = 0;
			return frame;
		}
	};

	/**
	 * Describes how the animation should progress, based on the current frame
	 * @param currentFrame The frame that the animation is currently at
	 * @param numFrames The total number of frames in the animation
	 * @return The next frame in the animation to go to
	 */
	public abstract int update(int currentFrame, int numFrames);

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return this;
		}
	}
}