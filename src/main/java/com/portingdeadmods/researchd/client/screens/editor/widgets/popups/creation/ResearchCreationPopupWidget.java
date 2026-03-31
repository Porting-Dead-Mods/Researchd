package com.portingdeadmods.researchd.client.screens.editor.widgets.popups.creation;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.client.RememberingLinearLayout;
import com.portingdeadmods.researchd.client.impl.editor.EditorContextImpl;
import com.portingdeadmods.researchd.api.client.editor.StandaloneEditorObject;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.client.screens.editor.widgets.popups.SelectPackPopupWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.DraggablePopupWidget;
import com.portingdeadmods.researchd.client.screens.lib.widgets.ScrollableWidget;
import com.portingdeadmods.researchd.client.screens.research.ResearchScreen;
import com.portingdeadmods.researchd.client.screens.research.widgets.PDLButton;
import com.portingdeadmods.researchd.impl.research.SimpleResearch;
import com.portingdeadmods.researchd.networking.editor.CreateResearchPayload;
import com.portingdeadmods.researchd.utils.researches.ResearchdManagers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.function.Function;

public class ResearchCreationPopupWidget extends AbstractStandaloneCreationPopupWidget<Research> {
    public static final ResourceLocation DEFAULT_ID = Researchd.rl(SimpleResearch.ID);

    public ResearchCreationPopupWidget(int x, int y, int width, int height) {
        super(DEFAULT_ID, ResearchdClient.CLIENT_RESEARCHES::get, x, y, width, height);
    }

    @Override
    protected void insertObjectToData(ResourceLocation id, Research object) {
        ResearchdManagers.getResearchesManager(Minecraft.getInstance().level).mergeContents(Collections.singletonMap(id, object));
        PacketDistributor.sendToServer(new CreateResearchPayload(ResourceKey.create(ResearchdRegistries.RESEARCH_KEY, id), object, true));
    }

    @Override
    protected Component getTitle() {
        return Component.literal("Create Research");
    }
}
