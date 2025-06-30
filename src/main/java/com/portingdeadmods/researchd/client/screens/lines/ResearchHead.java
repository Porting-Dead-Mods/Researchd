package com.portingdeadmods.researchd.client.screens.lines;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.ResearchScreenWidget;
import com.portingdeadmods.researchd.client.screens.graph.ResearchNode;
import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceKey;

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
		UniqueArray<ResearchNode> parents = node.getParents();
		int parentCount = parents.size();
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
			positions.addLast(new ResearchHead(startingX + i * 3,
					node.getY(),
					true
			));
		}

		return positions;
	}

	/**
	 * @param node {@link ResearchNode} to get output heads for
	 * @return {@link UniqueArray} of output heads, order left -> right
	 */
	public static UniqueArray<ResearchHead> outputsOf(ResearchNode node) {
		UniqueArray<ResearchHead> positions = new UniqueArray<>();
		UniqueArray<ResearchNode> children = node.getChildren();
		int childCount = children.size();
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
			positions.addLast(new ResearchHead(
					startingX + i * 3,
					node.getY() + ResearchScreenWidget.PANEL_HEIGHT - 1,
					false
			));
		}

		return positions;
	}

	/**
	 * @param node {@link ResearchNode} to get input heads for
	 * @param visibleNodes {@link Set} of visible nodes in the current graph
	 * @return {@link UniqueArray} of input heads, order left -> right
	 */
	public static UniqueArray<ResearchHead> inputsOf(ResearchNode node, Set<ResearchNode> visibleNodes) {
		UniqueArray<ResearchHead> positions = new UniqueArray<>();
		UniqueArray<ResearchNode> parents = new UniqueArray<>();

		// Only consider parents that are actually visible in the current graph
		for (ResearchNode parent : node.getParents()) {
			if (visibleNodes.contains(parent)) {
				parents.add(parent);
			}
		}

		int parentCount = parents.size();
		int width = ResearchScreenWidget.PANEL_WIDTH;

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
			positions.addLast(new ResearchHead(startingX + i * 3,
					node.getY(),
					true
					));
		}

		return positions;
	}

	/**
	 * @param node {@link ResearchNode} to get output heads for
	 * @param visibleNodes {@link Set} of visible nodes in the current graph
	 * @return {@link UniqueArray} of output heads, order left -> right
	 */
	public static UniqueArray<ResearchHead> outputsOf(ResearchNode node, Set<ResearchNode> visibleNodes) {
		UniqueArray<ResearchHead> positions = new UniqueArray<>();
		UniqueArray<ResearchNode> children = new UniqueArray<>();

		// Only consider children that are actually visible in the current graph
		for (ResearchNode child : node.getChildren()) {
			if (visibleNodes.contains(child)) {
				children.add(child);
			}
		}

		int childCount = children.size();
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
			positions.addLast(new ResearchHead(
					startingX + i * 3,
					node.getY() + ResearchScreenWidget.PANEL_HEIGHT - 1,
					false
			));
		}

		return positions;
	}

	/**
	 * Creates a specific number of input heads for a node, evenly distributed along the top edge.
	 *
	 * @param node The research node to create input heads for
	 * @param count The number of input heads to create
	 * @return A UniqueArray of evenly distributed input heads
	 */
	public static UniqueArray<ResearchHead> createInputHeads(ResearchNode node, int count) {
		UniqueArray<ResearchHead> heads = new UniqueArray<>();
		if (count <= 0) return heads;

		int width = ResearchScreenWidget.PANEL_WIDTH;
		int x = node.getX();
		int y = node.getY();

		// If one, create a centered head
		if (count == 1) {
			heads.add(new ResearchHead(x + width / 2, y, true));
			return heads;
		}

		// For multiple heads, evenly distribute them
		int startingX = (count % 2 == 0) ? (x + width / 2 - 2) : (x + width / 2);
		startingX -= ((count - 1) / 2) * 3; // 3 px spacing between heads

		for (int i = 0; i < count; i++) {
			heads.add(new ResearchHead(startingX + i * 3, y, true));
		}

		return heads;
	}

	/**
	 * Creates a specific number of output heads for a node, evenly distributed along the bottom edge.
	 *
	 * @param node The research node to create output heads for
	 * @param count The number of output heads to create
	 * @return A UniqueArray of evenly distributed output heads
	 */
	public static UniqueArray<ResearchHead> createOutputHeads(ResearchNode node, int count) {
		UniqueArray<ResearchHead> heads = new UniqueArray<>();
		if (count <= 0) return heads;

		int width = ResearchScreenWidget.PANEL_WIDTH;
		int height = ResearchScreenWidget.PANEL_HEIGHT;
		int x = node.getX();
		int y = node.getY() + height - 1; // Bottom of the node

		// If one, create a centered head
		if (count == 1) {
			heads.add(new ResearchHead(x + width / 2, y, false));
			return heads;
		}

		// For multiple heads, evenly distribute them
		int startingX = (count % 2 == 0) ? (x + width / 2 - 2) : (x + width / 2);
		startingX -= ((count - 1) / 2) * 3; // 3 px spacing between heads

		for (int i = 0; i < count; i++) {
			heads.add(new ResearchHead(startingX + i * 3, y, false));
		}

		return heads;
	}
}