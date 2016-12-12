package com.ezardlabs.dethsquare;

public class Physics {

	public static RaycastHit raycast(Vector2 origin, Vector2 direction, float distance) {
		direction.normalise();
		Vector2 end = new Vector2(origin.x + direction.x * distance,
				origin.y + direction.y * distance);
		RaycastHit raycastHit = null;
		for (Collider c : Collider.staticColliders) {
			Vector2 hit = c.bounds.intersect(origin, end);
			if (hit != null) {
				if (raycastHit == null) {
					raycastHit = new RaycastHit(hit, Vector2.distance(origin, hit), c.transform, c);
				} else if (Vector2.distance(origin, hit) < raycastHit.distance) {
					raycastHit.point = hit;
					raycastHit.distance = Vector2.distance(origin, hit);
					raycastHit.transform = c.transform;
					raycastHit.collider = c;
				}
			}
		}
		return raycastHit;
	}

	public static class RaycastHit {
		public Collider collider;
		public double distance;
		public Vector2 point;
		public Transform transform;

		RaycastHit(Vector2 point, double distance, Transform transform, Collider collider) {
			this.point = point;
			this.distance = distance;
			this.transform = transform;
			this.collider = collider;
		}
	}
}
