package com.example.shardplugin.managers;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.NamespacedKey;
import com.example.shardplugin.ShardPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShardManager {
    
    private final ShardPlugin plugin;
    private final Map<UUID, Integer> playerShards;
    private final Map<UUID, Integer> playerEnchantedShards;
    private final NamespacedKey shardKey;
    private final NamespacedKey enchantedShardKey;
    
    public ShardManager(final ShardPlugin plugin) {
        this.plugin = plugin;
        this.playerShards = new HashMap<>();
        this.playerEnchantedShards = new HashMap<>();
        this.shardKey = new NamespacedKey(plugin, "shards");
        this.enchantedShardKey = new NamespacedKey(plugin, "enchanted_shards");
    }
    
    public void addShards(final Player player, final int amount) {
        final UUID playerUUID = player.getUniqueId();
        final int currentShards = this.getShards(player);
        this.playerShards.put(playerUUID, currentShards + amount);
        this.savePlayerData(player);
    }

    public void addEnchantedShards(final Player player, final int amount) {
        final UUID playerUUID = player.getUniqueId();
        final int currentShards = this.getEnchantedShards(player);
        this.playerEnchantedShards.put(playerUUID, currentShards + amount);
        this.savePlayerData(player);
    }
    
    public boolean removeShards(final Player player, final int amount) {
        final UUID playerUUID = player.getUniqueId();
        final int currentShards = this.getShards(player);
        
        if (currentShards >= amount) {
            this.playerShards.put(playerUUID, currentShards - amount);
            this.savePlayerData(player);
            return true;
        }
        
        return false;
    }

    public boolean removeEnchantedShards(final Player player, final int amount) {
        final UUID playerUUID = player.getUniqueId();
        final int currentShards = this.getEnchantedShards(player);
        
        if (currentShards >= amount) {
            this.playerEnchantedShards.put(playerUUID, currentShards - amount);
            this.savePlayerData(player);
            return true;
        }
        
        return false;
    }
    
    public int getShards(final Player player) {
        return this.playerShards.getOrDefault(player.getUniqueId(), 0);
    }

    public int getEnchantedShards(final Player player) {
        return this.playerEnchantedShards.getOrDefault(player.getUniqueId(), 0);
    }
    
    public void loadPlayerData(final Player player) {
        final PersistentDataContainer container = player.getPersistentDataContainer();
        final int shards = container.getOrDefault(this.shardKey, PersistentDataType.INTEGER, 0);
        final int enchantedShards = container.getOrDefault(this.enchantedShardKey, PersistentDataType.INTEGER, 0);
        this.playerShards.put(player.getUniqueId(), shards);
        this.playerEnchantedShards.put(player.getUniqueId(), enchantedShards);
    }
    
    public void savePlayerData(final Player player) {
        final PersistentDataContainer container = player.getPersistentDataContainer();
        container.set(this.shardKey, PersistentDataType.INTEGER, this.getShards(player));
        container.set(this.enchantedShardKey, PersistentDataType.INTEGER, this.getEnchantedShards(player));
    }
    
    public void saveAllData() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            this.savePlayerData(player);
        }
    }
}