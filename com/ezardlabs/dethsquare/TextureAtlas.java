package com.ezardlabs.dethsquare;

import com.ezardlabs.dethsquare.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class TextureAtlas {
	final String imagePath;
	final String mapPath;
	private final HashMap<String, Sprite> atlas = new HashMap<>();
	public int textureName = -1;
	public int width;
	public int height;

	public TextureAtlas(String imagePath, String mapPath) {
		this.imagePath = imagePath;
		this.mapPath = mapPath;

		int[] data = Utils.loadImage(imagePath);
		textureName = data[0];
		width = data[1];
		height = data[2];

		try {
			String temp;
			BufferedReader reader = Utils.getReader(mapPath);
			while ((temp = reader.readLine()) != null) {
				String[] split = temp.split(" = ");
				String[] split2 = split[1].split(" ");
				atlas.put(split[0], new Sprite(Float.parseFloat(split2[0]) / width,
						Float.parseFloat(split2[1]) / height, Float.parseFloat(split2[2]) / width,
						Float.parseFloat(split2[3]) / height));
			}
		} catch (IOException ignored) {
		}
	}

	public Sprite getSprite(String name) {
		return atlas.get(name);
	}

	public static class Sprite {
		public float u;
		public float v;
		public float w;
		public float h;

		private Sprite(float u, float v, float w, float h) {
			this.u = u;
			this.v = v;
			this.w = w;
			this.h = h;
		}

		public String toString() {
			return "Sprite(u: " + u + ", v: " + v + ", w: " + w + ", h: " + h + ")";
		}
	}
}
