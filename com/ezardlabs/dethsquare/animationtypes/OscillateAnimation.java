package com.ezardlabs.dethsquare.animationtypes;

public class OscillateAnimation extends AnimationType {
	private int direction = 1;

	@Override
	public int update(int currentFrame, int numFrames) {
		currentFrame += direction;
		if (currentFrame == 0 || currentFrame == numFrames - 1) {
			direction *= -1;
		}
		return currentFrame;
	}
}
