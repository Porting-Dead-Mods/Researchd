package com.portingdeadmods.researchd.impl.team;

import com.portingdeadmods.researchd.api.team.ResearchTeam;
import com.portingdeadmods.researchd.api.team.ResearchTeamManager;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.UUID;

public class TeamIterator implements Iterator<ResearchTeam> {
    private final ResearchTeamManager manager;
    private final Iterator<UUID> teamIdIterator;

    private TeamIterator(ResearchTeamManager manager, Iterator<UUID> teamIdIterator) {
        this.manager = manager;
        this.teamIdIterator = teamIdIterator;
    }

    @Override
    public boolean hasNext() {
        return this.teamIdIterator.hasNext();
    }

    @Override
    public ResearchTeam next() {
        return this.manager.getTeamById(this.teamIdIterator.next());
    }

    public static class Iterable implements java.lang.Iterable<ResearchTeam> {
        private final ResearchTeamManager manager;
        private final Iterator<UUID> teamIdIterator;

        public Iterable(ResearchTeamManager manager, Iterator<UUID> teamIdIterator) {
            this.manager = manager;
            this.teamIdIterator = teamIdIterator;
        }

        @Override
        public @NotNull Iterator<ResearchTeam> iterator() {
            return new TeamIterator(this.manager, this.teamIdIterator);
        }
    }

}
