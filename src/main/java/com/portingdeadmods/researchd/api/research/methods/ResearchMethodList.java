package com.portingdeadmods.researchd.api.research.methods;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.impl.ResearchProgress;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;

public interface ResearchMethodList extends ResearchMethod {
    List<ResearchMethod> methods();

    @Override
    default void checkProgress(Level level, ResourceKey<Research> research, ResearchProgress.Task task, MethodContext context) {
        for (ResearchMethod method : methods()) {
            if (shouldCheckProgress()) method.checkProgress(level, research, task, context);
        }
    }
}
