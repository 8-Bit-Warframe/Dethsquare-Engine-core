package com.ezardlabs.dethsquare;

public final class Animator extends Script {
	private Animation[] animations;
	private int index = -1;
	private int frame = 0;
	private long nextFrameTime = 0;
	private boolean onAnimationFinishedCalled = false;
	private int direction = 1;

	public Animator(Animation... animations) {
		this.animations = animations;
	}

	public void setAnimations(Animation... animations) {
		this.animations = animations;
	}

	public void addAnimations(Animation... animations) {
		Animation[] newAnimations = new Animation[this.animations.length + animations.length];
		System.arraycopy(this.animations, 0, newAnimations, 0, this.animations.length);
		System.arraycopy(animations, 0, newAnimations, this.animations.length, animations.length);
		this.animations = newAnimations;
	}

	public void update() {
		int startFrame = frame;
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
			gameObject.renderer.sprite = animations[index].frames[frame];
		}
		if (frame != startFrame && animations[index].listener != null) {
			animations[index].listener.onFrame(frame);
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
				gameObject.renderer.sprite = animations[index].frames[frame];
				if (animations[index].listener != null)
					animations[index].listener.onAnimatedStarted(this);
				break;
			}
		}
	}

    public Animation getCurrentAnimation() {
        if (index == -1) return null;
        else return animations[index];
    }
}
