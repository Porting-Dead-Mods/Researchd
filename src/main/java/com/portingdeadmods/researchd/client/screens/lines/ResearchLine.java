package com.portingdeadmods.researchd.client.screens.lines;

import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ResearchLine extends AbstractWidget {

	/**
	 * A dirty fix for custom lines consisting of a fill between 2 points
	 */
	public static class LineCoordinates {
		public int x1;
		public int y1;
		public int x2;
		public int y2;

		public LineCoordinates(int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		public String toString() {
			return "LineCoordinates{" +
					"x1=" + x1 +
					", y1=" + y1 +
					", x2=" + x2 +
					", y2=" + y2 +
					'}';
		}
	}

	/**
	 * @param node {@link ResearchNode} to get input heads for
	 * @return Set of {@link LineCoordinates} for every input head
	 */
	public static Set<LineCoordinates> getInputHeadPositions(ResearchNode node) {
		Set<LineCoordinates> positions = new HashSet<>();
		int width = node.getWidth();   // Node width
		int height = node.getHeight(); // Node height
		int lineWidth = 4;             // Line width
		int inputs = 2;                // TODO: Number of parents

		if (inputs == 1) {
			int x = node.getX() + node.getWidth() / 2;
			int y = node.getY();

			positions.add(new LineCoordinates(x, y + 20,x + 4, y));
			return positions;
		}

		for (int i = 0; i < inputs; i++) {
			int x = node.getX() + (i * (width - lineWidth) / (inputs - 1)) + (lineWidth / 2);
			int y = node.getY();

			positions.add(new LineCoordinates(x, y + 20, x + 4, y));
		}

		return positions;
	}

	/**
	 * @param node {@link ResearchNode} to get output heads for
	 * @return Set of {@link LineCoordinates} for every output head
	 */
	public static Set<LineCoordinates> getOutputHeadPositions(ResearchNode node) {
		Set<LineCoordinates> positions = new HashSet<>();
		int width = node.getWidth();   // Node width
		int height = node.getHeight(); // Node height
		int lineWidth = 4;             // Line width
		int outputs = 2;                // TODO: Number of children

		if (outputs == 1) {
			int x = node.getX() + node.getWidth() / 2;
			int y = node.getY() + height;

			positions.add(new LineCoordinates(x, y,x + 4, y + 20));
			return positions;
		}

		for (int i = 0; i < outputs; i++) {
			int x = node.getX() + (i * (width - lineWidth) / (outputs - 1)) + (lineWidth / 2);
			int y = node.getY() + height;

			positions.add(new LineCoordinates(x, y, x + 4, y + 20));
		}

		return positions;
	}

	/**
	 * @param node {@link ResearchNode} to get input heads for
	 * @return {@link ResearchLine} object consisting of every input head
	 */
	public static ResearchLine getInputResearchHeads(ResearchNode node) {
		return new ResearchLine(getInputHeadPositions(node));
	}

	/**
	 * @param node {@link ResearchNode} to get output heads for
	 * @return {@link ResearchLine} object consisting of every output head
	 */
	public static ResearchLine getOutputResearchHeads(ResearchNode node) {
		return new ResearchLine(getOutputHeadPositions(node));
	}

	public ArrayList<LineCoordinates> lines;

	public ResearchLine(Collection<LineCoordinates> lines) {
		super(getX(lines), getY(lines), getWidth(lines), getHeight(lines), CommonComponents.EMPTY);
		this.lines = new ArrayList<>(lines);
	}

	@Override
	protected void renderWidget(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
		for (LineCoordinates line : lines) {
			// TODO: Fix lines not rendering even though they are in the correct position
			//System.out.printf("Rendering line: %d:%d -> %d:%d\n", line.x1, line.y1, line.x2, line.y2);
			guiGraphics.fill(line.x1, line.y1, line.x2, line.y2, 0xFFFFFFFF);
		}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

	}

	public List<LineCoordinates> getLineComponents() {
		return this.lines;
	}

	// Instance methods
	public int getWidth() {
		int xLeft = Integer.MAX_VALUE;
		int xRight = Integer.MIN_VALUE;

		for (LineCoordinates line : lines) {
			xLeft = Math.min(xLeft, line.x1);
			xRight = Math.max(xRight, line.x2);
		}

		return xRight - xLeft;
	}

	public int getHeight() {
		int yTop = Integer.MAX_VALUE;
		int yBottom = Integer.MIN_VALUE;

		for (LineCoordinates line : lines) {
			yTop = Math.min(yTop, line.y1);
			yBottom = Math.max(yBottom, line.y2);
		}

		return yBottom - yTop;
	}

	public int getX() {
		int xLeft = Integer.MAX_VALUE;

		for (LineCoordinates line : lines) {
			xLeft = Math.min(xLeft, line.x1);
		}

		return xLeft;
	}

	public int getY() {
		int yTop = Integer.MAX_VALUE;

		for (LineCoordinates line : lines) {
			yTop = Math.min(yTop, line.y1);
		}

		return yTop;
	}

	// Static methods

	/**
	 * @param lines Collection of {@link LineCoordinates}
	 * @return The "width" of the collection of lines, the distance between the leftmost and rightmost points
	 */
	public static int getWidth(Iterable<LineCoordinates> lines) {
		int xLeft = Integer.MAX_VALUE;
		int xRight = Integer.MIN_VALUE;

		for (LineCoordinates line : lines) {
			xLeft = Math.min(xLeft, line.x1);
			xRight = Math.max(xRight, line.x2);
		}

		return xRight - xLeft;
	}

	/**
	 * @param lines Collection of {@link LineCoordinates}
	 * @return The "height" of the collection of lines, the distance between the topmost and bottommost points
	 */
	public static int getHeight(Iterable<LineCoordinates> lines) {
		int yTop = Integer.MAX_VALUE;
		int yBottom = Integer.MIN_VALUE;

		for (LineCoordinates line : lines) {
			yTop = Math.min(yTop, line.y1);
			yBottom = Math.max(yBottom, line.y2);
		}

		return yBottom - yTop;
	}

	/**
	 * @param lines Collection of {@link LineCoordinates}
	 * @return The leftmost x coordinate of the collection of lines
	 */
	public static int getX(Iterable<LineCoordinates> lines) {
		int xLeft = Integer.MAX_VALUE;

		for (LineCoordinates line : lines) {
			xLeft = Math.min(xLeft, line.x1);
		}

		return xLeft;
	}

	/**
	 * @param lines Collection of {@link LineCoordinates}
	 * @return The topmost y coordinate of the collection of lines
	 */
	public static int getY(Iterable<LineCoordinates> lines) {
		int yTop = Integer.MAX_VALUE;

		for (LineCoordinates line : lines) {
			yTop = Math.min(yTop, line.y1);
		}

		return yTop;
	}
}
