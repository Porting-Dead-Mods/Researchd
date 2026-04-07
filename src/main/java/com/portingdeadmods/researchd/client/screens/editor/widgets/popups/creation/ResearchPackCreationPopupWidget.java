package com.portingdeadmods.researchd.client.screens.editor.widgets.popups.creation;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdClient;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.packs.ResearchPack;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.networking.editor.CreateResearchPackPayload;
import com.portingdeadmods.researchd.utils.researches.ResearchdManagers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collections;

public class ResearchPackCreationPopupWidget extends AbstractStandaloneCreationPopupWidget<ResearchPack> {
    public static final ResourceLocation DEFAULT_ID = Researchd.rl(ResearchPackImpl.ID);

    public ResearchPackCreationPopupWidget(int x, int y, int width, int height) {
        super(DEFAULT_ID, ResearchdClient.CLIENT_RESEARCH_PACKS::get, null, x, y, width, height);
    }

    @Override
    protected void insertObjectToData(ResourceLocation id, ResearchPack object) {
        ResearchdManagers.getResearchPacksManager(Minecraft.getInstance().level).mergeContents(Collections.singletonMap(id, object));
        PacketDistributor.sendToServer(new CreateResearchPackPayload(ResourceKey.create(ResearchdRegistries.RESEARCH_PACK_KEY, id), object, true));
    }

    @Override
    protected Component getTitle() {
        return Component.literal("Create Research Pack");
    }
}
