package net.thenextlvl.protect.api.flag.bound;

import net.thenextlvl.protect.api.flag.possibility.Possibilities;
import net.thenextlvl.protect.api.flag.possibility.Possibility;
import org.bukkit.entity.Entity;

import java.util.function.Predicate;

@FunctionalInterface
public interface EntityBound<T extends Entity> extends Predicate<T> {
    EntityBound<? super Entity> ALWAYS = entity -> true;
    EntityBound<? super Entity> NEVER = entity -> false;

    @Override
    default EntityBound<T> negate() {
        return entity -> !test(entity);
    }

    @Override
    default EntityBound<T> and(Predicate<? super T> other) {
        return entity -> test(entity) && other.test(entity);
    }

    @Override
    default EntityBound<T> or(Predicate<? super T> other) {
        return entity -> test(entity) || other.test(entity);
    }

    Possibilities<EntityBound<? super Entity>> POSSIBILITIES = Possibilities.of(
            new Possibility<>("always", ALWAYS),
            new Possibility<>("never", NEVER)
    );
}
