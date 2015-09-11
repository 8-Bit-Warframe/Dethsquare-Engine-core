package com.ezardlabs.dethsquare;

public final class Animator extends Script {
	private Animation[] animations;
	private int index = -1;
	private int frame = 0;
	private long nextFrameTime = 0;

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
		if (index == -1 ||frame == -1) return;
		if (System.currentTimeMillis() >= nextFrameTime) {
			nextFrameTime += animations[index].frameDuration;
			frame = animations[index].type.update(frame, animations[index].frames.length);
			if (frame == -1) {
				if (animations[index].listener != null) {
					animations[index].listener.onAnimationFinished(this);
				}
				return;
			}
			try {
				gameObject.renderer.sprite = animations[index].frames[frame];
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println(gameObject.name + ", " + animations[index].name);
			}
		}
		if (frame != startFrame && animations[index].listener != null) {
			animations[index].listener.onFrame(this, frame);
		}
	}

	public void play(String animationName) {
		if (index != -1 && animations[index].name.equals(animationName)) return;
		for (int i = 0; i < animations.length; i++) {
			if (i != index && animations[i].name.equals(animationName)) {
				index = i;
				frame = 0;
				nextFrameTime = System.currentTimeMillis() + animations[index].frameDuration;
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
