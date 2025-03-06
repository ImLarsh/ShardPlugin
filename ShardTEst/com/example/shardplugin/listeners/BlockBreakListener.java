/*     */ package com.example.shardplugin.listeners;
/*     */ 
/*     */ import com.example.shardplugin.ShardPlugin;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.block.BlockBreakEvent;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.scheduler.BukkitRunnable;
/*     */ 
/*     */ public class BlockBreakListener implements Listener {
/*     */   private final ShardPlugin plugin;
/*     */   private final Random random;
/*     */   
/*     */   public BlockBreakListener(ShardPlugin plugin) {
/*  23 */     this.plugin = plugin;
/*  24 */     this.random = new Random();
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   public void onBlockBreak(BlockBreakEvent event) {
/*  29 */     final Player player = event.getPlayer();
/*  30 */     ConfigurationSection blockSection = this.plugin.getConfigManager().getConfig().getConfigurationSection("settings.shard_blocks");
/*     */     
/*  32 */     if (blockSection != null) {
/*  33 */       String blockType = event.getBlock().getType().name();
/*     */       
/*  35 */       if (blockSection.contains(blockType)) {
/*  36 */         ConfigurationSection blockConfig = blockSection.getConfigurationSection(blockType);
/*     */         
/*  38 */         if (blockConfig != null) {
/*  39 */           int baseShards = blockConfig.getInt("amount");
/*     */ 
/*     */           
/*  42 */           double armorMultiplier = this.plugin.getArmorManager().getArmorMultiplier(player);
/*  43 */           double pickaxeMultiplier = this.plugin.getPickaxeManager().getPickaxeMultiplier(player);
/*  44 */           double totalMultiplier = armorMultiplier * pickaxeMultiplier;
/*     */           
/*  46 */           final int totalShards = (int)Math.round(baseShards * totalMultiplier);
/*     */ 
/*     */           
/*  49 */           this.plugin.getShardManager().addShards(player, totalShards);
/*     */ 
/*     */           
/*  52 */           ItemStack shardItem = createShardItem(totalShards);
/*  53 */           player.getInventory().addItem(new ItemStack[] { shardItem });
/*     */ 
/*     */           
/*  56 */           (new BukkitRunnable() {
/*  57 */               private int count = 0;
/*     */ 
/*     */               
/*     */               public void run() {
/*  61 */                 if (this.count < totalShards) {
/*  62 */                   player.sendMessage("§a+1 shard!");
/*  63 */                   this.count++;
/*     */                 } else {
/*  65 */                   cancel();
/*     */                 } 
/*     */               }
/*  68 */             }).runTaskTimer((Plugin)this.plugin, 0L, 2L);
/*     */ 
/*     */           
/*  71 */           double armorRolls = this.plugin.getArmorManager().getArmorRolls(player);
/*  72 */           double pickaxeRolls = this.plugin.getPickaxeManager().getPickaxeRolls(player);
/*  73 */           double totalRolls = armorRolls + pickaxeRolls;
/*     */ 
/*     */           
/*  76 */           int guaranteedRolls = (int)Math.floor(totalRolls);
/*  77 */           double extraRollChance = totalRolls - guaranteedRolls;
/*     */ 
/*     */           
/*  80 */           List<String> commands = blockConfig.getStringList("commands");
/*  81 */           for (int i = 0; i < guaranteedRolls; i++) {
/*  82 */             executeCommands(commands, player);
/*     */           }
/*     */ 
/*     */           
/*  86 */           if (extraRollChance > 0.0D && this.random.nextDouble() < extraRollChance) {
/*  87 */             executeCommands(commands, player);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void executeCommands(List<String> commands, Player player) {
/*  95 */     for (String command : commands) {
/*  96 */       String formattedCommand = command.replace("%player%", player.getName());
/*  97 */       this.plugin.getServer().dispatchCommand((CommandSender)this.plugin
/*  98 */           .getServer().getConsoleSender(), formattedCommand);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private ItemStack createShardItem(int amount) {
/* 105 */     ConfigurationSection currencySection = this.plugin.getConfigManager().getConfig().getConfigurationSection("settings.currency");
/*     */     
/* 107 */     ItemStack shardItem = new ItemStack(Material.valueOf(currencySection.getString("material", "AMETHYST_SHARD")), amount);
/*     */ 
/*     */     
/* 110 */     ItemMeta meta = shardItem.getItemMeta();
/*     */     
/* 112 */     if (meta != null) {
/* 113 */       meta.setCustomModelData(Integer.valueOf(currencySection.getInt("custom_model_data", 1001)));
/* 114 */       shardItem.setItemMeta(meta);
/*     */     } 
/*     */     
/* 117 */     return shardItem;
/*     */   }
/*     */ }


/* Location:              C:\Users\Jacob\Desktop\original-ShardPlugin-1.0-SNAPSHOT.jar!\com\example\shardplugin\listeners\BlockBreakListener.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */