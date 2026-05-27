package com.portingdeadmods.researchd.registries;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.commands.arguments.ResearchdTeamArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ResearchdCommandArguments {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>>  ARGUMENT_TYPE_INFOS = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Researchd.MODID);

    static {
        ARGUMENT_TYPE_INFOS.register("team", () -> ArgumentTypeInfos.registerByClass(ResearchdTeamArgument.class, SingletonArgumentInfo.contextAware(context -> ResearchdTeamArgument.teamArgument())));
    }
}
