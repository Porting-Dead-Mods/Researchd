package com.portingdeadmods.researchd.api.research.methods;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.data.helper.ResearchMethodProgress;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;

public interface ResearchMethodList extends ResearchMethod {
    List<ResearchMethod> methods();

    default void checkProgress(Level level, ResourceKey<Research> research, ResearchMethodProgress<?> progress, MethodContext context) {
        for (ResearchMethod method : this.methods()) {
            if (this.shouldCheckProgress()) {
                method.checkProgress(level, research, progress, context);
            }
        }
    }
}
