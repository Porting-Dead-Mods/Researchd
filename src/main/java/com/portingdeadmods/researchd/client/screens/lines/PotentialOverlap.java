package com.portingdeadmods.researchd.client.screens.lines;

import java.awt.*;

/**
 * Represents a potential overlap/intersection between line segments. May be empty.
 *
 * @param point     The intersection/middle point
 * @param seg1      First segment involved
 * @param seg2      Second segment involved
 * @param isOverlap True if segments overlap, false if they just intersect
 */
public record PotentialOverlap(Point point, LineSegment seg1, LineSegment seg2, boolean isOverlap) {
	@Override
	public String toString() {
		return "PotentialOverlap{" +
				"point=" + point +
				", isOverlap=" + isOverlap +
				'}';
	}

	public static PotentialOverlap of(LineSegment seg1, LineSegment seg2) {
		// Both segments are vertical
		if (seg1.isVertical() && seg2.isVertical()) {
			if (seg1.a().x != seg2.a().x) return null;

			int minY1 = Math.min(seg1.a().y, seg1.b().y);
			int maxY1 = Math.max(seg1.a().y, seg1.b().y);
			int minY2 = Math.min(seg2.a().y, seg2.b().y);
			int maxY2 = Math.max(seg2.a().y, seg2.b().y);

			if (maxY1 < minY2 || maxY2 < minY1) return null;

			int y = (Math.max(minY1, minY2) + Math.min(maxY1, maxY2)) / 2;
			return new PotentialOverlap(
					new Point(seg1.a().x, y),
					seg1,
					seg2,
					true  // Vertical overlaps are always true overlaps
			);
		}

		// Both segments are horizontal
		if (seg1.isHorizontal() && seg2.isHorizontal()) {
			if (seg1.a().y != seg2.a().y) return null;

			int minX1 = Math.min(seg1.a().x, seg1.b().x);
			int maxX1 = Math.max(seg1.a().x, seg1.b().x);
			int minX2 = Math.min(seg2.a().x, seg2.b().x);
			int maxX2 = Math.max(seg2.a().x, seg2.b().x);

			if (maxX1 < minX2 || maxX2 < minX1) return null;

			int x = (Math.max(minX1, minX2) + Math.min(maxX1, maxX2)) / 2;
			return new PotentialOverlap(
					new Point(x, seg1.a().y),
					seg1,
					seg2,
					true  // Horizontal overlaps are always true overlaps
			);
		}

		// One vertical, one horizontal - these are intersections, not overlaps
		if (seg1.isVertical() && seg2.isHorizontal()) {
			int x = seg1.a().x;
			int y = seg2.a().y;

			if (x >= Math.min(seg2.a().x, seg2.b().x) &&
					x <= Math.max(seg2.a().x, seg2.b().x) &&
					y >= Math.min(seg1.a().y, seg1.b().y) &&
					y <= Math.max(seg1.a().y, seg1.b().y)) {
				return new PotentialOverlap(
						new Point(x, y),
						seg1,
						seg2,
						false  // Cross intersections are not overlaps
				);
			}
		} else if (seg1.isHorizontal() && seg2.isVertical()) {
			int x = seg2.a().x;
			int y = seg1.a().y;

			if (x >= Math.min(seg1.a().x, seg1.b().x) &&
					x <= Math.max(seg1.a().x, seg1.b().x) &&
					y >= Math.min(seg2.a().y, seg2.b().y) &&
					y <= Math.max(seg2.a().y, seg2.b().y)) {
				return new PotentialOverlap(
						new Point(x, y),
						seg1,
						seg2,
						false  // Cross intersections are not overlaps
				);
			}
		}

		return null;
	}
}