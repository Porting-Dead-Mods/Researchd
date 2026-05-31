package com.portingdeadmods.researchd.api.team;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.impl.ResearchProgress;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.SequencedCollection;
import java.util.UUID;
import java.util.function.Function;

public interface ResearchTeam {
    /* Team Metadata */

    /**
     * @return The display name of this team
     */
    String getName();

    /**
     * Set the display name of this team
     *
     * @param name The new display name of the team
     */
    void setName(String name);

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

    /* Team Members */

    TeamMember getOwner();

    TeamMember getMember(UUID member);

    boolean hasMember(UUID member);

    void addMember(UUID member, ResearchTeamRole role);

    default void addMember(UUID member) {
        this.addMember(member, ResearchTeamRole.MEMBER);
    }

    void removeMember(UUID member);

    SequencedCollection<TeamMember> getMembers();

    void setRole(UUID member, ResearchTeamRole role);

    boolean isModerator(UUID member);

    boolean isOwner(UUID member);

    TeamSocialManager getSocialManager();

    /* Team Researches */

    /**
     * Gets the researchPack that is currently researching
     *
     * @return {@link ResourceKey} of the researchPack or null if no researchPack is currently in progress
     */
    default ResourceKey<Research> getCurrentResearch() {
        return this.getQueue().getFirst();
    }

    /**
     * Fetches the progress of the researchPack that is currently researching.
     *
     * @return {@link ResearchProgress} of the researchPack or null if no researchPack is currently in progress.
     */
    default ResearchProgress getCurrentProgress() {
        return this.getResearchProgresses().get(this.getCurrentResearch());
    }

    void init(Level level);

    ResearchQueue getQueue();

    Map<ResourceKey<Research>, ResearchInstance> getResearches();

    Map<ResourceKey<Research>, ResearchProgress> getResearchProgresses();

    void setResearchCompleted(ResourceKey<Research> research, long completionTime);

    void onCompleteResearch(ResourceKey<Research> research, long completionTime, boolean forced, Function<UUID, Player> playerGetter);

    default void onCompleteResearch(ResourceKey<Research> research, long completionTime, Function<UUID, Player> playerGetter) {
        this.onCompleteResearch(research, completionTime, false, playerGetter);
    }

    void refreshResearchStatus();

}
