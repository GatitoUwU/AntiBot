package twolovers.antibot.bungee.module;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.extendables.PunishableModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class BlacklistModule extends PunishableModule {
    private static final String BLACKLIST_PATH = "%datafolder%/blacklist.yml";
    private final ModuleManager moduleManager;
    private Collection<String> blacklist = new HashSet<>();

    BlacklistModule(final ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Override
    public final void reload(final ConfigUtil configUtil) {
        super.name = "blacklist";
        super.reload(configUtil);

        final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");

        punishCommands.clear();
        punishCommands.addAll(configYml.getStringList(name + ".commands"));

        load(configUtil);
    }

    public void setBlacklisted(final String address, final boolean blacklist) {
        if (blacklist) {
            moduleManager.getWhitelistModule().setWhitelisted(address, false);

            moduleManager.getRuntimeModule().addBlacklisted(address);

            this.blacklist.add(address);
        } else {
            this.blacklist.remove(address);
        }
    }

    final int getSize() {
        return blacklist.size();
    }

    public void save(final ConfigUtil configUtil) {
        final Configuration blacklistYml = configUtil.getConfiguration(BLACKLIST_PATH);

        if (blacklistYml != null) {
            blacklistYml.set("", new ArrayList<>(blacklist));
            configUtil.saveConfiguration(blacklistYml, BLACKLIST_PATH);
        }
    }

    public void load(final ConfigUtil configUtil) {
        final Configuration blacklistYml = configUtil.getConfiguration(BLACKLIST_PATH);

        this.blacklist.clear();
        this.blacklist.addAll(blacklistYml.getStringList(""));
    }

    public boolean check(final Connection connection) {
        return isBlacklisted(connection.getAddress().getHostString());
    }

    public boolean isBlacklisted(final String ip) {
        return this.blacklist.contains(ip);
    }

    public Collection<String> getBlacklist() {
        return this.blacklist;
    }
}
