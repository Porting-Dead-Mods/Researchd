package com.portingdeadmods.researchd.compat.jade;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabControllerBE;
import com.portingdeadmods.researchd.content.blockentities.ResearchLabPartBE;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.EnergyView;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;

public enum ResearchLabEnergyProvider
        implements IServerExtensionProvider<CompoundTag>, IClientExtensionProvider<CompoundTag, EnergyView> {
    INSTANCE;

    private static final ResourceLocation UID = Researchd.rl("research_lab_energy");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public List<ViewGroup<CompoundTag>> getGroups(Accessor<?> accessor) {
        if (ResearchLabControllerBE.getEnergyUsage() <= 0) return List.of();

        IEnergyStorage energyStorage = getEnergyStorage(accessor);
        if (energyStorage == null) return List.of();

        CompoundTag energy = EnergyView.of(energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored());
        return List.of(new ViewGroup<>(List.of(energy)));
    }

    @Override
    public List<ClientViewGroup<EnergyView>> getClientGroups(
            Accessor<?> accessor, List<ViewGroup<CompoundTag>> groups) {
        return ClientViewGroup.map(groups, tag -> EnergyView.read(tag, "FE"), null);
    }

    private static @Nullable IEnergyStorage getEnergyStorage(Accessor<?> accessor) {
        if (!(accessor instanceof BlockAccessor blockAccessor)) return null;

        BlockEntity blockEntity = blockAccessor.getBlockEntity();
        if (blockEntity instanceof ResearchLabControllerBE controller) {
            return controller.getEnergyStorage();
        }
        if (blockEntity instanceof ResearchLabPartBE part) {
            return part.getControllerEnergyStorage();
        }
        return null;
    }
}
