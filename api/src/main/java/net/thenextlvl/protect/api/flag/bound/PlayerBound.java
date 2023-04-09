package net.thenextlvl.protect.api.flag.bound;

import net.thenextlvl.protect.api.flag.possibility.Possibilities;
import net.thenextlvl.protect.api.flag.possibility.Possibility;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

@FunctionalInterface
public interface PlayerBound extends EntityBound<Player> {
    PlayerBound OPERATOR = PermissionBound.OPERATOR::test;
    PlayerBound GAMEMODE = player -> player.getGameMode().equals(GameMode.CREATIVE);
    PlayerBound ALWAYS = player -> true;
    PlayerBound NEVER = player -> false;

    @Override
    default PlayerBound negate() {
        return player -> !test(player);
    }

    @Override
    default PlayerBound and(Predicate<? super Player> other) {
        return player -> test(player) && other.test(player);
    }

    @Override
    default PlayerBound or(Predicate<? super Player> other) {
        return player -> test(player) || other.test(player);
    }

    Possibilities<PlayerBound> POSSIBILITIES = Possibilities.of(
            new Possibility<>("operator", OPERATOR),
            new Possibility<>("gamemode", GAMEMODE),
            new Possibility<>("always", ALWAYS),
            new Possibility<>("never", NEVER)
    );
}
