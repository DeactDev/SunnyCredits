package live.deact.sunnycredits.managers;

import live.deact.sunnycredits.SunnyCredits;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

public class PlaceholderManager extends PlaceholderExpansion {

    private final SunnyCredits plugin;

    public PlaceholderManager(SunnyCredits plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean canRegister(){
        return true;
    }
    public String getIdentifier() {
        return "credits";
    }
    @Override
    public String getAuthor() {
        return "SunnyMC";
    }
    @Override
    public String getVersion() {
        return "1.0";
    }
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equals("balance")) {
            double balance = plugin.getDatabaseManager().getBalance(player.getUniqueId());
            NumberFormat formatter = NumberFormat.getInstance(Locale.US);
            return formatter.format(balance);
        }

        return null;
    }


}