/*
 * eZProtector - Copyright (C) 2018-2020 DoNotSpamPls
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.donotspampls.ezprotector.waterfall;

import com.github.donotspampls.ezprotector.waterfall.listeners.*;
import com.github.donotspampls.ezprotector.waterfall.utilities.ExecutionUtil;
import com.github.donotspampls.ezprotector.waterfall.utilities.MessageUtil;
import net.md_5.bungee.api.event.ProxyReloadEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Main extends Plugin implements Listener {

    private Configuration config;

    @Override
    public void onEnable() {
        // eZProtector is only compatible with Waterfall!
        try {
            Class.forName("io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent");
        } catch (ClassNotFoundException e) {
            getLogger().severe("eZProtector is incompatible with this version of BungeeCord! Please make sure to use Waterfall(b262+), Travertine(b89+) or a compatible fork");
            return;
        }

        // Load the configuration
        loadConfig();

        ExecutionUtil execUtil = new ExecutionUtil(getProxy());
        MessageUtil msgUtil = new MessageUtil(config, execUtil);

        // Register listeners
        getProxy().getPluginManager().registerListener(this, new BrigadierListener(config));
        getProxy().getPluginManager().registerListener(this, new ByteMessageListener(config, execUtil, msgUtil));
        getProxy().getPluginManager().registerListener(this, new CustomCommands(config, msgUtil));
        getProxy().getPluginManager().registerListener(this, new FakeCommands(config, msgUtil));
        getProxy().getPluginManager().registerListener(this, new HiddenSyntaxes(config, msgUtil));
        getProxy().getPluginManager().registerListener(this, new PlayerJoinListener(config));
        getProxy().getPluginManager().registerListener(this, new TabCompletionListener(config));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadConfig() {
        // Copy default config
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Load configuration
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException ignored) {}
    }

    @EventHandler
    public void onReload(ProxyReloadEvent event) {
        loadConfig();
    }
}
