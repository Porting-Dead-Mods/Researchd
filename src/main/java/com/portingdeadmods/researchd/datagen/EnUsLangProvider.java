package com.portingdeadmods.researchd.datagen;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.ResearchdConfig;
import com.portingdeadmods.researchd.ResearchdRegistries;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectType;
import com.portingdeadmods.researchd.api.research.methods.ResearchMethodType;
import com.portingdeadmods.researchd.impl.research.ResearchPackImpl;
import com.portingdeadmods.researchd.pdl.config.PDLConfigHelper;
import com.portingdeadmods.researchd.registries.ResearchMethodTypes;
import com.portingdeadmods.researchd.translations.ResearchdTranslations;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.Map;
import java.util.function.Supplier;

import static com.portingdeadmods.researchd.registries.ResearchdBlocks.RESEARCH_LAB_CONTROLLER;
import static com.portingdeadmods.researchd.registries.ResearchdBlocks.RESEARCH_LAB_PART;

public final class EnUsLangProvider extends LanguageProvider {
    public EnUsLangProvider(PackOutput output) {
        super(output, Researchd.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        ResearchdTranslations.init();

        for (Map.Entry<String, String> entry : ResearchdTranslations.TRANSLATIONS.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }

        addBlock(RESEARCH_LAB_PART, "Research Lab Part");
        addBlock(RESEARCH_LAB_CONTROLLER, "Research Lab");

        addResearchMethodName(ResearchMethodTypes.OR, "Or");
        addResearchMethodName(ResearchMethodTypes.AND, "And");
        addResearchMethodName(ResearchMethodTypes.CONSUME_ITEM, "Consume Item");
        addResearchMethodName(ResearchMethodTypes.CONSUME_PACK, "Consume Pack");
        addResearchMethodName(ResearchMethodTypes.CHECK_ITEM_PRESENCE, "Check Item Presence");

        PDLConfigHelper.generateConfigNames(ResearchdConfig.Client.class, Researchd.MODID, this::add);
        PDLConfigHelper.generateConfigNames(ResearchdConfig.Common.class, Researchd.MODID, this::add);
    }

    private void addResearch(ResourceKey<Research> key, String name) {
        add(key.registry().getPath() + "." + key.location().getNamespace() + "." + key.location().getPath(), name);
    }

    private void addResearchDesc(ResourceKey<Research> key, String name) {
        add("research_desc." + key.location().getNamespace() + "." + key.location().getPath(), name);
    }

    private void addResearchMethod(ResourceLocation key, String name) {
        add("research_method." + key.getNamespace() + "." + key.getPath(), name);
    }

    private void addResearchMethodName(Supplier<ResearchMethodType> type, String name) {
        ResourceLocation loc = ResearchdRegistries.RESEARCH_METHOD_TYPE.getKey(type.get());
        add("research_method_type." + loc.getNamespace() + "." + loc.getPath(), name);
    }

    private void addResearchEffectName(Supplier<ResearchEffectType> type, String name) {
        ResourceLocation loc = ResearchdRegistries.RESEARCH_EFFECT_TYPE.getKey(type.get());
        add("research_effect_type." + loc.getNamespace() + "." + loc.getPath(), name);
    }

    private void addResearchPack(ResourceKey<ResearchPackImpl> key, String name) {
        add("item.researchd.research_pack_" + key.location().toString().replace(':', '_'), name);
    }

    private void addResearchPack(ResourceLocation key, String name) {
        add("item.researchd.research_pack_" + key.toString().replace(':', '_'), name);
    }

}
