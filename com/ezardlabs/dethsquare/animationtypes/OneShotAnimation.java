package com.ezardlabs.dethsquare.animationtypes;

public class OneShotAnimation extends AnimationType {

	@Override
	public int update(int currentFrame, int numFrames) {
		System.out.println("currentFrame: " + currentFrame + ", numFrames: " + numFrames);
		if (currentFrame < numFrames - 1) return ++currentFrame;
		else return -1;
	}
}
