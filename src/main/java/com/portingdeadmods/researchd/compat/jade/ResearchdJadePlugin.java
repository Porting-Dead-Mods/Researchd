package com.portingdeadmods.researchd.compat.jade;

import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabPartBE;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public final class ResearchdJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEnergyStorage(ResearchLabEnergyProvider.INSTANCE, ResearchLabControllerBE.class);
        registration.registerEnergyStorage(ResearchLabEnergyProvider.INSTANCE, ResearchLabPartBE.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEnergyStorageClient(ResearchLabEnergyProvider.INSTANCE);
    }
}
