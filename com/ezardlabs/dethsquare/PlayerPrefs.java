package com.ezardlabs.dethsquare;

import com.ezardlabs.dethsquare.util.Utils;

public final class PlayerPrefs {

	public static void setBoolean(String key, boolean value) {
		Utils.setBoolean(key, value);
	}

	public static void setInt(String key, int value) {
		Utils.setInt(key, value);
	}

	public static void setFloat(String key, float value) {
		Utils.setFloat(key, value);
	}

	public static void setString(String key, String value) {
		Utils.setString(key, value);
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		return Utils.getBoolean(key, defaultValue);
	}

	public static int getInt(String key, int defaultValue) {
		return Utils.getInt(key, defaultValue);
	}

	public static float getFloat(String key, float defaultValue) {
		return Utils.getFloat(key, defaultValue);
	}

	public static String getString(String key, String defaultValue) {
		return Utils.getString(key, defaultValue);
	}
}
