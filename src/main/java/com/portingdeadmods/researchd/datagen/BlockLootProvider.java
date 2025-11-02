package com.portingdeadmods.researchd.datagen;

import com.portingdeadmods.researchd.registries.ResearchdBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.level.block.Block;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class BlockLootProvider extends BlockLootSubProvider {
    private final Set<Block> knownBlocks = new ReferenceOpenHashSet<>();

    public BlockLootProvider(HolderLookup.Provider registries) {
        super(Collections.emptySet(), FeatureFlags.VANILLA_SET, registries);
    }

    @Override
    protected void generate() {
        dropSelf(ResearchdBlocks.RESEARCH_LAB_CONTROLLER.get());
    }

    @Override
    public @NotNull Set<Block> getKnownBlocks() {
        return knownBlocks;
    }

    @Override
    protected void add(@NotNull Block block, @NotNull LootTable.Builder table) {
        super.add(block, table);
        knownBlocks.add(block);
    }
}