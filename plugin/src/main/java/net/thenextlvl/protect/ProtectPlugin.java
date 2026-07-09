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
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.i18n.ComponentBundle;
import net.thenextlvl.nbt.serialization.NBT;
import net.thenextlvl.protect.adapters.FlagsAdapter;
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
import net.thenextlvl.protect.area.AreaProvider;
import net.thenextlvl.protect.area.AreaService;
import net.thenextlvl.protect.area.CraftAreaProvider;
import net.thenextlvl.protect.area.CraftAreaService;
import net.thenextlvl.protect.area.CraftCuboidArea;
import net.thenextlvl.protect.area.CraftCylinderArea;
import net.thenextlvl.protect.area.CraftEllipsoidArea;
import net.thenextlvl.protect.area.CraftGlobalArea;
import net.thenextlvl.protect.area.CraftGroupedArea;
import net.thenextlvl.protect.commands.AreaCommand;
import net.thenextlvl.protect.controllers.CollisionController;
import net.thenextlvl.protect.flag.BooleanFlag;
import net.thenextlvl.protect.flag.CraftFlagRegistry;
import net.thenextlvl.protect.flag.Flag;
import net.thenextlvl.protect.flag.FlagHolder;
import net.thenextlvl.protect.flag.FlagRegistry;
import net.thenextlvl.protect.flag.ProtectionFlag;
import net.thenextlvl.protect.flag.ValueFlag;
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
import net.thenextlvl.protect.service.ProtectionService;
import net.thenextlvl.protect.utils.MessageMigrator;
import net.thenextlvl.protect.version.PluginVersionChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
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
        registerServices();
        registerWrappers();
    }

    @Override
    public void onEnable() {
        context.ready();
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
        getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new MovementListener(this), this);
        getServer().getPluginManager().registerEvents(new PhysicsListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
        final var nexo = getServer().getPluginManager().getPlugin("Nexo");
        if (nexo != null) registerNexoEvents(nexo);
    }

    private void registerNexoEvents(final Plugin nexo) {
        getServer().getPluginManager().registerEvents(new NexoListener(this, nexo), this);
    }

    private void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event ->
                event.registrar().register(AreaCommand.create(this))));
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
        if (audience == null || !area.getFlag(flags.notifyFailedInteractions).getValue()) return;
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
            .registerTypeAdapter(new TypeToken<Set<Flag>>() {
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
            .registerTypeHierarchyAdapter(GroupedRegion.class, new GroupedRegionAdapter())
            .registerTypeHierarchyAdapter(BlockVector3.class, new BlockVectorAdapter())
            .registerTypeHierarchyAdapter(Vector2.class, new Vector2Adapter())
            .registerTypeHierarchyAdapter(Vector3.class, new Vector3Adapter())
            .build();

    public class Flags {
        public final FlagHolder<ValueFlag<Long>> time = flagRegistry().register(ProtectPlugin.this, CraftFlag.nullableLong(key("time")));
        public final FlagHolder<ValueFlag<String>> farewell = flagRegistry().register(ProtectPlugin.this, CraftFlag.string(key("farewell"), null));
        public final FlagHolder<ValueFlag<String>> farewellActionbar = flagRegistry().register(ProtectPlugin.this, CraftFlag.string(key("farewell_actionbar"), null));
        public final FlagHolder<ValueFlag<String>> farewellTitle = flagRegistry().register(ProtectPlugin.this, CraftFlag.string(key("farewell_title"), null));
        public final FlagHolder<ValueFlag<String>> greetings = flagRegistry().register(ProtectPlugin.this, CraftFlag.string(key("greetings"), null));
        public final FlagHolder<ValueFlag<String>> greetingsActionbar = flagRegistry().register(ProtectPlugin.this, CraftFlag.string(key("greetings_actionbar"), null));
        public final FlagHolder<ValueFlag<String>> greetingsTitle = flagRegistry().register(ProtectPlugin.this, CraftFlag.string(key("greetings_title"), null));
        public final FlagHolder<ValueFlag<WeatherType>> weather = flagRegistry().register(ProtectPlugin.this, CraftFlag.enumeration(key("weather"), WeatherType.class, null));

        public final FlagHolder<BooleanFlag> areaEnter = flagRegistry().register(ProtectPlugin.this, BooleanFlag.create(key("enter"), true));
        public final FlagHolder<BooleanFlag> areaLeave = flagRegistry().register(ProtectPlugin.this, BooleanFlag.create(key("leave"), true));
        public final FlagHolder<BooleanFlag> damage = flagRegistry().register(ProtectPlugin.this, BooleanFlag.create(key("damage"), true));
        public final FlagHolder<BooleanFlag> entityItemDrop = flagRegistry().register(ProtectPlugin.this, BooleanFlag.create(key("entity_item_drop"), true));
        public final FlagHolder<BooleanFlag> entityItemPickup = flagRegistry().register(ProtectPlugin.this, BooleanFlag.create(key("entity_item_pickup"), true));
        public final FlagHolder<BooleanFlag> gameEvents = flagRegistry().register(ProtectPlugin.this, BooleanFlag.create(key("game_events"), true));
        public final FlagHolder<BooleanFlag> gravity = flagRegistry().register(ProtectPlugin.this, BooleanFlag.create(key("gravity"), true));
        public final FlagHolder<BooleanFlag> hunger = flagRegistry().register(ProtectPlugin.this, BooleanFlag.create(key("hunger"), true));
        public final FlagHolder<BooleanFlag> liquidFlow = flagRegistry().register(ProtectPlugin.this, BooleanFlag.create(key("liquid_flow"), true));
        public final FlagHolder<BooleanFlag> naturalEntitySpawn = flagRegistry().register(ProtectPlugin.this, BooleanFlag.create(key("natural_entity_spawn"), true));
        public final FlagHolder<BooleanFlag> notifyFailedInteractions = flagRegistry().register(ProtectPlugin.this, BooleanFlag.create(key("notify_failed_interactions"), false));
        public final FlagHolder<BooleanFlag> physics = flagRegistry().register(ProtectPlugin.this, BooleanFlag.create(key("physics"), true));
        public final FlagHolder<BooleanFlag> redstone = flagRegistry().register(ProtectPlugin.this, BooleanFlag.create(key("redstone"), true));
        public final FlagHolder<BooleanFlag> shoot = flagRegistry().register(ProtectPlugin.this, BooleanFlag.create(key("shoot"), true));

        public final FlagHolder<ProtectionFlag> armorStandManipulate = protection("armor_stand_manipulate");
        public final FlagHolder<ProtectionFlag> blockAbsorb = protection("block_absorb");
        public final FlagHolder<ProtectionFlag> blockBurning = protection("block_burning");
        public final FlagHolder<ProtectionFlag> blockDrying = protection("block_drying");
        public final FlagHolder<ProtectionFlag> blockFading = protection("block_fading");
        public final FlagHolder<ProtectionFlag> blockFertilize = protection("block_fertilize");
        public final FlagHolder<ProtectionFlag> blockForming = protection("block_forming");
        public final FlagHolder<ProtectionFlag> blockGrowth = protection("block_growth");
        public final FlagHolder<ProtectionFlag> blockIgniting = protection("block_igniting");
        public final FlagHolder<ProtectionFlag> blockMoisturising = protection("block_moisturising");
        public final FlagHolder<ProtectionFlag> blockSpread = protection("block_spread");
        public final FlagHolder<ProtectionFlag> cauldronEvaporation = protection("cauldron_evaporation");
        public final FlagHolder<ProtectionFlag> cauldronExtinguishEntity = protection("cauldron_extinguish_entity");
        public final FlagHolder<ProtectionFlag> collisions = protection("collisions");
        public final FlagHolder<ProtectionFlag> cropTrample = protection("crop_trample");
        public final FlagHolder<ProtectionFlag> destroy = protection("destroy");
        public final FlagHolder<ProtectionFlag> entityAttackEntity = protection("entity_attack_entity");
        public final FlagHolder<ProtectionFlag> entityAttackPlayer = protection("entity_attack_player");
        public final FlagHolder<ProtectionFlag> entityBreakDoor = protection("entity_break_door");
        public final FlagHolder<ProtectionFlag> entityInteract = protection("entity_interact");
        public final FlagHolder<ProtectionFlag> entityShear = protection("entity_shear");
        public final FlagHolder<ProtectionFlag> explosions = protection("explosions");
        public final FlagHolder<ProtectionFlag> interact = protection("interact");
        public final FlagHolder<ProtectionFlag> knockback = protection("knockback");
        public final FlagHolder<ProtectionFlag> leavesDecay = protection("leaves_decay");
        public final FlagHolder<ProtectionFlag> naturalCauldronFill = protection("natural_cauldron_fill");
        public final FlagHolder<ProtectionFlag> physicalInteract = protection("physical_interact");
        public final FlagHolder<ProtectionFlag> place = protection("place");
        public final FlagHolder<ProtectionFlag> playerAttackEntity = protection("player_attack_entity");
        public final FlagHolder<ProtectionFlag> playerAttackPlayer = protection("player_attack_player");
        public final FlagHolder<ProtectionFlag> playerItemDrop = protection("player_item_drop");
        public final FlagHolder<ProtectionFlag> sheepEatGrass = protection("sheep_eat_grass");

        @SuppressWarnings("PatternValidation")
        private Key key(final String name) {
            return Key.key(getName().replace("-", "_").toLowerCase(Locale.ROOT), name);
        }

        private FlagHolder<ProtectionFlag> protection(final String name) {
            return flagRegistry().register(ProtectPlugin.this, ProtectionFlag.create(key(name), true, false));
        }
    }
}
