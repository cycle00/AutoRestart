package cycle00.autorestart.config;

import cycle00.autorestart.AutoRestart;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;

public class ConfigManager {
    static AutoRestart plugin = AutoRestart.getInstance();

    public static void setConfigDefaults() {
        FileConfiguration config = plugin.getConfig();
        config.options().header("warning-times are set in seconds before restart");
        List<Integer> times = Arrays.asList(600, 300, 60, 30, 15, 10, 5, 4, 3, 2, 1);
        config.addDefault("enabled", true);
        config.addDefault("warning-times", times);
        config.addDefault("interval.h", 5);
        config.addDefault("interval.m", 0);
        config.addDefault("interval.s", 0);
        config.addDefault("restart-command", "stop");
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public static boolean isEnabled() { return plugin.getConfig().getBoolean("enabled"); }

    public static List<Integer> getWarningTimes() { return plugin.getConfig().getIntegerList("warning-times"); }

    public static Long getH() { return plugin.getConfig().getLong("interval.h"); }

    public static Long getM() { return plugin.getConfig().getLong("interval.m"); }

    public static Long getS() { return plugin.getConfig().getLong("interval.s"); }

    public static String getCommand() { return plugin.getConfig().getString("restart-command"); }
}
