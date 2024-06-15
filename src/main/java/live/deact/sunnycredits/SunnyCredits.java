package live.deact.sunnycredits;

import dev.partyhat.inventory.InventoryManager;
import live.deact.sunnycredits.managers.DatabaseManager;
import live.deact.sunnycredits.listeners.PlayerJoinListener;
import live.deact.sunnycredits.commands.CreditsCommand;
import live.deact.sunnycredits.commands.CreditsadminCommand;
import live.deact.sunnycredits.managers.PlaceholderManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SunnyCredits extends JavaPlugin {

    private DatabaseManager databaseManager;
    private File langFile;
    private File shopFile;
    private FileConfiguration langConfig;
    private FileConfiguration shopConfig;
    private InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        // Ensure config.yml is created if it doesn't exist
        saveDefaultConfig();

        // Ensure lang.yml is created if it doesn't exist
        createLangFile();

        // Ensure shop.yml is created if it doesn't exist
        createShopFile();

        // Load database configuration and initialize DatabaseManager
        FileConfiguration config = getConfig();
        String host = config.getString("database.host");
        int port = config.getInt("database.port");
        String database = config.getString("database.name");
        String user = config.getString("database.user");
        String password = config.getString("database.password");

        databaseManager = new DatabaseManager(this, host, port, database, user, password);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // Register commands
        this.getCommand("credits").setExecutor(new CreditsCommand(this));
        this.getCommand("creditsadmin").setExecutor(new CreditsadminCommand(this));

        // Initialize Adventure API

        // Initialize SmartInvs InventoryManager
        inventoryManager = new InventoryManager(this);
        inventoryManager.init();

        // Register PlaceholderAPI expansion
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderManager(this).register();
        }
    }

    @Override
    public void onDisable() {
        databaseManager.closeConnection();
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public FileConfiguration getLangConfig() {
        return langConfig;
    }

    public FileConfiguration getShopConfig() {
        return shopConfig;
    }




    private void createLangFile() {
        langFile = new File(getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            saveResource("lang.yml", false);
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    private void createShopFile() {
        shopFile = new File(getDataFolder(), "shop.yml");
        if (!shopFile.exists()) {
            shopFile.getParentFile().mkdirs();
            saveResource("shop.yml", false);
        }
        shopConfig = YamlConfiguration.loadConfiguration(shopFile);
    }
    public String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', langConfig.getString(path, ""));
    }

    public String getMessage(String path, Object... replacements) {
        String message = langConfig.getString(path, "");
        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace((String) replacements[i], replacements[i + 1].toString());
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    public void reloadLangConfig() {
        if (langFile == null) {
            langFile = new File(getDataFolder(), "lang.yml");
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public void reloadShopConfig() {
        if (shopFile == null) {
            shopFile = new File(getDataFolder(), "shop.yml");
        }
        shopConfig = YamlConfiguration.loadConfiguration(shopFile);
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
}