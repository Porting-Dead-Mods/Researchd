package com.portingdeadmods.researchd.client.screens.lines;

import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;

/**
 * Represents a line segment between two points
 */
public class LineSegment {
	private final Point start;
	private final Point end;

	public LineSegment(Point start, Point end) {
		if ((start.x != end.x) && (start.y != end.y)) {
			throw new IllegalArgumentException("LineSegment must be either vertical or horizontal");
		}

		this.start = start;
		this.end = end;
	}

	/**
	 * @return The starting point of the line
	 */
	public Point a() {
		return this.start;
	}

	/**
	 * @return The ending point of the line
	 */
	public Point b() {
		return this.end;
	}

	public boolean isVertical() {
		return start.x == end.x;
	}

	public boolean isHorizontal() {
		return start.y == end.y;
	}

	public void render(GuiGraphics guiGraphics) {
		if (isVertical()) {
			guiGraphics.vLine(start.x, Math.min(start.y, end.y), Math.max(start.y, end.y), -1);
		} else if (isHorizontal()) {
			guiGraphics.hLine(Math.min(start.x, end.x), Math.max(start.x, end.x), start.y, -1);
		}
	}
}
