package me.serbob.asteroidtrollextension.commands;

import me.serbob.asteroidapi.commands.AsteroidCommand;
import me.serbob.asteroidapi.registries.FakePlayerEntity;
import me.serbob.asteroidapi.registries.FakePlayerRegistry;
import me.serbob.asteroidtrollextension.actions.TrollAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class TrollCommand implements AsteroidCommand {
    private final Map<UUID, TrollAction> activeTrolls = new HashMap<>();

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length < 1) {
            commandSender.sendMessage(ChatColor.RED + "Invalid args. Must specify one valid fake player.");
            return false;
        }

        Player fakePlayer = Bukkit.getPlayer(args[0]);

        if (fakePlayer == null) {
            commandSender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' was not found!");
            return false;
        }

        if (!FakePlayerRegistry.INSTANCE.isAFakePlayer(fakePlayer.getUniqueId())) {
            commandSender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' is not a fake player!");
            return false;
        }

        FakePlayerEntity fakePlayerEntity = (FakePlayerEntity)
                FakePlayerRegistry.INSTANCE.getFakePlayers().get(fakePlayer.getUniqueId());

        if (activeTrolls.containsKey(fakePlayer.getUniqueId())) {
            TrollAction existingAction = activeTrolls.get(fakePlayer.getUniqueId());
            fakePlayerEntity.getFBrain().getActionManager().forceUnregister(existingAction);
            activeTrolls.remove(fakePlayer.getUniqueId());

            commandSender.sendMessage(ChatColor.GREEN + "Stopped trolling " + fakePlayer.getName());
            return true;
        }

        TrollAction newAction = new TrollAction();
        fakePlayerEntity.getFBrain().getActionManager().register(newAction);
        activeTrolls.put(fakePlayer.getUniqueId(), newAction);

        commandSender.sendMessage(ChatColor.GREEN + "Started trolling " + fakePlayer.getName());

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (FakePlayerRegistry.INSTANCE.isAFakePlayer(player.getUniqueId())) {
                    completions.add(player.getName());
                }
            }
        }

        return completions;
    }
}
