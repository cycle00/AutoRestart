package cycle00.autorestart.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TabCompleterBase implements TabCompleter {
    public static List<String> getOnlinePlayers(String partialName) {
        return filterStartingWith(partialName, Bukkit.getOnlinePlayers().stream().map(Player::getName));
    }

    public static String joinArgsBeyond(int index, String delim, String[] args) {
        ++index;
        String[] data = new String[args.length - index];
        System.arraycopy(args, index, data, 0, data.length);
        return String.join(delim, data);
    }

    public static List<String> filterStartingWith(String prefix, Stream<String> stream) {
        return stream.filter(s -> s != null && !s.isEmpty() && s.toLowerCase().startsWith(prefix.toLowerCase())).collect(Collectors.toList());
    }

    public static List<String> filterStartingWith(String prefix, Collection<String> strings) {
        return filterStartingWith(prefix, strings.stream());
    }
}
