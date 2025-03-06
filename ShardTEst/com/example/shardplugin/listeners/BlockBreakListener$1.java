/*    */ package com.example.shardplugin.listeners;
/*    */ 
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.scheduler.BukkitRunnable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class null
/*    */   extends BukkitRunnable
/*    */ {
/* 57 */   private int count = 0;
/*    */ 
/*    */   
/*    */   public void run() {
/* 61 */     if (this.count < totalShards) {
/* 62 */       player.sendMessage("§a+1 shard!");
/* 63 */       this.count++;
/*    */     } else {
/* 65 */       cancel();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Jacob\Desktop\original-ShardPlugin-1.0-SNAPSHOT.jar!\com\example\shardplugin\listeners\BlockBreakListener$1.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */