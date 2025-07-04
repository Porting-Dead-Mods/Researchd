package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.menus.ResearchLabMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ResearchdMenuTypes {
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, Researchd.MODID);

	public static final Supplier<MenuType<ResearchLabMenu>> RESEARCH_LAB_MENU = MENU_TYPES.register("research_lab_menu",
			() -> IMenuTypeExtension.create(ResearchLabMenu::new));
}
