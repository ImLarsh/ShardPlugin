package com.example.shardplugin.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.NamespacedKey;
import com.example.shardplugin.ShardPlugin;
import java.util.HashMap;
import java.util.Map;

public class PickaxeManager {
    
    private final ShardPlugin plugin;
    private final Map<String, Double> pickaxeMultipliers;
    private final Map<String, Integer> pickaxeRolls;
    private final NamespacedKey pickaxeKey;
    
    public PickaxeManager(final ShardPlugin plugin) {
        this.plugin = plugin;
        this.pickaxeMultipliers = new HashMap<>();
        this.pickaxeRolls = new HashMap<>();
        this.pickaxeKey = new NamespacedKey(plugin, "pickaxe");
        this.loadPickaxes();
    }
    
    private void loadPickaxes() {
        final ConfigurationSection pickaxeSection = this.plugin.getConfig().getConfigurationSection("pickaxes");
        
        if (pickaxeSection != null) {
            for (final String key : pickaxeSection.getKeys(false)) {
                final double multiplier = pickaxeSection.getDouble(key + ".multiplier");
                final int rolls = pickaxeSection.getInt(key + ".rolls", 1);
                this.pickaxeMultipliers.put(key, multiplier);
                this.pickaxeRolls.put(key, rolls);
            }
        }
    }
    
    public double getPickaxeMultiplier(final Player player) {
        double multiplier = 1.0;
        final ItemStack mainHand = player.getInventory().getItemInMainHand();
        
        if (mainHand != null && mainHand.hasItemMeta()) {
            final PersistentDataContainer container = mainHand.getItemMeta().getPersistentDataContainer();
            final String pickaxeName = container.get(this.pickaxeKey, PersistentDataType.STRING);
            
            if (pickaxeName != null) {
                multiplier = this.pickaxeMultipliers.getOrDefault(pickaxeName, 1.0);
            }
        }
        
        return multiplier;
    }
    
    public int getPickaxeRolls(final Player player) {
        int rolls = 1;
        final ItemStack mainHand = player.getInventory().getItemInMainHand();
        
        if (mainHand != null && mainHand.hasItemMeta()) {
            final PersistentDataContainer container = mainHand.getItemMeta().getPersistentDataContainer();
            final String pickaxeName = container.get(this.pickaxeKey, PersistentDataType.STRING);
            
            if (pickaxeName != null) {
                rolls = this.pickaxeRolls.getOrDefault(pickaxeName, 1);
            }
        }
        
        return rolls;
    }
    
    public ItemStack createPickaxe(final String pickaxeName) {
        final ConfigurationSection pickaxeSection = this.plugin.getConfig().getConfigurationSection("pickaxes." + pickaxeName);
        
        if (pickaxeSection != null) {
            final ConfigurationSection itemSection = pickaxeSection.getConfigurationSection("item");
            if (itemSection != null) {
                final Material material = Material.valueOf(itemSection.getString("material", "IRON_PICKAXE"));
                final ItemStack pickaxe = new ItemStack(material);
                final ItemMeta meta = pickaxe.getItemMeta();
                
                if (meta != null) {
                    // Set display name
                    final String displayName = itemSection.getString("display_name", pickaxeSection.getString("name", pickaxeName));
                    meta.setDisplayName(displayName);
                    
                    // Set custom model data if specified
                    if (itemSection.contains("custom_model_data")) {
                        meta.setCustomModelData(itemSection.getInt("custom_model_data"));
                    }
                    
                    // Set lore if specified
                    if (itemSection.contains("lore")) {
                        meta.setLore(itemSection.getStringList("lore"));
                    }
                    
                    // Set pickaxe identifier
                    final PersistentDataContainer container = meta.getPersistentDataContainer();
                    container.set(this.pickaxeKey, PersistentDataType.STRING, pickaxeName);
                    
                    pickaxe.setItemMeta(meta);
                    return pickaxe;
                }
            }
        }
        
        // Fallback to default pickaxe if configuration is missing
        final ItemStack defaultPickaxe = new ItemStack(Material.IRON_PICKAXE);
        final ItemMeta meta = defaultPickaxe.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6" + pickaxeName);
            final PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(this.pickaxeKey, PersistentDataType.STRING, pickaxeName);
            defaultPickaxe.setItemMeta(meta);
        }
        return defaultPickaxe;
    }
}