package com.portingdeadmods.researchd.api.team;

import com.portingdeadmods.researchd.api.research.Research;
import com.portingdeadmods.researchd.api.research.ResearchInstance;
import com.portingdeadmods.researchd.impl.ResearchProgress;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.SequencedCollection;
import java.util.UUID;
import java.util.function.Function;

public interface ResearchTeam {
    /* Team Metadata */

    /**
     * @return The display name of this team
     */
    @NotNull String getName();

    /**
     * Set the display name of this team
     *
     * @param name The new display name of the team
     */
    void setName(String name);

    /**
     * @return The unique uuid of this team
     */
    @NotNull UUID getId();

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

    @Nullable TeamMember getOwner();

    /**
     * @return the team member for the given player uuid. Never null; players that are
     * not part of this team are returned as members with the
     * {@link ResearchTeamRole#NOT_MEMBER} role.
     */
    @NotNull TeamMember getMember(UUID member);

    boolean hasMember(UUID member);

    void addMember(UUID member, ResearchTeamRole role);

    default void addMember(UUID member) {
        this.addMember(member, ResearchTeamRole.MEMBER);
    }

    void removeMember(UUID member);

    @NotNull SequencedCollection<TeamMember> getMembers();

    void setRole(UUID member, ResearchTeamRole role);

    boolean isModerator(UUID member);

    boolean isOwner(UUID member);

    @NotNull TeamSocialManager getSocialManager();

    /* Team Researches */

    /**
     * Gets the researchPack that is currently researching
     *
     * @return {@link ResourceKey} of the researchPack or null if no researchPack is currently in progress
     */
    default @Nullable ResourceKey<Research> getCurrentResearch() {
        return this.getQueue().getFirst();
    }

    /**
     * Fetches the progress of the researchPack that is currently researching.
     *
     * @return {@link ResearchProgress} of the researchPack or null if no researchPack is currently in progress.
     */
    default @Nullable ResearchProgress getCurrentProgress() {
        ResourceKey<Research> currentResearch = this.getCurrentResearch();
        return currentResearch != null ? this.getResearchProgresses().get(currentResearch) : null;
    }

    void init(Level level);

    @NotNull ResearchQueue getQueue();

    @NotNull Map<ResourceKey<Research>, ResearchInstance> getResearches();

    @NotNull Map<ResourceKey<Research>, ResearchProgress> getResearchProgresses();

    void setResearchCompleted(ResourceKey<Research> research, long completionTime);

    void onCompleteResearch(ResourceKey<Research> research, long completionTime, boolean forced, Function<UUID, Player> playerGetter);

    default void onCompleteResearch(ResourceKey<Research> research, long completionTime, Function<UUID, Player> playerGetter) {
        this.onCompleteResearch(research, completionTime, false, playerGetter);
    }

    /**
     * Inverse of {@link #onCompleteResearch}: flips the research back to non-completed,
     * fires {@link com.portingdeadmods.researchd.api.research.effects.ResearchEffect#onLock onLock}
     * to reverse the effect, and syncs the team.
     */
    void onRemoveResearch(ResourceKey<Research> research, Function<UUID, Player> playerGetter);

    void refreshResearchStatus();

}
