package com.portingdeadmods.researchd.client.screens.research.graph.lines;

import com.portingdeadmods.researchd.Researchd;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;

import java.awt.*;

/**
 * Represents a line segment between two points
 */
public class LineSegment implements Renderable {
	private final Point start;
	private final Point end;

	public LineSegment(Point start, Point end) {
		if ((start.x != end.x) && (start.y != end.y)) {
			Researchd.LOGGER.error(
					"LineSegment must be either vertical or horizontal. Provided: (%d, %d) - (%d, %d)".formatted(start.x, start.y, end.x, end.y),
					new IllegalArgumentException("LineSegment must be either vertical or horizontal. Provided: (%d, %d) - (%d, %d)".formatted(start.x, start.y, end.x, end.y))
			);

			this.start = start;
			this.end = new Point(start.x, end.y);
		} else {
			this.start = start;
			this.end = end;
		}
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

	public void render(GuiGraphics guiGraphics, int color) {
		if (isVertical()) {
			guiGraphics.vLine(start.x, Math.min(start.y, end.y), Math.max(start.y, end.y), color);
		} else if (isHorizontal()) {
			guiGraphics.hLine(Math.min(start.x, end.x), Math.max(start.x, end.x), start.y, color);
		}
	}

	@Override
	public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
		render(guiGraphics, -1);
	}
}
