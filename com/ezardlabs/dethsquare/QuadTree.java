package com.ezardlabs.dethsquare;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public final class QuadTree<T extends BoundedComponent> {
	private final int maxObjects;
	private RectF bounds;
	private ArrayList<T> objects = new ArrayList<>();
	public QuadTree[] nodes = new QuadTree[4];

	public QuadTree(int maxObjects) {
		this.maxObjects = maxObjects;
	}

	QuadTree(int maxObjects, RectF bounds) {
		this(maxObjects);
		this.bounds = bounds;
	}

	public static int getCount(QuadTree qt) {
		if (qt.nodes[0] != null) {
			int count = 0;
			for (QuadTree qt2 : qt.nodes) {
				count += getCount(qt2);
			}
			return count;
		}
		return qt.objects.size();
	}

	public final void init(BoundedComponent[] items) {
		float x = 0;
		float y = 0;
		for (BoundedComponent bc : items) {
			if (bc.gameObject == null || bc.gameObject.isStatic && bc.bounds.right > x)
				x = bc.bounds.right;
			if (bc.gameObject == null || bc.gameObject.isStatic && bc.bounds.bottom > y)
				y = bc.bounds.bottom;
		}
		if (x >= y) {
			bounds = new RectF(0, 0, (int) x, (int) x);
		} else if (y > x) {
			bounds = new RectF(0, 0, (int) y, (int) y);
		}
		for (BoundedComponent bc : items) {
			if (bc.gameObject == null || bc.gameObject.isStatic) {
				insert(bc);
			}
		}
		finalise(items);
	}

	static <T extends BoundedComponent> ArrayList<T> retrieve(ArrayList<T> returnObjects,
			QuadTree<T> qt, BoundedComponent bc) {
		if (qt.nodes[0] != null) {
			for (QuadTree qt2 : qt.nodes) {
				if (qt2.bounds.contains(bc.bounds)) {
					return retrieve(returnObjects, qt2, bc);
				}
			}
			for (QuadTree qt2 : qt.nodes) {
				if (RectF.intersects(qt2.bounds, bc.bounds)) {
					retrieve(returnObjects, qt2, bc);
				}
			}
			if (returnObjects.size() > 0) return returnObjects;
		}
		returnObjects.addAll(qt.objects);
		return returnObjects;
	}

	final ArrayList<T> getVisibleObjects(ArrayList<T> returnObjects, QuadTree qt, Camera c) {
		if (c.bounds.contains(qt.bounds)) { // quad is completely inside camera
			addAllChildren(returnObjects, qt);
		}
		if (RectF.intersects(c.bounds, qt.bounds)) { // camera and quad are intersecting
			if (qt.nodes[0] == null) {
				returnObjects.addAll(qt.objects);
			} else {
				for (QuadTree qt2 : qt.nodes) {
					getVisibleObjects(returnObjects, qt2, Camera.main);
				}
			}
		}
		return returnObjects;
	}

	final void addAllChildren(ArrayList<T> returnObjects, QuadTree qt) {
		if (qt.nodes[0] == null) {
			returnObjects.addAll(qt.objects);
		} else {
			for (QuadTree qt2 : qt.nodes) {
				addAllChildren(returnObjects, qt2);
			}
		}
	}

	private void finalise(BoundedComponent[] items) {
		objects.clear();
		if (nodes[0] == null) {
			for (BoundedComponent bc : items) {
				if (bounds.contains(bc.bounds) || RectF.intersects(bounds, bc.bounds)) {
					objects.add((T) bc);
				}
			}
		} else {
			for (QuadTree qt2 : nodes) {
				qt2.finalise(items);
			}
		}
	}

	public int insertCount = 0;

	private void insert(BoundedComponent bc) {
		insertCount++;
		if (nodes[0] != null) {
			for (int i = 0; i < 4; i++) {
				if (nodes[i].bounds.contains(bc.bounds)) {
					nodes[i].insert(bc);
					return;
				}
			}
			for (int i = 0; i < 4; i++) {
				if (RectF.intersects(nodes[i].bounds, bc.bounds)) nodes[i].insert(bc);
			}
		} else {
			objects.add((T) bc);
			if (objects.size() > maxObjects) {
				if (nodes[0] == null) split();
				outer:
				while (objects.size() > 0) {
					for (int j = 0; j < 4; j++) {
						if (nodes[j].bounds.contains(bc.bounds)) {
							nodes[j].insert(objects.remove(0));
							continue outer;
						}
					}
					BoundedComponent bc2 = objects.remove(0);
					for (int j = 0; j < 4; j++) {
						if (RectF.intersects(nodes[j].bounds, bc2.bounds)) nodes[j].insert(bc2);
					}
				}
			}
		}
	}

	private void split() {
		float subWidth = bounds.width() / 2f;
		float subHeight = bounds.height() / 2f;
		float x = bounds.left;
		float y = bounds.top;
		nodes[0] = new QuadTree(maxObjects,
				new RectF(x, y, x + subWidth, y + subHeight)); // top left
		nodes[1] = new QuadTree(maxObjects,
				new RectF(x + subWidth, y, x + (subWidth * 2), y + subHeight)); // top right
		nodes[2] = new QuadTree(maxObjects,
				new RectF(x, y + subHeight, x + subWidth, y + (subHeight * 2))); // bottom left
		nodes[3] = new QuadTree(maxObjects,
				new RectF(x + subWidth, y + subHeight, x + (subWidth * 2),
						y + (subHeight * 2))); // bottom right
	}
}
