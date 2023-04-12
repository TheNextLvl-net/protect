package net.thenextlvl.protect;

import core.annotation.FieldsAreNonnullByDefault;
import core.annotation.MethodsReturnNonnullByDefault;
import core.api.placeholder.Placeholder;
import core.bukkit.plugin.CorePlugin;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.command.AreaCommand;
import net.thenextlvl.protect.listener.AreaListener;
import net.thenextlvl.protect.listener.EntityListener;
import net.thenextlvl.protect.listener.MoveListener;
import net.thenextlvl.protect.listener.WorldListener;
import net.thenextlvl.protect.util.Messages;
import net.thenextlvl.protect.util.Placeholders;
import org.bukkit.entity.Player;

@Getter
@FieldsAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Protect extends CorePlugin {
    @Accessors(fluent = true)
    private final Placeholder.Formatter<Player> formatter = new Placeholder.Formatter<>();

    @Override
    public void onLoad() {
        Messages.init();
        Placeholders.init(this);
    }

    @Override
    public void onEnable() {
        Area.init();
        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        Area.getSaves().save();
    }

    public void registerCommands() {
        try {
            AreaCommand.register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerListeners() {
        registerListener(new AreaListener());
        registerListener(new MoveListener());
        registerListener(new WorldListener());
        registerListener(new EntityListener());
    }
}
