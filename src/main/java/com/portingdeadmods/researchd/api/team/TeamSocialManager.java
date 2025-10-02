package com.portingdeadmods.researchd.api.team;

import java.util.UUID;

public interface TeamSocialManager {
    void addReceivedInvite(UUID uuid);

    void removeReceivedInvite(UUID uuid);

    boolean containsReceivedInvite(UUID uuid);

    void addSentInvite(UUID uuid);

    void removeSentInvite(UUID uuid);

    boolean containsSentInvite(UUID uuid);

    void addIgnore(UUID uuid);

    void removeIgnore(UUID uuid);

    boolean containsIgnore(UUID uuid);

}
