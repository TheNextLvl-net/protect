package net.thenextlvl.protect.api.flag.bound;

import core.api.function.TriPredicate;
import net.thenextlvl.protect.api.flag.possibility.Possibilities;
import net.thenextlvl.protect.api.flag.possibility.Possibility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface InteractionBound extends TriPredicate<Player, Location, Material> {
    InteractionBound AUTOMATIC = (player, block, material) -> PlayerBound.OPERATOR.test(player);
    InteractionBound ALWAYS = (player, location, material) -> true;
    InteractionBound NEVER = (player, location, material) -> false;

    @Override
    default InteractionBound negate() {
        return (player, location, material) -> !test(player, location, material);
    }

    @Override
    default InteractionBound and(TriPredicate<? super Player, ? super Location, ? super Material> other) {
        return (player, location, material) -> test(player, location, material) && other.test(player, location, material);
    }

    @Override
    default InteractionBound or(TriPredicate<? super Player, ? super Location, ? super Material> other) {
        return (player, location, material) -> test(player, location, material) || other.test(player, location, material);
    }

    Possibilities<InteractionBound> POSSIBILITIES = Possibilities.of(
            new Possibility<>("automatic", AUTOMATIC),
            new Possibility<>("always", ALWAYS),
            new Possibility<>("never", NEVER)
    );
}
