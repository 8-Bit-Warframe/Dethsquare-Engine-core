package com.ezardlabs.dethsquare;

import com.ezardlabs.dethsquare.Touch.TouchPhase;
import com.ezardlabs.dethsquare.util.GameListeners;
import com.ezardlabs.dethsquare.util.GameListeners.KeyListener;
import com.ezardlabs.dethsquare.util.GameListeners.MouseListener;

import java.util.ArrayList;
import java.util.HashMap;

public final class Input {
	static {
		GameListeners.addMouseListener(new MouseListener() {
			@Override
			public void onMove(int x, int y) {
				mousePosition.set(x, y);
			}

			@Override
			public void onButtonDown(int index) {
				switch(index) {
					case 1:
						setKeyDown(KeyCode.MOUSE_LEFT);
						break;
					case 2:
						setKeyDown(KeyCode.MOUSE_MIDDLE);
						break;
					case 3:
						setKeyDown(KeyCode.MOUSE_RIGHT);
						break;
				}
			}

			@Override
			public void onButtonUp(int index) {
				switch(index) {
					case 1:
						setKeyUp(KeyCode.MOUSE_LEFT);
						break;
					case 2:
						setKeyUp(KeyCode.MOUSE_MIDDLE);
						break;
					case 3:
						setKeyUp(KeyCode.MOUSE_RIGHT);
						break;
					default:
						break;
				}
			}
		});
		GameListeners.addKeyListener(new KeyListener() {
			@Override
			public void onKeyDown(String key) {
				setKeyDown(KeyCode.valueOf(key));
			}

			@Override
			public void onKeyUp(String key) {
				setKeyUp(KeyCode.valueOf(key));
			}
		});

		GameListeners.addUpdateListener(Input::update);
	}

	public static final Vector2 mousePosition = new Vector2();

	public static Touch[] touches = new Touch[0];

	/**
	 * Each key that has been pressed either has a value of true if it has been pressed down in the current frame, or false if it's been held down for multiple frames
	 */
	private static HashMap<KeyCode, Integer> keys = new HashMap<>();
	private static HashMap<KeyCode, Integer> keyChanges = new HashMap<>();

	private static ArrayList<Holder> changesToMake = new ArrayList<>(10);
	private static ArrayList<Touch> touchesToRemove = new ArrayList<>(10);

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
		F12,
		MOUSE_LEFT,
		MOUSE_RIGHT,
		MOUSE_MIDDLE
	}

	private static class Holder {
		int id;
		float x;
		float y;
		TouchPhase phase;

		Holder(int id, float x, float y, TouchPhase phase) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.phase = phase;
		}
	}

	public static void update() {
		Holder holder;
		for (int i = 0; i < changesToMake.size(); i++) {
			switch((holder = changesToMake.get(i)).phase) {
				case BEGAN:
					Touch[] temp = new Touch[touches.length + 1];
					System.arraycopy(touches, 0, temp, 0, touches.length);
					temp[touches.length] = new Touch(holder.id, new Vector2(holder.x, holder.y));
					touches = temp;
					break;
				case MOVED:
					for (Touch t : touches) {
						if (t.fingerId == holder.id && t.lastModified < Time.frameCount) {
							t.phase = Touch.TouchPhase.MOVED;
							t.position.set(holder.x, holder.y);
							t.lastModified = Time.frameCount;
						}
					}
					break;
				case STATIONARY:
					break;
				case ENDED:
					for (Touch t : touches) {
						if (t.fingerId == holder.id) {
							t.phase = Touch.TouchPhase.ENDED;
							t.position.set(holder.x, holder.y);
							t.lastModified = Time.frameCount;
						}
					}
					break;
				case CANCELLED:
					for (Touch t : touches) {
						if (t.fingerId == holder.id) {
							t.phase = Touch.TouchPhase.CANCELLED;
							t.position.set(holder.x, holder.y);
							t.lastModified = Time.frameCount;
						}
					}
					break;
				default:
					break;
			}
		}

		changesToMake.clear();

		for (KeyCode keyCode : keys.keySet().toArray(new KeyCode[keys.size()])) {
			switch (keys.get(keyCode)) {
				case 0:
					keys.put(keyCode, 1);
					break;
				case 2:
					keys.remove(keyCode);
					break;
				default:
					break;
			}
		}
		keys.putAll(keyChanges);
		keyChanges.clear();
		//noinspection ForLoopReplaceableByForEach
		for (int i = 0; i < touches.length; i++) {
			if ((touches[i].phase == Touch.TouchPhase.ENDED || touches[i].phase == Touch.TouchPhase.CANCELLED) && touches[i].lastModified < Time.frameCount) {
				touchesToRemove.add(touches[i]);
			} else if (touches[i].lastModified < Time.frameCount) {
				touches[i].lastModified = Time.frameCount;
				touches[i].phase = Touch.TouchPhase.STATIONARY;
			}
		}
		if (touchesToRemove.size() > 0) {
			Touch[] temp = new Touch[touches.length - touchesToRemove.size()];
			int count = 0;
			//noinspection ForLoopReplaceableByForEach
			for (int i = 0; i < touches.length; i++) {
				if (!touchesToRemove.contains(touches[i])) {
					temp[count++] = touches[i];
				}
			}
			touchesToRemove.clear();
			touches = temp;
		}
	}

	private static void setKeyDown(KeyCode keyCode) {
		if (!keys.containsKey(keyCode)) keyChanges.put(keyCode, 0);
	}

	private static void setKeyUp(KeyCode keyCode) {
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

	public static void addTouch(int id, float x, float y) {
		changesToMake.add(new Holder(id, x, y, TouchPhase.BEGAN));
	}

	public static void moveTouch(int id, float x, float y) {
		changesToMake.add(new Holder(id, x, y, TouchPhase.MOVED));
	}

	public static void removeTouch(int id, float x, float y) {
		changesToMake.add(new Holder(id, x, y, TouchPhase.ENDED));
	}

	public static void cancelTouch(int id, float x, float y) {
		changesToMake.add(new Holder(id, x, y, TouchPhase.CANCELLED));
	}
}
