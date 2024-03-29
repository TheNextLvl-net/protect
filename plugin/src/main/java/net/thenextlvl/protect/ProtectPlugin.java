package net.thenextlvl.protect;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
                IO.of(world.getWorldFolder(), ".areas"), new HashSet<>(), new TypeToken<>() {
        }, new GsonBuilder()
                .registerTypeHierarchyAdapter(World.class, WorldAdapter.Key.INSTANCE)
                .registerTypeHierarchyAdapter(Flag.class, new FlagAdapter())
                .registerTypeHierarchyAdapter(CuboidRegion.class, new CuboidRegionAdapter(w))
                .registerTypeHierarchyAdapter(CuboidArea.class, new CuboidAreaAdapter(this))
                .registerTypeHierarchyAdapter(GlobalArea.class, new GlobalAreaAdapter(w))
                .registerTypeAdapter(Area.class, new AreaAdapter())
                .setPrettyPrinting()
                .create()
        ));
        if (file.getRoot().add(areaProvider().getArea(world))) file.save();
        return file;
    }

    public class Flags {
        public final Flag<@NotNull Boolean> areaEnter = flagRegistry().register(ProtectPlugin.this, "enter", true);
        public final Flag<@NotNull Boolean> areaLeave = flagRegistry().register(ProtectPlugin.this, "leave", true);
        public final Flag<@NotNull Boolean> armorStandManipulate = flagRegistry().register(ProtectPlugin.this, "armorStandManipulate", true);
        public final Flag<@NotNull Boolean> blockBreak = flagRegistry().register(ProtectPlugin.this, "blockBreak", true);
        public final Flag<@NotNull Boolean> blockPlace = flagRegistry().register(ProtectPlugin.this, "blockPlace", true);
        public final Flag<@NotNull Boolean> blockSpread = flagRegistry().register(ProtectPlugin.this, "blockSpread", true);
        public final Flag<@NotNull Boolean> cropTrample = flagRegistry().register(ProtectPlugin.this, "cropTrample", true);
        public final Flag<@NotNull Boolean> damage = flagRegistry().register(ProtectPlugin.this, "damage", true);
        public final Flag<@NotNull Boolean> entityAttackEntity = flagRegistry().register(ProtectPlugin.this, "entityAttackEntity", true);
        public final Flag<@NotNull Boolean> entityAttackPlayer = flagRegistry().register(ProtectPlugin.this, "entityAttackPlayer", true);
        public final Flag<@NotNull Boolean> entityInteract = flagRegistry().register(ProtectPlugin.this, "entityInteract", true);
        public final Flag<@NotNull Boolean> entityItemDrop = flagRegistry().register(ProtectPlugin.this, "entityItemDrop", true);
        public final Flag<@NotNull Boolean> entityItemPickup = flagRegistry().register(ProtectPlugin.this, "entityItemPickup", true);
        public final Flag<@NotNull Boolean> entityShear = flagRegistry().register(ProtectPlugin.this, "entityShear", true);
        public final Flag<@NotNull Boolean> entitySpawn = flagRegistry().register(ProtectPlugin.this, "entitySpawn", true);
        public final Flag<@NotNull Boolean> explosions = flagRegistry().register(ProtectPlugin.this, "explosions", true);
        public final Flag<@NotNull Boolean> hangingBreak = flagRegistry().register(ProtectPlugin.this, "hangingBreak", true);
        public final Flag<@NotNull Boolean> hangingPlace = flagRegistry().register(ProtectPlugin.this, "hangingPlace", true);
        public final Flag<@NotNull Boolean> hunger = flagRegistry().register(ProtectPlugin.this, "hunger", true);
        public final Flag<@NotNull Boolean> interact = flagRegistry().register(ProtectPlugin.this, "interact", true);
        public final Flag<@NotNull Boolean> physics = flagRegistry().register(ProtectPlugin.this, "physics", true);
        public final Flag<@NotNull Boolean> playerAttackEntity = flagRegistry().register(ProtectPlugin.this, "playerAttackEntity", true);
        public final Flag<@NotNull Boolean> playerAttackPlayer = flagRegistry().register(ProtectPlugin.this, "playerAttackPlayer", true);
        public final Flag<@NotNull Boolean> redstone = flagRegistry().register(ProtectPlugin.this, "redstone", true);
        public final Flag<@NotNull Boolean> shoot = flagRegistry().register(ProtectPlugin.this, "shoot", true);
        public final Flag<@Nullable String> farewell = flagRegistry().register(ProtectPlugin.this, "farewell", null);
        public final Flag<@Nullable String> greetings = flagRegistry().register(ProtectPlugin.this, "greetings", null);
    }
}
