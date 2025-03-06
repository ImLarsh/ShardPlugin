/*    */ package com.example.shardplugin.managers;
/*    */ 
/*    */ import com.example.shardplugin.ShardPlugin;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import org.bukkit.configuration.file.FileConfiguration;
/*    */ import org.bukkit.configuration.file.YamlConfiguration;
/*    */ 
/*    */ public class ConfigManager {
/*    */   private final ShardPlugin plugin;
/*    */   private FileConfiguration config;
/*    */   private File configFile;
/*    */   
/*    */   public ConfigManager(ShardPlugin plugin) {
/* 15 */     this.plugin = plugin;
/* 16 */     setupConfig();
/*    */   }
/*    */   
/*    */   private void setupConfig() {
/* 20 */     if (!this.plugin.getDataFolder().exists()) {
/* 21 */       this.plugin.getDataFolder().mkdir();
/*    */     }
/*    */     
/* 24 */     this.configFile = new File(this.plugin.getDataFolder(), "config.yml");
/* 25 */     if (!this.configFile.exists()) {
/* 26 */       this.plugin.saveResource("config.yml", false);
/*    */     }
/*    */     
/* 29 */     this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.configFile);
/*    */   }
/*    */   
/*    */   public FileConfiguration getConfig() {
/* 33 */     return this.config;
/*    */   }
/*    */   
/*    */   public void saveAll() {
/*    */     try {
/* 38 */       this.config.save(this.configFile);
/* 39 */     } catch (IOException e) {
/* 40 */       this.plugin.getLogger().severe("Could not save config.yml!");
/* 41 */       e.printStackTrace();
/*    */     } 
/*    */   }
/*    */   
/*    */   public void reloadConfig() {
/* 46 */     this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.configFile);
/*    */   }
/*    */ }


/* Location:              C:\Users\Jacob\Desktop\original-ShardPlugin-1.0-SNAPSHOT.jar!\com\example\shardplugin\managers\ConfigManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */