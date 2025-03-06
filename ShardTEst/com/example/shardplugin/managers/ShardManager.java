/*    */ package com.example.shardplugin.managers;
/*    */ 
/*    */ import com.example.shardplugin.ShardPlugin;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.UUID;
/*    */ import org.bukkit.NamespacedKey;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.persistence.PersistentDataContainer;
/*    */ import org.bukkit.persistence.PersistentDataType;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ShardManager
/*    */ {
/*    */   private final ShardPlugin plugin;
/*    */   private final Map<UUID, Integer> playerShards;
/*    */   private final Map<UUID, Integer> playerEnchantedShards;
/*    */   private final NamespacedKey shardKey;
/*    */   private final NamespacedKey enchantedShardKey;
/*    */   
/*    */   public ShardManager(ShardPlugin plugin) {
/* 24 */     this.plugin = plugin;
/* 25 */     this.playerShards = new HashMap<>();
/* 26 */     this.playerEnchantedShards = new HashMap<>();
/* 27 */     this.shardKey = new NamespacedKey((Plugin)plugin, "shards");
/* 28 */     this.enchantedShardKey = new NamespacedKey((Plugin)plugin, "enchanted_shards");
/*    */   }
/*    */   
/*    */   public void addShards(Player player, int amount) {
/* 32 */     UUID playerUUID = player.getUniqueId();
/* 33 */     int currentShards = getShards(player);
/* 34 */     this.playerShards.put(playerUUID, Integer.valueOf(currentShards + amount));
/* 35 */     savePlayerData(player);
/*    */   }
/*    */   
/*    */   public void addEnchantedShards(Player player, int amount) {
/* 39 */     UUID playerUUID = player.getUniqueId();
/* 40 */     int currentShards = getEnchantedShards(player);
/* 41 */     this.playerEnchantedShards.put(playerUUID, Integer.valueOf(currentShards + amount));
/* 42 */     savePlayerData(player);
/*    */   }
/*    */   
/*    */   public boolean removeShards(Player player, int amount) {
/* 46 */     UUID playerUUID = player.getUniqueId();
/* 47 */     int currentShards = getShards(player);
/*    */     
/* 49 */     if (currentShards >= amount) {
/* 50 */       this.playerShards.put(playerUUID, Integer.valueOf(currentShards - amount));
/* 51 */       savePlayerData(player);
/* 52 */       return true;
/*    */     } 
/*    */     
/* 55 */     return false;
/*    */   }
/*    */   
/*    */   public boolean removeEnchantedShards(Player player, int amount) {
/* 59 */     UUID playerUUID = player.getUniqueId();
/* 60 */     int currentShards = getEnchantedShards(player);
/*    */     
/* 62 */     if (currentShards >= amount) {
/* 63 */       this.playerEnchantedShards.put(playerUUID, Integer.valueOf(currentShards - amount));
/* 64 */       savePlayerData(player);
/* 65 */       return true;
/*    */     } 
/*    */     
/* 68 */     return false;
/*    */   }
/*    */   
/*    */   public int getShards(Player player) {
/* 72 */     return ((Integer)this.playerShards.getOrDefault(player.getUniqueId(), Integer.valueOf(0))).intValue();
/*    */   }
/*    */   
/*    */   public int getEnchantedShards(Player player) {
/* 76 */     return ((Integer)this.playerEnchantedShards.getOrDefault(player.getUniqueId(), Integer.valueOf(0))).intValue();
/*    */   }
/*    */   
/*    */   public void loadPlayerData(Player player) {
/* 80 */     PersistentDataContainer container = player.getPersistentDataContainer();
/* 81 */     int shards = ((Integer)container.getOrDefault(this.shardKey, PersistentDataType.INTEGER, Integer.valueOf(0))).intValue();
/* 82 */     int enchantedShards = ((Integer)container.getOrDefault(this.enchantedShardKey, PersistentDataType.INTEGER, Integer.valueOf(0))).intValue();
/* 83 */     this.playerShards.put(player.getUniqueId(), Integer.valueOf(shards));
/* 84 */     this.playerEnchantedShards.put(player.getUniqueId(), Integer.valueOf(enchantedShards));
/*    */   }
/*    */   
/*    */   public void savePlayerData(Player player) {
/* 88 */     PersistentDataContainer container = player.getPersistentDataContainer();
/* 89 */     container.set(this.shardKey, PersistentDataType.INTEGER, Integer.valueOf(getShards(player)));
/* 90 */     container.set(this.enchantedShardKey, PersistentDataType.INTEGER, Integer.valueOf(getEnchantedShards(player)));
/*    */   }
/*    */   
/*    */   public void saveAllData() {
/* 94 */     for (Player player : this.plugin.getServer().getOnlinePlayers())
/* 95 */       savePlayerData(player); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Jacob\Desktop\original-ShardPlugin-1.0-SNAPSHOT.jar!\com\example\shardplugin\managers\ShardManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */