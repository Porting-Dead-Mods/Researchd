package com.portingdeadmods.researchd.client.screens.lines;

import com.portingdeadmods.researchd.events.ResearchedEvents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ResearchLine extends AbstractWidget {

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
	}

	public static ResearchLine INPUT_RESEARCH_HEAD(int x, int y) {
		return new ResearchLine(new LineCoordinates(x, y - 20, x + 4, y));
	}

	public static ResearchLine OUTPUT_RESEARCH_HEAD(int x, int y) {
		return new ResearchLine(new LineCoordinates(x, y, x + 4, y + 20));
	}

	public ArrayList<LineCoordinates> lines;

	public ResearchLine(LineCoordinates... lines) {
		super(getX(lines), getY(lines), getWidth(lines), getHeight(lines), CommonComponents.EMPTY);
		this.lines = new ArrayList<>();
	}

	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
		for (LineCoordinates line : lines) {
			guiGraphics.fill(line.x1, line.y1, line.x2, line.y2, 0xFF000000);
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
	public static int getWidth(LineCoordinates... lines) {
		int xLeft = Integer.MAX_VALUE;
		int xRight = Integer.MIN_VALUE;

		for (LineCoordinates line : lines) {
			xLeft = Math.min(xLeft, line.x1);
			xRight = Math.max(xRight, line.x2);
		}

		return xRight - xLeft;
	}

	public static int getHeight(LineCoordinates... lines) {
		int yTop = Integer.MAX_VALUE;
		int yBottom = Integer.MIN_VALUE;

		for (LineCoordinates line : lines) {
			yTop = Math.min(yTop, line.y1);
			yBottom = Math.max(yBottom, line.y2);
		}

		return yBottom - yTop;
	}

	public static int getX(LineCoordinates... lines) {
		int xLeft = Integer.MAX_VALUE;

		for (LineCoordinates line : lines) {
			xLeft = Math.min(xLeft, line.x1);
		}

		return xLeft;
	}

	public static int getY(LineCoordinates... lines) {
		int yTop = Integer.MAX_VALUE;

		for (LineCoordinates line : lines) {
			yTop = Math.min(yTop, line.y1);
		}

		return yTop;
	}
}
