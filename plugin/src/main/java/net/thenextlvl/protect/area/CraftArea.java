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
import net.thenextlvl.protect.flag.FlagInstance;
import org.bukkit.Server;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public abstract class CraftArea implements Area {
    protected final ProtectPlugin plugin;

    private final String name;
    private final World world;

    private final Set<UUID> members = ConcurrentHashMap.newKeySet();
    private volatile @Nullable UUID owner;

    private final Map<Key, Flag<?>> flags;
    private final Map<Key, Tag> unresolvedFlags = new ConcurrentHashMap<>();
    private volatile int priority;

    private final Map<String, Tag> dataContainer = new ConcurrentHashMap<>();

    protected CraftArea(final ProtectPlugin plugin,
                        final String name,
                        final World world,
                        final Set<UUID> members,
                        @Nullable final UUID owner,
                        final Set<Flag<?>> flags,
                        final int priority) {
        this.plugin = plugin;
        this.name = name;
        this.world = world;
        this.members.addAll(members);
        this.owner = owner;
        final var map = new HashMap<Key, Flag<?>>();
        flags.forEach(flag -> map.put(flag.instance().key(), flag));
        this.flags = new ConcurrentHashMap<>(map);
        this.priority = priority;
    }

    public CraftArea(final ProtectPlugin plugin, final World world, final String name, final CompoundTag tag) {
        this.plugin = plugin;
        this.name = name;
        this.world = world;
        this.flags = new ConcurrentHashMap<>();
        deserialize(tag);
    }

    @Override
    public Server getServer() {
        return plugin.getServer();
    }

    @Override
    public LinkedHashSet<Area> getParents() {
        final var parents = new LinkedHashSet<Area>();
        var parent = getParent().orElse(null);
        while (parent != null && parent != this && parents.add(parent))
            parent = parent.getParent().orElse(null);
        return parents;
    }

    @Override
    public Stream<Flag<?>> getFlags() {
        resolveFlags(); // todo: keep this?
        return flags.values().stream();
    }

    @Override
    public Optional<UUID> getOwner() {
        return Optional.ofNullable(owner);
    }

    @Override
    public boolean isMember(final UUID uuid) {
        return members.contains(uuid);
    }

    @Override
    public boolean isPermitted(final UUID uuid) {
        return uuid.equals(owner) || members.contains(uuid);
    }

    @Override
    public boolean removeMember(final UUID uuid) {
        final var event = new AreaMemberRemoveEvent(this, uuid);
        return event.callEvent() && members.remove(event.getMember());
    }

    @Override
    public boolean addMember(final UUID uuid) {
        final var event = new AreaMemberAddEvent(this, uuid);
        return event.callEvent() && members.add(event.getMember());
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setMembers(final Set<UUID> members) {
        if (Objects.equals(members, this.members)) return;
        for (final var member : this.members) if (!members.contains(member)) removeMember(member);
        for (final var member : members) if (!this.members.contains(member)) addMember(member);
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
    public boolean setOwner(@Nullable final UUID owner) {
        if (Objects.equals(owner, this.owner)) return false;
        final var event = new AreaOwnerChangeEvent(this, owner);
        if (event.callEvent()) this.owner = event.getOwner();
        return !event.isCancelled();
    }

    public boolean setPriority(final int priority) {
        if (this.priority == priority) return false;
        final var event = new AreaPriorityChangeEvent(this, priority);
        if (!event.callEvent()) return false;
        this.priority = priority;
        return true;
    }

    @Override
    public <T> Flag<T> getFlag(final FlagInstance<T> instance) {
        return instance.withValue(findFlag(instance).map(Flag::value).orElseGet(instance::defaultValue));
    }

    @Override
    public <T> T getFlagValue(final FlagInstance<T> instance) {
        return getFlag(instance).value();
    }

    @Override
    public <T> Optional<T> findFlagValue(final FlagInstance<T> flag) {
        return findFlag(flag).map(Flag::value);
    }

    @Override
    public <T> Optional<Flag<T>> findFlag(final FlagInstance<T> flag) {
        final var value = resolveFlag(flag);
        if (value != null) return Optional.of(value);
        return getParent().flatMap(area -> area.findFlag(flag));
    }

    @Override
    public <T> boolean setFlag(final FlagInstance<T> flagInstance, @Nullable final T value) {
        if (value == null) return removeFlag(flagInstance);
        if (findFlag(flagInstance).map(value::equals).orElse(false)) return false;

        final var event = new AreaFlagChangeEvent(this, flagInstance.withValue(value));
        if (!event.callEvent()) return false;

        final var newValue = event.<T>getFlag();
        final var previous = flags.put(flagInstance.key(), newValue);
        return !newValue.equals(previous);
    }

    @Override
    public boolean removeFlag(final FlagInstance<?> flag) {
        if (!flags.containsKey(flag.key()) && !unresolvedFlags.containsKey(flag.key())) return false;
        if (!new AreaFlagResetEvent(this, flag).callEvent()) return false;
        flags.remove(flag.key());
        unresolvedFlags.remove(flag.key());
        return true;
    }

    @Override
    public boolean hasFlag(final FlagInstance<?> flag) {
        return flags.containsKey(flag.key());
    }

    private void resolveFlags() {
        unresolvedFlags.keySet().forEach(key -> plugin.flagRegistry().getFlag(key).ifPresent(this::resolveFlag));
    }

    @SuppressWarnings("unchecked")
    private <T> @Nullable Flag<T> resolveFlag(final FlagInstance<T> flag) {
        final var tag = unresolvedFlags.remove(flag.key());
        if (tag != null) return decodeFlag(flag, tag);
        return (Flag<T>) flags.get(flag.key());
    }

    private <T> @Nullable Flag<T> decodeFlag(final FlagInstance<T> flag, final Tag tag) {
        try {
            final var value = flag.withValue(flag.flagType().decode(tag));
            flags.putIfAbsent(flag.key(), value);
            return value;
        } catch (final RuntimeException e) {
            plugin.getComponentLogger().warn("Failed to decode flag {} of area {}", flag.key().asString(), name, e);
            return null;
        }
    }

    @Override
    public int compareTo(final Area area) {
        return Integer.compare(getPriority(), area.getPriority());
    }

    @Override
    public CompoundTag serialize() {
        final var flags = CompoundTag.builder();
        unresolvedFlags.forEach((key, value) -> flags.put(key.asString(), value));
        this.flags.forEach((key, flag) -> flags.put(key.asString(), encode(flag)));
        final var members = Set.copyOf(this.members);
        final var data = new LinkedHashMap<>(dataContainer);
        final var owner = this.owner;

        final var tag = CompoundTag.builder();
        if (!flags.isEmpty()) tag.put("flags", flags.build());
        if (!members.isEmpty()) tag.put("members", plugin.nbt.serialize(members, new TypeToken<Set<UUID>>() {
        }.getType()));
        if (!data.isEmpty()) tag.put("data", CompoundTag.of(data));
        if (owner != null) tag.put("owner", plugin.nbt.serialize(owner));
        tag.put("priority", priority);
        final var adapter = plugin.areaService().getAdapter(getClass());
        tag.put("type", adapter.key().asString());
        return tag.build();
    }

    private static <T> Tag encode(final Flag<T> flag) {
        return flag.instance().flagType().encode(flag.value());
    }

    @Override
    public void deserialize(final CompoundTag tag) {
        readFlags(tag).ifPresent(unresolvedFlags::putAll);
        resolveFlags();
        readMembers(tag).ifPresent(members::addAll);
        readOwner(tag).ifPresent(owner -> this.owner = owner);
        readPriority(tag).ifPresent(priority -> this.priority = priority);
        tag.<CompoundTag>optional("data").ifPresent(data -> data.forEach(dataContainer::put));
    }

    @SuppressWarnings("PatternValidation")
    private Optional<Map<Key, Tag>> readFlags(final CompoundTag tag) {
        return tag.<CompoundTag>optional("flags").map(flags -> {
            final var map = new LinkedHashMap<Key, Tag>();
            flags.forEach((key, value) -> map.put(Key.key(key), value));
            return map;
        });
    }

    private Optional<Set<UUID>> readMembers(final CompoundTag tag) {
        return tag.optional("members").map(members ->
                plugin.nbt.deserialize(members, new TypeToken<Set<UUID>>() {
                }.getType()));
    }

    private Optional<UUID> readOwner(final CompoundTag tag) {
        return tag.optional("owner").map(owner ->
                plugin.nbt.deserialize(owner, UUID.class));
    }

    private Optional<Integer> readPriority(final CompoundTag tag) {
        return tag.optional("priority").map(Tag::getAsInt);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Tag> Optional<T> get(final Key key) {
        return Optional.ofNullable((T) dataContainer.get(key.asString()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Tag> Optional<T> remove(final Key key) {
        return Optional.ofNullable((T) dataContainer.remove(key.asString()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Tag> T getOrDefault(final Key key, final T defaultValue) {
        return (T) dataContainer.getOrDefault(key.asString(), defaultValue);
    }

    @Override
    public <T extends Tag> void set(final Key key, final T value) {
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
    public boolean has(final Key key) {
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
    public void copyTo(final DataContainer dataContainer) {
        copyTo(dataContainer, false);
    }

    @Override
    public void copyTo(final DataContainer dataContainer, final boolean replace) {
        if (replace) dataContainer.clear();
        forEach(dataContainer::set);
    }

    @Override
    public void forEach(final BiConsumer<Key, Tag> action) {
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
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CraftArea craftArea = (CraftArea) o;
        return Objects.equals(name, craftArea.name) && Objects.equals(world, craftArea.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, world);
    }
}
