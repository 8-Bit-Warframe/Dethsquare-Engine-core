package com.ezardlabs.dethsquare;

public class Animator extends Script {
	private Animation[] animations;
	private int index = -1;
	private int frame = 0;
	private long nextFrameTime = 0;

	public Animator(Animation... animations) {
		this.animations = animations;
		if (animations != null && animations.length > 0) {
			index = 0;
		}
	}

	public void update() {
		if (index == -1) return;
		if (System.currentTimeMillis() >= nextFrameTime) {
			nextFrameTime += animations[index].frameDuration;
			switch (animations[index].type) {
				case LOOP:
					frame++;
					if (frame == animations[index].frames.length) {
						frame = 0;
					}
					break;
				case ONE_SHOT:
					break;
			}
		}
	}

	public void play(String animationName) {
		if (animations[index].name.equals(animationName)) return;
		for (int i = 0; i < animations.length; i++) {
			if (i != index && animations[i].name.equals(animationName)) {
				index = i;
				frame = 0;
				nextFrameTime = System.currentTimeMillis() + animations[index].frameDuration;
			}
		}
	}
}
