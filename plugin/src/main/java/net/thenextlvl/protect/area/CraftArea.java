package net.thenextlvl.protect.area;

import core.nbt.tag.CompoundTag;
import core.nbt.tag.ListTag;
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
import org.bukkit.NamespacedKey;
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

    private Map<Flag<?>, @Nullable Object> flags;
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
        this.members = members;
        this.owner = owner;
        this.flags = flags;
        this.priority = priority;
    }

    public CraftArea(ProtectPlugin plugin, World world, String name, CompoundTag tag) {
        this.plugin = plugin;
        this.name = name;
        this.world = world;
        this.members = new HashSet<>();
        this.flags = new LinkedHashMap<>();
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
        writeFlags(tag);
        writeMembers(tag);
        writeOwner(tag);
        writePriority(tag);
        writeType(tag);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        readFlags(tag);
        readMembers(tag);
        readOwner(tag);
        readPriority(tag);
    }

    private void readFlags(CompoundTag tag) {
        tag.<CompoundTag>optional("flags").ifPresent(flags ->
                flags.forEach(this::readFlag));
    }

    private void readMembers(CompoundTag tag) {
        tag.<ListTag<CompoundTag>>optional("members")
                .ifPresent(tags -> tags.stream()
                        .map(this::readUUID)
                        .forEach(members::add));
    }

    private void readOwner(CompoundTag tag) {
        tag.<CompoundTag>optional("owner")
                .ifPresent(this::readOwner);
    }

    private void readPriority(CompoundTag tag) {
        tag.optional("priority").map(Tag::getAsInt)
                .ifPresent(value -> this.priority = value);
    }

    private void readFlag(String key, Tag value) {
        var split = key.split(":", 2);
        var namespace = new NamespacedKey(split[0], split[1]);
        plugin.flagRegistry().getFlag(namespace).ifPresentOrElse(flag -> {
            if (flag.type().isAssignableFrom(Number.class)) this.flags.put(flag, value.getAsNumber());
            else if (flag.type().isAssignableFrom(Boolean.class)) this.flags.put(flag, value.getAsBoolean());
            else if (flag.type().isAssignableFrom(String.class)) this.flags.put(flag, value.getAsString());
            else throw new IllegalArgumentException("Unsupported flag type: " + flag.type().getName());
        }, () -> plugin.getComponentLogger().warn("Non-existent flag {} in area {}", namespace, getName()));
    }

    private UUID readUUID(CompoundTag tag) {
        var most = tag.get("most").getAsLong();
        var least = tag.get("least").getAsLong();
        return new UUID(most, least);
    }

    private void writeFlags(CompoundTag root) {
        var flags = new CompoundTag();
        this.flags.forEach((flag, value) -> writeFlag(flag, value, flags));
        root.add("flags", flags);
    }

    private void writeFlag(Flag<?> flag, @Nullable Object value, CompoundTag flags) {
        switch (value) {
            case null -> flags.add(flag.key().asString(), new CompoundTag());
            case Boolean bool -> flags.add(flag.key().asString(), bool);
            case String str -> flags.add(flag.key().asString(), str);
            case Number number -> flags.add(flag.key().asString(), number);
            default -> throw new IllegalArgumentException("Unsupported flag type: " + flag.type().getName());
        }
    }

    private void writeMembers(CompoundTag tag) {
        var members = new ListTag<CompoundTag>(CompoundTag.ID);
        this.members.stream().map(uuid -> {
            var member = new CompoundTag();
            writeUUID(uuid, member);
            return member;
        }).forEach(members::add);
        tag.add("members", members);
    }

    private void writeOwner(CompoundTag root) {
        if (this.owner == null) return;
        var owner = new CompoundTag();
        writeUUID(this.owner, owner);
        root.add("owner", owner);
    }

    private void writeUUID(UUID uuid, CompoundTag tag) {
        tag.add("most", uuid.getMostSignificantBits());
        tag.add("least", uuid.getLeastSignificantBits());
    }

    private void writePriority(CompoundTag root) {
        root.add("priority", priority);
    }

    private void writeType(CompoundTag root) {
        var adapter = plugin.areaService().getAdapter(getClass());
        root.add("type", adapter.key().asString());
    }
}
