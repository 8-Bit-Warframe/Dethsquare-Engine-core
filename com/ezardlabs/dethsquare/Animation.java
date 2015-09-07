package com.ezardlabs.dethsquare;

import com.ezardlabs.dethsquare.TextureAtlas.Sprite;

public final class Animation {
	public final String name;
	final Sprite[] frames;
	final AnimationType type;
	final long frameDuration;
	AnimationListener listener;

	public enum AnimationType {
		LOOP,
		ONE_SHOT,
		OSCILLATE
	}

	public Animation(String name, Sprite[] frames, AnimationType type, long frameDuration) {
		this.name = name;
		this.frames = frames;
		this.type = type;
		this.frameDuration = frameDuration;
	}

	public Animation(String name, Sprite[] frames, AnimationType type, long frameDuration,
			AnimationListener listener) {
		this(name, frames, type, frameDuration);
		this.listener = listener;
	}

	public interface AnimationListener {
		void onAnimatedStarted(Animator animator);

		void onFrame(Animator animator, int frameNum);

		void onAnimationFinished(Animator animator);
	}
}
