package lol.maltest.arenasystem.redis;

public enum QueueType {

    STICKFIGHT_SINGLES, STICKFIGHT_DOUBLES;

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
            case STICKFIGHT_SINGLES:
                return 2;
            case STICKFIGHT_DOUBLES:
                return 4;
            default:
                return -1;
        }
    }
}
