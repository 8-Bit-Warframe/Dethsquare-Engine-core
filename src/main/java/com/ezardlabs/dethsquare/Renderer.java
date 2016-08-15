package com.ezardlabs.dethsquare;

import com.ezardlabs.dethsquare.TextureAtlas.Sprite;
import com.ezardlabs.dethsquare.util.GameListeners;
import com.ezardlabs.dethsquare.util.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Renderer extends BoundedComponent {
	/**
	 * Add hook into game loop
	 */
	static {
		GameListeners.addRenderListener(Renderer::renderAll);
	}
	static HashMap<String, int[]> textures = new HashMap<>();

	private static QuadTree<Renderer> qt = new QuadTree<>(30);
	private static ArrayList<Renderer> renderers = new ArrayList<>();

	Sprite sprite;
	public float width;
	public float height;
	private int zIndex = 0;
	public boolean hFlipped = false;
	public boolean vFlipped = false;

	public int textureName = -1;
	public Mode mode = Mode.NONE;
	private int xOffset;
	private int yOffset;

	private enum Mode {
		NONE,
		IMAGE,
		SPRITE
	}

	public Renderer() {
	}

	public Renderer(String imagePath, float width, float height) {
		setImage(imagePath, width, height);
	}

	public Renderer(TextureAtlas textureAtlas, Sprite sprite, float width, float height) {
		setTextureAtlas(textureAtlas, width, height);
		this.sprite = sprite;
	}

	public Renderer setFlipped(boolean hFlipped, boolean vFlipped) {
		this.hFlipped = hFlipped;
		this.vFlipped = vFlipped;
		return this;
	}

	public void setImage(String imagePath, float width, float height) {
		mode = Mode.IMAGE;
		if (textures.containsKey(imagePath)) {
			textureName = textures.get(imagePath)[0];
		} else {
			int[] data = Utils.loadImage(imagePath);
			textures.put(imagePath, data);
			textureName = data[0];
		}
		this.width = width;
		this.height = height;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setOffsets(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public void setTextureAtlas(TextureAtlas textureAtlas, float spriteWidth, float spriteHeight) {
		textureName = textureAtlas.textureName;
		mode = Mode.SPRITE;
		width = spriteWidth;
		height = spriteHeight;
	}

	public Renderer setzIndex(int zIndex) {
		this.zIndex = zIndex;
		return this;
	}

	protected float getXPos() {
		return transform.position.x + xOffset;
	}

	protected float getYPos() {
		return transform.position.y + yOffset;
	}

	protected int getZIndex() {
		return zIndex;
	}

	@Override
	public void start() {
		renderers.add(this);
		vertices = new float[vertices.length + 12];
		indices = new short[indices.length + 6];
		uvs = new float[uvs.length + 8];
		vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		indexBuffer = ByteBuffer.allocateDirect(indices.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
		uvBuffer = ByteBuffer.allocateDirect(uvs.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}

	@Override
	protected void destroy() {
		renderers.remove(this);
		vertices = new float[vertices.length - 12];
		indices = new short[indices.length - 6];
		uvs = new float[uvs.length - 8];
	}

	public static void init() {
		ArrayList<Renderer> staticRenderers = new ArrayList<>();
		for (Renderer r : renderers.toArray(new Renderer[renderers.size()])) {
			r.bounds.set(r.transform.position.x, r.transform.position.y, r.transform.position.x + r.width, r.transform.position.y + r.height);
			if (r.gameObject.isStatic) {
				staticRenderers.add(r);
				renderers.remove(r);
			}
		}
		qt.init(staticRenderers.toArray(new Renderer[staticRenderers.size()]));
	}

	static void clearAll() {
		renderers.clear();
	}

	static void destroyAllTextures() {
		Utils.destroyAllTextures(textures);
		textures.clear();
	}

	static void clearQuadTree() {
		qt = new QuadTree<>(30);
	}

	private static ArrayList<Integer> idsRendered = new ArrayList<>();
	private static int drawCalls = 0;

	static HashMap<Integer, ArrayList<Renderer>> map = new HashMap<>();

	private static void renderAll() {
		drawCalls = 0;
		visible.clear();
		qt.getVisibleObjects(visible, qt, Camera.main);
//		System.out.println(Camera.main.bounds);

		visible.addAll(renderers); // TODO only add renderers that are visible
		map.clear();
		for (int i = 0; i < visible.size(); i++) {
			if (map.size() == 0 || !map.containsKey(visible.get(i).getZIndex())) {
				map.put(visible.get(i).getZIndex(), new ArrayList<>());
			}
			map.get(visible.get(i).getZIndex()).add(visible.get(i));
		}

		ArrayList<Integer> zIndices = new ArrayList<>(map.keySet());
		Collections.sort(zIndices);

		for (int zIndex = 0; zIndex < zIndices.size(); zIndex++) {
			idsRendered.clear();
			ArrayList<Renderer> temp = map.get(zIndices.get(zIndex));
			for (int i = 0; i < temp.size(); i++) {
				if (!idsRendered.contains(temp.get(i).textureName)) {
					idsRendered.add(temp.get(i).textureName);

					visible.clear();

					for (int j = i; j < temp.size(); j++) {
						if (temp.get(j).textureName == temp.get(i).textureName) {
							visible.add(temp.get(j));
						}
					}

					setupRenderData(visible);

					Utils.render(temp.get(i).textureName, vertexBuffer, uvBuffer, visible.size()
							* 6, indexBuffer, Camera.main.transform.position.x, Camera.main
							.transform.position.y, Screen.scale);

					drawCalls++;
				}
			}
		}
//		Log.i("", "Draw calls: " + drawCalls);
	}

	private static ArrayList<Renderer> visible = new ArrayList<>();

	private static float[] vertices = new float[0];
	private static short[] indices = new short[0];
	private static float[] uvs = new float[0];
	private static FloatBuffer vertexBuffer;
	private static ShortBuffer indexBuffer;
	private static FloatBuffer uvBuffer;

	private static void setupRenderData(ArrayList<Renderer> renderers) {
		int i = 0;
		int last = 0;
		Renderer r;
		for (int j = 0; j < renderers.size(); j++) {
			r = renderers.get(j);
//			vertices[(i * 12)] = r.transform.position.x * Screen.scale;
//			vertices[(i * 12) + 1] = r.transform.position.y * Screen.scale + (r.height * Screen.scale);
//			vertices[(i * 12) + 2] = 0;
//			vertices[(i * 12) + 3] = r.transform.position.x * Screen.scale;
//			vertices[(i * 12) + 4] = r.transform.position.y * Screen.scale;
//			vertices[(i * 12) + 5] = 0;
//			vertices[(i * 12) + 6] = r.transform.position.x * Screen.scale + (r.width * Screen.scale);
//			vertices[(i * 12) + 7] = r.transform.position.y * Screen.scale;
//			vertices[(i * 12) + 8] = 0;
//			vertices[(i * 12) + 9] = r.transform.position.x * Screen.scale + (r.width * Screen.scale);
//			vertices[(i * 12) + 10] = r.transform.position.y * Screen.scale + (r.height * Screen.scale);
//			vertices[(i * 12) + 11] = 0;

			vertices[(i * 12)] = vertices[(i * 12) + 3] = r.getXPos() * Screen.scale;
			vertices[(i * 12) + 1] = vertices[(i * 12) + 10] = r.getYPos() * Screen.scale + (r.height * Screen.scale);
			vertices[(i * 12) + 2] = vertices[(i * 12) + 5] = vertices[(i * 12) + 8] = vertices[(i * 12) + 11] = 0;
			vertices[(i * 12) + 4] = vertices[(i * 12) + 7] = r.getYPos() * Screen.scale;
			vertices[(i * 12) + 6] = vertices[(i * 12) + 9] = r.getXPos() * Screen.scale + (r.width * Screen.scale);

//			indices[(i * 6)] = (short) (last);
//			indices[(i * 6) + 1] = (short) (last + 1);
//			indices[(i * 6) + 2] = (short) (last + 2);
//			indices[(i * 6) + 3] = (short) (last);
//			indices[(i * 6) + 4] = (short) (last + 2);
//			indices[(i * 6) + 5] = (short) (last + 3);

			indices[(i * 6)] = indices[(i * 6) + 3] = (short) (last);
			indices[(i * 6) + 1] = (short) (last + 1);
			indices[(i * 6) + 2] = indices[(i * 6) + 4] = (short) (last + 2);
			indices[(i * 6) + 5] = (short) (last + 3);
			last = last + 4;

			float u;
			float v;
			float w;
			float h;
			switch (r.mode) {
				case IMAGE:
					u = 0;
					v = 0;
					w = 1;
					h = 1;
					break;
				case SPRITE:
					u = r.sprite.u;
					v = r.sprite.v;
					w = r.sprite.w;
					h = r.sprite.h;
					break;
				case NONE:
				default:
					u = v = w = h = 0;
			}
			if (r.hFlipped && r.vFlipped) {
				uvs[(i * 8) + 6] = u;
				uvs[(i * 8) + 7] = v;
				uvs[(i * 8) + 4] = u;
				uvs[(i * 8) + 5] = v + h;
				uvs[(i * 8) + 2] = u + w;
				uvs[(i * 8) + 3] = v + h;
				uvs[(i * 8)] = u + w;
				uvs[(i * 8) + 1] = v;
			} else if (r.hFlipped) {
				uvs[(i * 8) + 4] = u;
				uvs[(i * 8) + 5] = v;
				uvs[(i * 8) + 6] = u;
				uvs[(i * 8) + 7] = v + h;
				uvs[(i * 8)] = u + w;
				uvs[(i * 8) + 1] = v + h;
				uvs[(i * 8) + 2] = u + w;
				uvs[(i * 8) + 3] = v;
			} else if (r.vFlipped) {
				uvs[(i * 8)] = u;
				uvs[(i * 8) + 1] = v;
				uvs[(i * 8) + 2] = u;
				uvs[(i * 8) + 3] = v + h;
				uvs[(i * 8) + 4] = u + w;
				uvs[(i * 8) + 5] = v + h;
				uvs[(i * 8) + 6] = u + w;
				uvs[(i * 8) + 7] = v;
			} else {
				uvs[(i * 8) + 2] = u;
				uvs[(i * 8) + 3] = v;
				uvs[(i * 8)] = u;
				uvs[(i * 8) + 1] = v + h;
				uvs[(i * 8) + 6] = u + w;
				uvs[(i * 8) + 7] = v + h;
				uvs[(i * 8) + 4] = u + w;
				uvs[(i * 8) + 5] = v;
			}

			i++;
		}
		vertexBuffer.position(0);
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		indexBuffer.position(0);
		indexBuffer.put(indices);
		indexBuffer.position(0);

		uvBuffer.position(0);
		uvBuffer.put(uvs);
		uvBuffer.position(0);
	}
}