package me.serbob.asteroidtrollextension;

import me.serbob.asteroidapi.commands.AsteroidCommandManager;
import me.serbob.asteroidapi.enums.MinecraftVersion;
import me.serbob.asteroidapi.extension.ExtensionLifecycle;
import me.serbob.asteroidapi.interfaces.Version;
import me.serbob.asteroidtrollextension.commands.TrollCommand;

/*
 * MinecraftVersion isn't fully necessary
 * if it's not used, by default it will put ALL
 */
@Version(MinecraftVersion.ALL)
public final class AsteroidTrollExtension extends ExtensionLifecycle {

    @Override
    public void onEnable() {
        AsteroidCommandManager.registerCommand("troll", new TrollCommand());
    }

    @Override
    public void onDisable() {}
}
