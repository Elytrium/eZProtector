/*
 * eZProtector - Copyright (C) 2018-2020 DoNotSpamPls
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.donotspampls.ezprotector.paper.utilities;

import com.github.donotspampls.ezprotector.paper.Main;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class MessageUtil {

    private final Main plugin;
    private final ExecutionUtil execUtil;
    private final boolean papi;

    public MessageUtil(Main plugin, ExecutionUtil execUtil, boolean papi) {
        this.plugin = plugin;
        this.execUtil = execUtil;
        this.papi = papi;
    }

    public String placeholders(String args, Player player, String errorMessage, String command) {
        String cargs =
                args.replace("%player%", player.getName())
                    .replace("%errormessage%", errorMessage == null ? "" : errorMessage)
                    .replace("%command%", command == null ? "" : command);

        if (papi)
            return PlaceholderAPI.setPlaceholders(player,
                ChatColor.translateAlternateColorCodes('&', cargs));
        else
            return ChatColor.translateAlternateColorCodes('&', cargs);

    }

    public void punishPlayers(String module, Player player, String errorMessage, String command) {
        FileConfiguration config = plugin.getConfig();
        if (config.getBoolean(module + ".punish-player.enabled")) {
            String punishCommand = config.getString(module + ".punish-player.command");
            execUtil.executeConsoleCommand(placeholders(punishCommand, player, errorMessage, command));
        }
    }

    public void notifyAdmins(String module, Player player, String command, String perm) {
        FileConfiguration config = plugin.getConfig();
        if (config.getBoolean(module + ".notify-admins.enabled")) {
            String msg = config.getString(module + ".notify-admins.message");

            String notifyMessage =  placeholders(msg, player, null, command);
            execUtil.notifyAdmins(notifyMessage, "ezprotector.notify." + perm);
        }
    }

}
