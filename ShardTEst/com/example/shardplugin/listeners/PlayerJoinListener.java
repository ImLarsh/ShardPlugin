/*    */ package com.example.shardplugin.listeners;
/*    */ 
/*    */ import com.example.shardplugin.ShardPlugin;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.player.PlayerJoinEvent;
/*    */ 
/*    */ public class PlayerJoinListener
/*    */   implements Listener {
/*    */   private final ShardPlugin plugin;
/*    */   
/*    */   public PlayerJoinListener(ShardPlugin plugin) {
/* 13 */     this.plugin = plugin;
/*    */   }
/*    */ 
/*    */   
/*    */   @EventHandler
/*    */   public void onPlayerJoin(PlayerJoinEvent event) {
/* 19 */     this.plugin.getShardManager().loadPlayerData(event.getPlayer());
/*    */   }
/*    */ }


/* Location:              C:\Users\Jacob\Desktop\original-ShardPlugin-1.0-SNAPSHOT.jar!\com\example\shardplugin\listeners\PlayerJoinListener.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */