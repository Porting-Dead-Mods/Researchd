package com.portingdeadmods.researchd.client.screens.lines;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Represents a potential overlap/intersection between line segments. May be empty.
 *
 * @param length    The length of the overlap, or 1 for point intersections
 * @param point     The intersection/middle point
 * @param seg1      First segment involved
 * @param seg2      Second segment involved
 * @param isOverlap True if segments overlap, false if they just intersect
 * @param isSegment True if the overlap is a segment (horizontal or vertical), false if it's just a point intersection
 */
public record PotentialOverlap(int length, Point point, LineSegment seg1, LineSegment seg2, boolean isOverlap, boolean isSegment) {
	@Override
	public @NotNull String toString() {
		return "PotentialOverlap{" +
				"length=" + length + ", " +
				"point=" + point + ", " +
				"isOverlap=" + isOverlap + ", " +
				"isSegment=" + isSegment +
				'}';
	}

	public static PotentialOverlap of(LineSegment seg1, LineSegment seg2) {
		// Both segments are vertical
		if (seg1.isVertical() && seg2.isVertical()) {
			if (seg1.a().x != seg2.a().x) return new PotentialOverlap(-1, null, seg1, seg2, false, false);

			int minY1 = Math.min(seg1.a().y, seg1.b().y);
			int maxY1 = Math.max(seg1.a().y, seg1.b().y);
			int minY2 = Math.min(seg2.a().y, seg2.b().y);
			int maxY2 = Math.max(seg2.a().y, seg2.b().y);

			if (maxY1 < minY2 || maxY2 < minY1) return new PotentialOverlap(-1, null, seg1, seg2, false, false);

			int overlapStart = Math.max(minY1, minY2);
			int overlapEnd = Math.min(maxY1, maxY2);
			int overlapLength = overlapEnd - overlapStart + 1;
			int midY = (overlapStart + overlapEnd) / 2;

			return new PotentialOverlap(
					overlapLength,
					new Point(seg1.a().x, midY),
					seg1,
					seg2,
					true,
					true
			);
		}

		// Both segments are horizontal
		if (seg1.isHorizontal() && seg2.isHorizontal()) {
			if (seg1.a().y != seg2.a().y) return new PotentialOverlap(-1, null, seg1, seg2, false, false);

			int minX1 = Math.min(seg1.a().x, seg1.b().x);
			int maxX1 = Math.max(seg1.a().x, seg1.b().x);
			int minX2 = Math.min(seg2.a().x, seg2.b().x);
			int maxX2 = Math.max(seg2.a().x, seg2.b().x);

			if (maxX1 < minX2 || maxX2 < minX1) return new PotentialOverlap(-1, null, seg1, seg2, false, false);

			int overlapStart = Math.max(minX1, minX2);
			int overlapEnd = Math.min(maxX1, maxX2);
			int overlapLength = overlapEnd - overlapStart + 1;
			int midX = (overlapStart + overlapEnd) / 2;

			return new PotentialOverlap(
					overlapLength,
					new Point(midX, seg1.a().y),
					seg1,
					seg2,
					true,
					true
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
						1,
						new Point(x, y),
						seg1,
						seg2,
						false, // This is an intersection, not an overlap
						false
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
						1,
						new Point(x, y),
						seg1,
						seg2,
						false, // This is an intersection, not an overlap
						false
				);
			}
		}

		return new PotentialOverlap(-1, null, seg1, seg2, false, false);
	}
}