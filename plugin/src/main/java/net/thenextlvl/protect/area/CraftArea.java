package net.thenextlvl.protect.area;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.event.flag.AreaFlagChangeEvent;
import net.thenextlvl.protect.area.event.flag.AreaFlagResetEvent;
import net.thenextlvl.protect.area.event.inheritance.AreaPriorityChangeEvent;
import net.thenextlvl.protect.area.event.member.AreaMemberAddEvent;
import net.thenextlvl.protect.area.event.member.AreaMemberRemoveEvent;
import net.thenextlvl.protect.area.event.member.AreaOwnerChangeEvent;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

@Setter
@ToString
@EqualsAndHashCode
public abstract class CraftArea implements Area {
    protected final ProtectPlugin plugin;

    @Getter
    private final @NotNull String name;
    @Getter
    private final @NotNull World world;

    private final @NotNull Set<@NotNull UUID> members;
    private @Nullable UUID owner;

    private @NotNull Map<@NotNull Flag<?>, @Nullable Object> flags;
    private @Getter int priority;

    protected CraftArea(@NotNull ProtectPlugin plugin,
                        @NotNull String name,
                        @NotNull World world,
                        @NotNull Set<@NotNull UUID> members,
                        @Nullable UUID owner,
                        @NotNull Map<@NotNull Flag<?>, @Nullable Object> flags,
                        int priority) {
        this.plugin = plugin;
        this.name = name;
        this.world = world;
        this.members = members;
        this.owner = owner;
        this.flags = flags;
        this.priority = priority;
    }

    @Override
    public @NotNull Server getServer() {
        return plugin.getServer();
    }

    @Override
    public @NotNull LinkedHashSet<@NotNull Area> getParents() {
        var parents = new LinkedHashSet<Area>();
        var parent = getParent().orElse(null);
        while (parent != null && parent != this && parents.add(parent))
            parent = parent.getParent().orElse(null);
        return parents;
    }

    @Override
    public @NotNull Map<@NotNull Flag<?>, @Nullable Object> getFlags() {
        return Map.copyOf(flags);
    }

    @Override
    public @NotNull Optional<UUID> getOwner() {
        return Optional.ofNullable(owner);
    }

    @Override
    public boolean isMember(@NotNull UUID uuid) {
        return members.contains(uuid);
    }

    @Override
    public boolean isPermitted(@NotNull UUID uuid) {
        return (owner != null && owner.equals(uuid)) || members.contains(uuid);
    }

    @Override
    public boolean removeMember(@NotNull UUID uuid) {
        var event = new AreaMemberRemoveEvent(this, uuid);
        return event.callEvent() && members.remove(event.getMember());
    }

    @Override
    public boolean addMember(@NotNull UUID uuid) {
        var event = new AreaMemberAddEvent(this, uuid);
        return event.callEvent() && members.add(event.getMember());
    }

    @Override
    public void setMembers(@NotNull Set<@NotNull UUID> members) {
        if (Objects.equals(members, this.members)) return;
        for (var member : this.members) if (!members.contains(member)) removeMember(member);
        for (var member : members) if (!this.members.contains(member)) addMember(member);
    }

    @Override
    public @NotNull Set<@NotNull UUID> getMembers() {
        return Set.copyOf(members);
    }

    @Override
    public boolean setOwner(@Nullable UUID owner) {
        if (Objects.equals(owner, this.owner)) return false;
        var event = new AreaOwnerChangeEvent(this, owner);
        if (event.callEvent()) this.owner = event.getOwner();
        return !event.isCancelled();
    }

    public boolean setPriority(int priority) {
        if (this.priority == priority) return false;
        var event = new AreaPriorityChangeEvent(this, priority);
        if (!event.callEvent()) return false;
        this.priority = priority;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setFlags(@NotNull Map<@NotNull Flag<?>, @Nullable Object> flags) {
        if (Objects.equals(this.flags, flags)) return;
        flags.forEach((flag, o) -> setFlag((Flag<Object>) flag, o));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getFlag(@NotNull Flag<T> flag) {
        var value = (T) getFlags().get(flag);
        if (value != null) return value;
        return getParent().map(area -> area.getFlag(flag))
                .orElseGet(flag::defaultValue);
    }

    @Override
    public <T> boolean setFlag(@NotNull Flag<T> flag, T state) {
        if (Objects.equals(getFlag(flag), state)) return false;
        var event = new AreaFlagChangeEvent<>(this, flag, state);
        return event.callEvent() && !Objects.equals(flags.put(flag, event.getNewState()), event.getNewState());
    }

    @Override
    public <T> boolean removeFlag(@NotNull Flag<T> flag) {
        if (!flags.containsKey(flag)) return false;
        var event = new AreaFlagResetEvent<>(this, flag);
        if (event.callEvent()) flags.remove(flag);
        return !event.isCancelled();
    }

    @Override
    public <T> boolean hasFlag(@NotNull Flag<T> flag) {
        return flags.containsKey(flag);
    }

    @Override
    @Unmodifiable
    public @NotNull List<@NotNull Entity> getHighestEntities() {
        return getEntities().stream()
                .filter(player -> plugin.areaProvider().getArea(player).equals(this))
                .toList();
    }

    @Override
    @Unmodifiable
    public @NotNull List<@NotNull Player> getHighestPlayers() {
        return getPlayers().stream()
                .filter(player -> plugin.areaProvider().getArea(player).equals(this))
                .toList();
    }

    @Override
    public int compareTo(@NotNull Area area) {
        return Integer.compare(getPriority(), area.getPriority());
    }
}
