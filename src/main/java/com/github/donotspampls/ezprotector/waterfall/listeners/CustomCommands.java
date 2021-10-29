/*
 * eZProtector - Copyright (C) 2018-2020 DoNotSpamPls
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.donotspampls.ezprotector.waterfall.listeners;

import com.github.donotspampls.ezprotector.waterfall.Main;
import com.github.donotspampls.ezprotector.waterfall.utilities.MessageUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

public class CustomCommands implements Listener {

    private final Configuration config;
    private final MessageUtil msgUtil;

    public CustomCommands(Configuration config, MessageUtil msgUtil) {
        this.config = config;
        this.msgUtil = msgUtil;
    }

    @EventHandler
    public void execute(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) return;

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String command = event.getMessage().split(" ")[0];

        if (config.getBoolean("custom-commands.blocked") && !player.hasPermission("ezprotector.bypass.command.custom")) {
            for (String message : config.getStringList("custom-commands.commands")) {
                if (command.equalsIgnoreCase("/" + message)) {
                    event.setCancelled(true);

                    String errorMessage = config.getString("custom-commands.error-message");
                    if (!errorMessage.trim().isEmpty())
                        player.sendMessage(msgUtil.placeholders(errorMessage, player, null, command));

                    msgUtil.punishPlayers("custom-commands", player, errorMessage, command);
                    msgUtil.notifyAdmins("custom-commands", player, command, "command.custom");

                    break;
                }
            }
        }
    }
}
