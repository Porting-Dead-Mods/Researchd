package com.portingdeadmods.researchd.compat.jade;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabPartBE;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.EnergyView;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;

public enum ResearchLabEnergyProvider
        implements IServerExtensionProvider<EnergyView.Data>, IClientExtensionProvider<EnergyView.Data, EnergyView> {
    INSTANCE;

    private static final Identifier UID = Researchd.rl("research_lab_energy");

    @Override
    public Identifier getUid() {
        return UID;
    }

    @Override
    public List<ViewGroup<EnergyView.Data>> getGroups(Accessor<?> accessor) {
        if (ResearchLabControllerBE.getEnergyUsage() <= 0) return List.of();

        EnergyHandler energyHandler = getEnergyHandler(accessor);
        if (energyHandler == null) return List.of();

        EnergyView.Data energy =
                new EnergyView.Data(energyHandler.getAmountAsLong(), energyHandler.getCapacityAsLong());
        return List.of(new ViewGroup<>(List.of(energy)));
    }

    @Override
    public List<ClientViewGroup<EnergyView>> getClientGroups(
            Accessor<?> accessor, List<ViewGroup<EnergyView.Data>> groups) {
        return ClientViewGroup.map(groups, data -> EnergyView.read(data, "FE"), null);
    }

    private static @Nullable EnergyHandler getEnergyHandler(Accessor<?> accessor) {
        if (!(accessor instanceof BlockAccessor blockAccessor)) return null;

        BlockEntity blockEntity = blockAccessor.getBlockEntity();
        if (blockEntity instanceof ResearchLabControllerBE controller) {
            return controller.getEnergyHandler();
        }
        if (blockEntity instanceof ResearchLabPartBE part) {
            return part.getControllerEnergyStorage();
        }
        return null;
    }
}
