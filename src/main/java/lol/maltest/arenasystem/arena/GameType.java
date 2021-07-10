package lol.maltest.arenasystem.arena;

public enum GameType {

    VOID_SINGLE, VOID_DOUBLES, LAVA_RISE_SINGLE, LAVA_RISE_DOUBLES;

    public boolean isFFA() {
        return countTeams() == 1;
    }

    public int countTeams() {
        switch (this) {
            case VOID_DOUBLES:
            case LAVA_RISE_DOUBLES:
                return 2;
            default:
                return 1;
        }
    }

    public int getMaxPlayers() {
        switch (this) {
            case VOID_SINGLE:
            case LAVA_RISE_SINGLE:
                return 2;
            case VOID_DOUBLES:
            case LAVA_RISE_DOUBLES:
                return 4;
            default:
                return -1;
        }
    }
}