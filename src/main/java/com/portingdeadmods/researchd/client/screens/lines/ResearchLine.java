package com.portingdeadmods.researchd.client.screens.lines;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import org.jetbrains.annotations.NotNull;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ResearchLine {
	private final LinkedList<Point> points;

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
	 * Creates a new line builder starting at the given coordinates
	 */
	public static ResearchLine start(int x, int y) {
		return start(new Point(x, y));
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
	 * Builds a simple L-shaped connection between two points.
	 * Goes vertical first, then horizontal.
	 */
	public static ResearchLine createLConnection(Point start, Point end) {
		return ResearchLine.start(start)
				.then(start.x, end.y)
				.then(end);
	}

	/**
	 * Builds a connection between input and output points of research nodes
	 * with an L-shaped path.
	 */
	public static ResearchLine connectNodes(Point outputPoint, Point inputPoint) {
		// Creates path from output -> input with a vertical segment followed by horizontal
		return createLConnection(outputPoint, inputPoint);
	}

	public void render(@NotNull GuiGraphics guiGraphics) {
		if (points.size() < 2) return;

		Point prev = points.getFirst();
		for (Point curr : points.stream().skip(1).toList()) {
			new LineSegment(prev, curr).render(guiGraphics);
			prev = curr;
		}
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
}