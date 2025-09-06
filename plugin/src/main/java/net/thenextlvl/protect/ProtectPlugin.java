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
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.nbt.serialization.NBT;
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
import net.thenextlvl.protect.command.AreaCommand;
import net.thenextlvl.protect.controller.CollisionController;
import net.thenextlvl.protect.flag.CraftFlagRegistry;
import net.thenextlvl.protect.flag.Flag;
import net.thenextlvl.protect.flag.FlagRegistry;
import net.thenextlvl.protect.flag.standard.BooleanFlag;
import net.thenextlvl.protect.flag.standard.EnumFlag;
import net.thenextlvl.protect.flag.standard.LongFlag;
import net.thenextlvl.protect.flag.standard.StringFlag;
import net.thenextlvl.protect.listener.AreaListener;
import net.thenextlvl.protect.listener.ConnectionListener;
import net.thenextlvl.protect.listener.EntityListener;
import net.thenextlvl.protect.listener.MovementListener;
import net.thenextlvl.protect.listener.NexoListener;
import net.thenextlvl.protect.listener.PhysicsListener;
import net.thenextlvl.protect.listener.WorldListener;
import net.thenextlvl.protect.mask.ProtectMaskManager;
import net.thenextlvl.protect.region.GroupedRegion;
import net.thenextlvl.protect.service.CraftProtectionService;
import net.thenextlvl.protect.service.ProtectionService;
import net.thenextlvl.protect.util.MessageMigrator;
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

import java.io.File;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@NullMarked
public class ProtectPlugin extends JavaPlugin {
    private final Metrics metrics = new Metrics(this, 21712);
    private final File schematicFolder = new File(getDataFolder(), "schematics");
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
        var nexo = getServer().getPluginManager().getPlugin("Nexo");
        if (nexo != null) registerNexoEvents(nexo);
    }

    private void registerNexoEvents(Plugin nexo) {
        getServer().getPluginManager().registerEvents(new NexoListener(this, nexo), this);
    }

    private void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event ->
                event.registrar().register(AreaCommand.create(this))));
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

    public File schematicFolder() {
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
            .registerTypeHierarchyAdapter(GroupedRegion.class, new GroupedRegionAdapter())
            .registerTypeHierarchyAdapter(BlockVector3.class, new BlockVectorAdapter())
            .registerTypeHierarchyAdapter(Vector2.class, new Vector2Adapter())
            .registerTypeHierarchyAdapter(Vector3.class, new Vector3Adapter())
            .build();

    public class Flags {
        public final LongFlag time = register(new LongFlag(key("time")));
        public final StringFlag farewell = register(new StringFlag(key("farewell")));
        public final StringFlag farewellActionbar = register(new StringFlag(key("farewell_actionbar")));
        public final StringFlag farewellTitle = register(new StringFlag(key("farewell_title")));
        public final StringFlag greetings = register(new StringFlag(key("greetings")));
        public final StringFlag greetingsActionbar = register(new StringFlag(key("greetings_actionbar")));
        public final StringFlag greetingsTitle = register(new StringFlag(key("greetings_title")));
        public final EnumFlag<@Nullable WeatherType> weather = register(new EnumFlag<>(key("weather"), WeatherType.class));

        public final BooleanFlag areaEnter = register(new BooleanFlag(key("enter"), true));
        public final BooleanFlag areaLeave = register(new BooleanFlag(key("leave"), true));
        public final BooleanFlag damage = register(new BooleanFlag(key("damage"), true));
        public final BooleanFlag entityItemDrop = register(new BooleanFlag(key("entity_item_drop"), true));
        public final BooleanFlag entityItemPickup = register(new BooleanFlag(key("entity_item_pickup"), true));
        public final BooleanFlag gameEvents = register(new BooleanFlag(key("game_events"), true));
        public final BooleanFlag gravity = register(new BooleanFlag(key("gravity"), true));
        public final BooleanFlag hunger = register(new BooleanFlag(key("hunger"), true));
        public final BooleanFlag liquidFlow = register(new BooleanFlag(key("liquid_flow"), true));
        public final BooleanFlag naturalEntitySpawn = register(new BooleanFlag(key("natural_entity_spawn"), true));
        public final BooleanFlag notifyFailedInteractions = register(new BooleanFlag(key("notify_failed_interactions"), false));
        public final BooleanFlag physics = register(new BooleanFlag(key("physics"), true));
        public final BooleanFlag redstone = register(new BooleanFlag(key("redstone"), true));
        public final BooleanFlag shoot = register(new BooleanFlag(key("shoot"), true));

        public final BooleanFlag armorStandManipulate = register(new BooleanFlag(key("armor_stand_manipulate"), true, false));
        public final BooleanFlag blockAbsorb = register(new BooleanFlag(key("block_absorb"), true, false));
        public final BooleanFlag blockBurning = register(new BooleanFlag(key("block_burning"), true, false));
        public final BooleanFlag blockDrying = register(new BooleanFlag(key("block_drying"), true, false));
        public final BooleanFlag blockFading = register(new BooleanFlag(key("block_fading"), true, false));
        public final BooleanFlag blockFertilize = register(new BooleanFlag(key("block_fertilize"), true, false));
        public final BooleanFlag blockForming = register(new BooleanFlag(key("block_forming"), true, false));
        public final BooleanFlag blockGrowth = register(new BooleanFlag(key("block_growth"), true, false));
        public final BooleanFlag blockIgniting = register(new BooleanFlag(key("block_igniting"), true, false));
        public final BooleanFlag blockMoisturising = register(new BooleanFlag(key("block_moisturising"), true, false));
        public final BooleanFlag blockSpread = register(new BooleanFlag(key("block_spread"), true, false));
        public final BooleanFlag cauldronEvaporation = register(new BooleanFlag(key("cauldron_evaporation"), true, false));
        public final BooleanFlag cauldronExtinguishEntity = register(new BooleanFlag(key("cauldron_extinguish_entity"), true, false));
        public final BooleanFlag collisions = register(new BooleanFlag(key("collisions"), true, false));
        public final BooleanFlag cropTrample = register(new BooleanFlag(key("crop_trample"), true, false));
        public final BooleanFlag destroy = register(new BooleanFlag(key("destroy"), true, false));
        public final BooleanFlag entityAttackEntity = register(new BooleanFlag(key("entity_attack_entity"), true, false));
        public final BooleanFlag entityAttackPlayer = register(new BooleanFlag(key("entity_attack_player"), true, false));
        public final BooleanFlag entityInteract = register(new BooleanFlag(key("entity_interact"), true, false));
        public final BooleanFlag entityShear = register(new BooleanFlag(key("entity_shear"), true, false));
        public final BooleanFlag explosions = register(new BooleanFlag(key("explosions"), true, false));
        public final BooleanFlag interact = register(new BooleanFlag(key("interact"), true, false));
        public final BooleanFlag leavesDecay = register(new BooleanFlag(key("leaves_decay"), true, false));
        public final BooleanFlag naturalCauldronFill = register(new BooleanFlag(key("natural_cauldron_fill"), true, false));
        public final BooleanFlag physicalInteract = register(new BooleanFlag(key("physical_interact"), true, false));
        public final BooleanFlag place = register(new BooleanFlag(key("place"), true, false));
        public final BooleanFlag playerAttackEntity = register(new BooleanFlag(key("player_attack_entity"), true, false));
        public final BooleanFlag playerAttackPlayer = register(new BooleanFlag(key("player_attack_player"), true, false));
        public final BooleanFlag playerItemDrop = register(new BooleanFlag(key("player_item_drop"), true, false));

        private <T extends Flag<?>> T register(T flag) {
            flagRegistry.register(ProtectPlugin.this, flag);
            return flag;
        }

        private Key key(@KeyPattern.Value String name) {
            return Key.key("protect", name);
        }
    }
}
