package lol.maltest.arenasystem.redis;

public enum QueueType {

    STICKFIGHT_SINGLES, STICKFIGHT_DOUBLES,
    PVPBRAWL_SINGLES, PVPBRAWL_DOUBLES,
    SPLEEF_SINGLES, SPLEEF_DOUBLES,
    TNTRUN_SINGLES, TNTRUN_DOUBLES,
    PARKOURRACE_SINGLES;


    public int countTeams() {
        switch (this) {
            case SPLEEF_DOUBLES:
            case TNTRUN_DOUBLES:
            case PVPBRAWL_DOUBLES:
            case STICKFIGHT_DOUBLES:
                return 2;
            default:
                return 1;
        }
    }

    public int getMaxPlayers() {
        switch (this) {
            case PVPBRAWL_SINGLES:
            case TNTRUN_SINGLES:
            case SPLEEF_SINGLES:
            case STICKFIGHT_SINGLES:
                return 2;
            case SPLEEF_DOUBLES:
            case TNTRUN_DOUBLES:
            case PVPBRAWL_DOUBLES:
            case STICKFIGHT_DOUBLES:
                return 4;
            default:
                return -1;
        }
    }
}
