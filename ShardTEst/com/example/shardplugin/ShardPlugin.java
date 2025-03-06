/*    */ package com.example.shardplugin;
/*    */ import com.example.shardplugin.commands.ShardCommands;
/*    */ import com.example.shardplugin.listeners.InventoryListener;
/*    */ import com.example.shardplugin.managers.ArmorManager;
/*    */ import com.example.shardplugin.managers.ConfigManager;
/*    */ import com.example.shardplugin.managers.MessageManager;
/*    */ import com.example.shardplugin.managers.PickaxeManager;
/*    */ import com.example.shardplugin.managers.ShardManager;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public class ShardPlugin extends JavaPlugin {
/*    */   private static ShardPlugin instance;
/*    */   private ShardManager shardManager;
/*    */   private ArmorManager armorManager;
/*    */   
/*    */   public static ShardPlugin getInstance() {
/* 19 */     return instance;
/*    */   }
/*    */   private PickaxeManager pickaxeManager; private MessageManager messageManager; private InventoryListener inventoryListener; private ConfigManager configManager;
/*    */   public ShardManager getShardManager() {
/* 23 */     return this.shardManager;
/*    */   }
/*    */   
/*    */   public ArmorManager getArmorManager() {
/* 27 */     return this.armorManager;
/*    */   }
/*    */   
/*    */   public PickaxeManager getPickaxeManager() {
/* 31 */     return this.pickaxeManager;
/*    */   }
/*    */   
/*    */   public MessageManager getMessageManager() {
/* 35 */     return this.messageManager;
/*    */   }
/*    */   
/*    */   public InventoryListener getInventoryListener() {
/* 39 */     return this.inventoryListener;
/*    */   }
/*    */   
/*    */   public ConfigManager getConfigManager() {
/* 43 */     return this.configManager;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onEnable() {
/* 48 */     instance = this;
/*    */ 
/*    */     
/* 51 */     this.configManager = new ConfigManager(this);
/* 52 */     this.messageManager = new MessageManager(this);
/* 53 */     this.shardManager = new ShardManager(this);
/* 54 */     this.armorManager = new ArmorManager(this);
/* 55 */     this.pickaxeManager = new PickaxeManager(this);
/* 56 */     this.inventoryListener = new InventoryListener(this);
/*    */ 
/*    */     
/* 59 */     registerListeners();
/* 60 */     registerCommands();
/*    */     
/* 62 */     getLogger().info("ShardPlugin has been enabled!");
/*    */   }
/*    */ 
/*    */   
/*    */   public void onDisable() {
/* 67 */     this.shardManager.saveAllData();
/* 68 */     this.configManager.saveAll();
/* 69 */     getLogger().info("ShardPlugin has been disabled!");
/*    */   }
/*    */   
/*    */   private void registerListeners() {
/* 73 */     getServer().getPluginManager().registerEvents((Listener)new BlockBreakListener(this), (Plugin)this);
/* 74 */     getServer().getPluginManager().registerEvents((Listener)this.inventoryListener, (Plugin)this);
/* 75 */     getServer().getPluginManager().registerEvents((Listener)new ArmorDropListener(this), (Plugin)this);
/* 76 */     getServer().getPluginManager().registerEvents((Listener)new PickaxeDropListener(this), (Plugin)this);
/* 77 */     getServer().getPluginManager().registerEvents((Listener)new PlayerJoinListener(this), (Plugin)this);
/*    */   }
/*    */   
/*    */   private void registerCommands() {
/* 81 */     getCommand("shards").setExecutor((CommandExecutor)new ShardCommands(this));
/* 82 */     getCommand("armorshop").setExecutor((CommandExecutor)new ShardCommands(this));
/* 83 */     getCommand("giveshards").setExecutor((CommandExecutor)new ShardCommands(this));
/*    */   }
/*    */ 
/*    */   
/*    */   public void reloadManagers() {
/* 88 */     this.messageManager = new MessageManager(this);
/* 89 */     this.armorManager = new ArmorManager(this);
/* 90 */     this.pickaxeManager = new PickaxeManager(this);
/*    */   }
/*    */ }


/* Location:              C:\Users\Jacob\Desktop\original-ShardPlugin-1.0-SNAPSHOT.jar!\com\example\shardplugin\ShardPlugin.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */