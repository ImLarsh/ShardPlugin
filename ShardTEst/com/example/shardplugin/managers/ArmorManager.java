/*     */ package com.example.shardplugin.managers;
/*     */ 
/*     */ import com.example.shardplugin.ShardPlugin;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.bukkit.Color;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.NamespacedKey;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ import org.bukkit.inventory.meta.LeatherArmorMeta;
/*     */ import org.bukkit.persistence.PersistentDataContainer;
/*     */ import org.bukkit.persistence.PersistentDataType;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ArmorManager
/*     */ {
/*     */   private final ShardPlugin plugin;
/*     */   private final Map<String, Double> armorMultipliers;
/*     */   private final Map<String, Double> armorRolls;
/*     */   private final NamespacedKey armorSetKey;
/*     */   private final NamespacedKey multiplierKey;
/*     */   private final NamespacedKey rollsKey;
/*     */   
/*     */   public ArmorManager(ShardPlugin plugin) {
/*  30 */     this.plugin = plugin;
/*  31 */     this.armorMultipliers = new HashMap<>();
/*  32 */     this.armorRolls = new HashMap<>();
/*  33 */     this.armorSetKey = new NamespacedKey((Plugin)plugin, "armor_set");
/*  34 */     this.multiplierKey = new NamespacedKey((Plugin)plugin, "multiplier");
/*  35 */     this.rollsKey = new NamespacedKey((Plugin)plugin, "rolls");
/*  36 */     loadArmorSets();
/*     */   }
/*     */   
/*     */   private void loadArmorSets() {
/*  40 */     ConfigurationSection armorSection = this.plugin.getConfig().getConfigurationSection("armor_sets");
/*     */     
/*  42 */     if (armorSection != null) {
/*  43 */       for (String key : armorSection.getKeys(false)) {
/*  44 */         ConfigurationSection pieces = armorSection.getConfigurationSection(key + ".pieces");
/*  45 */         if (pieces != null) {
/*  46 */           for (String piece : pieces.getKeys(false)) {
/*  47 */             String fullKey = key + "." + key;
/*  48 */             double multiplier = pieces.getDouble(piece + ".multiplier", 1.0D);
/*  49 */             double rolls = pieces.getDouble(piece + ".rolls", 1.0D);
/*  50 */             this.armorMultipliers.put(fullKey, Double.valueOf(multiplier));
/*  51 */             this.armorRolls.put(fullKey, Double.valueOf(rolls));
/*     */           } 
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public double getArmorMultiplier(Player player) {
/*  59 */     double totalMultiplier = 1.0D;
/*  60 */     ItemStack[] armor = player.getInventory().getArmorContents();
/*     */     
/*  62 */     if (armor != null) {
/*  63 */       for (ItemStack piece : armor) {
/*  64 */         if (piece != null && piece.hasItemMeta()) {
/*  65 */           ItemMeta meta = piece.getItemMeta();
/*  66 */           PersistentDataContainer container = meta.getPersistentDataContainer();
/*  67 */           Double pieceMultiplier = (Double)container.get(this.multiplierKey, PersistentDataType.DOUBLE);
/*  68 */           if (pieceMultiplier != null) {
/*  69 */             totalMultiplier *= pieceMultiplier.doubleValue();
/*     */           }
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*  75 */     return totalMultiplier;
/*     */   }
/*     */   
/*     */   public double getArmorRolls(Player player) {
/*  79 */     double totalRolls = 0.0D;
/*  80 */     ItemStack[] armor = player.getInventory().getArmorContents();
/*     */     
/*  82 */     if (armor != null) {
/*  83 */       for (ItemStack piece : armor) {
/*  84 */         if (piece != null && piece.hasItemMeta()) {
/*  85 */           ItemMeta meta = piece.getItemMeta();
/*  86 */           PersistentDataContainer container = meta.getPersistentDataContainer();
/*  87 */           Double pieceRolls = (Double)container.get(this.rollsKey, PersistentDataType.DOUBLE);
/*  88 */           if (pieceRolls != null) {
/*  89 */             totalRolls += pieceRolls.doubleValue();
/*     */           }
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*  95 */     return Math.max(0.0D, totalRolls);
/*     */   }
/*     */   
/*     */   public ItemStack createArmorPiece(String setName, Material defaultMaterial) {
/*  99 */     ConfigurationSection setSection = this.plugin.getConfig().getConfigurationSection("armor_sets." + setName);
/*     */     
/* 101 */     if (setSection != null) {
/* 102 */       String armorType = "";
/* 103 */       if (defaultMaterial.name().contains("HELMET")) { armorType = "helmet"; }
/* 104 */       else if (defaultMaterial.name().contains("CHESTPLATE")) { armorType = "chestplate"; }
/* 105 */       else if (defaultMaterial.name().contains("LEGGINGS")) { armorType = "leggings"; }
/* 106 */       else if (defaultMaterial.name().contains("BOOTS")) { armorType = "boots"; }
/*     */       
/* 108 */       ConfigurationSection armorSection = setSection.getConfigurationSection("armor." + armorType);
/* 109 */       ConfigurationSection piecesSection = setSection.getConfigurationSection("pieces." + armorType);
/*     */       
/* 111 */       if (armorSection != null && piecesSection != null) {
/* 112 */         Material material = Material.valueOf(armorSection.getString("material", defaultMaterial.name()));
/* 113 */         ItemStack armorPiece = new ItemStack(material);
/* 114 */         ItemMeta meta = armorPiece.getItemMeta();
/*     */         
/* 116 */         if (meta != null) {
/*     */           
/* 118 */           String displayName = armorSection.getString("display_name", setSection.getString("name", setName));
/* 119 */           meta.setDisplayName(displayName);
/*     */ 
/*     */           
/* 122 */           if (armorSection.contains("custom_model_data")) {
/* 123 */             meta.setCustomModelData(Integer.valueOf(armorSection.getInt("custom_model_data")));
/*     */           }
/*     */ 
/*     */           
/* 127 */           if (armorSection.contains("lore")) {
/* 128 */             meta.setLore(armorSection.getStringList("lore"));
/*     */           }
/*     */ 
/*     */           
/* 132 */           if (meta instanceof LeatherArmorMeta && armorSection.contains("color")) {
/* 133 */             ConfigurationSection colorSection = armorSection.getConfigurationSection("color");
/* 134 */             if (colorSection != null) {
/* 135 */               int red = colorSection.getInt("red", 0);
/* 136 */               int green = colorSection.getInt("green", 0);
/* 137 */               int blue = colorSection.getInt("blue", 0);
/* 138 */               ((LeatherArmorMeta)meta).setColor(Color.fromRGB(red, green, blue));
/*     */             } 
/*     */           } 
/*     */ 
/*     */           
/* 143 */           PersistentDataContainer container = meta.getPersistentDataContainer();
/* 144 */           container.set(this.armorSetKey, PersistentDataType.STRING, setName);
/*     */ 
/*     */           
/* 147 */           container.set(this.multiplierKey, PersistentDataType.DOUBLE, Double.valueOf(piecesSection.getDouble("multiplier", 1.0D)));
/* 148 */           container.set(this.rollsKey, PersistentDataType.DOUBLE, Double.valueOf(piecesSection.getDouble("rolls", 1.0D)));
/*     */           
/* 150 */           armorPiece.setItemMeta(meta);
/* 151 */           return armorPiece;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 156 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\Jacob\Desktop\original-ShardPlugin-1.0-SNAPSHOT.jar!\com\example\shardplugin\managers\ArmorManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */