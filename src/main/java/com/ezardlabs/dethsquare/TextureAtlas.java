package com.ezardlabs.dethsquare;

import com.ezardlabs.dethsquare.util.IOUtils;
import com.ezardlabs.dethsquare.util.RenderUtils;

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

	private TextureAtlas(String imagePath, String mapPath, int tileWidth, int tileHeight) {
		this.imagePath = imagePath;
		this.mapPath = mapPath;

		int[] data;
		if (Renderer.textures.containsKey(imagePath)) {
			data = Renderer.textures.get(imagePath);
		} else {
			data = RenderUtils.loadImage(imagePath);
			Renderer.textures.put(imagePath, data);
		}

		textureName = data[0];
		width = data[1];
		height = data[2];

		if(mapPath != null) {
			try {
				String temp;
				BufferedReader reader = IOUtils.getReader(mapPath);
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
			int count = 0;
			for(int y = 0; y < height; y += tileHeight) {
				for(int x = 0; x < width; x += tileWidth) {
					atlas.put(String.valueOf(count++),
						new Sprite(
							(float) x / width,
							(float) y / height,
							(float) tileWidth / width,
							(float) tileHeight / height
						)
					);
				}
			}
		}
	}

	public TextureAtlas(String imagePath, String mapPath) {
		this(imagePath, mapPath, 0, 0);
	}

	public TextureAtlas(String imagePath, int tileWidth, int tileHeight) {
		this(imagePath, null, tileWidth, tileHeight);
	}

	public Sprite getSprite(String name) {
		if (!atlas.containsKey(name)) throw new SpriteNotFoundInAtlasError(name, mapPath);
		Sprite sprite = atlas.get(name);
		return sprite;
	}

	public Sprite[] getSprites() {
		return atlas.values().toArray(new Sprite[atlas.size()]);
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

	private static class SpriteNotFoundInAtlasError extends Error {

		private SpriteNotFoundInAtlasError(String spriteName, String atlasPath) {
			super("Sprite '" + spriteName + "' was not found in the atlas located at " + atlasPath);
		}
	}
}
