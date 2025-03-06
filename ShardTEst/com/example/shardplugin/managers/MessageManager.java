/*    */ package com.example.shardplugin.managers;
/*    */ 
/*    */ import com.example.shardplugin.ShardPlugin;
/*    */ import org.bukkit.configuration.ConfigurationSection;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public class MessageManager
/*    */ {
/*    */   private final ShardPlugin plugin;
/*    */   private final ConfigurationSection messageConfig;
/*    */   private final boolean enabled;
/*    */   private final String prefix;
/*    */   
/*    */   public MessageManager(ShardPlugin plugin) {
/* 15 */     this.plugin = plugin;
/* 16 */     this.messageConfig = plugin.getConfig().getConfigurationSection("settings.messages");
/* 17 */     this.enabled = this.messageConfig.getBoolean("enabled", true);
/* 18 */     this.prefix = this.messageConfig.getString("prefix", "§8[§bShards§8]");
/*    */   }
/*    */   
/*    */   public void sendMessage(Player player, String key, Object... args) {
/* 22 */     if (!this.enabled)
/*    */       return; 
/* 24 */     String message = this.messageConfig.getString(key, "Message not found: " + key);
/* 25 */     message = message.replace("%prefix%", this.prefix);
/*    */     
/* 27 */     for (int i = 0; i < args.length; i += 2) {
/* 28 */       if (i + 1 < args.length) {
/* 29 */         message = message.replace(args[i].toString(), args[i + 1].toString());
/*    */       }
/*    */     } 
/*    */     
/* 33 */     player.sendMessage(message);
/*    */   }
/*    */ }


/* Location:              C:\Users\Jacob\Desktop\original-ShardPlugin-1.0-SNAPSHOT.jar!\com\example\shardplugin\managers\MessageManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */