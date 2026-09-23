package com.portingdeadmods.researchd.content.blockentities;

import com.portingdeadmods.researchd.ResearchdConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.energy.EnergyStorage;

/** Energy storage with the server config rules */
public final class ResearchLabEnergyStorage extends EnergyStorage {
    private final Runnable onChanged;

    ResearchLabEnergyStorage(int capacity, int maxReceive, int maxExtract, Runnable onChanged) {
        super(capacity, maxReceive, maxExtract);
        this.onChanged = onChanged;
        this.refreshLimits();
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        this.refreshLimits();
        int received = super.receiveEnergy(toReceive, simulate);
        if (received > 0 && !simulate) {
            this.onChanged.run();
        }
        return received;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        this.refreshLimits();
        int extracted = super.extractEnergy(toExtract, simulate);
        if (extracted > 0 && !simulate) {
            this.onChanged.run();
        }
        return extracted;
    }

    @Override
    public int getEnergyStored() {
        this.refreshLimits();
        return super.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        this.refreshLimits();
        return super.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        this.refreshLimits();
        return super.canExtract();
    }

    @Override
    public boolean canReceive() {
        this.refreshLimits();
        return super.canReceive();
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, Tag nbt) {
        super.deserializeNBT(provider, nbt);
        this.refreshLimits();
    }

    private void refreshLimits() {
        int configuredCapacity = ResearchdConfig.Server.getResearchLabEnergyCapacity();
        this.capacity = configuredCapacity;
        this.maxReceive = configuredCapacity;
        this.maxExtract = configuredCapacity;

        int clampedEnergy = Math.max(0, Math.min(this.energy, configuredCapacity));
        if (this.energy != clampedEnergy) {
            this.energy = clampedEnergy;
            this.onChanged.run();
        }
    }
}
