package live.deact.sunnycredits.listeners;

import live.deact.sunnycredits.SunnyCredits;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TabComplete implements TabCompleter {

    private final SunnyCredits plugin;

    public TabComplete(SunnyCredits plugin) {
        this.plugin = plugin;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("credits")) {
            if (args.length == 1) {
                List<String> subCommands = Arrays.asList("shop");
                completions = subCommands.stream()
                        .filter(s -> s.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            }
        } else if (command.getName().equalsIgnoreCase("creditsadmin")) {
            if (!sender.hasPermission("credits.admin")) {
                return completions;
            }

            if (args.length == 1) {
                List<String> subCommands = Arrays.asList("give", "take", "set", "reset", "delete", "reload");
                completions = subCommands.stream()
                        .filter(s -> s.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            } else if (args.length == 2 && Arrays.asList("give", "take", "set", "reset").contains(args[0].toLowerCase())) {
                completions = plugin.getServer().getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        return completions;
    }
}
