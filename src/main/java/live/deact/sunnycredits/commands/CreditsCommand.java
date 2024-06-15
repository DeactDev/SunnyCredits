package live.deact.sunnycredits.commands;

import live.deact.sunnycredits.SunnyCredits;
import live.deact.sunnycredits.guihandler.HomeGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Locale;

public class CreditsCommand implements CommandExecutor {

    private final SunnyCredits plugin;

    public CreditsCommand(SunnyCredits plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                double balance = plugin.getDatabaseManager().getBalance(player.getUniqueId());
                NumberFormat formatter = NumberFormat.getInstance(Locale.US);
                String formattedBalance = formatter.format(balance);
                String message = plugin.getMessage("messages.balance", "%balance%", formattedBalance);
                player.sendMessage(message);
            } else {
                sender.sendMessage(plugin.getMessage("messages.no_permission"));
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("shop")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                HomeGUI.open(plugin, player);
            } else {
                sender.sendMessage(plugin.getMessage("messages.no_permission"));
            }
        } else {
            sender.sendMessage(plugin.getMessage("messages.usage", "%usage%", "/credits <shop>"));
        }
        return true;
    }
}