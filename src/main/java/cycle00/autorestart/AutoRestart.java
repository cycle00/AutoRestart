package cycle00.autorestart;

import cycle00.autorestart.commands.AutoRestartCommand;
import cycle00.autorestart.config.ConfigManager;
import cycle00.autorestart.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutoRestart extends JavaPlugin {
    public static AutoRestart instance;
    public static AutoRestart getInstance() { return instance; }
    private static Logger log = Bukkit.getLogger();

    public boolean enabled = true;

    private long current_h = 4;
    private long current_m = 0;
    private long current_s = 0;

    private static long startTime;
    private static long stopTime;
    private static long seconds;

    private static String command = "stop";

    private static List<Integer> warnings = new ArrayList<>();
    private static List<TimerTask> warningTasks = new ArrayList<>();
    private static Timer warningTask;
    private static Timer restartTask;

    @Override
    public void onEnable() {
        instance = this;
        new AutoRestartCommand(this); // i forgot to add this when testing this pain

        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            ConfigManager.setConfigDefaults();
        }

        reload();
    }

    @Override
    public void onDisable() {
        warnings = null;
        warningTask = null;
        warningTasks = null;
        restartTask = null;
    }

    private void loadConfig() {
        reloadConfig();
        command = ConfigManager.getCommand();
        enabled = ConfigManager.isEnabled();
        current_h = ConfigManager.getH();
        current_m = ConfigManager.getM();
        current_s = ConfigManager.getS();
        warnings = ConfigManager.getWarningTimes();
    }

    public void reload() {
        cancelRestart();
        loadConfig();
        if (enabled) {
            scheduleRestart(current_h, current_m, current_s);
        }
        log.log(Level.INFO, Chat.chat("&c[AutoRestart] &fReloaded config."));
    }

    public String cancelRestart() {
        enabled = false;
        if (warningTasks == null || warningTasks.isEmpty()) {
            return Chat.chat("&c[AutoRestart] &fNo restart scheduled.");
        }

        log.log(Level.INFO, Chat.chat("&c[AutoRestart] &fCancelling restart."));
        for (TimerTask time : warningTasks) {
            time.cancel();
        }
        warningTask.cancel();
        restartTask.cancel();
        current_s = 0;
        current_m = 0;
        current_h = 0;
        warningTasks = new ArrayList<>();
        restartTask = new Timer();
        warningTask = new Timer();
        log.log(Level.INFO, Chat.chat("&c[AutoRestart] &fRestart cancelled."));
        return Chat.chat("&c[AutoRestart] &fRestart cancelled.");
    }

    public void scheduleRestart(long h, long m, long s) {
        cancelRestart();
        enabled = true;
        current_h = h;
        current_m = m;
        current_s = s;

        startTime = System.nanoTime();
        seconds = ((h * 60) * 60) + (m * 60) + s;
        stopTime = (seconds * 1000000000) + startTime;

        for (int warn : warnings) {
            if (seconds > warn) {
                scheduleWarning(warn);
            }
        }

        restartTask = new Timer();
        TimerTask reTask = new TimerTask() {
            @Override
            public void run() {
                instance.getServer().broadcastMessage(Chat.chat("&c[AutoRestart] Restarting now!"));
                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                try {
                    Bukkit.getScheduler().callSyncMethod(instance, () -> {
                        Bukkit.dispatchCommand(console, command);
                        return true;
                    }).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        restartTask.schedule(reTask, (seconds * 1000));
        log.log(Level.INFO, Chat.chat("&c[AutoRestart] &fRestart scheduled: " + current_h + "h " + current_m + "m " + current_s + "s"));
    }

    private void scheduleWarning(int time) {
        Timer warning = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                if (time > 59) {
                    int minutes = time / 60;
                    if (minutes > 59) {
                        int hours = time / 3600;
                        if (hours > 24) {
                            try {
                                Bukkit.getScheduler().callSyncMethod(instance, () -> {
                                    Bukkit.broadcastMessage(Chat.chat("&c[AutoRestart] &fRoutine server restart in &e" + hours + " &chours."));
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        p.sendTitle(
                                                Chat.chat("&cRestarting server in"),
                                                Chat.chat("&e" + hours + " &fhours"),
                                                10,
                                                60,
                                                10
                                        );
                                    }
                                    return true;
                                }).get();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            Bukkit.getScheduler().callSyncMethod(instance, () -> {
                                Bukkit.broadcastMessage(Chat.chat("&c[AutoRestart] &fRoutine server restart in &e" + minutes + " &cminutes."));
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    p.sendTitle(
                                            Chat.chat("&cRestarting server in"),
                                            Chat.chat("&e" + minutes + " &fminutes"),
                                            10,
                                            60,
                                            10
                                    );
                                }
                                return true;
                            }).get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        Bukkit.getScheduler().callSyncMethod(instance, () -> {
                            Bukkit.broadcastMessage(Chat.chat("&c[AutoRestart] &fRoutine server restart in &e" + time + " &cseconds."));
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.sendTitle(
                                        Chat.chat("&cRestarting server in"),
                                        Chat.chat("&e" + time + " &fseconds"),
                                        10,
                                        60,
                                        10
                                );
                            }
                            return true;
                        }).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        warning.schedule(task, ((seconds - time) * 1000));
        warningTask = warning;
        warningTasks.add(task);
    }

    public static String remainingTime() {
        double seconds = (stopTime - System.nanoTime()) / 1000000000.0;
        double s = seconds % 60;
        double totalMinutes = seconds / 60;
        double m = totalMinutes % 60;
        double totalHours = totalMinutes / 60;
        double h = totalHours % 60;
        return (int) Math.floor(h) + "h " + (int) Math.floor(m) + "m " + (int) Math.floor(s) + "s";
    }
}
