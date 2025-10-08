package com.portingdeadmods.researchd.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.portingdeadmods.researchd.Researchd;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.common.util.Lazy;

public final class ResearchdKeybinds {
    public static final Lazy<KeyMapping> OPEN_RESEARCH_SCREEN = keyBind("open_research_screen", InputConstants.KEY_R);
    public static final Lazy<KeyMapping> OPEN_RESEARCH_TEAM_SCREEN = keyBind("open_research_team_screen", InputConstants.KEY_M);

    public static Lazy<KeyMapping> keyBind(String name, int key) {
        return Lazy.of(() -> new KeyMapping(name, InputConstants.Type.KEYSYM, key, Researchd.MODNAME));
    }
}
