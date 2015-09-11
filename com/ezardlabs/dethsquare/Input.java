package com.ezardlabs.dethsquare;

import java.util.Arrays;
import java.util.HashMap;

public class Input {
	public static OnTouchListener[] onTouchListeners = new OnTouchListener[0];

	public static void addOnTouchListener(OnTouchListener listener) {
		onTouchListeners = Arrays.copyOf(onTouchListeners, onTouchListeners.length + 1);
		Input.onTouchListeners[onTouchListeners.length - 1] = listener;
	}

	/**
	 * Each key that has been pressed either has a value of true if it has been pressed down in the current frame, or false if it's been held down for multiple frames
	 */
	private static HashMap<KeyCode, Integer> keys = new HashMap<>();
	private static HashMap<KeyCode, Integer> keyChanges = new HashMap<>();

	public enum KeyCode {
		A,
		B,
		C,
		D,
		E,
		F,
		G,
		H,
		I,
		J,
		K,
		L,
		M,
		N,
		O,
		P,
		Q,
		R,
		S,
		T,
		U,
		V,
		W,
		X,
		Y,
		Z,
		ALPHA_0,
		ALPHA_1,
		ALPHA_2,
		ALPHA_3,
		ALPHA_4,
		ALPHA_5,
		ALPHA_6,
		ALPHA_7,
		ALPHA_8,
		ALPHA_9,
		SPACE,
		RETURN,
		ESCAPE,
		BACKSPACE,
		DELETE,
		F1,
		F2,
		F3,
		F4,
		F5,
		F6,
		F7,
		F8,
		F9,
		F10,
		F11,
		F12
	}

	public static void update() {
		for (KeyCode keyCode : keys.keySet().toArray(new KeyCode[keys.size()])) {
			switch(keys.get(keyCode)) {
				case 0:
					keys.put(keyCode, 1);
					break;
				case 2:
					keys.remove(keyCode);
					break;
			}
		}
		keys.putAll(keyChanges);
		keyChanges.clear();
	}

	public static void setKeyDown(KeyCode keyCode) {
		if (!keys.containsKey(keyCode)) keyChanges.put(keyCode, 0);
	}

	public static void setKeyUp(KeyCode keyCode) {
		keyChanges.put(keyCode, 2);
	}

	public static boolean getKeyDown(KeyCode keyCode) {
		return keys.containsKey(keyCode) && keys.get(keyCode) == 0;
	}

	public static boolean getKey(KeyCode keyCode) {
		return keys.containsKey(keyCode) && keys.get(keyCode) < 2;
	}

	public static boolean getKeyUp(KeyCode keyCode) {
		return keys.containsKey(keyCode) && keys.get(keyCode) == 2;
	}

	public interface OnTouchListener {
		void onTouchDown(int id, float x, float y);

		void onTouchMove(int id, float x, float y);

		void onTouchUp(int id, float x, float y);

		void onTouchCancel(int id);

		void onTouchOutside(int id);
	}
}
