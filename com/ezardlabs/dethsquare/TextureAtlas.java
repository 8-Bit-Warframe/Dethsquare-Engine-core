package com.ezardlabs.dethsquare;

import com.ezardlabs.dethsquare.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public final class TextureAtlas {
	final String imagePath;
	final String mapPath;
	private final HashMap<String, Sprite> atlas = new HashMap<>();
	public int textureName = -1;
	public int width;
	public int height;

	public TextureAtlas(String imagePath, String mapPath) {
		this.imagePath = imagePath;
		this.mapPath = mapPath;

		int[] data;
		if (Renderer.textures.containsKey(imagePath)) {
			data = Renderer.textures.get(imagePath);
		} else {
			data = Utils.loadImage(imagePath);
			Renderer.textures.put(imagePath, data);
		}

		textureName = data[0];
		width = data[1];
		height = data[2];

		if(mapPath != null) {
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
		} else {
			float tileWidth = 16.0f;
			float tileHeight = 16.0f;
			int count = 0;
			for(int y = 0; y < height; y += 16) {
				for(int x = 0; x < width; x += 16) {
					atlas.put(String.valueOf(count++),
						new Sprite(
							(float) x / width,
							(float) y / height,
							tileWidth / width,
							tileHeight / height
						)
					);
				}
			}
		}
	}

	public TextureAtlas(String imagePath) {
		this(imagePath, null);
	}

	public Sprite getSprite(String name) {
		if (!atlas.containsKey(name)) throw new Error("Sprite \"" + name + "\" not found in " +
				mapPath);
		Sprite sprite = atlas.get(name);
		return sprite;
	}

	public static class Sprite {
		public float u;
		public float v;
		public float w;
		public float h;

		public Sprite(float u, float v, float w, float h) {
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
