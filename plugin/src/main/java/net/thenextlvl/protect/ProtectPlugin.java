package net.thenextlvl.protect;

import com.fastasyncworldedit.core.util.WEManager;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import core.i18n.file.ComponentBundle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.protect.adapter.area.CuboidAreaAdapter;
import net.thenextlvl.protect.adapter.area.CylinderAreaAdapter;
import net.thenextlvl.protect.adapter.area.EllipsoidAreaAdapter;
import net.thenextlvl.protect.adapter.area.GlobalAreaAdapter;
import net.thenextlvl.protect.area.*;
import net.thenextlvl.protect.command.AreaCommand;
import net.thenextlvl.protect.flag.CraftFlagRegistry;
import net.thenextlvl.protect.flag.Flag;
import net.thenextlvl.protect.flag.FlagRegistry;
import net.thenextlvl.protect.flag.ProtectionFlag;
import net.thenextlvl.protect.listener.AreaListener;
import net.thenextlvl.protect.listener.EntityListener;
import net.thenextlvl.protect.listener.MovementListener;
import net.thenextlvl.protect.listener.WorldListener;
import net.thenextlvl.protect.mask.ProtectMaskManager;
import net.thenextlvl.protect.service.CraftProtectionService;
import net.thenextlvl.protect.service.ProtectionService;
import net.thenextlvl.protect.version.PluginVersionChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Getter
@Accessors(fluent = true)
public class ProtectPlugin extends JavaPlugin {
    private final Metrics metrics = new Metrics(this, 21712);
    private final File schematicFolder = new File(getDataFolder(), "schematics");
    private final PluginVersionChecker versionChecker = new PluginVersionChecker(this);

    private final CraftProtectionService protectionService = new CraftProtectionService(this);
    private final CraftFlagRegistry flagRegistry = new CraftFlagRegistry();
    private final CraftAreaProvider areaProvider = new CraftAreaProvider(this);
    private final CraftAreaService areaService = new CraftAreaService(this);

    public final @Getter(AccessLevel.NONE) Flags flags = new Flags();

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
        registerServices();
        registerAdapter();
    }

    @Override
    public void onEnable() {
        registerCommands();
        registerEvents();
    }

    @Override
    public void onDisable() {
        getServer().getWorlds().forEach(world -> {
            areaProvider().saveAreas(world);
            areaProvider().unloadAreas(world);
        });
        metrics().shutdown();
    }

    private void registerServices() {
        getServer().getServicesManager().register(ProtectionService.class, protectionService(), this, ServicePriority.Highest);
        getServer().getServicesManager().register(FlagRegistry.class, flagRegistry(), this, ServicePriority.Highest);
        getServer().getServicesManager().register(AreaProvider.class, areaProvider(), this, ServicePriority.Highest);
        getServer().getServicesManager().register(AreaService.class, areaService(), this, ServicePriority.Highest);
    }

    private void registerAdapter() {
        areaService().registerAdapter(CraftGlobalArea.class, new GlobalAreaAdapter(this));
        areaService().registerAdapter(CraftCuboidArea.class, new CuboidAreaAdapter(this));
        areaService().registerAdapter(CraftCylinderArea.class, new CylinderAreaAdapter(this));
        areaService().registerAdapter(CraftEllipsoidArea.class, new EllipsoidAreaAdapter(this));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new AreaListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new MovementListener(this), this);
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

    public class Flags {
        public final Flag<@Nullable Long> time = flagRegistry().register(ProtectPlugin.this, Long.class, "time", null);
        public final Flag<@Nullable String> farewell = flagRegistry().register(ProtectPlugin.this, String.class, "farewell", null);
        public final Flag<@Nullable String> greetings = flagRegistry().register(ProtectPlugin.this, String.class, "greetings", null);
        public final Flag<@Nullable WeatherType> weather = flagRegistry().register(ProtectPlugin.this, WeatherType.class, "weather", null);


        public final Flag<Boolean> areaEnter = flagRegistry().register(ProtectPlugin.this, "enter", true);
        public final Flag<Boolean> areaLeave = flagRegistry().register(ProtectPlugin.this, "leave", true);
        public final Flag<Boolean> notifyFailedInteractions = flagRegistry().register(ProtectPlugin.this, "notify_failed_interactions", false);

        public final ProtectionFlag<Boolean> armorStandManipulate = flagRegistry().register(ProtectPlugin.this, "armor_stand_manipulate", true, false);
        public final ProtectionFlag<Boolean> armorWashing = flagRegistry().register(ProtectPlugin.this, Boolean.class, "armor_washing", true, false);
        public final ProtectionFlag<Boolean> bannerWashing = flagRegistry().register(ProtectPlugin.this, Boolean.class, "banner_washing", true, false);
        public final ProtectionFlag<Boolean> blockAbsorb = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_absorb", true, false);
        public final ProtectionFlag<Boolean> blockBreak = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_break", true, false);
        public final ProtectionFlag<Boolean> blockBurning = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_burning", true, false);
        public final ProtectionFlag<Boolean> blockDrying = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_drying", true, false);
        public final ProtectionFlag<Boolean> blockFading = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_fading", true, false);
        public final ProtectionFlag<Boolean> blockFertilize = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_fertilize", true, false);
        public final ProtectionFlag<Boolean> blockGrowth = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_growth", true, false);
        public final ProtectionFlag<Boolean> blockIgniting = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_igniting", true, false);
        public final ProtectionFlag<Boolean> blockMoisturising = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_moisturising", true, false);
        public final ProtectionFlag<Boolean> blockPlace = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_place", true, false);
        public final ProtectionFlag<Boolean> blockSpread = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_spread", true, false);
        public final ProtectionFlag<Boolean> cauldronEvaporation = flagRegistry().register(ProtectPlugin.this, Boolean.class, "cauldron_evaporation", true, false);
        public final ProtectionFlag<Boolean> cauldronExtinguishEntity = flagRegistry().register(ProtectPlugin.this, Boolean.class, "cauldron_extinguish_entity", true, false);
        public final ProtectionFlag<Boolean> cauldronLevelChangeUnknown = flagRegistry().register(ProtectPlugin.this, Boolean.class, "cauldron_level_change_unknown", true, false);
        public final ProtectionFlag<Boolean> cropTrample = flagRegistry().register(ProtectPlugin.this, Boolean.class, "crop_trample", true, false);
        public final ProtectionFlag<Boolean> damage = flagRegistry().register(ProtectPlugin.this, Boolean.class, "damage", true, false);
        public final ProtectionFlag<Boolean> emptyBottle = flagRegistry().register(ProtectPlugin.this, Boolean.class, "empty_bottle", true, false);
        public final ProtectionFlag<Boolean> emptyBucket = flagRegistry().register(ProtectPlugin.this, Boolean.class, "empty_bucket", true, false);
        public final ProtectionFlag<Boolean> entityAttackEntity = flagRegistry().register(ProtectPlugin.this, Boolean.class, "entity_attack_entity", true, false);
        public final ProtectionFlag<Boolean> entityAttackPlayer = flagRegistry().register(ProtectPlugin.this, Boolean.class, "entity_attack_player", true, false);
        public final ProtectionFlag<Boolean> entityInteract = flagRegistry().register(ProtectPlugin.this, Boolean.class, "entity_interact", true, false);
        public final ProtectionFlag<Boolean> entityItemDrop = flagRegistry().register(ProtectPlugin.this, Boolean.class, "entity_item_drop", true, false);
        public final ProtectionFlag<Boolean> entityItemPickup = flagRegistry().register(ProtectPlugin.this, Boolean.class, "entity_item_pickup", true, false);
        public final ProtectionFlag<Boolean> entityShear = flagRegistry().register(ProtectPlugin.this, Boolean.class, "entity_shear", true, false);
        public final ProtectionFlag<Boolean> explosions = flagRegistry().register(ProtectPlugin.this, Boolean.class, "explosions", true, false);
        public final ProtectionFlag<Boolean> fillBottle = flagRegistry().register(ProtectPlugin.this, Boolean.class, "fill_bottle", true, false);
        public final ProtectionFlag<Boolean> fillBucket = flagRegistry().register(ProtectPlugin.this, Boolean.class, "fill_bucket", true, false);
        public final ProtectionFlag<Boolean> hangingBreak = flagRegistry().register(ProtectPlugin.this, Boolean.class, "hanging_break", true, false);
        public final ProtectionFlag<Boolean> hangingPlace = flagRegistry().register(ProtectPlugin.this, Boolean.class, "hanging_place", true, false);
        public final ProtectionFlag<Boolean> hunger = flagRegistry().register(ProtectPlugin.this, Boolean.class, "hunger", true, false);
        public final ProtectionFlag<Boolean> interact = flagRegistry().register(ProtectPlugin.this, Boolean.class, "interact", true, false);
        public final ProtectionFlag<Boolean> leavesDecay = flagRegistry().register(ProtectPlugin.this, Boolean.class, "leaves_decay", true, false);
        public final ProtectionFlag<Boolean> naturalCauldronFill = flagRegistry().register(ProtectPlugin.this, Boolean.class, "natural_cauldron_fill", true, false);
        public final ProtectionFlag<Boolean> naturalEntitySpawn = flagRegistry().register(ProtectPlugin.this, Boolean.class, "natural_entity_spawn", true, false);
        public final ProtectionFlag<Boolean> physicalInteract = flagRegistry().register(ProtectPlugin.this, Boolean.class, "physical_interact", true, false);
        public final ProtectionFlag<Boolean> physics = flagRegistry().register(ProtectPlugin.this, Boolean.class, "physics", true, false);
        public final ProtectionFlag<Boolean> playerAttackEntity = flagRegistry().register(ProtectPlugin.this, Boolean.class, "player_attack_entity", true, false);
        public final ProtectionFlag<Boolean> playerAttackPlayer = flagRegistry().register(ProtectPlugin.this, Boolean.class, "player_attack_player", true, false);
        public final ProtectionFlag<Boolean> playerItemDrop = flagRegistry().register(ProtectPlugin.this, Boolean.class, "player_item_drop", true, false);
        public final ProtectionFlag<Boolean> redstone = flagRegistry().register(ProtectPlugin.this, Boolean.class, "redstone", true, false);
        public final ProtectionFlag<Boolean> shoot = flagRegistry().register(ProtectPlugin.this, Boolean.class, "shoot", true, false);
        public final ProtectionFlag<Boolean> shulkerWashing = flagRegistry().register(ProtectPlugin.this, Boolean.class, "shulker_washing", true, false);
    }
}
