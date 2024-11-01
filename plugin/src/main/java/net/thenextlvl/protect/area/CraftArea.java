package net.thenextlvl.protect.area;

import com.google.common.reflect.TypeToken;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.Tag;
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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@Setter
@ToString
@NullMarked
@EqualsAndHashCode
public abstract class CraftArea implements Area {
    protected final ProtectPlugin plugin;

    @Getter
    private final String name;
    @Getter
    private final World world;

    private final Set<UUID> members;
    private @Nullable UUID owner;

    private final Map<Flag<?>, @Nullable Object> flags;
    private @Getter int priority;

    protected CraftArea(ProtectPlugin plugin,
                        String name,
                        World world,
                        Set<UUID> members,
                        @Nullable UUID owner,
                        Map<Flag<?>, @Nullable Object> flags,
                        int priority) {
        this.plugin = plugin;
        this.name = name;
        this.world = world;
        this.members = new HashSet<>(members);
        this.owner = owner;
        this.flags = new HashMap<>(flags);
        this.priority = priority;
    }

    public CraftArea(ProtectPlugin plugin, World world, String name, CompoundTag tag) {
        this.plugin = plugin;
        this.name = name;
        this.world = world;
        this.members = new HashSet<>();
        this.flags = new HashMap<>();
        deserialize(tag);
    }

    @Override
    public Server getServer() {
        return plugin.getServer();
    }

    @Override
    public LinkedHashSet<Area> getParents() {
        var parents = new LinkedHashSet<Area>();
        var parent = getParent().orElse(null);
        while (parent != null && parent != this && parents.add(parent))
            parent = parent.getParent().orElse(null);
        return parents;
    }

    @Override
    public Map<Flag<?>, @Nullable Object> getFlags() {
        return Map.copyOf(flags);
    }

    @Override
    public Optional<UUID> getOwner() {
        return Optional.ofNullable(owner);
    }

    @Override
    public boolean isMember(UUID uuid) {
        return members.contains(uuid);
    }

    @Override
    public boolean isPermitted(UUID uuid) {
        return (owner != null && owner.equals(uuid)) || members.contains(uuid);
    }

    @Override
    public boolean removeMember(UUID uuid) {
        var event = new AreaMemberRemoveEvent(this, uuid);
        return event.callEvent() && members.remove(event.getMember());
    }

    @Override
    public boolean addMember(UUID uuid) {
        var event = new AreaMemberAddEvent(this, uuid);
        return event.callEvent() && members.add(event.getMember());
    }

    @Override
    public void setMembers(Set<UUID> members) {
        if (Objects.equals(members, this.members)) return;
        for (var member : this.members) if (!members.contains(member)) removeMember(member);
        for (var member : members) if (!this.members.contains(member)) addMember(member);
    }

    @Override
    public Set<UUID> getMembers() {
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
    public void setFlags(Map<Flag<?>, @Nullable Object> flags) {
        if (Objects.equals(this.flags, flags)) return;
        flags.forEach((flag, o) -> setFlag((Flag<Object>) flag, o));
    }

    @Override
    @NullUnmarked
    @SuppressWarnings("unchecked")
    public <T> T getFlag(@NonNull Flag<T> flag) {
        var value = (T) getFlags().get(flag);
        if (value != null) return value;
        return getParent().map(area -> area.getFlag(flag))
                .orElseGet(flag::defaultValue);
    }

    @Override
    @NullUnmarked
    public <T> boolean setFlag(@NonNull Flag<T> flag, T state) {
        if (Objects.equals(getFlag(flag), state)) return false;
        var event = new AreaFlagChangeEvent<>(this, flag, state);
        return event.callEvent() && !Objects.equals(flags.put(flag, event.getNewState()), event.getNewState());
    }

    @Override
    public <T> boolean removeFlag(Flag<T> flag) {
        if (!flags.containsKey(flag)) return false;
        var event = new AreaFlagResetEvent<>(this, flag);
        if (event.callEvent()) flags.remove(flag);
        return !event.isCancelled();
    }

    @Override
    public <T> boolean hasFlag(Flag<T> flag) {
        return flags.containsKey(flag);
    }

    @Override
    public List<Entity> getHighestEntities() {
        return getEntities().stream()
                .filter(player -> plugin.areaProvider().getArea(player).equals(this))
                .toList();
    }

    @Override
    public List<Player> getHighestPlayers() {
        return getPlayers().stream()
                .filter(player -> plugin.areaProvider().getArea(player).equals(this))
                .toList();
    }

    @Override
    public int compareTo(Area area) {
        return Integer.compare(getPriority(), area.getPriority());
    }

    @Override
    public CompoundTag serialize() {
        var tag = new CompoundTag();
        if (!flags.isEmpty()) tag.add("flags", plugin.nbt.toTag(flags, new TypeToken<Map<Flag<?>, Object>>() {
        }.getType()));
        if (!members.isEmpty()) tag.add("members", plugin.nbt.toTag(members, new TypeToken<Set<UUID>>() {
        }.getType()));
        if (owner != null) tag.add("owner", plugin.nbt.toTag(owner));
        tag.add("priority", priority);
        var adapter = plugin.areaService().getAdapter(getClass());
        tag.add("type", adapter.key().asString());
        return tag;
    }

    @Override
    public void deserialize(Tag tag) {
        var compound = tag.getAsCompound();
        readFlags(compound).ifPresent(flags::putAll);
        readMembers(compound).ifPresent(members::addAll);
        readOwner(compound).ifPresent(owner -> this.owner = owner);
        readPriority(compound).ifPresent(priority -> this.priority = priority);
    }

    private Optional<Map<Flag<?>, Object>> readFlags(CompoundTag tag) {
        return tag.optional("flags").map(flags -> {
            var type = new TypeToken<Map<Flag<?>, Object>>() {
            }.getType();
            return plugin.nbt.fromTag(flags, type);
        });
    }

    private Optional<Set<UUID>> readMembers(CompoundTag tag) {
        return tag.optional("members").map(members ->
                plugin.nbt.fromTag(members, new TypeToken<Set<UUID>>() {
                }.getType()));
    }

    private Optional<UUID> readOwner(CompoundTag tag) {
        return tag.optional("owner").map(owner ->
                plugin.nbt.fromTag(owner, UUID.class));
    }

    private Optional<Integer> readPriority(CompoundTag tag) {
        return tag.optional("priority").map(Tag::getAsInt);
    }
}
