package cycle00.autorestart.commands;

import cycle00.autorestart.AutoRestart;
import cycle00.autorestart.utils.Chat;
import cycle00.autorestart.utils.TabCompleterBase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AutoRestartCommand implements CommandExecutor, TabExecutor {

    AutoRestart plugin;
    public AutoRestartCommand(AutoRestart plugin) {
        this.plugin = plugin;
        plugin.getCommand("autorestart").setExecutor(this);
        plugin.getCommand("autorestart").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("autorestart.ar")) {
            sender.sendMessage(Chat.chat("&cYou don't have permission to do this."));
            return true;
        } else {
            if (args.length == 0) {
                sender.sendMessage(Chat.chat("&c[AutoRestart] &fAutoRestartCommands"));
                if (sender.hasPermission("autorestart.admin")) {
                    sender.sendMessage("/ar <hours> <minutes> <seconds> (30 seconds min. for warning)");
                    sender.sendMessage("/ar cancel");
                    sender.sendMessage("/ar now");
                    sender.sendMessage("/ar reload");
                }
                sender.sendMessage("/ar status");
            }

            if (args.length == 1) {
                switch (args[0]) {
                    case "now":
                        if (sender.hasPermission("autorestart.admin")) {
                            this.plugin.scheduleRestart(0, 0, 1);
                            sender.sendMessage(Chat.chat("&c[AutoRestart] &fImpatience accepted -- restarting..."));
                            break;
                        }
                    case "cancel":
                        if (sender.hasPermission("autorestart.admin")) {
                            sender.sendMessage(this.plugin.cancelRestart());
                            break;
                        }
                    case "reload":
                        if (sender.hasPermission("autorestart.admin")) {
                            this.plugin.reload();
                            sender.sendMessage(Chat.chat("&c[AutoRestart] &fConfig reloaded."));
                            break;
                        }
                    case "status":
                        if (this.plugin.enabled) {
                            sender.sendMessage(Chat.chat("&c[AutoRestart] &fRestarting in &b" + AutoRestart.remainingTime()));
                        } else {
                            sender.sendMessage(Chat.chat("&c[AutoRestart] &fThe server is not scheduled to restart."));
                        }
                        break;
                }
            }
            if (args.length == 3) {
                if (sender.hasPermission("autorestart.admin")) {
                    try {
                        this.plugin.scheduleRestart(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                        sender.sendMessage(Chat.chat("&c[AutoRestart] &fServer restarting in &b" + AutoRestart.remainingTime()));
                        return true;
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Chat.chat("&c[AutoRestart] &fThe time values entered could not be understood."));
                        return false;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> arguments = new ArrayList<>();
        //region args
        if (sender.hasPermission("autorestart.admin")){
            arguments.add("now"); arguments.add("cancel"); arguments.add("reload");
        }
        arguments.add("status");
        //endregion

        if (args.length == 1) {
            return TabCompleterBase.filterStartingWith(args[0], arguments);
        } else {
            return Collections.emptyList();
        }
    }
}
