/*    */ package com.example.shardplugin.listeners;
/*    */ 
/*    */ import com.example.shardplugin.ShardPlugin;
/*    */ import com.example.shardplugin.gui.ArmorShopGUI;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.UUID;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.inventory.InventoryClickEvent;
/*    */ import org.bukkit.event.inventory.InventoryCloseEvent;
/*    */ 
/*    */ public class InventoryListener
/*    */   implements Listener
/*    */ {
/*    */   private final ShardPlugin plugin;
/*    */   private final Map<UUID, ArmorShopGUI> openGUIs;
/*    */   
/*    */   public InventoryListener(ShardPlugin plugin) {
/* 20 */     this.plugin = plugin;
/* 21 */     this.openGUIs = new HashMap<>();
/*    */   }
/*    */   
/*    */   public void registerGUI(UUID playerUUID, ArmorShopGUI gui) {
/* 25 */     this.openGUIs.put(playerUUID, gui);
/*    */   }
/*    */   
/*    */   public void unregisterGUI(UUID playerUUID) {
/* 29 */     this.openGUIs.remove(playerUUID);
/*    */   }
/*    */   
/*    */   @EventHandler
/*    */   public void onInventoryClick(InventoryClickEvent event) {
/* 34 */     UUID playerUUID = event.getWhoClicked().getUniqueId();
/* 35 */     ArmorShopGUI gui = this.openGUIs.get(playerUUID);
/*    */     
/* 37 */     if (gui != null) {
/* 38 */       gui.handleClick(event);
/*    */     }
/*    */   }
/*    */   
/*    */   @EventHandler
/*    */   public void onInventoryClose(InventoryCloseEvent event) {
/* 44 */     UUID playerUUID = event.getPlayer().getUniqueId();
/* 45 */     unregisterGUI(playerUUID);
/*    */   }
/*    */ }


/* Location:              C:\Users\Jacob\Desktop\original-ShardPlugin-1.0-SNAPSHOT.jar!\com\example\shardplugin\listeners\InventoryListener.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */