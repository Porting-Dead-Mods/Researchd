package com.portingdeadmods.researchd.api;

import com.portingdeadmods.portingdeadlibs.utils.PlayerUtils;
import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectData;
import com.portingdeadmods.researchd.api.research.effects.ResearchEffectManager;
import com.portingdeadmods.researchd.api.research.serializers.ResearchEffectDataType;
import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.ResearchTeamManager;
import com.portingdeadmods.researchd.api.research.ResearchManager;
import com.portingdeadmods.researchd.client.cache.ResearchTeamCache;
import com.portingdeadmods.researchd.data.ResearchdAttachments;
import com.portingdeadmods.researchd.data.saved.TeamResearchEffectSavedData;
import com.portingdeadmods.researchd.data.saved.TeamSavedData;
import com.portingdeadmods.researchd.impl.research.ResearchManagerImpl;
import com.portingdeadmods.researchd.impl.research.effect.data.DimensionUnlockEffectData;
import com.portingdeadmods.researchd.impl.research.effect.data.ItemUnlockEffectData;
import com.portingdeadmods.researchd.impl.research.effect.data.RecipeUnlockEffectData;
import com.portingdeadmods.researchd.client.ClientResearchdApi;
import com.portingdeadmods.researchd.registries.ResearchdEffectDataTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Supplier;

public final class ResearchdApi {
    /* Research Screen Api */
    public static void openScreen() {
        if (FMLEnvironment.dist.isClient()) {
            ClientResearchdApi.openResearchScreen();
        }
    }

    public static void openTeamScreen() {
        if (FMLEnvironment.dist.isClient()) {
            ClientResearchdApi.openTeamScreen();
        }
    }

    public static void openScreenForResearch(ResourceKey<Research> research) {
        if (FMLEnvironment.dist.isClient()) {
            ClientResearchdApi.openScreenForResearch(research);
        }
    }

    /* Research Team Api */
    public static @Nullable ResearchTeamManager getTeamManager(Level level) {
        if (!level.isClientSide()) {
            return TeamSavedData.getData((ServerLevel) level);
        }
        return ResearchTeamCache.researchTeamMap;
    }

    /* Research Api */
    public static @Nullable ResearchManager getResearchManager() {
        return ResearchManagerImpl.getInstance();
    }

    public static ResearchEffectManager getResearchEffectManager(Level level) {
        if (!level.isClientSide()) {
            return TeamResearchEffectSavedData.getData((ServerLevel) level);
        }
        return ResearchTeamCache.teamResearchEffectDataMap;
    }

    /* Research Effect Data Api */
    public static <T extends ResearchEffectData<?>> @Nullable T getEffectDataForPlayer(Player player, ResearchEffectDataType<T> type) {
        ResearchTeamManager teamManager = getTeamManager(player.level());
        if (teamManager == null) return null;
        ResearchTeam team = teamManager.getTeamByPlayer(player);
        if (team == null) return null;
        return getEffectDataForTeam(player.level(), team.getId(), type);
    }

    public static <T extends ResearchEffectData<?>> @Nullable T getEffectDataForPlayer(Player player, Supplier<ResearchEffectDataType<T>> type) {
        return getEffectDataForPlayer(player, type.get());
    }

    public static <T extends ResearchEffectData<?>> @Nullable T getEffectDataForTeam(Level level, UUID teamId, ResearchEffectDataType<T> type) {
        if (teamId == null) return null;
        ResearchEffectManager researchEffectManager = getResearchEffectManager(level);
        if (researchEffectManager == null) return null;
        return researchEffectManager.getEffectData(teamId, type);
    }

    public static <T extends ResearchEffectData<?>> @Nullable T getEffectDataForTeam(Level level, UUID teamId, Supplier<ResearchEffectDataType<T>> type) {
        return getEffectDataForTeam(level, teamId, type.get());
    }

    /* Blocked Item / Recipe / Dimension Api */
    public static boolean isItemBlocked(Player player, Item item) {
        ItemUnlockEffectData data = getEffectDataForPlayer(player, ResearchdEffectDataTypes.ITEM_UNLOCK);
        return data != null && data.isBlocked(item);
    }

    public static boolean isItemBlocked(Player player, ItemStack stack) {
        return isItemBlocked(player, stack.getItem());
    }

    public static boolean isItemBlocked(Player player, ItemLike item) {
        return isItemBlocked(player, item.asItem());
    }

    public static boolean isItemBlocked(Player player, ResourceKey<Item> itemKey) {
        ItemUnlockEffectData data = getEffectDataForPlayer(player, ResearchdEffectDataTypes.ITEM_UNLOCK);
        return data != null && data.blockedItems().contains(itemKey);
    }

    public static boolean isItemBlocked(Level level, UUID teamId, Item item) {
        ItemUnlockEffectData data = getEffectDataForTeam(level, teamId, ResearchdEffectDataTypes.ITEM_UNLOCK);
        return data != null && data.isBlocked(item);
    }

    public static boolean isItemBlocked(Level level, UUID teamId, ItemStack stack) {
        return isItemBlocked(level, teamId, stack.getItem());
    }

    public static boolean isItemBlocked(Level level, UUID teamId, ItemLike item) {
        return isItemBlocked(level, teamId, item.asItem());
    }

    public static boolean isItemBlocked(Level level, UUID teamId, ResourceKey<Item> itemKey) {
        ItemUnlockEffectData data = getEffectDataForTeam(level, teamId, ResearchdEffectDataTypes.ITEM_UNLOCK);
        return data != null && data.blockedItems().contains(itemKey);
    }

    public static boolean isRecipeBlocked(Player player, ResourceLocation recipeId) {
        RecipeUnlockEffectData data = getEffectDataForPlayer(player, ResearchdEffectDataTypes.RECIPE_UNLOCK);
        return data != null && data.contains(recipeId);
    }

    public static boolean isRecipeBlocked(Player player, RecipeHolder<?> holder) {
        return isRecipeBlocked(player, holder.id());
    }

    public static boolean isRecipeBlocked(Level level, UUID teamId, ResourceLocation recipeId) {
        RecipeUnlockEffectData data = getEffectDataForTeam(level, teamId, ResearchdEffectDataTypes.RECIPE_UNLOCK);
        return data != null && data.contains(recipeId);
    }

    public static boolean isRecipeBlocked(Level level, UUID teamId, RecipeHolder<?> holder) {
        return isRecipeBlocked(level, teamId, holder.id());
    }

    public static boolean isDimensionBlocked(Player player, ResourceKey<DimensionType> dimension) {
        DimensionUnlockEffectData data = getEffectDataForPlayer(player, ResearchdEffectDataTypes.DIMENSION_UNLOCK);
        return data != null && data.blockedDimensions().contains(dimension);
    }

    public static boolean isDimensionBlocked(Level level, UUID teamId, ResourceKey<DimensionType> dimension) {
        DimensionUnlockEffectData data = getEffectDataForTeam(level, teamId, ResearchdEffectDataTypes.DIMENSION_UNLOCK);
        return data != null && data.blockedDimensions().contains(dimension);
    }

    /* Placement Owner Api */
    // Returns the owning team UUID for a block entity, silently migrating legacy player-UUID
    // values to the placer's current team UUID on first read.
    public static @Nullable UUID getOrMigratePlacedByTeam(BlockEntity be, Level level) {
        UUID stored = be.getData(ResearchdAttachments.PLACED_BY_UUID);
        if (stored == null || stored.equals(PlayerUtils.EmptyUUID)) return stored;

        ResearchTeamManager mgr = getTeamManager(level);
        if (mgr == null) return stored;

        if (mgr.getTeamById(stored) != null) return stored;

        ResearchTeam team = mgr.getTeamByPlayerId(stored);
        if (team != null) {
            be.setData(ResearchdAttachments.PLACED_BY_UUID, team.getId());
            be.setChanged();
            return team.getId();
        }
        return stored;
    }

}
