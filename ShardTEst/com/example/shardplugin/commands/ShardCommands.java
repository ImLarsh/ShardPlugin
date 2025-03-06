/*     */ package com.example.shardplugin.commands;
/*     */ 
/*     */ import com.example.shardplugin.ShardPlugin;
/*     */ import com.example.shardplugin.gui.ArmorShopGUI;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class ShardCommands
/*     */   implements CommandExecutor {
/*     */   private final ShardPlugin plugin;
/*     */   
/*     */   public ShardCommands(ShardPlugin plugin) {
/*  16 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
/*  21 */     if (!(sender instanceof Player) && !label.equals("giveshards") && (!label.equals("armorshop") || args.length <= 0 || (!args[0].equals("reload") && !args[0].equals("shards")))) {
/*  22 */       sender.sendMessage("§cThis command can only be used by players!");
/*  23 */       return true;
/*     */     } 
/*     */     
/*  26 */     switch (label.toLowerCase()) {
/*     */       case "shards":
/*  28 */         return handleShardsCommand((Player)sender);
/*     */       case "armorshop":
/*  30 */         return handleArmorShopCommand(sender, args);
/*     */       case "giveshards":
/*  32 */         return handleGiveShardsCommand(sender, args);
/*     */     } 
/*  34 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean handleShardsCommand(Player player) {
/*  39 */     int shards = this.plugin.getShardManager().getShards(player);
/*  40 */     player.sendMessage("§aYou have " + shards + " shards!");
/*  41 */     return true;
/*     */   }
/*     */   
/*     */   private boolean handleArmorShopCommand(CommandSender sender, String[] args) {
/*  45 */     if (args.length > 0) {
/*  46 */       if (args[0].equals("reload")) {
/*  47 */         if (!sender.hasPermission("shardplugin.admin")) {
/*  48 */           sender.sendMessage("§cYou don't have permission to reload the plugin!");
/*  49 */           return true;
/*     */         } 
/*     */ 
/*     */         
/*  53 */         this.plugin.getConfigManager().reloadConfig();
/*     */ 
/*     */         
/*  56 */         this.plugin.reloadManagers();
/*     */         
/*  58 */         sender.sendMessage("§aPlugin configuration reloaded successfully!");
/*  59 */         return true;
/*  60 */       }  if (args[0].equals("shards") && args.length > 1 && args[1].equals("give")) {
/*  61 */         if (!sender.hasPermission("shardplugin.admin")) {
/*  62 */           sender.sendMessage("§cYou don't have permission to give shards!");
/*  63 */           return true;
/*     */         } 
/*     */         
/*  66 */         if (args.length != 4) {
/*  67 */           sender.sendMessage("§cUsage: /armorshop shards give <player> <amount>");
/*  68 */           return true;
/*     */         } 
/*     */         
/*  71 */         Player target = Bukkit.getPlayer(args[2]);
/*  72 */         if (target == null) {
/*  73 */           sender.sendMessage("§cPlayer not found!");
/*  74 */           return true;
/*     */         } 
/*     */         
/*     */         try {
/*  78 */           int amount = Integer.parseInt(args[3]);
/*  79 */           if (amount <= 0) {
/*  80 */             sender.sendMessage("§cAmount must be greater than 0!");
/*  81 */             return true;
/*     */           } 
/*     */           
/*  84 */           this.plugin.getShardManager().addShards(target, amount);
/*  85 */           sender.sendMessage("§aGave " + amount + " shards to " + target.getName());
/*  86 */           target.sendMessage("§aYou received " + amount + " shards!");
/*  87 */           return true;
/*  88 */         } catch (NumberFormatException e) {
/*  89 */           sender.sendMessage("§cInvalid amount!");
/*  90 */           return true;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/*  95 */     if (!(sender instanceof Player)) {
/*  96 */       sender.sendMessage("§cThis command can only be used by players!");
/*  97 */       return true;
/*     */     } 
/*     */     
/* 100 */     (new ArmorShopGUI(this.plugin, (Player)sender)).open();
/* 101 */     return true;
/*     */   }
/*     */   
/*     */   private boolean handleGiveShardsCommand(CommandSender sender, String[] args) {
/* 105 */     if (!sender.hasPermission("shardplugin.admin")) {
/* 106 */       sender.sendMessage("§cYou don't have permission to use this command!");
/* 107 */       return true;
/*     */     } 
/*     */     
/* 110 */     if (args.length != 2) {
/* 111 */       sender.sendMessage("§cUsage: /giveshards <player> <amount>");
/* 112 */       return true;
/*     */     } 
/*     */     
/* 115 */     Player target = Bukkit.getPlayer(args[0]);
/*     */     
/* 117 */     if (target == null) {
/* 118 */       sender.sendMessage("§cPlayer not found!");
/* 119 */       return true;
/*     */     } 
/*     */     
/*     */     try {
/* 123 */       int amount = Integer.parseInt(args[1]);
/* 124 */       this.plugin.getShardManager().addShards(target, amount);
/* 125 */       sender.sendMessage("§aGave " + amount + " shards to " + target.getName());
/* 126 */       target.sendMessage("§aYou received " + amount + " shards!");
/* 127 */       return true;
/* 128 */     } catch (NumberFormatException e) {
/* 129 */       sender.sendMessage("§cInvalid amount!");
/* 130 */       return true;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Jacob\Desktop\original-ShardPlugin-1.0-SNAPSHOT.jar!\com\example\shardplugin\commands\ShardCommands.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */