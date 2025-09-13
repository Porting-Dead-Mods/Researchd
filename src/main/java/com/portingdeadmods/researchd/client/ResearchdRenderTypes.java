package com.portingdeadmods.researchd.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.portingdeadmods.researchd.Researchd;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;

import static net.minecraft.client.renderer.RenderStateShard.*;

public class ResearchdRenderTypes {
	public static final RenderType LINES_NONTRANSLUCENT = createDefault(
			Researchd.MODID+":nontranslucent_lines", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES,
			RenderType.CompositeState.builder()
					.setShaderState(RENDERTYPE_LINES_SHADER)
					.setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(2)))
					.setLayeringState(VIEW_OFFSET_Z_LAYERING)
					.setTransparencyState(NO_TRANSPARENCY)
					.setOutputState(ITEM_ENTITY_TARGET)
					.setWriteMaskState(COLOR_DEPTH_WRITE)
					.setCullState(NO_CULL)
					.createCompositeState(false)
	);

	private static RenderType createDefault(String name, VertexFormat format, VertexFormat.Mode mode, RenderType.CompositeState state) {
		return RenderType.create(name, format, mode, 256, false, false, state);
	}
}
