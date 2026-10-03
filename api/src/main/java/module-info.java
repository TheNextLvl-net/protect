import net.thenextlvl.protect.area.AreaProvider;
import net.thenextlvl.protect.area.AreaService;
import net.thenextlvl.protect.flag.FlagRegistry;
import net.thenextlvl.protect.lifecycle.LifecycleManager;
import net.thenextlvl.protect.service.ProtectionService;
import org.jspecify.annotations.NullMarked;

@NullMarked
module net.thenextlvl.protect {
    exports net.thenextlvl.protect.area.event.flag;
    exports net.thenextlvl.protect.area.event.inheritance;
    exports net.thenextlvl.protect.area.event.member;
    exports net.thenextlvl.protect.area.event.player;
    exports net.thenextlvl.protect.area.event.region;
    exports net.thenextlvl.protect.area.event.schematic;
    exports net.thenextlvl.protect.area.event;
    exports net.thenextlvl.protect.area;
    exports net.thenextlvl.protect.exception;
    exports net.thenextlvl.protect.flag;
    exports net.thenextlvl.protect.io;
    exports net.thenextlvl.protect.lifecycle.event;
    exports net.thenextlvl.protect.lifecycle;
    exports net.thenextlvl.protect.region;
    exports net.thenextlvl.protect.schematic;
    exports net.thenextlvl.protect.service;

    requires com.google.common;
    requires net.kyori.adventure.key;
    requires net.kyori.adventure;
    requires net.kyori.examination.api;
    requires net.thenextlvl.nbt;
    requires org.bukkit;

    requires static org.jetbrains.annotations;
    requires static org.jspecify;

    uses AreaProvider;
    uses AreaService;
    uses FlagRegistry;
    uses LifecycleManager;
    uses ProtectionService;
}