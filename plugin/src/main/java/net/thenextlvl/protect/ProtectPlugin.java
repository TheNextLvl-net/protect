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
import dev.faststats.ErrorTracker;
import dev.faststats.bukkit.BukkitContext;
import io.papermc.paper.ServerBuildInfo;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.i18n.ComponentBundle;
import net.thenextlvl.nbt.serialization.NBT;
import net.thenextlvl.protect.adapters.KeyAdapter;
import net.thenextlvl.protect.adapters.LocationAdapter;
import net.thenextlvl.protect.adapters.MembersAdapter;
import net.thenextlvl.protect.adapters.WeatherTypeAdapter;
import net.thenextlvl.protect.adapters.WorldAdapter;
import net.thenextlvl.protect.adapters.area.CuboidAreaAdapter;
import net.thenextlvl.protect.adapters.area.CylinderAreaAdapter;
import net.thenextlvl.protect.adapters.area.EllipsoidAreaAdapter;
import net.thenextlvl.protect.adapters.area.GlobalAreaAdapter;
import net.thenextlvl.protect.adapters.area.GroupedAreaAdapter;
import net.thenextlvl.protect.adapters.region.CuboidRegionAdapter;
import net.thenextlvl.protect.adapters.region.CylinderRegionAdapter;
import net.thenextlvl.protect.adapters.region.EllipsoidRegionAdapter;
import net.thenextlvl.protect.adapters.region.GroupedRegionAdapter;
import net.thenextlvl.protect.adapters.vector.BlockVectorAdapter;
import net.thenextlvl.protect.adapters.vector.Vector2Adapter;
import net.thenextlvl.protect.adapters.vector.Vector3Adapter;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.CraftAreaProvider;
import net.thenextlvl.protect.area.CraftAreaService;
import net.thenextlvl.protect.area.CraftCuboidArea;
import net.thenextlvl.protect.area.CraftCylinderArea;
import net.thenextlvl.protect.area.CraftEllipsoidArea;
import net.thenextlvl.protect.area.CraftGlobalArea;
import net.thenextlvl.protect.area.CraftGroupedArea;
import net.thenextlvl.protect.commands.AreaCommand;
import net.thenextlvl.protect.controllers.CollisionController;
import net.thenextlvl.protect.flag.BooleanFlagType;
import net.thenextlvl.protect.flag.CraftFlagRegistry;
import net.thenextlvl.protect.flag.EnumFlagType;
import net.thenextlvl.protect.flag.FlagInstance;
import net.thenextlvl.protect.flag.FlagType;
import net.thenextlvl.protect.flag.LongFlagType;
import net.thenextlvl.protect.flag.ProtectionFlagInstance;
import net.thenextlvl.protect.flag.StringFlagType;
import net.thenextlvl.protect.lifecycle.CraftLifecycleManager;
import net.thenextlvl.protect.lifecycle.LifecycleEvent;
import net.thenextlvl.protect.lifecycle.event.CraftFlagRegistrationEvent;
import net.thenextlvl.protect.listeners.AreaListener;
import net.thenextlvl.protect.listeners.ConnectionListener;
import net.thenextlvl.protect.listeners.EntityListener;
import net.thenextlvl.protect.listeners.MovementListener;
import net.thenextlvl.protect.listeners.NexoListener;
import net.thenextlvl.protect.listeners.PhysicsListener;
import net.thenextlvl.protect.listeners.WorldListener;
import net.thenextlvl.protect.masks.ProtectMaskManager;
import net.thenextlvl.protect.region.GroupedRegion;
import net.thenextlvl.protect.service.CraftProtectionService;
import net.thenextlvl.protect.utils.MessageMigrator;
import net.thenextlvl.protect.version.PluginVersionChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@NullMarked
public final class ProtectPlugin extends JavaPlugin {
    public static final String ISSUES = "https://github.com/TheNextLvl-net/protect/issues/new?template=bug_report.yml";
    public static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware();

    private final Metrics metrics = new Metrics(this, 21712);
    private final BukkitContext context = new BukkitContext.Factory(this, "702e90699ba58f71536c54256557454e")
            .metrics(dev.faststats.Metrics.Factory::create)
            .errorTrackerService(ERROR_TRACKER)
            .create();

    private final Path schematicFolder = getDataPath().resolve("schematics");
    private final PluginVersionChecker versionChecker = new PluginVersionChecker(this);

    private final CollisionController collisionController = new CollisionController();

    private final CraftProtectionService protectionService = new CraftProtectionService(this);
    private final CraftFlagRegistry flagRegistry = new CraftFlagRegistry();
    private final CraftLifecycleManager lifecycleManager = new CraftLifecycleManager(this);
    private final CraftAreaProvider areaProvider = new CraftAreaProvider(this);
    private final CraftAreaService areaService = new CraftAreaService(this);

    public final Flags flags = new Flags();

    private final Key key = Key.key("protect", "translations");
    private final Path translations = getDataPath().resolve("translations");
    private final ComponentBundle bundle = ComponentBundle.builder(key, translations)
            .migrator(new MessageMigrator())
            .placeholder("failed_prefix", "prefix.failed")
            .placeholder("prefix", "prefix")
            .resource("protect.properties", Locale.US)
            .resource("protect_german.properties", Locale.GERMANY)
            .build();

    @Override
    public void onLoad() {
        WEManager.weManager().addManager(new ProtectMaskManager(this));
        versionChecker.checkVersion();
        registerAdapters();
        registerFlags();
        registerWrappers();
    }

    @Override
    public void onEnable() {
        context.ready();
        lifecycleManager.fire(LifecycleEvent.FLAG_REGISTRATION, owner -> new CraftFlagRegistrationEvent(owner, flagRegistry));
        flagRegistry.freeze();
        getServer().getWorlds().forEach(areaProvider()::load);
        registerEvents();
        registerCommands();
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(collisionController::remove);
        getServer().getWorlds().forEach(world -> {
            areaProvider().save(world);
            areaProvider().unload(world);
        });
        context.shutdown();
        metrics.shutdown();
    }

    private void registerFlags() {
        lifecycleManager.registerHandler(this, LifecycleEvent.FLAG_REGISTRATION, event -> flags.all.forEach(event::register));
        final var nexo = getServer().getPluginManager().getPlugin("Nexo");
        if (nexo != null) lifecycleManager.registerHandler(nexo, LifecycleEvent.FLAG_REGISTRATION, event -> {
            event.register(flags.nexoFurnitureBreak);
            event.register(flags.nexoFurniturePlace);
            event.register(flags.nexoFurnitureInteract);
        });
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
        getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new MovementListener(this), this);
        getServer().getPluginManager().registerEvents(new PhysicsListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
        final var nexo = getServer().getPluginManager().getPlugin("Nexo");
        if (nexo != null) registerNexoEvents(nexo);
    }

    private void registerNexoEvents(final Plugin nexo) {
        getServer().getPluginManager().registerEvents(new NexoListener(this), this);
    }

    private void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> {
            event.registrar().register(AreaCommand.create(this));
        }));
    }


    private static final Set<String> oldVersions = Set.of(
            "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11"
    );

    public static Path getDataFolder(final World world) {
        final var version = ServerBuildInfo.buildInfo().minecraftVersionId();
        final var path = world.getWorldFolder().toPath();
        if (oldVersions.contains(version)) return path.resolve("areas");
        return path.resolve("data").resolve("areas");
    }

    private final Cache<Audience, String> cooldown = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .build();

    public void failed(@Nullable final Audience audience, final Area area, final String message) {
        if (audience == null || !area.getFlagValue(flags.notifyFailedInteractions)) return;
        if (message.equals(cooldown.getIfPresent(audience))) return;
        bundle().sendMessage(audience, message, Placeholder.parsed("area", area.getName()));
        cooldown.put(audience, message);
    }

    public void failed(@Nullable final Audience audience, final Cancellable cancellable, final Area area, final String message) {
        if (cancellable.isCancelled()) failed(audience, area, message);
    }

    public Path schematicFolder() {
        return schematicFolder;
    }

    public CollisionController collisionController() {
        return collisionController;
    }

    public CraftProtectionService protectionService() {
        return protectionService;
    }

    public CraftFlagRegistry flagRegistry() {
        return flagRegistry;
    }

    public CraftLifecycleManager lifecycleManager() {
        return lifecycleManager;
    }

    public CraftAreaProvider areaProvider() {
        return areaProvider;
    }

    public CraftAreaService areaService() {
        return areaService;
    }

    public ComponentBundle bundle() {
        return bundle;
    }

    public final NBT nbt = NBT.builder()
            .registerTypeAdapter(new TypeToken<Set<UUID>>() {
            }.getType(), new MembersAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Key.class, new KeyAdapter())
            .registerTypeHierarchyAdapter(WeatherType.class, new WeatherTypeAdapter())
            .registerTypeHierarchyAdapter(World.class, new WorldAdapter(getServer()))
            .registerTypeHierarchyAdapter(CuboidRegion.class, new CuboidRegionAdapter())
            .registerTypeHierarchyAdapter(CylinderRegion.class, new CylinderRegionAdapter())
            .registerTypeHierarchyAdapter(EllipsoidRegion.class, new EllipsoidRegionAdapter())
            .registerTypeHierarchyAdapter(GroupedRegion.class, new GroupedRegionAdapter())
            .registerTypeHierarchyAdapter(BlockVector3.class, new BlockVectorAdapter())
            .registerTypeHierarchyAdapter(Vector2.class, new Vector2Adapter())
            .registerTypeHierarchyAdapter(Vector3.class, new Vector3Adapter())
            .build();

    public static final class Flags {
        private final List<FlagInstance<?>> all = new ArrayList<>();

        public final FlagInstance<Long> time = register("time", LongFlagType.longType(0), 0L);
        public final FlagInstance<String> farewell = register("farewell", StringFlagType.stringType(), "");
        public final FlagInstance<String> farewellActionbar = register("farewell_actionbar", StringFlagType.stringType(), "");
        public final FlagInstance<String> farewellTitle = register("farewell_title", StringFlagType.stringType(), "");
        public final FlagInstance<String> greetings = register("greetings", StringFlagType.stringType(), "");
        public final FlagInstance<String> greetingsActionbar = register("greetings_actionbar", StringFlagType.stringType(), "");
        public final FlagInstance<String> greetingsTitle = register("greetings_title", StringFlagType.stringType(), "");
        public final FlagInstance<WeatherType> weather = register("weather", EnumFlagType.enumType(WeatherType.class), WeatherType.CLEAR);

        public final FlagInstance<Boolean> areaEnter = register("enter", true);
        public final FlagInstance<Boolean> areaLeave = register("leave", true);
        public final FlagInstance<Boolean> damage = register("damage", true);
        public final FlagInstance<Boolean> entityItemDrop = register("entity_item_drop", true);
        public final FlagInstance<Boolean> entityItemPickup = register("entity_item_pickup", true);
        public final FlagInstance<Boolean> gameEvents = register("game_events", true);
        public final FlagInstance<Boolean> gravity = register("gravity", true);
        public final FlagInstance<Boolean> hunger = register("hunger", true);
        public final FlagInstance<Boolean> liquidFlow = register("liquid_flow", true);
        public final FlagInstance<Boolean> naturalEntitySpawn = register("natural_entity_spawn", true);
        public final FlagInstance<Boolean> notifyFailedInteractions = register("notify_failed_interactions", false);
        public final FlagInstance<Boolean> physics = register("physics", true);
        public final FlagInstance<Boolean> redstone = register("redstone", true);
        public final FlagInstance<Boolean> shoot = register("shoot", true);

        public final ProtectionFlagInstance<Boolean> armorStandManipulate = protection("armor_stand_manipulate");
        public final ProtectionFlagInstance<Boolean> blockAbsorb = protection("block_absorb");
        public final ProtectionFlagInstance<Boolean> blockBurning = protection("block_burning");
        public final ProtectionFlagInstance<Boolean> blockDrying = protection("block_drying");
        public final ProtectionFlagInstance<Boolean> blockFading = protection("block_fading");
        public final ProtectionFlagInstance<Boolean> blockFertilize = protection("block_fertilize");
        public final ProtectionFlagInstance<Boolean> blockForming = protection("block_forming");
        public final ProtectionFlagInstance<Boolean> blockGrowth = protection("block_growth");
        public final ProtectionFlagInstance<Boolean> blockIgniting = protection("block_igniting");
        public final ProtectionFlagInstance<Boolean> blockMoisturising = protection("block_moisturising");
        public final ProtectionFlagInstance<Boolean> blockSpread = protection("block_spread");
        public final ProtectionFlagInstance<Boolean> cauldronEvaporation = protection("cauldron_evaporation");
        public final ProtectionFlagInstance<Boolean> cauldronExtinguishEntity = protection("cauldron_extinguish_entity");
        public final ProtectionFlagInstance<Boolean> collisions = protection("collisions");
        public final ProtectionFlagInstance<Boolean> cropTrample = protection("crop_trample");
        public final ProtectionFlagInstance<Boolean> destroy = protection("destroy");
        public final ProtectionFlagInstance<Boolean> entityAttackEntity = protection("entity_attack_entity");
        public final ProtectionFlagInstance<Boolean> entityAttackPlayer = protection("entity_attack_player");
        public final ProtectionFlagInstance<Boolean> entityBreakDoor = protection("entity_break_door");
        public final ProtectionFlagInstance<Boolean> entityInteract = protection("entity_interact");
        public final ProtectionFlagInstance<Boolean> entityShear = protection("entity_shear");
        public final ProtectionFlagInstance<Boolean> explosions = protection("explosions");
        public final ProtectionFlagInstance<Boolean> interact = protection("interact");
        public final ProtectionFlagInstance<Boolean> knockback = protection("knockback");
        public final ProtectionFlagInstance<Boolean> leavesDecay = protection("leaves_decay");
        public final ProtectionFlagInstance<Boolean> naturalCauldronFill = protection("natural_cauldron_fill");
        public final ProtectionFlagInstance<Boolean> physicalInteract = protection("physical_interact");
        public final ProtectionFlagInstance<Boolean> place = protection("place");
        public final ProtectionFlagInstance<Boolean> playerAttackEntity = protection("player_attack_entity");
        public final ProtectionFlagInstance<Boolean> playerAttackPlayer = protection("player_attack_player");
        public final ProtectionFlagInstance<Boolean> playerItemDrop = protection("player_item_drop");
        public final ProtectionFlagInstance<Boolean> sheepEatGrass = protection("sheep_eat_grass");

        public final ProtectionFlagInstance<Boolean> nexoFurnitureBreak = nexo("furniture_break");
        public final ProtectionFlagInstance<Boolean> nexoFurniturePlace = nexo("furniture_place");
        public final ProtectionFlagInstance<Boolean> nexoFurnitureInteract = nexo("furniture_interact");

        private <T> FlagInstance<T> register(@KeyPattern.Value final String name, final FlagType<T> type, final T defaultValue) {
            final var flag = FlagInstance.create(Key.key("protect", name), type, defaultValue);
            all.add(flag);
            return flag;
        }

        private FlagInstance<Boolean> register(@KeyPattern.Value final String name, final boolean defaultValue) {
            return register(name, BooleanFlagType.booleanType(), defaultValue);
        }

        private ProtectionFlagInstance<Boolean> protection(@KeyPattern.Value final String name) {
            final var flag = ProtectionFlagInstance.create(Key.key("protect", name), BooleanFlagType.booleanType(), true, false);
            all.add(flag);
            return flag;
        }

        private static ProtectionFlagInstance<Boolean> nexo(@KeyPattern.Value final String name) {
            return ProtectionFlagInstance.create(Key.key("nexo", name), BooleanFlagType.booleanType(), true, false);
        }
    }
}
