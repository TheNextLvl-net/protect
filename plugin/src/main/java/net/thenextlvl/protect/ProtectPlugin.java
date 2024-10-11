package net.thenextlvl.protect;

import core.i18n.file.ComponentBundle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.protect.adapter.CuboidAreaAdapter;
import net.thenextlvl.protect.adapter.GlobalAreaAdapter;
import net.thenextlvl.protect.area.*;
import net.thenextlvl.protect.command.AreaCommand;
import net.thenextlvl.protect.flag.CraftFlagRegistry;
import net.thenextlvl.protect.flag.Flag;
import net.thenextlvl.protect.flag.FlagRegistry;
import net.thenextlvl.protect.listener.AreaListener;
import net.thenextlvl.protect.listener.EntityListener;
import net.thenextlvl.protect.listener.MovementListener;
import net.thenextlvl.protect.listener.WorldListener;
import net.thenextlvl.protect.service.CraftProtectionService;
import net.thenextlvl.protect.service.ProtectionService;
import net.thenextlvl.protect.version.PluginVersionChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Locale;

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
                    Placeholder.component("prefix", bundle.component(Locale.US, "prefix"))
            )).build());

    @Override
    public void onLoad() {
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

    public class Flags {
        public final Flag<@NotNull Boolean> areaEnter = flagRegistry().register(ProtectPlugin.this, Boolean.class, "enter", true);
        public final Flag<@NotNull Boolean> areaLeave = flagRegistry().register(ProtectPlugin.this, Boolean.class, "leave", true);
        public final Flag<@NotNull Boolean> armorStandManipulate = flagRegistry().register(ProtectPlugin.this, Boolean.class, "armor_stand_manipulate", true);
        public final Flag<@NotNull Boolean> armorWashing = flagRegistry().register(ProtectPlugin.this, Boolean.class, "armor_washing", true);
        public final Flag<@NotNull Boolean> bannerWashing = flagRegistry().register(ProtectPlugin.this, Boolean.class, "banner_washing", true);
        public final Flag<@NotNull Boolean> blockAbsorb = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_absorb", true);
        public final Flag<@NotNull Boolean> blockBreak = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_break", true);
        public final Flag<@NotNull Boolean> blockBurning = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_burning", true);
        public final Flag<@NotNull Boolean> blockDrying = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_drying", true);
        public final Flag<@NotNull Boolean> blockFading = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_fading", true);
        public final Flag<@NotNull Boolean> blockGrowth = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_growth", true);
        public final Flag<@NotNull Boolean> blockIgniting = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_igniting", true);
        public final Flag<@NotNull Boolean> blockMoisturising = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_moisturising", true);
        public final Flag<@NotNull Boolean> blockPlace = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_place", true);
        public final Flag<@NotNull Boolean> blockSpread = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_spread", true);
        public final Flag<@NotNull Boolean> cauldronEvaporation = flagRegistry().register(ProtectPlugin.this, Boolean.class, "cauldron_evaporation", true);
        public final Flag<@NotNull Boolean> cauldronExtinguishEntity = flagRegistry().register(ProtectPlugin.this, Boolean.class, "cauldron_extinguish_entity", true);
        public final Flag<@NotNull Boolean> cauldronLevelChangeUnknown = flagRegistry().register(ProtectPlugin.this, Boolean.class, "cauldron_level_change_unknown", true);
        public final Flag<@NotNull Boolean> cropTrample = flagRegistry().register(ProtectPlugin.this, Boolean.class, "crop_trample", true);
        public final Flag<@NotNull Boolean> damage = flagRegistry().register(ProtectPlugin.this, Boolean.class, "damage", true);
        public final Flag<@NotNull Boolean> emptyBottle = flagRegistry().register(ProtectPlugin.this, Boolean.class, "empty_bottle", true);
        public final Flag<@NotNull Boolean> emptyBucket = flagRegistry().register(ProtectPlugin.this, Boolean.class, "empty_bucket", true);
        public final Flag<@NotNull Boolean> entityAttackEntity = flagRegistry().register(ProtectPlugin.this, Boolean.class, "entity_attack_entity", true);
        public final Flag<@NotNull Boolean> entityAttackPlayer = flagRegistry().register(ProtectPlugin.this, Boolean.class, "entity_attack_player", true);
        public final Flag<@NotNull Boolean> entityInteract = flagRegistry().register(ProtectPlugin.this, Boolean.class, "entity_interact", true);
        public final Flag<@NotNull Boolean> entityItemDrop = flagRegistry().register(ProtectPlugin.this, Boolean.class, "entity_item_drop", true);
        public final Flag<@NotNull Boolean> entityItemPickup = flagRegistry().register(ProtectPlugin.this, Boolean.class, "entity_item_pickup", true);
        public final Flag<@NotNull Boolean> entityShear = flagRegistry().register(ProtectPlugin.this, Boolean.class, "entity_shear", true);
        public final Flag<@NotNull Boolean> explosions = flagRegistry().register(ProtectPlugin.this, Boolean.class, "explosions", true);
        public final Flag<@NotNull Boolean> fillBottle = flagRegistry().register(ProtectPlugin.this, Boolean.class, "fill_bottle", true);
        public final Flag<@NotNull Boolean> fillBucket = flagRegistry().register(ProtectPlugin.this, Boolean.class, "fill_bucket", true);
        public final Flag<@NotNull Boolean> hangingBreak = flagRegistry().register(ProtectPlugin.this, Boolean.class, "hanging_break", true);
        public final Flag<@NotNull Boolean> hangingPlace = flagRegistry().register(ProtectPlugin.this, Boolean.class, "hanging_place", true);
        public final Flag<@NotNull Boolean> hunger = flagRegistry().register(ProtectPlugin.this, Boolean.class, "hunger", true);
        public final Flag<@NotNull Boolean> interact = flagRegistry().register(ProtectPlugin.this, Boolean.class, "interact", true);
        public final Flag<@NotNull Boolean> leavesDecay = flagRegistry().register(ProtectPlugin.this, Boolean.class, "leaves_decay", true);
        public final Flag<@NotNull Boolean> naturalCauldronFill = flagRegistry().register(ProtectPlugin.this, Boolean.class, "natural_cauldron_fill", true);
        public final Flag<@NotNull Boolean> naturalEntitySpawn = flagRegistry().register(ProtectPlugin.this, Boolean.class, "natural_entity_spawn", true);
        public final Flag<@NotNull Boolean> physicalInteract = flagRegistry().register(ProtectPlugin.this, Boolean.class, "physical_interact", true);
        public final Flag<@NotNull Boolean> physics = flagRegistry().register(ProtectPlugin.this, Boolean.class, "physics", true);
        public final Flag<@NotNull Boolean> playerAttackEntity = flagRegistry().register(ProtectPlugin.this, Boolean.class, "player_attack_entity", true);
        public final Flag<@NotNull Boolean> playerAttackPlayer = flagRegistry().register(ProtectPlugin.this, Boolean.class, "player_attack_player", true);
        public final Flag<@NotNull Boolean> playerItemDrop = flagRegistry().register(ProtectPlugin.this, Boolean.class, "player_item_drop", true);
        public final Flag<@NotNull Boolean> redstone = flagRegistry().register(ProtectPlugin.this, Boolean.class, "redstone", true);
        public final Flag<@NotNull Boolean> shoot = flagRegistry().register(ProtectPlugin.this, Boolean.class, "shoot", true);
        public final Flag<@NotNull Boolean> shulkerWashing = flagRegistry().register(ProtectPlugin.this, Boolean.class, "shulker_washing", true);
        public final Flag<@NotNull Boolean> worldedit = flagRegistry().register(ProtectPlugin.this, Boolean.class, "worldedit", true);
        public final Flag<@Nullable Long> time = flagRegistry().register(ProtectPlugin.this, Long.class, "time", null);
        public final Flag<@Nullable String> farewell = flagRegistry().register(ProtectPlugin.this, String.class, "farewell", null);
        public final Flag<@Nullable String> greetings = flagRegistry().register(ProtectPlugin.this, String.class, "greetings", null);
        public final Flag<@Nullable WeatherType> weather = flagRegistry().register(ProtectPlugin.this, WeatherType.class, "weather", null);
    }
}
