package cycle00.autorestart.utils;

import org.bukkit.ChatColor;

public class Chat {
    public static String chat(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
