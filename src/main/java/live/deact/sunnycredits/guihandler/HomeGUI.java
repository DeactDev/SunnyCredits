package live.deact.sunnycredits.guihandler;

import dev.partyhat.inventory.ClickableItem;
import dev.partyhat.inventory.SmartInventory;
import dev.partyhat.inventory.content.InventoryContents;
import dev.partyhat.inventory.content.InventoryProvider;
import dev.partyhat.inventory.content.SlotPos;
import live.deact.sunnycredits.SunnyCredits;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class HomeGUI implements InventoryProvider {

    private final SunnyCredits plugin;

    public HomeGUI(SunnyCredits plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        FileConfiguration shopConfig = plugin.getShopConfig();

        // Debugging information
        plugin.getLogger().info("Initializing HomeGUI for player " + player.getName());
        Set<String> itemKeys = shopConfig.getConfigurationSection("home-gui.items").getKeys(false);
        plugin.getLogger().info("Found " + itemKeys.size() + " items in home-gui.items");

        // Check if the configuration section exists
        if (shopConfig.getConfigurationSection("home-gui.items") == null) {
            plugin.getLogger().severe("Configuration section 'home-gui.items' is missing in shop.yml.");
            return;
        }

        itemKeys.forEach(key -> {
            int slot = shopConfig.getInt("home-gui.items." + key + ".slot");
            String materialName = shopConfig.getString("home-gui.items." + key + ".material");
            String name = shopConfig.getString("home-gui.items." + key + ".name");
            int price = shopConfig.getInt("home-gui.items." + key + ".price");
            String gui = shopConfig.getString("home-gui.items." + key + ".gui");
            List<String> lore = shopConfig.getStringList("home-gui.items." + key + ".lore").stream()
                    .map(line -> line.replace("%price%", String.valueOf(price)))
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList());

            // Check if materialName is null
            if (materialName == null) {
                plugin.getLogger().severe("Material for item '" + key + "' is missing in shop.yml.");
                return;
            }

            Material material = Material.getMaterial(materialName);
            // Check if the material is valid
            if (material == null) {
                plugin.getLogger().severe("Invalid material '" + materialName + "' for item '" + key + "' in shop.yml.");
                return;
            }

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            meta.setLore(lore);
            item.setItemMeta(meta);

            contents.set(SlotPos.of(slot / 9, slot % 9), ClickableItem.of(item, e -> {
                if (gui != null) {
                    DynamicGUI.open(plugin, player, gui);
                } else {
                    // Handle item purchase here
                }
            }));
        });
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        // No periodic update needed for now
    }

    public static void open(SunnyCredits plugin, Player player) {
        FileConfiguration shopConfig = plugin.getShopConfig();
        Component title = LegacyComponentSerializer.legacyAmpersand().deserialize(Objects.requireNonNull(shopConfig.getString("home-gui.title")));
        SmartInventory.builder()
                .provider(new HomeGUI(plugin))
                .size(shopConfig.getInt("home-gui.rows"), 9)
                .title(title)
                .manager(plugin.getInventoryManager())
                .build()
                .open(player);
    }
}