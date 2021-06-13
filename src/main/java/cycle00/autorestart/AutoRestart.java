package cycle00.autorestart;

import org.bukkit.plugin.java.JavaPlugin;

public class AutoRestart extends JavaPlugin {
    public static AutoRestart instance;
    public static AutoRestart getInstance() { return instance; }

    private boolean enabled = true;

    

    @Override
    public void onEnable() {
        instance = this;
    }
}
