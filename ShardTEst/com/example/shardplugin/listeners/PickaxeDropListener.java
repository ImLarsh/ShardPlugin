/*    */ package com.example.shardplugin.listeners;
/*    */ 
/*    */ import com.example.shardplugin.ShardPlugin;
/*    */ import org.bukkit.NamespacedKey;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.player.PlayerDropItemEvent;
/*    */ import org.bukkit.inventory.ItemStack;
/*    */ import org.bukkit.inventory.meta.ItemMeta;
/*    */ import org.bukkit.persistence.PersistentDataContainer;
/*    */ import org.bukkit.persistence.PersistentDataType;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public class PickaxeDropListener implements Listener {
/*    */   private final ShardPlugin plugin;
/*    */   private final NamespacedKey pickaxeKey;
/*    */   
/*    */   public PickaxeDropListener(ShardPlugin plugin) {
/* 19 */     this.plugin = plugin;
/* 20 */     this.pickaxeKey = new NamespacedKey((Plugin)plugin, "pickaxe");
/*    */   }
/*    */   
/*    */   @EventHandler
/*    */   public void onPlayerDropItem(PlayerDropItemEvent event) {
/* 25 */     ItemStack item = event.getItemDrop().getItemStack();
/*    */     
/* 27 */     if (item.hasItemMeta()) {
/* 28 */       ItemMeta meta = item.getItemMeta();
/* 29 */       PersistentDataContainer container = meta.getPersistentDataContainer();
/*    */ 
/*    */       
/* 32 */       if (container.has(this.pickaxeKey, PersistentDataType.STRING)) {
/* 33 */         event.setCancelled(true);
/* 34 */         this.plugin.getMessageManager().sendMessage(event
/* 35 */             .getPlayer(), "cannot_drop_pickaxe", new Object[0]);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Jacob\Desktop\original-ShardPlugin-1.0-SNAPSHOT.jar!\com\example\shardplugin\listeners\PickaxeDropListener.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */