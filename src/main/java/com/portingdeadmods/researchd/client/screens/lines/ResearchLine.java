package com.portingdeadmods.researchd.client.screens.lines;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ResearchLine implements Renderable {
	private final LinkedList<Point> points;
	private @Nullable ResearchHead startHead;
	private @Nullable ResearchHead endHead;
	private int color = FastColor.ARGB32.color(255, 255, 255, 255);

	private ResearchLine() {
		this.points = new LinkedList<>();
	}

	/**
	 * Creates a new line builder starting at the given point
	 */
	public static ResearchLine start(Point start) {
		ResearchLine line = new ResearchLine();
		line.points.add(start);
		return line;
	}

	/**
	 * Creates a new line builder starting at the given research head
	 */
	public static ResearchLine start(ResearchHead start) {
		ResearchLine line = new ResearchLine();
		line.points.add(start.getConnectionPoint());
		line.startHead = start;
		return line;
	}

	/**
	 * Creates a new line builder starting at the given coordinates
	 */
	public static ResearchLine start(int x, int y) {
		return start(new Point(x, y));
	}

	public ResearchLine setEndHead(@NotNull ResearchHead endHead) {
		this.endHead = endHead;
		return this;
	}

	public ResearchLine setStartHead(@NotNull ResearchHead startHead) {
		this.startHead = startHead;
		return this;
	}

	public @Nullable ResearchHead getStartHead() {
		return startHead;
	}

	public @Nullable ResearchHead getEndHead() {
		return endHead;
	}

	/**
	 * Adds a point to the line path
	 */
	public ResearchLine then(Point point) {
		points.add(point);
		return this;
	}

	/**
	 * Adds a point to the line path using coordinates
	 */
	public ResearchLine then(int x, int y) {
		return then(new Point(x, y));
	}

	/**
	 * Adds a relative horizontal line segment
	 */
	public ResearchLine horizontal(int dx) {
		Point last = points.getLast();
		return then(last.x + dx, last.y);
	}

	/**
	 * Adds a relative vertical line segment
	 */
	public ResearchLine vertical(int dy) {
		Point last = points.getLast();
		return then(last.x, last.y + dy);
	}

	/**
	 * Shifts the index'th line by the given delta. <br>
	 * If the line is vertical, it will shift it horizontally (x += delta). <br>
	 * If the line is horizontal, it will shift it vertically (y += delta). <br>
	 * <br>
	 * This method keeps continuity of the line intact. <br>
	 * This method DOES NOT create any other points. <br>
	 * @see #shiftAndCreate(int, int) for bends.
	 *
	 * @param index The line index
	 * @param delta The amount of shifting
	 * @return self
	 *
	 * @throws IndexOutOfBoundsException if the index is out of bounds
	 * @throws IllegalArgumentException if the index is 0 or the last index (if the line is linked to a ResearchHead)
	 */
	public ResearchLine shift(int index, int delta) {
		if (this.points.size() < index || index < 0) throw new IndexOutOfBoundsException("Index out of bounds for shifting: " + index);
		if (index == 0 || index == this.points.size() - 2) throw new IllegalArgumentException("Cannot shift a line linked to a ResearchHead");

		Point prev = this.points.get(index - 1);
		Point a = this.points.get(index);
		Point b = this.points.get(index + 1);
		Point next = this.points.get(index + 2);

		boolean isVertical = a.x == b.x;
		if (isVertical) {
			if ((prev.y != a.y) || (next.y != b.y)) {
				// The other 2 segments would need to be horizontal to keep continuity
				throw new IllegalStateException("The shift would create a discontinuity in the line");
			}

			// Shift horizontally
			this.points.set(index, new Point(a.x + delta, a.y));
			this.points.set(index + 1, new Point(b.x + delta, b.y));
		} else {
			if ((prev.x != a.x) || (next.x != b.x)) {
				// The other 2 segments would need to be vertical to keep continuity
				throw new IllegalStateException("The shift would create a discontinuity in the line");
			}

			// Shift vertically
			this.points.set(index, new Point(a.x, a.y + delta));
			this.points.set(index + 1, new Point(b.x, b.y + delta));
		}

		return this;
	}

	/**
	 * Shifts the index'th line by the given delta. <br>
	 * If the line is vertical, it will shift it horizontally (x += delta). <br>
	 * If the line is horizontal, it will shift it vertically (y += delta). <br>
	 * <br>
	 * This method keeps continuity of the line intact. <br>
	 * If a bent gets created, the original points will be duplicated to create a bend. <br>
	 *
	 * @param index The line index
	 * @param delta The amount of shifting
	 * @return self
	 *
	 * @throws IndexOutOfBoundsException if the index is out of bounds
	 */
	public ResearchLine shiftAndCreate(int index, int delta) {
		if (this.points.size() < index || index < 0) throw new IndexOutOfBoundsException("Index out of bounds for shifting: " + index);

		boolean duplicateA = false;
		boolean duplicateB = false;

		// Check if the line index is linked to a ResearchHead
		if (index == 0) {
			duplicateA = true;
		}
		if (index == this.points.size() - 2) {
			duplicateB = true;
		}

		Point a = this.points.get(index);
		Point b = this.points.get(index + 1);

		boolean isVertical = a.x == b.x;

		if (isVertical) {
			if (!duplicateA) {
				Point prev = this.points.get(index - 1);
				if (prev.y != a.y) { // If not horizontal
					duplicateA = true;
				}
			}
			if (!duplicateB) {
				Point next = this.points.get(index + 2);
				if (next.y != b.y) { // If not horizontal
					duplicateB = true;
				}
			}
		} else {
			if (!duplicateA) {
				Point prev = this.points.get(index - 1);
				if (prev.x != a.x) { // If not vertical
					duplicateA = true;
				}
			}
			if (!duplicateB) {
				Point next = this.points.get(index + 2);
				if (next.x != b.x) { // If not vertical
					duplicateB = true;
				}
			}
		}

		if (duplicateA) {
			// Duplicate the point at index to create a bend
			this.points.add(this.points.indexOf(a), new Point(a.x, a.y));
		}

		if (duplicateB) {
			this.points.add(this.points.indexOf(b) + 1, new Point(b.x, b.y));
		}

		if (isVertical) {
			// Shift horizontally
			a.x += delta;
			b.x += delta;
		} else {
			// Shift vertically
			a.y += delta;
			b.y += delta;
		}

		return this;
	}

	/**
	 * Builds a simple L-shaped connection between two points.
	 * Goes vertical first, then horizontal.
	 */
	public static ResearchLine createLConnection(Point start, Point end, boolean verticalFirst) {
		if (verticalFirst) {
			return ResearchLine.start(start)
					.then(start.x, end.y)
					.then(end);
		} else {
			return ResearchLine.start(start)
					.then(end.x, start.y)
					.then(end);
		}
	}

	public static ResearchLine createLConnection(ResearchHead start, ResearchHead end, boolean verticalFirst) {
		return createLConnection(start.getConnectionPoint(), end.getConnectionPoint(), verticalFirst)
				.setStartHead(start)
				.setEndHead(end);
	}

	/**
	 * The same as the L connection but with a vertical offset.
	 * Goes vertical first (verticalOffset), then horizontal, then the remaining vertical distance.
	 */
	public static ResearchLine createSConnection(Point start, Point end, int verticalOffset) {
		if (verticalOffset < 0 || verticalOffset > Math.abs(start.y - end.y)) {
			throw new IllegalArgumentException("Vertical offset must be between 0 and the vertical distance between the points");
		}

		if (start.y < end.y) {
			return ResearchLine.start(start)
					.then(start.x, start.y + verticalOffset)
					.then(end.x, start.y + verticalOffset)
					.then(end);
		} else {
			return ResearchLine.start(start)
					.then(start.x, start.y - verticalOffset)
					.then(end.x, start.y - verticalOffset)
					.then(end);
		}
	}


	public static ResearchLine createSConnection(ResearchHead start, ResearchHead end, int verticalOffset) {
		return createSConnection(start.getConnectionPoint(), end.getConnectionPoint(), verticalOffset)
				.setStartHead(start)
				.setEndHead(end);
	}

	/**
	 * Creates a direct connection between two research heads.
	 * This will not create any bends or curves, just a straight line.
	 * At the condition that either the X or the Y coordinate of the start and end points are the same.
	 */
	public static ResearchLine direct(ResearchHead start, ResearchHead end) {
		return ResearchLine.start(start.getConnectionPoint())
				.then(end.getConnectionPoint())
				.setStartHead(start)
				.setEndHead(end);
	}

	/**
	 * Builds a connection between input and output points of research nodes
	 * with an L-shaped path.
	 */
	public static ResearchLine connectNodes(Point outputPoint, Point inputPoint, boolean verticalFirst) {
		// Creates path from output -> input with a vertical segment followed by horizontal
		return createLConnection(outputPoint, inputPoint, verticalFirst);
	}

	public void render(@NotNull GuiGraphics guiGraphics) {
		if (points.size() < 2) return;

		Point prev = points.getFirst();
		for (Point curr : points.stream().skip(1).toList()) {
			new LineSegment(prev, curr).render(guiGraphics, this.getColor());
			prev = curr;
		}
	}

	@Override
	public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
		render(guiGraphics);
	}

	/**
	 * Gets all points in this line
	 */
	public LinkedList<Point> getPoints() {
		return new LinkedList<>(points);
	}

	/**
	 * Translates all points in this line by the given amounts
	 */
	public ResearchLine translate(int dx, int dy) {
		points.replaceAll(p -> new Point(p.x + dx, p.y + dy));
		return this;
	}

	/**
	 * Returns the start point of this line
	 */
	public Point getStart() {
		return points.getFirst();
	}

	/**
	 * Returns the end point of this line
	 */
	public Point getEnd() {
		return points.getLast();
	}

	/**
	 * Finds all intersections/overlaps between this line and another line
	 * @param other The other ResearchLine to check against
	 * @return Set of PotentialOverlaps where the lines intersect or overlap
	 */
	public Set<PotentialOverlap> getOverlaps(ResearchLine other) {
		return getOverlaps(this, other);
	}

	/**
	 * Extracts all the line segments from a research line
	 */
	private static List<LineSegment> getLineSegments(ResearchLine line) {
		List<LineSegment> segments = new ArrayList<>();
		List<Point> points = line.getPoints().stream().toList();

		for (int i = 0; i < points.size() - 1; i++) {
			segments.add(new LineSegment(points.get(i), points.get(i + 1)));
		}

		return segments;
	}

	/**
	 * Finds all intersections/overlaps between two research lines
	 * @param line1 First ResearchLine
	 * @param line2 Second ResearchLine
	 * @return Set of PotentialOverlaps where the lines intersect or overlap
	 */
	public static Set<PotentialOverlap> getOverlaps(ResearchLine line1, ResearchLine line2) {
		Set<PotentialOverlap> overlaps = new HashSet<>();

		List<LineSegment> segments1 = getLineSegments(line1);
		List<LineSegment> segments2 = getLineSegments(line2);

		for (LineSegment seg1 : segments1) {
			for (LineSegment seg2 : segments2) {
				PotentialOverlap overlap = PotentialOverlap.of(seg1, seg2);
				if (overlap != null) {
					overlaps.add(overlap);
				}
			}
		}

		return overlaps;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
}

