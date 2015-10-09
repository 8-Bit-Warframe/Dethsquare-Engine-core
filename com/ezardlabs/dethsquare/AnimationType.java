package com.ezardlabs.dethsquare;

public abstract class AnimationType {
	public static final AnimationType ONE_SHOT = new AnimationType() {
		@Override
		public int update(int currentFrame, int numFrames) {
			if (currentFrame < numFrames - 1) return ++currentFrame;
			else return -1;
		}
	};
	public static final AnimationType OSCILLATE = new AnimationType() {
		private int direction = 1;

		@Override
		public int update(int currentFrame, int numFrames) {
			currentFrame += direction;
			if (currentFrame == 0 || currentFrame == numFrames - 1) {
				direction *= -1;
			}
			return currentFrame;
		}
	};
	public static final AnimationType LOOP = new AnimationType() {
		@Override
		public int update(int currentFrame, int numFrames) {
			currentFrame++;
			if (currentFrame == numFrames) currentFrame = 0;
			return currentFrame;
		}
	};

	public abstract int update(int currentFrame, int numFrames);
}