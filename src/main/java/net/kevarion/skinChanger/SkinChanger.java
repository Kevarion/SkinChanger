 package net.kevarion.skinChanger;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import net.kevarion.skinChanger.command.SkinCommand;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("LombokGetterMayBeUsed")
public final class SkinChanger extends JavaPlugin {

    @Getter private static SkinChanger instance;
    private PaperCommandManager commandManager;

    public static SkinChanger getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;
        commandManager = new PaperCommandManager(this);

        commandManager.registerCommand(new SkinCommand());

    }
}
