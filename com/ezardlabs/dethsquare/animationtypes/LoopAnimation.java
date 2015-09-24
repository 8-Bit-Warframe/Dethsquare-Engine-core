package com.ezardlabs.dethsquare.animationtypes;

public class LoopAnimation extends AnimationType {

	@Override
	public int update(int currentFrame, int numFrames) {
		currentFrame++;
		if (currentFrame == numFrames) currentFrame = 0;
		return currentFrame;
	}
}
