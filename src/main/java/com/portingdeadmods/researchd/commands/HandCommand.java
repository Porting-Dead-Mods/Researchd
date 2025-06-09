package com.portingdeadmods.researchd.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.stream.Collectors;

public class HandCommand {
    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("hand")
                .executes(context -> {
                    CommandSourceStack source = context.getSource();
                    ServerPlayer player = source.getPlayer();

                    if (player != null) {
                        ItemStack heldItem = player.getMainHandItem();

                        if (heldItem.isEmpty()) {
                            source.sendSystemMessage(Component.literal("You must be holding an item to use this command."));
                            return 0;
                        }

                        RecipeManager recipeManager = source.getLevel().getRecipeManager();
                        List<RecipeHolder<?>> matchingRecipes = recipeManager.getRecipes().stream()
                                .filter(recipeHolder -> {
                                    Recipe<?> recipe = recipeHolder.value();
                                    ItemStack result = recipe.getResultItem(source.getLevel().registryAccess());
                                    return !result.isEmpty() && result.is(heldItem.getItem());
                                })
                                .toList();

                        if (matchingRecipes.isEmpty()) {
                            source.sendSystemMessage(Component.literal("No recipes found for: " + heldItem.getHoverName().getString()));
                        } else {
                            source.sendSystemMessage(Component.literal("Found " + matchingRecipes.size() + " recipes for: " + heldItem.getHoverName().getString()));

                            for (RecipeHolder<?> recipeHolder : matchingRecipes) {
                                Recipe<?> recipe = recipeHolder.value();
                                String recipeId = recipeHolder.id().toString();
                                String recipeType = recipe.getType().toString();

                                MutableComponent recipeIdComponent = Component.literal(recipeId)
                                        .setStyle(Style.EMPTY
                                                .withColor(ChatFormatting.GREEN)
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, recipeId))
                                                .withHoverEvent(
                                                        new HoverEvent(
                                                                net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                                                Component.literal("Click to copy recipe ID")))
                                        );

                                // Combine with recipe type info
                                MutableComponent fullMessage = Component.literal(" - ")
                                        .append(recipeIdComponent)
                                        .append(Component.literal(" (" + recipeType + ")"));

                                source.sendSystemMessage(fullMessage);
                            }
                        }
                    }

                    return 1;
                })
                .build();
    }
}
