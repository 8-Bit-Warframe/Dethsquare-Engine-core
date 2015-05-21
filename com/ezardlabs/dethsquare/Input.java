package com.ezardlabs.dethsquare;

import java.util.Arrays;

public class Input {
	public static OnTouchListener[] onTouchListeners = new OnTouchListener[0];

	public static void addOnTouchListener(OnTouchListener listener) {
		onTouchListeners = Arrays.copyOf(onTouchListeners, onTouchListeners.length + 1);
		Input.onTouchListeners[onTouchListeners.length - 1] = listener;
	}

	public interface OnTouchListener {
		void onTouchDown(int id, float x, float y);

		void onTouchMove(int id, float x, float y);

		void onTouchUp(int id, float x, float y);
	}
}
