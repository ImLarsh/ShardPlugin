/*     */ package com.example.shardplugin.managers;
/*     */ 
/*     */ import com.example.shardplugin.ShardPlugin;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.NamespacedKey;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ import org.bukkit.persistence.PersistentDataContainer;
/*     */ import org.bukkit.persistence.PersistentDataType;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public class PickaxeManager {
/*     */   private final ShardPlugin plugin;
/*     */   private final Map<String, Double> pickaxeMultipliers;
/*     */   private final Map<String, Double> pickaxeRolls;
/*     */   private final NamespacedKey pickaxeKey;
/*     */   private final NamespacedKey multiplierKey;
/*     */   private final NamespacedKey rollsKey;
/*     */   
/*     */   public PickaxeManager(ShardPlugin plugin) {
/*  25 */     this.plugin = plugin;
/*  26 */     this.pickaxeMultipliers = new HashMap<>();
/*  27 */     this.pickaxeRolls = new HashMap<>();
/*  28 */     this.pickaxeKey = new NamespacedKey((Plugin)plugin, "pickaxe");
/*  29 */     this.multiplierKey = new NamespacedKey((Plugin)plugin, "multiplier");
/*  30 */     this.rollsKey = new NamespacedKey((Plugin)plugin, "rolls");
/*  31 */     loadPickaxes();
/*     */   }
/*     */   
/*     */   private void loadPickaxes() {
/*  35 */     ConfigurationSection pickaxeSection = this.plugin.getConfig().getConfigurationSection("pickaxes");
/*     */     
/*  37 */     if (pickaxeSection != null) {
/*  38 */       for (String key : pickaxeSection.getKeys(false)) {
/*  39 */         double multiplier = pickaxeSection.getDouble(key + ".multiplier", 1.0D);
/*  40 */         double rolls = pickaxeSection.getDouble(key + ".rolls", 1.0D);
/*  41 */         this.pickaxeMultipliers.put(key, Double.valueOf(multiplier));
/*  42 */         this.pickaxeRolls.put(key, Double.valueOf(rolls));
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public double getPickaxeMultiplier(Player player) {
/*  48 */     double multiplier = 1.0D;
/*  49 */     ItemStack mainHand = player.getInventory().getItemInMainHand();
/*     */     
/*  51 */     if (mainHand != null && mainHand.hasItemMeta()) {
/*  52 */       PersistentDataContainer container = mainHand.getItemMeta().getPersistentDataContainer();
/*  53 */       String pickaxeName = (String)container.get(this.pickaxeKey, PersistentDataType.STRING);
/*     */       
/*  55 */       if (pickaxeName != null) {
/*  56 */         multiplier = ((Double)this.pickaxeMultipliers.getOrDefault(pickaxeName, Double.valueOf(1.0D))).doubleValue();
/*     */       }
/*     */     } 
/*     */     
/*  60 */     return multiplier;
/*     */   }
/*     */   
/*     */   public double getPickaxeRolls(Player player) {
/*  64 */     double rolls = 0.0D;
/*  65 */     ItemStack mainHand = player.getInventory().getItemInMainHand();
/*     */     
/*  67 */     if (mainHand != null && mainHand.hasItemMeta()) {
/*  68 */       PersistentDataContainer container = mainHand.getItemMeta().getPersistentDataContainer();
/*  69 */       String pickaxeName = (String)container.get(this.pickaxeKey, PersistentDataType.STRING);
/*     */       
/*  71 */       if (pickaxeName != null) {
/*  72 */         rolls = ((Double)this.pickaxeRolls.getOrDefault(pickaxeName, Double.valueOf(1.0D))).doubleValue();
/*     */       }
/*     */     } 
/*     */     
/*  76 */     return rolls;
/*     */   }
/*     */   
/*     */   public ItemStack createPickaxe(String pickaxeName) {
/*  80 */     ConfigurationSection pickaxeSection = this.plugin.getConfig().getConfigurationSection("pickaxes." + pickaxeName);
/*     */     
/*  82 */     if (pickaxeSection != null) {
/*  83 */       ConfigurationSection itemSection = pickaxeSection.getConfigurationSection("item");
/*  84 */       if (itemSection != null) {
/*  85 */         Material material = Material.valueOf(itemSection.getString("material", "IRON_PICKAXE"));
/*  86 */         ItemStack pickaxe = new ItemStack(material);
/*  87 */         ItemMeta meta = pickaxe.getItemMeta();
/*     */         
/*  89 */         if (meta != null) {
/*     */           
/*  91 */           String displayName = itemSection.getString("display_name", pickaxeSection.getString("name", pickaxeName));
/*  92 */           meta.setDisplayName(displayName);
/*     */ 
/*     */           
/*  95 */           if (itemSection.contains("custom_model_data")) {
/*  96 */             meta.setCustomModelData(Integer.valueOf(itemSection.getInt("custom_model_data")));
/*     */           }
/*     */ 
/*     */           
/* 100 */           if (itemSection.contains("lore")) {
/* 101 */             meta.setLore(itemSection.getStringList("lore"));
/*     */           }
/*     */ 
/*     */           
/* 105 */           PersistentDataContainer container = meta.getPersistentDataContainer();
/* 106 */           container.set(this.pickaxeKey, PersistentDataType.STRING, pickaxeName);
/* 107 */           container.set(this.multiplierKey, PersistentDataType.DOUBLE, Double.valueOf(pickaxeSection.getDouble("multiplier", 1.0D)));
/* 108 */           container.set(this.rollsKey, PersistentDataType.DOUBLE, Double.valueOf(pickaxeSection.getDouble("rolls", 1.0D)));
/*     */           
/* 110 */           pickaxe.setItemMeta(meta);
/* 111 */           return pickaxe;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 116 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\Jacob\Desktop\original-ShardPlugin-1.0-SNAPSHOT.jar!\com\example\shardplugin\managers\PickaxeManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */