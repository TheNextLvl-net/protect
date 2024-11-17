package net.thenextlvl.protect;

import com.fastasyncworldedit.core.util.WEManager;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.reflect.TypeToken;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import core.i18n.file.ComponentBundle;
import core.nbt.serialization.NBT;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.protect.adapter.area.CuboidAreaAdapter;
import net.thenextlvl.protect.adapter.area.CylinderAreaAdapter;
import net.thenextlvl.protect.adapter.area.EllipsoidAreaAdapter;
import net.thenextlvl.protect.adapter.area.GlobalAreaAdapter;
import net.thenextlvl.protect.adapter.area.GroupedAreaAdapter;
import net.thenextlvl.protect.adapter.other.FlagsAdapter;
import net.thenextlvl.protect.adapter.other.KeyAdapter;
import net.thenextlvl.protect.adapter.other.LocationAdapter;
import net.thenextlvl.protect.adapter.other.MembersAdapter;
import net.thenextlvl.protect.adapter.other.WeatherTypeAdapter;
import net.thenextlvl.protect.adapter.other.WorldAdapter;
import net.thenextlvl.protect.adapter.region.CuboidRegionAdapter;
import net.thenextlvl.protect.adapter.region.CylinderRegionAdapter;
import net.thenextlvl.protect.adapter.region.EllipsoidRegionAdapter;
import net.thenextlvl.protect.adapter.region.GroupedRegionAdapter;
import net.thenextlvl.protect.adapter.vector.BlockVectorAdapter;
import net.thenextlvl.protect.adapter.vector.Vector2Adapter;
import net.thenextlvl.protect.adapter.vector.Vector3Adapter;
import net.thenextlvl.protect.area.*;
import net.thenextlvl.protect.command.AreaCommand;
import net.thenextlvl.protect.flag.CraftFlagRegistry;
import net.thenextlvl.protect.flag.Flag;
import net.thenextlvl.protect.flag.FlagRegistry;
import net.thenextlvl.protect.flag.ProtectionFlag;
import net.thenextlvl.protect.listener.AreaListener;
import net.thenextlvl.protect.listener.EntityListener;
import net.thenextlvl.protect.listener.MovementListener;
import net.thenextlvl.protect.listener.PhysicsListener;
import net.thenextlvl.protect.listener.WorldListener;
import net.thenextlvl.protect.mask.ProtectMaskManager;
import net.thenextlvl.protect.region.GroupedRegion;
import net.thenextlvl.protect.service.CraftProtectionService;
import net.thenextlvl.protect.service.ProtectionService;
import net.thenextlvl.protect.version.PluginVersionChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@NullMarked
@Accessors(fluent = true)
public class ProtectPlugin extends JavaPlugin {
    private final Metrics metrics = new Metrics(this, 21712);
    private final File schematicFolder = new File(getDataFolder(), "schematics");
    private final PluginVersionChecker versionChecker = new PluginVersionChecker(this);

    private final CraftProtectionService protectionService = new CraftProtectionService(this);
    private final CraftFlagRegistry flagRegistry = new CraftFlagRegistry();
    private final CraftAreaProvider areaProvider = new CraftAreaProvider(this);
    private final CraftAreaService areaService = new CraftAreaService(this);

    @Getter(AccessLevel.NONE)
    public final Flags flags = new Flags();

    private final ComponentBundle bundle = new ComponentBundle(new File(getDataFolder(), "translations"),
            audience -> audience instanceof Player player ? player.locale() : Locale.US)
            .register("protect", Locale.US)
            .register("protect_german", Locale.GERMANY)
            .miniMessage(bundle -> MiniMessage.builder().tags(TagResolver.resolver(
                    TagResolver.standard(),
                    Placeholder.component("failed_prefix", bundle.component(Locale.US, "prefix.failed")),
                    Placeholder.component("prefix", bundle.component(Locale.US, "prefix"))
            )).build());

    @Override
    public void onLoad() {
        WEManager.weManager().addManager(new ProtectMaskManager(this));
        versionChecker().checkVersion();
        registerAdapters();
        registerServices();
        registerWrappers();
    }

    @Override
    public void onEnable() {
        registerCommands();
        registerEvents();
    }

    @Override
    public void onDisable() {
        getServer().getWorlds().forEach(world -> {
            areaProvider().save(world);
            areaProvider().unload(world);
        });
        metrics().shutdown();
    }

    private void registerServices() {
        getServer().getServicesManager().register(ProtectionService.class, protectionService(), this, ServicePriority.Highest);
        getServer().getServicesManager().register(FlagRegistry.class, flagRegistry(), this, ServicePriority.Highest);
        getServer().getServicesManager().register(AreaProvider.class, areaProvider(), this, ServicePriority.Highest);
        getServer().getServicesManager().register(AreaService.class, areaService(), this, ServicePriority.Highest);
    }

    private void registerAdapters() {
        areaService().registerAdapter(CraftCuboidArea.class, new CuboidAreaAdapter(this));
        areaService().registerAdapter(CraftCylinderArea.class, new CylinderAreaAdapter(this));
        areaService().registerAdapter(CraftEllipsoidArea.class, new EllipsoidAreaAdapter(this));
        areaService().registerAdapter(CraftGlobalArea.class, new GlobalAreaAdapter(this));
        areaService().registerAdapter(CraftGroupedArea.class, new GroupedAreaAdapter(this));
    }

    private void registerWrappers() {
        areaService().registerWrapper(CuboidRegion.class, creator -> new CraftCuboidArea(this, creator));
        areaService().registerWrapper(CylinderRegion.class, creator -> new CraftCylinderArea(this, creator));
        areaService().registerWrapper(EllipsoidRegion.class, creator -> new CraftEllipsoidArea(this, creator));
        areaService().registerWrapper(GroupedRegion.class, creator -> new CraftGroupedArea(this, creator));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new AreaListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new MovementListener(this), this);
        getServer().getPluginManager().registerEvents(new PhysicsListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
    }

    private void registerCommands() {
        new AreaCommand(this).register();
    }

    private final Cache<Audience, String> cooldown = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .build();

    public void failed(@Nullable Audience audience, Area area, String message) {
        if (audience == null || !area.getFlag(flags.notifyFailedInteractions)) return;
        if (message.equals(cooldown.getIfPresent(audience))) return;
        bundle().sendMessage(audience, message, Placeholder.parsed("area", area.getName()));
        cooldown.put(audience, message);
    }

    public void failed(@Nullable Audience audience, Cancellable cancellable, Area area, String message) {
        if (cancellable.isCancelled()) failed(audience, area, message);
    }

    public final NBT nbt = NBT.builder()
            .registerTypeAdapter(new TypeToken<Map<Flag<?>, @Nullable Object>>() {
            }.getType(), new FlagsAdapter(this))
            .registerTypeAdapter(new TypeToken<Set<UUID>>() {
            }.getType(), new MembersAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Key.class, new KeyAdapter())
            .registerTypeHierarchyAdapter(WeatherType.class, new WeatherTypeAdapter())
            .registerTypeHierarchyAdapter(World.class, new WorldAdapter(getServer()))
            .registerTypeHierarchyAdapter(CuboidRegion.class, new CuboidRegionAdapter())
            .registerTypeHierarchyAdapter(CylinderRegion.class, new CylinderRegionAdapter())
            .registerTypeHierarchyAdapter(EllipsoidRegion.class, new EllipsoidRegionAdapter())
            .registerTypeHierarchyAdapter(GroupedRegion.class, new GroupedRegionAdapter(this))
            .registerTypeHierarchyAdapter(BlockVector3.class, new BlockVectorAdapter())
            .registerTypeHierarchyAdapter(Vector2.class, new Vector2Adapter())
            .registerTypeHierarchyAdapter(Vector3.class, new Vector3Adapter())
            .build();

    public class Flags {
        public final Flag<@Nullable Integer> time = flagRegistry().register(ProtectPlugin.this, Integer.class, "time", null);
        public final Flag<@Nullable String> farewell = flagRegistry().register(ProtectPlugin.this, String.class, "farewell", null);
        public final Flag<@Nullable String> farewellActionbar = flagRegistry().register(ProtectPlugin.this, String.class, "farewell_actionbar", null);
        public final Flag<@Nullable String> farewellTitle = flagRegistry().register(ProtectPlugin.this, String.class, "farewell_title", null);
        public final Flag<@Nullable String> greetings = flagRegistry().register(ProtectPlugin.this, String.class, "greetings", null);
        public final Flag<@Nullable String> greetingsActionbar = flagRegistry().register(ProtectPlugin.this, String.class, "greetings_actionbar", null);
        public final Flag<@Nullable String> greetingsTitle = flagRegistry().register(ProtectPlugin.this, String.class, "greetings_title", null);
        public final Flag<@Nullable WeatherType> weather = flagRegistry().register(ProtectPlugin.this, WeatherType.class, "weather", null);

        public final Flag<Boolean> areaEnter = flagRegistry().register(ProtectPlugin.this, "enter", true);
        public final Flag<Boolean> areaLeave = flagRegistry().register(ProtectPlugin.this, "leave", true);
        public final Flag<Boolean> damage = flagRegistry().register(ProtectPlugin.this, "damage", true);
        public final Flag<Boolean> entityItemDrop = flagRegistry().register(ProtectPlugin.this, "entity_item_drop", true);
        public final Flag<Boolean> entityItemPickup = flagRegistry().register(ProtectPlugin.this, "entity_item_pickup", true);
        public final Flag<Boolean> gameEvents = flagRegistry().register(ProtectPlugin.this, "game_events", true);
        public final Flag<Boolean> gravity = flagRegistry().register(ProtectPlugin.this, "gravity", true);
        public final Flag<Boolean> hunger = flagRegistry().register(ProtectPlugin.this, "hunger", true);
        public final Flag<Boolean> liquidFlow = flagRegistry().register(ProtectPlugin.this, "liquid_flow", true);
        public final Flag<Boolean> naturalEntitySpawn = flagRegistry().register(ProtectPlugin.this, "natural_entity_spawn", true);
        public final Flag<Boolean> notifyFailedInteractions = flagRegistry().register(ProtectPlugin.this, "notify_failed_interactions", false);
        public final Flag<Boolean> physics = flagRegistry().register(ProtectPlugin.this, "physics", true);
        public final Flag<Boolean> redstone = flagRegistry().register(ProtectPlugin.this, "redstone", true);
        public final Flag<Boolean> shoot = flagRegistry().register(ProtectPlugin.this, "shoot", true);

        public final ProtectionFlag<Boolean> armorStandManipulate = flagRegistry().register(ProtectPlugin.this, "armor_stand_manipulate", true, false);
        public final ProtectionFlag<Boolean> armorWashing = flagRegistry().register(ProtectPlugin.this, "armor_washing", true, false);
        public final ProtectionFlag<Boolean> bannerWashing = flagRegistry().register(ProtectPlugin.this, "banner_washing", true, false);
        public final ProtectionFlag<Boolean> blockAbsorb = flagRegistry().register(ProtectPlugin.this, "block_absorb", true, false);
        public final ProtectionFlag<Boolean> blockBurning = flagRegistry().register(ProtectPlugin.this, "block_burning", true, false);
        public final ProtectionFlag<Boolean> blockDrying = flagRegistry().register(ProtectPlugin.this, "block_drying", true, false);
        public final ProtectionFlag<Boolean> blockFading = flagRegistry().register(ProtectPlugin.this, "block_fading", true, false);
        public final ProtectionFlag<Boolean> blockFertilize = flagRegistry().register(ProtectPlugin.this, "block_fertilize", true, false);
        public final ProtectionFlag<Boolean> blockForming = flagRegistry().register(ProtectPlugin.this, "block_forming", true, false);
        public final ProtectionFlag<Boolean> blockGrowth = flagRegistry().register(ProtectPlugin.this, "block_growth", true, false);
        public final ProtectionFlag<Boolean> blockIgniting = flagRegistry().register(ProtectPlugin.this, "block_igniting", true, false);
        public final ProtectionFlag<Boolean> blockMoisturising = flagRegistry().register(ProtectPlugin.this, "block_moisturising", true, false);
        public final ProtectionFlag<Boolean> blockSpread = flagRegistry().register(ProtectPlugin.this, "block_spread", true, false);
        public final ProtectionFlag<Boolean> cauldronEvaporation = flagRegistry().register(ProtectPlugin.this, "cauldron_evaporation", true, false);
        public final ProtectionFlag<Boolean> cauldronExtinguishEntity = flagRegistry().register(ProtectPlugin.this, "cauldron_extinguish_entity", true, false);
        public final ProtectionFlag<Boolean> cauldronLevelChangeUnknown = flagRegistry().register(ProtectPlugin.this, "cauldron_level_change_unknown", true, false);
        public final ProtectionFlag<Boolean> cropTrample = flagRegistry().register(ProtectPlugin.this, "crop_trample", true, false);
        public final ProtectionFlag<Boolean> destroy = flagRegistry().register(ProtectPlugin.this, "destroy", true, false);
        public final ProtectionFlag<Boolean> emptyBottle = flagRegistry().register(ProtectPlugin.this, "empty_bottle", true, false);
        public final ProtectionFlag<Boolean> entityAttackEntity = flagRegistry().register(ProtectPlugin.this, "entity_attack_entity", true, false);
        public final ProtectionFlag<Boolean> entityAttackPlayer = flagRegistry().register(ProtectPlugin.this, "entity_attack_player", true, false);
        public final ProtectionFlag<Boolean> entityInteract = flagRegistry().register(ProtectPlugin.this, "entity_interact", true, false);
        public final ProtectionFlag<Boolean> entityShear = flagRegistry().register(ProtectPlugin.this, "entity_shear", true, false);
        public final ProtectionFlag<Boolean> explosions = flagRegistry().register(ProtectPlugin.this, "explosions", true, false);
        public final ProtectionFlag<Boolean> fillBottle = flagRegistry().register(ProtectPlugin.this, "fill_bottle", true, false);
        public final ProtectionFlag<Boolean> interact = flagRegistry().register(ProtectPlugin.this, "interact", true, false);
        public final ProtectionFlag<Boolean> leavesDecay = flagRegistry().register(ProtectPlugin.this, "leaves_decay", true, false);
        public final ProtectionFlag<Boolean> naturalCauldronFill = flagRegistry().register(ProtectPlugin.this, "natural_cauldron_fill", true, false);
        public final ProtectionFlag<Boolean> physicalInteract = flagRegistry().register(ProtectPlugin.this, "physical_interact", true, false);
        public final ProtectionFlag<Boolean> place = flagRegistry().register(ProtectPlugin.this, "place", true, false);
        public final ProtectionFlag<Boolean> playerAttackEntity = flagRegistry().register(ProtectPlugin.this, "player_attack_entity", true, false);
        public final ProtectionFlag<Boolean> playerAttackPlayer = flagRegistry().register(ProtectPlugin.this, "player_attack_player", true, false);
        public final ProtectionFlag<Boolean> playerItemDrop = flagRegistry().register(ProtectPlugin.this, "player_item_drop", true, false);
        public final ProtectionFlag<Boolean> shulkerWashing = flagRegistry().register(ProtectPlugin.this, "shulker_washing", true, false);
    }
}
