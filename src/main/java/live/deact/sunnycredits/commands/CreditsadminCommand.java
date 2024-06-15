package live.deact.sunnycredits.commands;

import live.deact.sunnycredits.SunnyCredits;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CreditsadminCommand implements CommandExecutor {

    private final SunnyCredits plugin;

    public CreditsadminCommand(SunnyCredits plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessage("messages.admin_usage"));
            return true;
        }

        if (!sender.hasPermission("credits.admin")) {
            sender.sendMessage(plugin.getMessage("messages.no_permission"));
            return true;
        }

        if (args[0].equalsIgnoreCase("give") && args.length == 3) {
            Player target = plugin.getServer().getPlayer(args[1]);
            if (target != null) {
                int amount = Integer.parseInt(args[2]);
                plugin.getDatabaseManager().addCredits(target.getUniqueId(), amount);
                sender.sendMessage(plugin.getMessage("messages.given_credits")
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%player%", target.getName()));
            } else {
                sender.sendMessage(plugin.getMessage("messages.player_not_found"));
            }
        } else if (args[0].equalsIgnoreCase("take") && args.length == 3) {
            Player target = plugin.getServer().getPlayer(args[1]);
            if (target != null) {
                int amount = Integer.parseInt(args[2]);
                plugin.getDatabaseManager().removeCredits(target.getUniqueId(), amount);
                sender.sendMessage(plugin.getMessage("messages.taken_credits")
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%player%", target.getName()));
            } else {
                sender.sendMessage(plugin.getMessage("messages.player_not_found"));
            }
        } else if (args[0].equalsIgnoreCase("set") && args.length == 3) {
            Player target = plugin.getServer().getPlayer(args[1]);
            if (target != null) {
                int amount = Integer.parseInt(args[2]);
                plugin.getDatabaseManager().setBalance(target.getUniqueId(), amount);
                sender.sendMessage(plugin.getMessage("messages.set_credits")
                        .replace("%player%", target.getName())
                        .replace("%amount%", String.valueOf(amount)));
            } else {
                sender.sendMessage(plugin.getMessage("messages.player_not_found"));
            }
        } else if (args[0].equalsIgnoreCase("reset") && args.length == 2) {
            Player target = plugin.getServer().getPlayer(args[1]);
            if (target != null) {
                plugin.getDatabaseManager().setBalance(target.getUniqueId(), 0);
                sender.sendMessage(plugin.getMessage("messages.reset_credits", "%player%", target.getName()));
            } else {
                sender.sendMessage(plugin.getMessage("messages.player_not_found"));
            }
        } else if (args[0].equalsIgnoreCase("delete")) {
            plugin.getDatabaseManager().resetDatabase();
            sender.sendMessage(plugin.getMessage("messages.database_reset"));
        } else if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadLangConfig();
            plugin.reloadShopConfig();
            sender.sendMessage(plugin.getMessage("messages.config_reloaded"));
        } else {
            sender.sendMessage(plugin.getMessage("messages.admin_usage"));
        }

        return true;
    }
}
