package com.ezardlabs.dethsquare;

import java.util.Arrays;

public class Input {
	public static OnTouchListener[] onTouchListeners = new OnTouchListener[0];
	public static OnKeyListener[] onKeyListeners = new OnKeyListener[0];

	public static void addOnTouchListener(OnTouchListener listener) {
		onTouchListeners = Arrays.copyOf(onTouchListeners, onTouchListeners.length + 1);
		Input.onTouchListeners[onTouchListeners.length - 1] = listener;
	}

	public static void addOnKeyListener(OnKeyListener listener) {
		onKeyListeners = Arrays.copyOf(onKeyListeners, onKeyListeners.length + 1);
		Input.onKeyListeners[onKeyListeners.length - 1] = listener;
	}

	public interface OnTouchListener {
		void onTouchDown(int id, float x, float y);

		void onTouchMove(int id, float x, float y);

		void onTouchUp(int id, float x, float y);

		void onTouchCancel(int id);

		void onTouchOutside(int id);
	}

	public interface OnKeyListener {
		void onKeyTyped(char keyChar);

		void onKeyDown(char keyChar);

		void onKeyUp(char keyChar);
	}
}
