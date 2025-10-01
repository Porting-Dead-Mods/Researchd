package com.portingdeadmods.researchd.api.team;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.data.helper.ResearchMethodProgress;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.UUID;

public interface ResearchTeam {
    // Team metadata

    /**
     * @return The display name of this team
     */
    String getName();

    /**
     * @return The unique uuid of this team
     */
    UUID getId();

    /**
     * Gets the creation time of the team
     *
     * @return the time in ticks when the team was created
     */
    long getCreationTime();

    /**
     * Sets the creation time of the team. This should be correctly relative to further calculations.
     *
     * @param creationTime The time in ticks when the team was created.
     */
    void setCreationTime(long creationTime);

    // Members
    TeamMember getOwner();

    Map<UUID, TeamMember> getMembers();

    boolean isModerator(UUID member);

    boolean isOwner(UUID member);

    // Researches

    /**
     * Gets the research that is currently researching
     *
     * @return {@link ResourceKey} of the research or null if no research is currently in progress
     */
    default ResourceKey<Research> getCurrentResearch() {
        return this.getQueue().getFirst();
    }

    /**
     * Fetches the progress of the research that is currently researching.
     *
     * @return {@link ResearchMethodProgress} of the research or null if no research is currently in progress.
     */
    default ResearchMethodProgress<?> getCurrentProgress() {
        return this.getResearchProgresses().get(this.getCurrentResearch());
    }

    ResearchQueue getQueue();

    Map<ResourceKey<Research>, ResearchInstance> getResearches();

    Map<ResourceKey<Research>, ResearchMethodProgress<?>> getResearchProgresses();

    void completeResearch(ResourceKey<Research> research, long completionTime, Level level);

}
