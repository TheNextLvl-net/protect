package net.thenextlvl.protect.area;

import com.google.common.reflect.TypeToken;
import net.kyori.adventure.key.Key;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@NullMarked
public abstract class CraftArea implements Area {
    protected final ProtectPlugin plugin;

    private final String name;
    private final World world;

    private final Set<UUID> members;
    private @Nullable UUID owner;

    private final Map<Flag<?>, @Nullable Object> flags;
    private int priority;

    private final Map<String, Tag> dataContainer = new LinkedHashMap<>();

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
    public int getPriority() {
        return priority;
    }

    @Override
    public void setMembers(Set<UUID> members) {
        if (Objects.equals(members, this.members)) return;
        for (var member : this.members) if (!members.contains(member)) removeMember(member);
        for (var member : members) if (!this.members.contains(member)) addMember(member);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public World getWorld() {
        return world;
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
    public int compareTo(Area area) {
        return Integer.compare(getPriority(), area.getPriority());
    }

    @Override
    public CompoundTag serialize() {
        var tag = CompoundTag.builder();
        if (!flags.isEmpty()) tag.put("flags", plugin.nbt.serialize(flags, new TypeToken<Map<Flag<?>, Object>>() {
        }.getType()));
        if (!members.isEmpty()) tag.put("members", plugin.nbt.serialize(members, new TypeToken<Set<UUID>>() {
        }.getType()));
        if (!dataContainer.isEmpty()) tag.put("data", CompoundTag.of(dataContainer));
        if (owner != null) tag.put("owner", plugin.nbt.serialize(owner));
        tag.put("priority", priority);
        var adapter = plugin.areaService().getAdapter(getClass());
        tag.put("type", adapter.key().asString());
        return tag.build();
    }

    @Override
    public void deserialize(CompoundTag tag) {
        readFlags(tag).ifPresent(flags::putAll);
        readMembers(tag).ifPresent(members::addAll);
        readOwner(tag).ifPresent(owner -> this.owner = owner);
        readPriority(tag).ifPresent(priority -> this.priority = priority);
        tag.<CompoundTag>optional("data").ifPresent(data -> data.forEach(dataContainer::put));
    }

    private Optional<Map<Flag<?>, Object>> readFlags(CompoundTag tag) {
        return tag.optional("flags").map(flags -> {
            var type = new TypeToken<Map<Flag<?>, Object>>() {
            }.getType();
            return plugin.nbt.deserialize(flags, type);
        });
    }

    private Optional<Set<UUID>> readMembers(CompoundTag tag) {
        return tag.optional("members").map(members ->
                plugin.nbt.deserialize(members, new TypeToken<Set<UUID>>() {
                }.getType()));
    }

    private Optional<UUID> readOwner(CompoundTag tag) {
        return tag.optional("owner").map(owner ->
                plugin.nbt.deserialize(owner, UUID.class));
    }

    private Optional<Integer> readPriority(CompoundTag tag) {
        return tag.optional("priority").map(Tag::getAsInt);
    }

    @Override
    public <T extends Tag> Optional<T> get(Key key) {
        return Optional.ofNullable((T) dataContainer.get(key.asString()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Tag> Optional<T> remove(Key key) {
        return Optional.ofNullable((T) dataContainer.remove(key.asString()));
    }

    @Override
    public <T extends Tag> T getOrDefault(Key key, T defaultValue) {
        return (T) dataContainer.getOrDefault(key.asString(), defaultValue);
    }

    @Override
    public <T extends Tag> void set(Key key, T value) {
        dataContainer.put(key.asString(), value);
    }

    @Override
    @SuppressWarnings("PatternValidation")
    public Map<Key, Tag> getTags() {
        return dataContainer.entrySet().stream()
                .map(entry -> Map.entry(Key.key(entry.getKey()), entry.getValue()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Set<Key> getKeys() {
        return dataContainer.keySet().stream()
                .map(Key::key)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<Map.Entry<Key, Tag>> entrySet() {
        return getTags().entrySet();
    }

    @Override
    public boolean has(Key key) {
        return dataContainer.containsKey(key.asString());
    }

    @Override
    public boolean isEmpty() {
        return dataContainer.isEmpty();
    }

    @Override
    public void clear() {
        dataContainer.clear();
    }

    @Override
    public void copyTo(DataContainer dataContainer) {
        copyTo(dataContainer, false);
    }

    @Override
    public void copyTo(DataContainer dataContainer, boolean replace) {
        if (replace) dataContainer.clear();
        forEach(dataContainer::set);
    }

    @Override
    public void forEach(BiConsumer<Key, Tag> action) {
        entrySet().forEach(entry -> action.accept(entry.getKey(), entry.getValue()));
    }

    @Override
    public String toString() {
        return "CraftArea{" +
                "name='" + name + '\'' +
                ", world=" + world +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CraftArea craftArea = (CraftArea) o;
        return Objects.equals(name, craftArea.name) && Objects.equals(world, craftArea.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, world);
    }
}
