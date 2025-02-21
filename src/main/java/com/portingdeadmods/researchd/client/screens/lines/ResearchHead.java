package com.portingdeadmods.researchd.client.screens.lines;

import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import com.portingdeadmods.researchd.utils.UniqueArray;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class ResearchHead {
	private int x;
	private int y;
	private final boolean isInput;

	/**
	 *
	 * @param x Should be based on the x position of the node, split evenly if there are multiple heads
	 * @param y Should be the y position of the node <br>(+ Node height if it's output)
	 * @param isInput Should be true if it's an input head, false if it's an output head
	 */
	public ResearchHead(int x, int y, boolean isInput) {
		this.x = x;
		this.y = y;
		this.isInput = isInput;
	}

	public int getX() { return this.x; }
	public int getY() { return this.y; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }


	/**
	 * @return The point where the last pixel is drawn for the head (going from the node outwards)
	 */
	public Point getConnectionPoint() {
		return this.isInput ? new Point(this.x, this.y - 3) : new Point(this.x, this.y + 3);
	}

	public boolean isInput() { return isInput; }

	public void translate(int dx, int dy) {
		this.x += dx;
		this.y += dy;
	}

	public void render(GuiGraphics graphics) {
		if (this.isInput)
			graphics.vLine(this.x, this.y - 4, this.y, -1);
		else
			graphics.vLine(this.x, this.y, this.y + 4, -1);
	}

	/**
	 * @param node {@link ResearchNode} to get input heads for
	 * @return {@link UniqueArray} of input heads, order left -> right
	 */
	public static UniqueArray<ResearchHead> inputsOf(ResearchNode node) {
		UniqueArray<ResearchHead> positions = new UniqueArray<>();
		int parentCount = node.getParents().size();
		int width = ResearchScreenWidget.PANEL_WIDTH; // Node width

		if (parentCount == 0) return positions;

		// If one, create a centered head
		if (parentCount == 1) {
			int x = node.getX() + width / 2;
			int y = node.getY();
			positions.add(new ResearchHead(x, y, true));
			return positions;
		}

		int startingX = (parentCount % 2 == 0) ? (node.getX() + width / 2 - 2) : (node.getX() + width / 2);

		startingX -= ((parentCount - 1) / 2) * 3; // 3 px per pair of heads

		for (int i = 0; i < parentCount; i++) {
			positions.addLast(new ResearchHead(startingX + i * 3, node.getY(), true));
		}

		return positions;
	}

	/**
	 * @param node {@link ResearchNode} to get output heads for
	 * @return {@link UniqueArray} of output heads, order left -> right
	 */
	public static UniqueArray<ResearchHead> outputsOf(ResearchNode node) {
		UniqueArray<ResearchHead> positions = new UniqueArray<>();
		int childCount = node.getChildren().size();
		int width = ResearchScreenWidget.PANEL_WIDTH; // Node width

		if (childCount == 0) return positions;

		// If one, create a centered head
		if (childCount == 1) {
			int x = node.getX() + width / 2;
			int y = node.getY() + ResearchScreenWidget.PANEL_HEIGHT - 1;
			positions.add(new ResearchHead(x, y, false));
			return positions;
		}

		int startingX = (childCount % 2 == 0) ? (node.getX() + width / 2 - 2) : (node.getX() + width / 2);

		startingX -= ((childCount - 1) / 2) * 3; // 2 px per pair of heads

		for (int i = 0; i < childCount; i++) {
			positions.addLast(new ResearchHead(startingX + i * 3, node.getY() + ResearchScreenWidget.PANEL_HEIGHT - 1, false));
		}

		return positions;
	}
}