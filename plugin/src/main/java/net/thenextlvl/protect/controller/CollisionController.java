package net.thenextlvl.protect.controller;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public class CollisionController {
    private static final String TEAM_NAME = "collision_protection";
    private @Nullable Team collisionTeam;
    
    private Team getOrCreateCollisionTeam(Player player) {
        return getCollisionTeam(player).orElseGet(() -> {
            var team = player.getScoreboard().registerNewTeam(TEAM_NAME);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            return team;
        });
    }

    private Optional<Team> getCollisionTeam(Player player) {
        return Optional.ofNullable(player.getScoreboard().getTeam(TEAM_NAME));
    }

    public void setCollidable(Player player, boolean collides) {
        var team = getOrCreateCollisionTeam(player);
        if (collides) team.removePlayer(player);
        else team.addPlayer(player);
    }

    public void remove(Player player) {
        getCollisionTeam(player).ifPresent(Team::unregister);
    }
}
