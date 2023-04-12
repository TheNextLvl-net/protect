package net.thenextlvl.protect.flag.bound;

import net.thenextlvl.protect.flag.possibility.Possibilities;
import net.thenextlvl.protect.flag.possibility.Possibility;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.ServerOperator;

import java.util.function.Predicate;

@FunctionalInterface
public interface PermissionBound extends Predicate<Permissible> {
    PermissionBound OPERATOR = ServerOperator::isOp;
    PermissionBound ALWAYS = permissible -> true;
    PermissionBound NEVER = permissible -> false;

    @Override
    default PermissionBound negate() {
        return permissible -> !test(permissible);
    }

    @Override
    default PermissionBound and(Predicate<? super Permissible> other) {
        return permissible -> test(permissible) && other.test(permissible);
    }

    @Override
    default PermissionBound or(Predicate<? super Permissible> other) {
        return permissible -> test(permissible) || other.test(permissible);
    }

    Possibilities<PermissionBound> POSSIBILITIES = Possibilities.of(
            new Possibility<>("automatic", PermissionBound.OPERATOR),
            new Possibility<>("always", ALWAYS),
            new Possibility<>("never", NEVER)
    );
}
