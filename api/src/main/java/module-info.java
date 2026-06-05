import org.jspecify.annotations.NullMarked;

@NullMarked
module net.thenextlvl.protect {
    requires com.google.common;
    requires net.kyori.adventure.key;
    requires net.kyori.examination.api;
    requires net.thenextlvl.nbt;
    requires org.bukkit;

    requires static org.jetbrains.annotations;
    requires static org.jspecify;
}