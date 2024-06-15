package live.deact.sunnycredits.guihandler;


import dev.partyhat.inventory.ClickableItem;
import dev.partyhat.inventory.SmartInventory;
import dev.partyhat.inventory.content.InventoryContents;
import dev.partyhat.inventory.content.InventoryProvider;
import dev.partyhat.inventory.content.SlotPos;
import live.deact.sunnycredits.SunnyCredits;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DynamicGUI implements InventoryProvider {

    private final SunnyCredits plugin;
    private final String guiName;

    public DynamicGUI(SunnyCredits plugin, String guiName) {
        this.plugin = plugin;
        this.guiName = guiName;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        FileConfiguration shopConfig = plugin.getShopConfig();

        // Debugging information
        plugin.getLogger().info("Initializing DynamicGUI (" + guiName + ") for player " + player.getName());
        Set<String> itemKeys = shopConfig.getConfigurationSection(guiName + ".items").getKeys(false);
        plugin.getLogger().info("Found " + itemKeys.size() + " items in " + guiName + ".items");

        // Check if the configuration section exists
        if (shopConfig.getConfigurationSection(guiName + ".items") == null) {
            plugin.getLogger().severe("Configuration section '" + guiName + ".items' is missing in shop.yml.");
            return;
        }

        itemKeys.forEach(key -> {
            int slot = shopConfig.getInt(guiName + ".items." + key + ".slot");
            String materialName = shopConfig.getString(guiName + ".items." + key + ".material");
            String name = shopConfig.getString(guiName + ".items." + key + ".name");
            int price = shopConfig.getInt(guiName + ".items." + key + ".price");
            List<String> lore = shopConfig.getStringList(guiName + ".items." + key + ".lore").stream()
                    .map(line -> line.replace("%price%", String.valueOf(price)))
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList());
            List<String> commands = shopConfig.getStringList(guiName + ".items." + key + ".commands");

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
                if (plugin.getDatabaseManager().getBalance(player.getUniqueId()) >= price) {
                    plugin.getDatabaseManager().setBalance(player.getUniqueId(), plugin.getDatabaseManager().getBalance(player.getUniqueId()) - price);
                    for (String command : commands) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                    }
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("messages.purchased_item")
                            .replace("%item%", name).replace("%price%", String.valueOf(price))));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("messages.insufficient_credits")));
                }
            }));
        });
        addFillerItems(contents, shopConfig, guiName);
    }
    private void addFillerItems(InventoryContents contents, FileConfiguration shopConfig, String guiName) {
        String materialName = shopConfig.getString(guiName + ".filler.material", "BLACK_STAINED_GLASS_PANE");
        String name = shopConfig.getString(guiName + ".filler.name", "&7");
        List<String> lore = shopConfig.getStringList(guiName + ".filler.lore").stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());

        Material material = Material.getMaterial(materialName);
        if (material == null) {
            material = Material.BLACK_STAINED_GLASS_PANE;
        }

        ItemStack fillerItem = new ItemStack(material);
        ItemMeta meta = fillerItem.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(lore);
        fillerItem.setItemMeta(meta);

        ClickableItem filler = ClickableItem.empty(fillerItem);

        for (int row = 0; row < contents.inventory().getRows(); row++) {
            for (int col = 0; col < contents.inventory().getColumns(); col++) {
                SlotPos pos = SlotPos.of(row, col);
                if (!contents.get(pos).isPresent()) {
                    contents.set(pos, filler);
                }
            }
        }
    }


    @Override
    public void update(Player player, InventoryContents contents) {
        // No periodic update needed for now
    }

    public static void open(SunnyCredits plugin, Player player, String guiName) {
        FileConfiguration shopConfig = plugin.getShopConfig();
        Component title = LegacyComponentSerializer.legacyAmpersand().deserialize(shopConfig.getString(guiName + ".title"));

        SmartInventory.builder()
                .provider(new DynamicGUI(plugin, guiName))
                .size(shopConfig.getInt(guiName + ".rows"), 9)
                .title(title)
                .manager(plugin.getInventoryManager())
                .build()
                .open(player);
    }
}