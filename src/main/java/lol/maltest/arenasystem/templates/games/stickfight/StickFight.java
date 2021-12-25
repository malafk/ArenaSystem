package lol.maltest.arenasystem.templates.games.stickfight;

import lol.maltest.arenasystem.arena.ArenaManager;
import lol.maltest.arenasystem.templates.Game;
import lol.maltest.arenasystem.templates.games.stickfight.kit.StickFightKit;

import java.util.ArrayList;
import java.util.UUID;

public class StickFight extends Game {

    private static final String[] schematicNames = {
            "stickfight_map1"
    };

    public StickFight(ArenaManager arenaManager) {
        super(arenaManager,2, 2, schematicNames, new StickFightKit(arenaManager));
    }
}
