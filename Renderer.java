package com.ezardlabs.dethsquare;

import com.ezardlabs.dethsquare.TextureAtlas.Sprite;
import com.ezardlabs.dethsquare.util.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

public class Renderer extends BoundedComponent {
	private static QuadTree<Renderer> qt = new QuadTree<>(30);
	private static ArrayList<Renderer> renderers = new ArrayList<>();

	private Sprite sprite;
	public float width;
	public float height;
	private int zIndex = 0;
	public boolean hFlipped = false;
	public boolean vFlipped = false;

	public int textureName = -1;
	public Mode mode = Mode.NONE;

	public int offsetX = 0;
	public int offsetY = 0;

	public enum Mode {
		NONE,
		IMAGE,
		SPRITE
	}

	public Renderer() {
		renderers.add(this);
	}

	public Renderer(String imagePath, float width, float height) {
		this();
		mode = Mode.IMAGE;
		textureName = Utils.loadImage(imagePath)[0];
		this.width = width;
		this.height = height;
	}

	public Renderer(TextureAtlas textureAtlas, Sprite sprite, float width, float height) {
		this();
		mode = Mode.SPRITE;
		textureName = textureAtlas.textureName;
		this.sprite = sprite;
		this.width = width;
		this.height = height;
	}

	public Renderer setFlipped(boolean hFlipped, boolean vFlipped) {
		this.hFlipped = hFlipped;
		this.vFlipped = vFlipped;
		return this;
	}

	public void setzIndex(int zIndex) {
		this.zIndex = zIndex;
	}

	public static void init() {
		ArrayList<Renderer> staticRenderers = new ArrayList<>();
		for (Renderer r : renderers.toArray(new Renderer[renderers.size()])) {
			r.bounds.set(r.transform.position.x, r.transform.position.y,
					r.transform.position.x + r.width, r.transform.position.y + r.height);
			if (r.gameObject.isStatic) {
				staticRenderers.add(r);
				renderers.remove(r);
			}
		}
		qt.init(staticRenderers.toArray(new Renderer[staticRenderers.size()]));
	}

	private static ArrayList<Integer> idsRenderered = new ArrayList<>();
	private static Renderer[] visibleArray;
	private static int drawCalls = 0;

	public static void renderAll() {
		drawCalls = 0;
		visible.clear();
		qt.getVisibleObjects(visible, qt, Camera.main);

		visible.addAll(renderers); // TODO only add renderers that are visible

		visibleArray = visible.toArray(new Renderer[visible.size()]);
		idsRenderered.clear();

		for (int i = 0; i < visibleArray.length; i++) {
			if (!idsRenderered.contains(visibleArray[i].textureName)) {
				idsRenderered.add(visibleArray[i].textureName);

				visible.clear();

				for (int j = i; j < visibleArray.length; j++) {
					if (visibleArray[j].textureName == visibleArray[i].textureName) {
						visible.add(visibleArray[j]);
					}
				}

				setupRenderData(visible);

				Utils.render(visibleArray[i].textureName, vertexBuffer, uvBuffer, indices,
						drawListBuffer);

				drawCalls++;
			}
		}
	}

	private static float[] vertices;
	private static short[] indices;
	private static float[] uvs;
	private static ArrayList<Renderer> visible = new ArrayList<>();
	private static FloatBuffer vertexBuffer;
	private static ShortBuffer drawListBuffer;
	private static FloatBuffer uvBuffer;

	private static void setupRenderData(ArrayList<Renderer> renderers) {
		vertices = new float[renderers.size() * 4 * 3];
		indices = new short[renderers.size() * 6];
		uvs = new float[renderers.size() * 4 * 2];

		int i = 0;
		int last = 0;
		for (Renderer r : renderers) {
			vertices[(i * 12)] = (r.transform.position.x + r.offsetX) * Screen.scale;
			vertices[(i * 12) + 1] =
					(r.transform.position.y + r.offsetY) * Screen.scale + (r.height * Screen.scale);
			vertices[(i * 12) + 2] = r.zIndex;
			vertices[(i * 12) + 3] = (r.transform.position.x + r.offsetX) * Screen.scale;
			vertices[(i * 12) + 4] = (r.transform.position.y + r.offsetY) * Screen.scale;
			vertices[(i * 12) + 5] = r.zIndex;
			vertices[(i * 12) + 6] =
					(r.transform.position.x + r.offsetX) * Screen.scale + (r.width * Screen.scale);
			vertices[(i * 12) + 7] = (r.transform.position.y + r.offsetY) * Screen.scale;
			vertices[(i * 12) + 8] = r.zIndex;
			vertices[(i * 12) + 9] =
					(r.transform.position.x + r.offsetX) * Screen.scale + (r.width * Screen.scale);
			vertices[(i * 12) + 10] =
					(r.transform.position.y + r.offsetY) * Screen.scale + (r.height * Screen.scale);
			vertices[(i * 12) + 11] = r.zIndex;

			indices[(i * 6)] = (short) (last);
			indices[(i * 6) + 1] = (short) (last + 1);
			indices[(i * 6) + 2] = (short) (last + 2);
			indices[(i * 6) + 3] = (short) (last);
			indices[(i * 6) + 4] = (short) (last + 2);
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
					return;
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

		ByteBuffer vb = ByteBuffer.allocateDirect(vertices.length * 4);
		vb.order(ByteOrder.nativeOrder());
		vertexBuffer = vb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		// initialize byte buffer for the draw list
		ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
		dlb.order(ByteOrder.nativeOrder());
		drawListBuffer = dlb.asShortBuffer();
		drawListBuffer.put(indices);
		drawListBuffer.position(0);

		ByteBuffer uvb = ByteBuffer.allocateDirect(uvs.length * 4);
		uvb.order(ByteOrder.nativeOrder());
		uvBuffer = uvb.asFloatBuffer();
		uvBuffer.put(uvs);
		uvBuffer.position(0);
	}
}
