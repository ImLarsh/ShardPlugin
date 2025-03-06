package com.example.shardplugin.managers;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.Color;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.NamespacedKey;
import com.example.shardplugin.ShardPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

public class ArmorManager {
    
    private final ShardPlugin plugin;
    private final Map<String, Double> armorMultipliers;
    private final Map<String, Integer> armorRolls;
    private final NamespacedKey armorSetKey;
    
    public ArmorManager(final ShardPlugin plugin) {
        this.plugin = plugin;
        this.armorMultipliers = new HashMap<>();
        this.armorRolls = new HashMap<>();
        this.armorSetKey = new NamespacedKey(plugin, "armor_set");
        this.loadArmorSets();
    }
    
    private void loadArmorSets() {
        final ConfigurationSection armorSection = this.plugin.getConfig().getConfigurationSection("armor_sets");
        
        if (armorSection != null) {
            for (final String key : armorSection.getKeys(false)) {
                final double multiplier = armorSection.getDouble(key + ".multiplier");
                final int rolls = armorSection.getInt(key + ".rolls", 1);
                this.armorMultipliers.put(key, multiplier);
                this.armorRolls.put(key, rolls);
            }
        }
    }
    
    public double getArmorMultiplier(final Player player) {
        double multiplier = 1.0;
        final ItemStack[] armor = player.getInventory().getArmorContents();
        
        if (armor != null) {
            String setName = null;
            boolean fullSet = true;
            
            for (final ItemStack piece : armor) {
                if (piece == null || !piece.hasItemMeta()) {
                    fullSet = false;
                    break;
                }
                
                final PersistentDataContainer container = piece.getItemMeta().getPersistentDataContainer();
                final String pieceName = container.get(this.armorSetKey, PersistentDataType.STRING);
                
                if (pieceName == null || (setName != null && !setName.equals(pieceName))) {
                    fullSet = false;
                    break;
                }
                
                setName = pieceName;
            }
            
            if (fullSet && setName != null) {
                multiplier = this.armorMultipliers.getOrDefault(setName, 1.0);
            }
        }
        
        return multiplier;
    }
    
    public int getArmorRolls(final Player player) {
        int rolls = 1;
        final ItemStack[] armor = player.getInventory().getArmorContents();
        
        if (armor != null) {
            String setName = null;
            boolean fullSet = true;
            
            for (final ItemStack piece : armor) {
                if (piece == null || !piece.hasItemMeta()) {
                    fullSet = false;
                    break;
                }
                
                final PersistentDataContainer container = piece.getItemMeta().getPersistentDataContainer();
                final String pieceName = container.get(this.armorSetKey, PersistentDataType.STRING);
                
                if (pieceName == null || (setName != null && !setName.equals(pieceName))) {
                    fullSet = false;
                    break;
                }
                
                setName = pieceName;
            }
            
            if (fullSet && setName != null) {
                rolls = this.armorRolls.getOrDefault(setName, 1);
            }
        }
        
        return rolls;
    }
    
    public ItemStack createArmorPiece(final String setName, final Material defaultMaterial) {
        final ConfigurationSection setSection = this.plugin.getConfig().getConfigurationSection("armor_sets." + setName);
        
        if (setSection != null) {
            String armorType = "";
            if (defaultMaterial.name().contains("HELMET")) armorType = "helmet";
            else if (defaultMaterial.name().contains("CHESTPLATE")) armorType = "chestplate";
            else if (defaultMaterial.name().contains("LEGGINGS")) armorType = "leggings";
            else if (defaultMaterial.name().contains("BOOTS")) armorType = "boots";
            
            final ConfigurationSection armorSection = setSection.getConfigurationSection("armor." + armorType);
            if (armorSection != null) {
                // Get the material from config, or use default if not specified
                final Material material = Material.valueOf(armorSection.getString("material", defaultMaterial.name()));
                final ItemStack armorPiece = new ItemStack(material);
                final ItemMeta meta = armorPiece.getItemMeta();
                
                if (meta != null) {
                    // Set display name
                    final String displayName = armorSection.getString("display_name", setSection.getString("name", setName));
                    meta.setDisplayName(displayName);
                    
                    // Set custom model data if specified
                    if (armorSection.contains("custom_model_data")) {
                        meta.setCustomModelData(armorSection.getInt("custom_model_data"));
                    }
                    
                    // Set lore if specified
                    if (armorSection.contains("lore")) {
                        meta.setLore(armorSection.getStringList("lore"));
                    }
                    
                    // Set color for leather armor
                    if (meta instanceof LeatherArmorMeta && armorSection.contains("color")) {
                        final ConfigurationSection colorSection = armorSection.getConfigurationSection("color");
                        if (colorSection != null) {
                            final int red = colorSection.getInt("red", 0);
                            final int green = colorSection.getInt("green", 0);
                            final int blue = colorSection.getInt("blue", 0);
                            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(red, green, blue));
                        }
                    }
                    
                    // Set armor set identifier
                    final PersistentDataContainer container = meta.getPersistentDataContainer();
                    container.set(this.armorSetKey, PersistentDataType.STRING, setName);
                    
                    armorPiece.setItemMeta(meta);
                    return armorPiece;
                }
            }
        }
        
        // Fallback to default armor piece if configuration is missing
        final ItemStack defaultPiece = new ItemStack(defaultMaterial);
        final ItemMeta meta = defaultPiece.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6" + setName);
            final PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(this.armorSetKey, PersistentDataType.STRING, setName);
            defaultPiece.setItemMeta(meta);
        }
        return defaultPiece;
    }
}