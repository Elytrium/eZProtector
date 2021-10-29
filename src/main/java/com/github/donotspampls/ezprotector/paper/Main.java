/*
 * eZProtector - Copyright (C) 2018-2020 DoNotSpamPls
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.donotspampls.ezprotector.paper;

import com.github.donotspampls.ezprotector.paper.listeners.*;
import com.github.donotspampls.ezprotector.paper.utilities.ExecutionUtil;
import com.github.donotspampls.ezprotector.paper.utilities.MessageUtil;
import com.github.donotspampls.ezprotector.paper.utilities.PaperLib;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Main extends JavaPlugin {

    // Mod channels
    public static String ZIG;
    public static String BSM;
    public static String MCBRAND;
    public static String SCHEMATICA;
    public static String WDLINIT;
    public static String WDLCONTROL;

    private boolean papi = false; // is PlaceholderAPI available?

    private MessageUtil msgUtil;

    @Override
    public void onEnable() {
        if (!getServer().getBukkitVersion().matches("1\\.1[2-9](.\\d)?-(R0.1-)?SNAPSHOT")) {
            getLogger().severe("eZProtector is not supported on versions lower than 1.12.2!");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            saveDefaultConfig();

            if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) papi = true;

            ExecutionUtil execUtil = new ExecutionUtil(getServer());
            msgUtil = new MessageUtil(this, execUtil, papi);
            ByteMessageListener bml = new ByteMessageListener(this, execUtil, msgUtil);

            // Check if the server is 1.13 or above
            boolean newerversion;
            try {
                Class.forName("org.bukkit.entity.Dolphin");
                newerversion = true;
            } catch (ClassNotFoundException ignored) {
                newerversion = false;
            }

            // Set mod channels
            if (!newerversion) {
                ZIG = "5zig_Set";
                BSM = "BSM";
                MCBRAND = "MC|Brand";
                SCHEMATICA = "schematica";
                WDLINIT = "WDL|INIT";
                WDLCONTROL = "WDL|CONTROL";

                getServer().getPluginManager().registerEvents(new TabCompletionListener(this), this);
            } else {
                ZIG = "the5zigmod:5zig_set";
                BSM = "bsm:settings";
                MCBRAND = "minecraft:brand";
                SCHEMATICA = "dev:null"; // no schematica for 1.13+
                WDLINIT = "wdl:init";
                WDLCONTROL = "wdl:control";

                getServer().getPluginManager().registerEvents(new BrigadierListener(this), this);
            }

            getCommand("ezp").setExecutor(this);

            getServer().getMessenger().registerIncomingPluginChannel(this, ZIG, bml);
            getServer().getMessenger().registerIncomingPluginChannel(this, BSM, bml);
            getServer().getMessenger().registerIncomingPluginChannel(this, MCBRAND, bml);
            getServer().getMessenger().registerIncomingPluginChannel(this, SCHEMATICA, bml);
            getServer().getMessenger().registerIncomingPluginChannel(this, WDLINIT, bml);

            getServer().getMessenger().registerOutgoingPluginChannel(this, ZIG);
            getServer().getMessenger().registerOutgoingPluginChannel(this, BSM);
            getServer().getMessenger().registerOutgoingPluginChannel(this, SCHEMATICA);
            getServer().getMessenger().registerOutgoingPluginChannel(this, WDLCONTROL);

            getServer().getPluginManager().registerEvents(new CustomCommands(this), this);
            getServer().getPluginManager().registerEvents(new FakeCommands(this), this);
            getServer().getPluginManager().registerEvents(new HiddenSyntaxes(this), this);
            getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

            // Suggest Paper to unsuspecting server owners
            PaperLib.suggestPaper(this);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length != 1) return false;

        if (args[0].equals("reload")) {
            reloadConfig();
            sender.sendMessage(new TextComponent("Config reloaded!"));
            return true;
        }

        return false;
    }

    public MessageUtil getMsgUtil() {
        return msgUtil;
    }

}
