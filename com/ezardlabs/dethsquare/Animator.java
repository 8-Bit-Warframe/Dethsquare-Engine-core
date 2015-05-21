package com.ezardlabs.dethsquare;

public class Animator extends Script {
	private Animation[] animations;
	private int index = -1;
	private int frame = 0;
	private long nextFrameTime = 0;
	private boolean onAnimationFinishedCalled = false;
	private int direction = 1;

	public Animator(Animation... animations) {
		this.animations = animations;
	}

	public void update() {
		if (index == -1) return;
		if (System.currentTimeMillis() >= nextFrameTime) {
			nextFrameTime += animations[index].frameDuration;
			switch (animations[index].type) {
				case LOOP:
					frame += direction;
					if (frame == animations[index].frames.length) {
						frame = 0;
					}
					break;
				case ONE_SHOT:
					if (frame < animations[index].frames.length - 1) {
						frame += direction;
					} else {
						if (!onAnimationFinishedCalled && animations[index].listener != null) {
							animations[index].listener.onAnimationFinished(this);
							onAnimationFinishedCalled = true;
						}
						return;
					}
					break;
				case OSCILLATE:
					frame += direction;
					if (frame == 0 || frame == animations[index].frames.length - 1) {
						direction *= -1;
					}
					break;
			}
			getComponent(Renderer.class).sprite = animations[index].frames[frame];
		}
	}

	public void play(String animationName) {
		if (index != -1 && animations[index].name.equals(animationName)) return;
		for (int i = 0; i < animations.length; i++) {
			if (i != index && animations[i].name.equals(animationName)) {
				index = i;
				frame = 0;
				direction = 1;
				nextFrameTime = System.currentTimeMillis() + animations[index].frameDuration;
				onAnimationFinishedCalled = false;
				getComponent(Renderer.class).sprite = animations[index].frames[frame];
				break;
			}
		}
	}
}
