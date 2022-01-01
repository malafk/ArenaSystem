package lol.maltest.arenasystem.templates;

import org.bukkit.Material;

import java.util.HashSet;

public class GameplayFlags {

    public boolean canPvP = true;
    public boolean canDamageTeamSelf = false;
    public boolean canDamageTeamMates = false;

    public boolean canBreakBlocks = true;
    public boolean canPlaceBlocks = true;

    public HashSet<Material> blockBreakAllowed = new HashSet<Material>();
    public HashSet<Material> blockPlaceAllowed = new HashSet<Material>();


}
