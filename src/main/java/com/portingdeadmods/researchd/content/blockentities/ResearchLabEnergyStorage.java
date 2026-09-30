package com.portingdeadmods.researchd.content.blockentities;

import com.portingdeadmods.portingdeadlibs.api.data.transfer.PDLSimpleEnergyHandler;
import com.portingdeadmods.researchd.ResearchdConfig;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Energy storage with the server config rules: the buffer and its per-tick transfer limit follow the configured
 * capacity while the game runs.
 * <p>
 * Inserts and extracts go through {@link net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler}, whose journal
 * snapshots the stored amount before each change, so an aborted transaction puts it back. Inside a transaction only
 * the limits are refreshed. Energy above a lowered capacity is trimmed by {@link #clampToCapacity()}, which runs
 * outside any transaction; until then the storage accepts nothing and still gives out what it holds.
 */
public final class ResearchLabEnergyStorage extends PDLSimpleEnergyHandler {
    ResearchLabEnergyStorage() {
        super(ResearchdConfig.Server.getResearchLabEnergyCapacity());
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        this.refreshLimits();
        return super.insert(amount, transaction);
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        this.refreshLimits();
        return super.extract(amount, transaction);
    }

    @Override
    public long getCapacityAsLong() {
        this.refreshLimits();
        return super.getCapacityAsLong();
    }

    @Override
    public void deserialize(ValueInput input) {
        super.deserialize(input);
        this.clampToCapacity();
    }

    /** Trims stored energy down to the configured capacity. Must not be called inside a transaction. */
    void clampToCapacity() {
        this.refreshLimits();
        if (this.energy > this.capacity) {
            this.set(this.capacity);
        }
    }

    private void refreshLimits() {
        int configuredCapacity = ResearchdConfig.Server.getResearchLabEnergyCapacity();
        this.capacity = configuredCapacity;
        this.maxInsert = configuredCapacity;
        this.maxExtract = configuredCapacity;
    }
}
