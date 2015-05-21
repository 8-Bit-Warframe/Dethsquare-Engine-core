package com.ezardlabs.dethsquare;

import java.util.ArrayList;

public class Collider extends BoundedComponent {
	public static final ArrayList<Collider> normalColliders = new ArrayList<>();
	public static final ArrayList<Collider> staticColliders = new ArrayList<>();
	public static final ArrayList<Collider> triggerColliders = new ArrayList<>();
	public static QuadTree<Collider> qt = new QuadTree<>(20);
	public final RectF lastBounds = new RectF();
	private final float height;
	private final float width;
	ArrayList<Collider> possible = new ArrayList<>();
	private Collider[] triggers;
	boolean isTrigger = false;

	public enum CollisionLocation {
		TOP,
		RIGHT,
		BOTTOM,
		LEFT
	}

	public Collider(float width, float height) {
		this(width, height, false);
	}

	public Collider(float width, float height, boolean isTrigger) {
		this.width = width;
		this.height = height;
		this.isTrigger = isTrigger;
	}

	public static void init() {
		qt.init(staticColliders.toArray(new Collider[staticColliders.size()]));
	}

	public void start() {
		if (gameObject.isStatic || gameObject.name.equals("Door")) staticColliders.add(this);
		if (isTrigger) addTrigger();
		if (!gameObject.isStatic && !isTrigger) normalColliders.add(this);
		recalculateBounds();
	}

	private void addTrigger() {
		triggerColliders.add(this);
		triggers = new Collider[triggerColliders.size()];
	}

	private void removeTrigger() {
		triggerColliders.remove(this);
		triggers = new Collider[triggerColliders.size()];
	}

	public void destroy() {
		if (gameObject.isStatic) staticColliders.remove(this);
		if (isTrigger) removeTrigger();
		if (!gameObject.isStatic && !isTrigger) normalColliders.remove(this);
	}

	void move(float x, float y) {
		if (isTrigger) {
			transform.position.x += x;
			transform.position.y += y;
			recalculateBounds();
			triggerCheck();
			return;
		}
		possible.clear();

		lastBounds.left = bounds.left;
		lastBounds.top = bounds.top;
		lastBounds.right = bounds.right;
		lastBounds.bottom = bounds.bottom;
		if (x > 0) bounds.right += x;
		if (x < 0) bounds.left += x;
		if (y > 0) bounds.bottom += y;
		if (y < 0) bounds.top += y;

		QuadTree.retrieve(possible, qt, this);

		bounds.left = lastBounds.left;
		bounds.top = lastBounds.top;
		bounds.right = lastBounds.right;
		bounds.bottom = lastBounds.bottom;

		if (possible.size() > 0) {
			transform.position.y += y;
			recalculateBounds();
			for (Collider c : possible) {
				if (c != this && c != null && !c.isTrigger && RectF.intersects(bounds, c.bounds)) {
					if (y > 0 && bounds.bottom > c.bounds.top) {
						transform.position.y = Math.round(c.bounds.top - bounds.height());
//						if (gameObject.name.equals("Player")) PlayerBase.gravity = 0;
						gameObject.onCollision(c, CollisionLocation.BOTTOM);
					} else if (y < 0 && bounds.top < c.bounds.bottom) {
						transform.position.y = Math.round(c.bounds.bottom);
//						if (gameObject.name.equals("Player")) PlayerBase.gravity = 0;
						gameObject.onCollision(c, CollisionLocation.TOP);
					}
					recalculateBounds();
				}
			}
			transform.position.x += x;
			recalculateBounds();
			for (Collider c : possible) {
				if (c != this && c != null && !c.isTrigger && RectF.intersects(bounds, c.bounds)) {
					if (x > 0 && bounds.right > c.bounds.left) {
						transform.position.x = Math.round(c.bounds.left - bounds.width());
					} else if (x < 0 && bounds.left < c.bounds.right) {
						transform.position.x = Math.round(c.bounds.right);
					}
					recalculateBounds();
				}
			}
		} else {
			transform.position.x += x;
			transform.position.y += y;
		}
		recalculateBounds();
		triggerCheck();
	}

	public void recalculateBounds() {
		bounds.left = transform.position.x;
		bounds.top = transform.position.y;
		bounds.right = transform.position.x + width;
		bounds.bottom = transform.position.y + height;
	}

	public void triggerCheck() {
		if (isTrigger) {
			possible.clear();
			QuadTree.retrieve(possible, qt, this);
			for (BoundedComponent bc : possible) {
				if (RectF.intersects(bounds, bc.bounds)) {
					gameObject.onTriggerEnter((Collider) bc);
				}
			}
			for (Collider c : normalColliders) {
				if (c != this && RectF.intersects(bounds, c.bounds)) {
					gameObject.onTriggerEnter(c);
				}
			}
			for (Collider c : triggerColliders.toArray(triggers)) {
				if (c != this && c != null && RectF.intersects(bounds, c.bounds)) {
					c.gameObject.onTriggerEnter(this);
					gameObject.onTriggerEnter(c);
				}
			}
		} else {
			for (Collider c : triggerColliders) {
				if (c != this && RectF.intersects(bounds, c.bounds)) {
					c.gameObject.onTriggerEnter(this);
				}
			}
		}
	}
}
