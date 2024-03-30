package net.thenextlvl.protect;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import core.file.FileIO;
import core.file.format.GsonFile;
import core.i18n.file.ComponentBundle;
import core.io.IO;
import core.paper.adapters.world.WorldAdapter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.protect.adapter.*;
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
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

@Getter
@Accessors(fluent = true)
public class ProtectPlugin extends JavaPlugin {
    private final File schematicFolder = new File(getDataFolder(), "schematics");

    private final ProtectionService protectionService = new CraftProtectionService(this);
    private final FlagRegistry flagRegistry = new CraftFlagRegistry();
    private final AreaProvider areaProvider = new CraftAreaProvider(this);
    private final AreaService areaService = new CraftAreaService(this);

    private final Map<World, FileIO<Set<Area>>> areasFiles = new HashMap<>();
    public final @Getter(AccessLevel.NONE) Flags flags = new Flags();

    private final ComponentBundle bundle = new ComponentBundle(new File(getDataFolder(), "translations"),
            audience -> audience instanceof Player player ? player.locale() : Locale.US)
            .register("protect", Locale.US)
            .register("protect_german", Locale.GERMANY);

    @Override
    public void onLoad() {
        bundle().miniMessage(MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolvers(TagResolver.standard())
                        .resolver(Placeholder.component("prefix", bundle.component(Locale.US, "prefix")))
                        .build())
                .build());
        Bukkit.getServicesManager().register(ProtectionService.class, protectionService, this, ServicePriority.Highest);
        Bukkit.getServicesManager().register(FlagRegistry.class, flagRegistry, this, ServicePriority.Highest);
        Bukkit.getServicesManager().register(AreaProvider.class, areaProvider, this, ServicePriority.Highest);
        Bukkit.getServicesManager().register(AreaService.class, areaService, this, ServicePriority.Highest);
    }

    @Override
    public void onEnable() {
        Bukkit.getAsyncScheduler().runNow(this, scheduledTask -> Bukkit.getWorlds().forEach(this::loadAreas));
        registerCommands();
        registerEvents();
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new AreaListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EntityListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MovementListener(this), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(this), this);
    }

    private void registerCommands() {
        try {
            new AreaCommand(this).register();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        areasFiles().forEach((world, file) -> file.save());
    }

    public FileIO<Set<Area>> loadAreas(World world) {
        var file = areasFiles().computeIfAbsent(world, w -> new GsonFile<>(
                IO.of(world.getWorldFolder(), "areas.json"), new HashSet<>(), new TypeToken<>() {
        }, new GsonBuilder()
                .registerTypeHierarchyAdapter(CuboidRegion.class, new CuboidRegionAdapter())
                .registerTypeHierarchyAdapter(CuboidArea.class, new CuboidAreaAdapter(this, w))
                .registerTypeHierarchyAdapter(BlockVector3.class, new BlockVector3Adapter())
                .registerTypeHierarchyAdapter(GlobalArea.class, new GlobalAreaAdapter(w))
                .registerTypeHierarchyAdapter(World.class, WorldAdapter.Key.INSTANCE)
                .registerTypeHierarchyAdapter(new TypeToken<Map<Flag<?>, Object>>() {
                }.getRawType(), new FlagsAdapter(this))
                .registerTypeHierarchyAdapter(Flag.class, new FlagAdapter(this))
                .registerTypeAdapter(Area.class, new AreaAdapter())
                .setPrettyPrinting()
                .create()
        ));
        if (file.getRoot().add(areaProvider().getArea(world))) file.save();
        return file;
    }

    public class Flags {
        public final Flag<@NotNull Boolean> areaEnter = flagRegistry().register(ProtectPlugin.this, Boolean.class, "enter", true);
        public final Flag<@NotNull Boolean> areaLeave = flagRegistry().register(ProtectPlugin.this, Boolean.class, "leave", true);
        public final Flag<@NotNull Boolean> armorStandManipulate = flagRegistry().register(ProtectPlugin.this, Boolean.class, "armor_stand_manipulate", true);
        public final Flag<@NotNull Boolean> blockBreak = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_break", true);
        public final Flag<@NotNull Boolean> blockPlace = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_place", true);
        public final Flag<@NotNull Boolean> blockSpread = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_spread", true);
        public final Flag<@NotNull Boolean> cropTrample = flagRegistry().register(ProtectPlugin.this, Boolean.class, "crop_trample", true);
        public final Flag<@NotNull Boolean> damage = flagRegistry().register(ProtectPlugin.this, Boolean.class, "damage", true);
        public final Flag<@NotNull Boolean> entityAttackEntity = flagRegistry().register(ProtectPlugin.this, Boolean.class, "entity_attack_entity", true);
        public final Flag<@NotNull Boolean> entityAttackPlayer = flagRegistry().register(ProtectPlugin.this, Boolean.class, "entity_attack_player", true);
        public final Flag<@NotNull Boolean> entityInteract = flagRegistry().register(ProtectPlugin.this, Boolean.class, "entity_interact", true);
        public final Flag<@NotNull Boolean> entityItemDrop = flagRegistry().register(ProtectPlugin.this, Boolean.class, "entity_item_drop", true);
        public final Flag<@NotNull Boolean> playerItemDrop = flagRegistry().register(ProtectPlugin.this, Boolean.class, "player_item_drop", true);
        public final Flag<@NotNull Boolean> entityItemPickup = flagRegistry().register(ProtectPlugin.this, Boolean.class, "entity_item_pickup", true);
        public final Flag<@NotNull Boolean> entityShear = flagRegistry().register(ProtectPlugin.this, Boolean.class, "entity_shear", true);
        public final Flag<@NotNull Boolean> naturalEntitySpawn = flagRegistry().register(ProtectPlugin.this, Boolean.class, "natural_entity_spawn", true);
        public final Flag<@NotNull Boolean> explosions = flagRegistry().register(ProtectPlugin.this, Boolean.class, "explosions", true);
        public final Flag<@NotNull Boolean> hangingBreak = flagRegistry().register(ProtectPlugin.this, Boolean.class, "hanging_break", true);
        public final Flag<@NotNull Boolean> hangingPlace = flagRegistry().register(ProtectPlugin.this, Boolean.class, "hanging_place", true);
        public final Flag<@NotNull Boolean> hunger = flagRegistry().register(ProtectPlugin.this, Boolean.class, "hunger", true);
        public final Flag<@NotNull Boolean> interact = flagRegistry().register(ProtectPlugin.this, Boolean.class, "interact", true);
        public final Flag<@NotNull Boolean> physics = flagRegistry().register(ProtectPlugin.this, Boolean.class, "physics", true);
        public final Flag<@NotNull Boolean> blockDrying = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_drying", true);
        public final Flag<@NotNull Boolean> blockMoisturising = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_moisturising", true);
        public final Flag<@NotNull Boolean> blockGrowth = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_growth", true);
        public final Flag<@NotNull Boolean> blockAbsorb = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_absorb", true);
        public final Flag<@NotNull Boolean> blockFading = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_fading", true);
        public final Flag<@NotNull Boolean> blockBurning = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_burning", true);
        public final Flag<@NotNull Boolean> blockIgniting = flagRegistry().register(ProtectPlugin.this, Boolean.class, "block_igniting", true);
        public final Flag<@NotNull Boolean> playerAttackEntity = flagRegistry().register(ProtectPlugin.this, Boolean.class, "player_attack_entity", true);
        public final Flag<@NotNull Boolean> playerAttackPlayer = flagRegistry().register(ProtectPlugin.this, Boolean.class, "player_attack_player", true);
        public final Flag<@NotNull Boolean> redstone = flagRegistry().register(ProtectPlugin.this, Boolean.class, "redstone", true);
        public final Flag<@NotNull Boolean> shoot = flagRegistry().register(ProtectPlugin.this, Boolean.class, "shoot", true);
        public final Flag<@Nullable String> farewell = flagRegistry().register(ProtectPlugin.this, String.class, "farewell", null);
        public final Flag<@Nullable String> greetings = flagRegistry().register(ProtectPlugin.this, String.class, "greetings", null);
        public final Flag<@Nullable WeatherType> weather = flagRegistry().register(ProtectPlugin.this, WeatherType.class, "weather", null);
    }
}
