package net.thenextlvl.protect.flag;

import com.google.common.base.Preconditions;
import core.annotation.FieldsAreNonnullByDefault;
import core.annotation.MethodsReturnNonnullByDefault;
import lombok.*;
import lombok.experimental.Accessors;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.bound.InteractionBound;
import net.thenextlvl.protect.flag.bound.PermissionBound;
import net.thenextlvl.protect.flag.bound.PlayerBound;
import net.thenextlvl.protect.flag.possibility.Possibilities;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@Accessors(fluent = true)
@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@EqualsAndHashCode(callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class Flag<T> {
    private static final HashMap<String, Flag<?>> flags = new HashMap<>();

    public static final Flag<PlayerBound> ENTER = new Flag<>("enter", player -> true, PlayerBound.POSSIBILITIES).register();
    public static final Flag<PlayerBound> LEAVE = new Flag<>("leave", player -> true, PlayerBound.POSSIBILITIES).register();
    public static final Flag<PlayerBound> ENTITY_INTERACT = new Flag<>("entityInteract", PlayerBound.OPERATOR, PlayerBound.POSSIBILITIES).register();
    public static final Flag<PlayerBound> ARMOR_STAND_MANIPULATE = new Flag<>("armorStandManipulate", PlayerBound.OPERATOR, PlayerBound.POSSIBILITIES).register();
    public static final Flag<PlayerBound> ITEM_PICKUP = new Flag<>("itemPickup", PlayerBound.OPERATOR, PlayerBound.POSSIBILITIES).register();
    public static final Flag<PlayerBound> ITEM_DROP = new Flag<>("itemDrop", PlayerBound.OPERATOR, PlayerBound.POSSIBILITIES).register();
    public static final Flag<PlayerBound> HANGING_PLACE = new Flag<>("hangingPlace", PlayerBound.OPERATOR, PlayerBound.POSSIBILITIES).register();
    public static final Flag<InteractionBound> USE = new Flag<>("use", InteractionBound.AUTOMATIC, InteractionBound.POSSIBILITIES).register();
    public static final Flag<InteractionBound> BREAK = new Flag<>("break", InteractionBound.AUTOMATIC, InteractionBound.POSSIBILITIES).register();
    public static final Flag<InteractionBound> PLACE = new Flag<>("place", InteractionBound.AUTOMATIC, InteractionBound.POSSIBILITIES).register();
    public static final Flag<InteractionBound> CROP_TRAMPLE = new Flag<>("cropTrample", InteractionBound.AUTOMATIC, InteractionBound.POSSIBILITIES).register();
    public static final Flag<PermissionBound> HANGING_BREAK = new Flag<>("hangingBreak", PermissionBound.OPERATOR, PermissionBound.POSSIBILITIES).register();
    public static final Flag<Boolean> ENTITY_SPAWN = new Flag<>("entitySpawn", true, Possibilities.BOOLEAN_VALUES).register();
    public static final Flag<Boolean> REDSTONE = new Flag<>("redstone", true, Possibilities.BOOLEAN_VALUES).register();
    public static final Flag<Boolean> DAMAGE = new Flag<>("damage", true, Possibilities.BOOLEAN_VALUES).register();
    public static final Flag<Boolean> ATTACK_PLAYERS = new Flag<>("attackPlayers", true, Possibilities.BOOLEAN_VALUES).register();
    public static final Flag<Boolean> ATTACK_ENTITIES = new Flag<>("attackEntities", true, Possibilities.BOOLEAN_VALUES).register();
    public static final Flag<Boolean> HUNGER = new Flag<>("hunger", true, Possibilities.BOOLEAN_VALUES).register();
    public static final Flag<Boolean> PHYSICS = new Flag<>("physics", true, Possibilities.BOOLEAN_VALUES).register();
    public static final Flag<Boolean> EXPLOSIONS = new Flag<>("explosions", true, Possibilities.BOOLEAN_VALUES).register();
    public static final Flag<Boolean> SHOOT = new Flag<>("shoot", true, Possibilities.BOOLEAN_VALUES).register();

    @ToString.Include
    private final String name;
    private final T defaultValue;
    @EqualsAndHashCode.Exclude
    private final Possibilities<T> possibilities;

    /**
     * registers a flag
     *
     * @return the flag
     */
    public Flag<T> register() {
        Preconditions.checkArgument(!Area.isInitialized(), "Areas are already initialized");
        flags.put(name(), this);
        return this;
    }

    /**
     * unregisters a flag
     */
    public void unregister() {
        flags.remove(name());
    }

    /**
     * the value type provided by the flag
     *
     * @return the class of the value
     */
    public Class<T> type() {
        return (Class<T>) defaultValue().getClass();
    }

    /**
     * returns the flag registered for the name
     *
     * @param name the name used to register the desired flag
     * @param <T>  the type of the value hold by the flag
     * @return the flag
     */
    @Nullable
    public static <T> Flag<T> valueOf(String name) {
        return (Flag<T>) flags.get(name);
    }

    /**
     * @return all the registered flags
     */
    public static Flag<?>[] values() {
        return flags.values().toArray(new Flag[]{});
    }

    /**
     * @return all the names of the registered flags
     */
    public static Set<String> names() {
        return flags.keySet();
    }
}
