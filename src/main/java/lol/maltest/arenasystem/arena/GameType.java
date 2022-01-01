package lol.maltest.arenasystem.arena;

import lol.maltest.arenasystem.ArenaSystem;
import lol.maltest.arenasystem.templates.games.stickfight.StickFight;
import lol.maltest.arenasystem.templates.games.stickfight.kit.StickFightKit;

public enum GameType {

    STICKFIGHT_SINGLE, STICKFIGHT_DOUBLES;

    public boolean isFFA() {
        return countTeams() == 1;
    }

    public int countTeams() {
        switch (this) {
            case STICKFIGHT_DOUBLES:
                return 2;
            default:
                return 1;
        }
    }

    public int getMaxPlayers() {
        switch (this) {
            case STICKFIGHT_SINGLE:
                return 2;
            case STICKFIGHT_DOUBLES:
                return 4;
            default:
                return -1;
        }
    }

    public void createGame() {

    }
}