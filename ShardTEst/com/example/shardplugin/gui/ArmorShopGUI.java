/*     */ package com.example.shardplugin.gui;
/*     */ 
/*     */ import com.example.shardplugin.ShardPlugin;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Color;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.NamespacedKey;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.inventory.InventoryClickEvent;
/*     */ import org.bukkit.inventory.Inventory;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ import org.bukkit.inventory.meta.LeatherArmorMeta;
/*     */ import org.bukkit.persistence.PersistentDataContainer;
/*     */ import org.bukkit.persistence.PersistentDataType;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ArmorShopGUI
/*     */ {
/*     */   private final ShardPlugin plugin;
/*     */   private final Player player;
/*     */   private Inventory inventory;
/*     */   private int currentPage;
/*     */   
/*     */   public ArmorShopGUI(ShardPlugin plugin, Player player) {
/*  35 */     this.plugin = plugin;
/*  36 */     this.player = player;
/*  37 */     this.currentPage = 1;
/*  38 */     this.slotToArmorSet = new HashMap<>();
/*  39 */     this.slotToPickaxe = new HashMap<>();
/*     */     
/*  41 */     ConfigurationSection guiConfig = this.plugin.getConfig().getConfigurationSection("settings.gui");
/*  42 */     this.enchantedShardSlot = guiConfig.getInt("enchanted_shard.slot", 4);
/*     */     
/*  44 */     int maxPageFound = 1;
/*  45 */     ConfigurationSection armorSection = this.plugin.getConfig().getConfigurationSection("armor_sets");
/*  46 */     ConfigurationSection pickaxeSection = this.plugin.getConfig().getConfigurationSection("pickaxes");
/*     */     
/*  48 */     if (armorSection != null) {
/*  49 */       for (String key : armorSection.getKeys(false)) {
/*  50 */         int page = armorSection.getInt(key + ".page", 1);
/*  51 */         maxPageFound = Math.max(maxPageFound, page);
/*     */       } 
/*     */     }
/*     */     
/*  55 */     if (pickaxeSection != null) {
/*  56 */       for (String key : pickaxeSection.getKeys(false)) {
/*  57 */         int page = pickaxeSection.getInt(key + ".page", 1);
/*  58 */         maxPageFound = Math.max(maxPageFound, page);
/*     */       } 
/*     */     }
/*     */     
/*  62 */     this.maxPage = maxPageFound;
/*  63 */     createInventory();
/*     */   }
/*     */   private final int maxPage; private final Map<Integer, String> slotToArmorSet; private final Map<Integer, String> slotToPickaxe; private final int enchantedShardSlot;
/*     */   private void createInventory() {
/*  67 */     ConfigurationSection guiConfig = this.plugin.getConfig().getConfigurationSection("settings.gui");
/*     */     
/*  69 */     String title = guiConfig.getString("title", "Equipment Shop - Page %page%").replace("%page%", String.valueOf(this.currentPage));
/*  70 */     int size = guiConfig.getInt("size", 54);
/*     */     
/*  72 */     this.inventory = Bukkit.createInventory(null, size, title);
/*  73 */     setupGUI();
/*     */   }
/*     */   
/*     */   private void setupGUI() {
/*  77 */     this.slotToArmorSet.clear();
/*  78 */     this.slotToPickaxe.clear();
/*     */ 
/*     */     
/*  81 */     ConfigurationSection fillerConfig = this.plugin.getConfig().getConfigurationSection("settings.gui.filler");
/*  82 */     if (fillerConfig != null) {
/*  83 */       ItemStack filler = new ItemStack(Material.valueOf(fillerConfig.getString("material", "GRAY_STAINED_GLASS_PANE")));
/*  84 */       ItemMeta fillerMeta = filler.getItemMeta();
/*  85 */       if (fillerMeta != null) {
/*  86 */         fillerMeta.setDisplayName(fillerConfig.getString("name", " "));
/*  87 */         filler.setItemMeta(fillerMeta);
/*     */         
/*  89 */         for (int i = 0; i < this.inventory.getSize(); i++) {
/*  90 */           this.inventory.setItem(i, filler.clone());
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  96 */     addEnchantedShardOption();
/*     */ 
/*     */     
/*  99 */     ConfigurationSection armorSection = this.plugin.getConfig().getConfigurationSection("armor_sets");
/* 100 */     if (armorSection != null) {
/* 101 */       for (String setName : armorSection.getKeys(false)) {
/* 102 */         int page = armorSection.getInt(setName + ".page", 1);
/* 103 */         if (page == this.currentPage) {
/* 104 */           addArmorSet(setName);
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 110 */     ConfigurationSection pickaxeSection = this.plugin.getConfig().getConfigurationSection("pickaxes");
/* 111 */     if (pickaxeSection != null) {
/* 112 */       for (String pickaxeName : pickaxeSection.getKeys(false)) {
/* 113 */         int page = pickaxeSection.getInt(pickaxeName + ".page", 1);
/* 114 */         if (page == this.currentPage) {
/* 115 */           addPickaxe(pickaxeName);
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 121 */     setupNavigation();
/*     */   }
/*     */   
/*     */   private void addEnchantedShardOption() {
/* 125 */     ConfigurationSection enchantedConfig = this.plugin.getConfig().getConfigurationSection("settings.gui.enchanted_shard");
/* 126 */     if (enchantedConfig == null)
/*     */       return; 
/* 128 */     Material material = Material.valueOf(enchantedConfig.getString("material", "AMETHYST_SHARD"));
/* 129 */     ItemStack enchantedShard = new ItemStack(material);
/* 130 */     ItemMeta meta = enchantedShard.getItemMeta();
/*     */     
/* 132 */     if (meta != null) {
/* 133 */       meta.setDisplayName(enchantedConfig.getString("name", "§d✨ Enchanted Shard"));
/* 134 */       meta.setCustomModelData(Integer.valueOf(enchantedConfig.getInt("custom_model_data", 1002)));
/*     */       
/* 136 */       List<String> lore = enchantedConfig.getStringList("lore");
/* 137 */       meta.setLore(lore);
/*     */       
/* 139 */       enchantedShard.setItemMeta(meta);
/* 140 */       this.inventory.setItem(this.enchantedShardSlot, enchantedShard);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void setupNavigation() {
/* 145 */     ConfigurationSection navConfig = this.plugin.getConfig().getConfigurationSection("settings.gui.navigation");
/* 146 */     if (navConfig == null) {
/*     */       return;
/*     */     }
/* 149 */     if (this.currentPage > 1) {
/* 150 */       ConfigurationSection prevConfig = navConfig.getConfigurationSection("previous_page");
/* 151 */       if (prevConfig != null) {
/* 152 */         ItemStack prevButton = new ItemStack(Material.valueOf(prevConfig.getString("material", "ARROW")));
/* 153 */         ItemMeta prevMeta = prevButton.getItemMeta();
/* 154 */         if (prevMeta != null) {
/* 155 */           prevMeta.setDisplayName(prevConfig.getString("name", "§e← Previous Page"));
/* 156 */           prevButton.setItemMeta(prevMeta);
/* 157 */           this.inventory.setItem(prevConfig.getInt("slot", 45), prevButton);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 163 */     if (this.currentPage < this.maxPage) {
/* 164 */       ConfigurationSection nextConfig = navConfig.getConfigurationSection("next_page");
/* 165 */       if (nextConfig != null) {
/* 166 */         ItemStack nextButton = new ItemStack(Material.valueOf(nextConfig.getString("material", "ARROW")));
/* 167 */         ItemMeta nextMeta = nextButton.getItemMeta();
/* 168 */         if (nextMeta != null) {
/* 169 */           nextMeta.setDisplayName(nextConfig.getString("name", "§e→ Next Page"));
/* 170 */           nextButton.setItemMeta(nextMeta);
/* 171 */           this.inventory.setItem(nextConfig.getInt("slot", 53), nextButton);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 177 */     ConfigurationSection infoConfig = navConfig.getConfigurationSection("info");
/* 178 */     if (infoConfig != null) {
/* 179 */       ItemStack infoButton = new ItemStack(Material.valueOf(infoConfig.getString("material", "BOOK")));
/* 180 */       ItemMeta infoMeta = infoButton.getItemMeta();
/* 181 */       if (infoMeta != null) {
/* 182 */         infoMeta.setDisplayName(infoConfig
/* 183 */             .getString("name", "§ePage %page%/%total%")
/* 184 */             .replace("%page%", String.valueOf(this.currentPage))
/* 185 */             .replace("%total%", String.valueOf(this.maxPage)));
/*     */         
/* 187 */         infoButton.setItemMeta(infoMeta);
/* 188 */         this.inventory.setItem(infoConfig.getInt("slot", 49), infoButton);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addArmorSet(String setName) {
/* 194 */     ConfigurationSection setSection = this.plugin.getConfig().getConfigurationSection("armor_sets." + setName);
/* 195 */     if (setSection == null)
/*     */       return; 
/* 197 */     ConfigurationSection guiSection = setSection.getConfigurationSection("gui");
/* 198 */     ConfigurationSection costs = setSection.getConfigurationSection("cost");
/* 199 */     ConfigurationSection enchantedCosts = setSection.getConfigurationSection("cost_enchanted");
/* 200 */     ConfigurationSection slots = setSection.getConfigurationSection("slots");
/* 201 */     ConfigurationSection pieces = setSection.getConfigurationSection("pieces");
/*     */     
/* 203 */     if (guiSection != null && slots != null && pieces != null && (costs != null || enchantedCosts != null)) {
/* 204 */       addArmorPiece(setName, "helmet", guiSection, costs, enchantedCosts, slots, pieces);
/* 205 */       addArmorPiece(setName, "chestplate", guiSection, costs, enchantedCosts, slots, pieces);
/* 206 */       addArmorPiece(setName, "leggings", guiSection, costs, enchantedCosts, slots, pieces);
/* 207 */       addArmorPiece(setName, "boots", guiSection, costs, enchantedCosts, slots, pieces);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addArmorPiece(String setName, String type, ConfigurationSection guiSection, ConfigurationSection costs, ConfigurationSection enchantedCosts, ConfigurationSection slots, ConfigurationSection pieces) {
/* 217 */     ConfigurationSection pieceSection = guiSection.getConfigurationSection(type);
/* 218 */     ConfigurationSection pieceStats = pieces.getConfigurationSection(type);
/* 219 */     if (pieceSection == null || pieceStats == null)
/*     */       return; 
/* 221 */     int slot = slots.getInt(type, -1);
/* 222 */     if (slot < 0 || slot >= this.inventory.getSize())
/*     */       return; 
/* 224 */     Material material = Material.valueOf(pieceSection.getString("material", "DIAMOND_" + type.toUpperCase()));
/* 225 */     ItemStack item = new ItemStack(material);
/* 226 */     ItemMeta meta = item.getItemMeta();
/* 227 */     if (meta == null)
/*     */       return; 
/* 229 */     String displayName = pieceSection.getString("display_name", "§6" + setName + " " + type);
/* 230 */     meta.setDisplayName(displayName);
/*     */     
/* 232 */     List<String> lore = new ArrayList<>();
/*     */ 
/*     */     
/* 235 */     if (enchantedCosts != null && enchantedCosts.contains(type)) {
/* 236 */       int enchantedCost = enchantedCosts.getInt(type, 1);
/* 237 */       lore.add("§d§lCost: §5✨ " + enchantedCost + " Enchanted Shards");
/* 238 */     } else if (costs != null && costs.contains(type)) {
/* 239 */       int cost = costs.getInt(type, 100);
/* 240 */       lore.add("§7Cost: §e" + cost + " shards");
/*     */     } 
/*     */     
/* 243 */     lore.add("");
/*     */ 
/*     */     
/* 246 */     String requiredSet = this.plugin.getConfig().getConfigurationSection("armor_sets." + setName).getString("requires");
/* 247 */     if (requiredSet != null) {
/* 248 */       lore.add("§c§lRequires: §7" + requiredSet + " " + type);
/* 249 */       lore.add("");
/*     */     } 
/*     */     
/* 252 */     if (pieceSection.contains("lore")) {
/* 253 */       lore.addAll(pieceSection.getStringList("lore"));
/* 254 */       lore.add("");
/*     */     } 
/*     */ 
/*     */     
/* 258 */     double multiplier = pieceStats.getDouble("multiplier", 1.0D);
/* 259 */     int rolls = pieceStats.getInt("rolls", 1);
/* 260 */     lore.add("§6Piece Bonus:");
/* 261 */     lore.add("§e+" + (int)((multiplier - 1.0D) * 100.0D) + "% Shard Drop Rate");
/* 262 */     if (rolls > 1) {
/* 263 */       lore.add("§e+" + rolls - 1 + " Extra Roll" + ((rolls > 2) ? "s" : ""));
/*     */     }
/* 265 */     lore.add("");
/* 266 */     lore.add("§eClick to purchase!");
/*     */     
/* 268 */     meta.setLore(lore);
/*     */     
/* 270 */     if (pieceSection.contains("custom_model_data")) {
/* 271 */       meta.setCustomModelData(Integer.valueOf(pieceSection.getInt("custom_model_data")));
/*     */     }
/*     */     
/* 274 */     if (meta instanceof LeatherArmorMeta && pieceSection.contains("color")) {
/* 275 */       ConfigurationSection colorSection = pieceSection.getConfigurationSection("color");
/* 276 */       if (colorSection != null) {
/* 277 */         int red = colorSection.getInt("red", 0);
/* 278 */         int green = colorSection.getInt("green", 0);
/* 279 */         int blue = colorSection.getInt("blue", 0);
/* 280 */         ((LeatherArmorMeta)meta).setColor(Color.fromRGB(red, green, blue));
/*     */       } 
/*     */     } 
/*     */     
/* 284 */     item.setItemMeta(meta);
/* 285 */     this.inventory.setItem(slot, item);
/* 286 */     this.slotToArmorSet.put(Integer.valueOf(slot), setName + ":" + setName);
/*     */   }
/*     */   
/*     */   private void addPickaxe(String pickaxeName) {
/* 290 */     ConfigurationSection pickaxeSection = this.plugin.getConfig().getConfigurationSection("pickaxes." + pickaxeName);
/* 291 */     if (pickaxeSection == null)
/*     */       return; 
/* 293 */     int slot = pickaxeSection.getInt("slot", -1);
/* 294 */     if (slot < 0 || slot >= this.inventory.getSize())
/*     */       return; 
/* 296 */     ConfigurationSection guiSection = pickaxeSection.getConfigurationSection("gui");
/* 297 */     if (guiSection == null)
/*     */       return; 
/* 299 */     Material material = Material.valueOf(guiSection.getString("material", "IRON_PICKAXE"));
/* 300 */     ItemStack pickaxe = new ItemStack(material);
/* 301 */     ItemMeta meta = pickaxe.getItemMeta();
/* 302 */     if (meta == null)
/*     */       return; 
/* 304 */     String displayName = guiSection.getString("display_name", "§6" + pickaxeName);
/* 305 */     meta.setDisplayName(displayName);
/*     */     
/* 307 */     List<String> lore = new ArrayList<>();
/* 308 */     int cost = pickaxeSection.getInt("cost", 100);
/* 309 */     lore.add("§7Cost: §e" + cost + " shards");
/* 310 */     lore.add("");
/*     */ 
/*     */     
/* 313 */     String requiredPickaxe = pickaxeSection.getString("requires");
/* 314 */     if (requiredPickaxe != null) {
/* 315 */       lore.add("§c§lRequires: §7" + requiredPickaxe);
/* 316 */       lore.add("");
/*     */     } 
/*     */     
/* 319 */     if (guiSection.contains("lore")) {
/* 320 */       lore.addAll(guiSection.getStringList("lore"));
/*     */     }
/*     */     
/* 323 */     lore.add("");
/* 324 */     lore.add("§eClick to purchase!");
/*     */     
/* 326 */     meta.setLore(lore);
/*     */     
/* 328 */     if (guiSection.contains("custom_model_data")) {
/* 329 */       meta.setCustomModelData(Integer.valueOf(guiSection.getInt("custom_model_data")));
/*     */     }
/*     */     
/* 332 */     pickaxe.setItemMeta(meta);
/* 333 */     this.inventory.setItem(slot, pickaxe);
/* 334 */     this.slotToPickaxe.put(Integer.valueOf(slot), pickaxeName);
/*     */   }
/*     */   
/*     */   private boolean hasRequiredArmorPiece(String requiredSet, String type) {
/* 338 */     if (requiredSet == null) return true;
/*     */     
/* 340 */     ItemStack[] inventory = this.player.getInventory().getContents();
/* 341 */     NamespacedKey armorSetKey = new NamespacedKey((Plugin)this.plugin, "armor_set");
/*     */     
/* 343 */     for (ItemStack item : inventory) {
/* 344 */       if (item != null && item.hasItemMeta()) {
/*     */         
/* 346 */         ItemMeta meta = item.getItemMeta();
/* 347 */         PersistentDataContainer container = meta.getPersistentDataContainer();
/* 348 */         String setName = (String)container.get(armorSetKey, PersistentDataType.STRING);
/*     */         
/* 350 */         if (setName != null && setName.equals(requiredSet)) {
/* 351 */           String itemType = item.getType().name().toLowerCase();
/* 352 */           if (itemType.contains(type.toLowerCase())) {
/* 353 */             return true;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 358 */     return false;
/*     */   }
/*     */   
/*     */   private boolean hasRequiredPickaxe(String requiredPickaxe) {
/* 362 */     if (requiredPickaxe == null) return true;
/*     */     
/* 364 */     ItemStack[] inventory = this.player.getInventory().getContents();
/* 365 */     NamespacedKey pickaxeKey = new NamespacedKey((Plugin)this.plugin, "pickaxe");
/*     */     
/* 367 */     for (ItemStack item : inventory) {
/* 368 */       if (item != null && item.hasItemMeta()) {
/*     */         
/* 370 */         ItemMeta meta = item.getItemMeta();
/* 371 */         PersistentDataContainer container = meta.getPersistentDataContainer();
/* 372 */         String pickaxeName = (String)container.get(pickaxeKey, PersistentDataType.STRING);
/*     */         
/* 374 */         if (pickaxeName != null && pickaxeName.equals(requiredPickaxe)) {
/* 375 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 379 */     return false;
/*     */   }
/*     */   
/*     */   private void removeRequiredArmorPiece(String requiredSet, String type) {
/* 383 */     if (requiredSet == null)
/*     */       return; 
/* 385 */     ItemStack[] inventory = this.player.getInventory().getContents();
/* 386 */     NamespacedKey armorSetKey = new NamespacedKey((Plugin)this.plugin, "armor_set");
/*     */     
/* 388 */     for (int i = 0; i < inventory.length; i++) {
/* 389 */       ItemStack item = inventory[i];
/* 390 */       if (item != null && item.hasItemMeta()) {
/*     */         
/* 392 */         ItemMeta meta = item.getItemMeta();
/* 393 */         PersistentDataContainer container = meta.getPersistentDataContainer();
/* 394 */         String setName = (String)container.get(armorSetKey, PersistentDataType.STRING);
/*     */         
/* 396 */         if (setName != null && setName.equals(requiredSet)) {
/* 397 */           String itemType = item.getType().name().toLowerCase();
/* 398 */           if (itemType.contains(type.toLowerCase())) {
/* 399 */             this.player.getInventory().setItem(i, null);
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   private void removeRequiredPickaxe(String requiredPickaxe) {
/* 407 */     if (requiredPickaxe == null)
/*     */       return; 
/* 409 */     ItemStack[] inventory = this.player.getInventory().getContents();
/* 410 */     NamespacedKey pickaxeKey = new NamespacedKey((Plugin)this.plugin, "pickaxe");
/*     */     
/* 412 */     for (int i = 0; i < inventory.length; i++) {
/* 413 */       ItemStack item = inventory[i];
/* 414 */       if (item != null && item.hasItemMeta()) {
/*     */         
/* 416 */         ItemMeta meta = item.getItemMeta();
/* 417 */         PersistentDataContainer container = meta.getPersistentDataContainer();
/* 418 */         String pickaxeName = (String)container.get(pickaxeKey, PersistentDataType.STRING);
/*     */         
/* 420 */         if (pickaxeName != null && pickaxeName.equals(requiredPickaxe)) {
/* 421 */           this.player.getInventory().setItem(i, null);
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   public void handleClick(InventoryClickEvent event) {
/* 428 */     event.setCancelled(true);
/*     */     
/* 430 */     ItemStack clicked = event.getCurrentItem();
/* 431 */     if (clicked == null || !clicked.hasItemMeta())
/*     */       return; 
/* 433 */     int slot = event.getRawSlot();
/* 434 */     if (slot >= this.inventory.getSize()) {
/*     */       return;
/*     */     }
/* 437 */     if (slot == this.enchantedShardSlot) {
/* 438 */       handleEnchantedShardConversion();
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 443 */     ConfigurationSection navConfig = this.plugin.getConfig().getConfigurationSection("settings.gui.navigation");
/* 444 */     if (navConfig != null) {
/* 445 */       int prevSlot = navConfig.getInt("previous_page.slot", 45);
/* 446 */       int nextSlot = navConfig.getInt("next_page.slot", 53);
/*     */       
/* 448 */       if (slot == prevSlot && this.currentPage > 1) {
/* 449 */         this.currentPage--;
/* 450 */         setupGUI();
/*     */         
/*     */         return;
/*     */       } 
/* 454 */       if (slot == nextSlot && this.currentPage < this.maxPage) {
/* 455 */         this.currentPage++;
/* 456 */         setupGUI();
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     
/* 462 */     ItemMeta meta = clicked.getItemMeta();
/* 463 */     if (!meta.hasLore()) {
/*     */       return;
/*     */     }
/* 466 */     List<String> lore = meta.getLore();
/* 467 */     if (lore == null || lore.isEmpty())
/*     */       return; 
/*     */     try {
/* 470 */       String costText = ((String)lore.get(0)).replaceAll("§[0-9a-fklmnor]", "");
/* 471 */       boolean isEnchantedShardCost = costText.contains("Enchanted Shards");
/*     */       
/* 473 */       Pattern pattern = Pattern.compile("\\d+");
/* 474 */       Matcher matcher = pattern.matcher(costText);
/* 475 */       if (!matcher.find()) {
/* 476 */         this.player.sendMessage("§cFailed to parse item cost!");
/*     */         
/*     */         return;
/*     */       } 
/* 480 */       int cost = Integer.parseInt(matcher.group());
/*     */ 
/*     */       
/* 483 */       String armorInfo = this.slotToArmorSet.get(Integer.valueOf(slot));
/* 484 */       if (armorInfo != null) {
/* 485 */         String[] parts = armorInfo.split(":");
/* 486 */         String setName = parts[0];
/* 487 */         String type = parts[1];
/*     */ 
/*     */         
/* 490 */         ConfigurationSection armorSection = this.plugin.getConfig().getConfigurationSection("armor_sets." + setName);
/* 491 */         String requiredSet = armorSection.getString("requires");
/*     */         
/* 493 */         if (!hasRequiredArmorPiece(requiredSet, type)) {
/* 494 */           this.player.sendMessage("§cYou need the " + requiredSet + " " + type + " to purchase this!");
/*     */           
/*     */           return;
/*     */         } 
/* 498 */         if (isEnchantedShardCost) {
/* 499 */           int playerEnchantedShards = this.plugin.getShardManager().getEnchantedShards(this.player);
/* 500 */           if (playerEnchantedShards < cost) {
/* 501 */             this.player.sendMessage("§cYou don't have enough enchanted shards!");
/*     */             return;
/*     */           } 
/*     */         } else {
/* 505 */           int playerShards = this.plugin.getShardManager().getShards(this.player);
/* 506 */           if (playerShards < cost) {
/* 507 */             this.player.sendMessage("§cYou don't have enough shards!");
/*     */             
/*     */             return;
/*     */           } 
/*     */         } 
/* 512 */         ItemStack armor = this.plugin.getArmorManager().createArmorPiece(setName, clicked.getType());
/* 513 */         if (armor != null) {
/*     */           
/* 515 */           removeRequiredArmorPiece(requiredSet, type);
/*     */ 
/*     */           
/* 518 */           if (isEnchantedShardCost) {
/* 519 */             this.plugin.getShardManager().removeEnchantedShards(this.player, cost);
/*     */           } else {
/* 521 */             this.plugin.getShardManager().removeShards(this.player, cost);
/*     */           } 
/* 523 */           this.player.getInventory().addItem(new ItemStack[] { armor });
/* 524 */           this.player.sendMessage("§aYou purchased " + clicked.getItemMeta().getDisplayName() + "§a!");
/*     */         } 
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 530 */       String pickaxeName = this.slotToPickaxe.get(Integer.valueOf(slot));
/* 531 */       if (pickaxeName != null) {
/*     */         
/* 533 */         ConfigurationSection pickaxeSection = this.plugin.getConfig().getConfigurationSection("pickaxes." + pickaxeName);
/* 534 */         String requiredPickaxe = pickaxeSection.getString("requires");
/*     */         
/* 536 */         if (!hasRequiredPickaxe(requiredPickaxe)) {
/* 537 */           this.player.sendMessage("§cYou need the " + requiredPickaxe + " pickaxe to purchase this pickaxe!");
/*     */           
/*     */           return;
/*     */         } 
/* 541 */         if (isEnchantedShardCost) {
/* 542 */           int playerEnchantedShards = this.plugin.getShardManager().getEnchantedShards(this.player);
/* 543 */           if (playerEnchantedShards < cost) {
/* 544 */             this.player.sendMessage("§cYou don't have enough enchanted shards!");
/*     */             return;
/*     */           } 
/*     */         } else {
/* 548 */           int playerShards = this.plugin.getShardManager().getShards(this.player);
/* 549 */           if (playerShards < cost) {
/* 550 */             this.player.sendMessage("§cYou don't have enough shards!");
/*     */             
/*     */             return;
/*     */           } 
/*     */         } 
/* 555 */         ItemStack pickaxe = this.plugin.getPickaxeManager().createPickaxe(pickaxeName);
/* 556 */         if (pickaxe != null) {
/*     */           
/* 558 */           removeRequiredPickaxe(requiredPickaxe);
/*     */ 
/*     */           
/* 561 */           if (isEnchantedShardCost) {
/* 562 */             this.plugin.getShardManager().removeEnchantedShards(this.player, cost);
/*     */           } else {
/* 564 */             this.plugin.getShardManager().removeShards(this.player, cost);
/*     */           } 
/* 566 */           this.player.getInventory().addItem(new ItemStack[] { pickaxe });
/* 567 */           this.player.sendMessage("§aYou purchased " + clicked.getItemMeta().getDisplayName() + "§a!");
/*     */         } 
/*     */       } 
/* 570 */     } catch (Exception e) {
/* 571 */       this.player.sendMessage("§cAn error occurred while processing your purchase.");
/* 572 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void handleEnchantedShardConversion() {
/* 577 */     ConfigurationSection currencyConfig = this.plugin.getConfig().getConfigurationSection("settings.currency.enchanted");
/* 578 */     if (currencyConfig == null)
/*     */       return; 
/* 580 */     int conversionRate = currencyConfig.getInt("conversion_rate", 10);
/* 581 */     int playerShards = this.plugin.getShardManager().getShards(this.player);
/*     */     
/* 583 */     if (playerShards < conversionRate) {
/* 584 */       this.player.sendMessage("§cYou need at least " + conversionRate + " regular shards to convert to an enchanted shard!");
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 589 */     this.plugin.getShardManager().removeShards(this.player, conversionRate);
/* 590 */     this.plugin.getShardManager().addEnchantedShards(this.player, 1);
/*     */     
/* 592 */     this.player.sendMessage(this.plugin
/* 593 */         .getConfig()
/* 594 */         .getString("settings.messages.enchanted_shard_purchase", "§aYou converted %regular% regular shards into %enchanted% enchanted shards!")
/* 595 */         .replace("%regular%", String.valueOf(conversionRate))
/* 596 */         .replace("%enchanted%", "1"));
/*     */   }
/*     */ 
/*     */   
/*     */   private int countShards() {
/* 601 */     return this.plugin.getShardManager().getShards(this.player);
/*     */   }
/*     */   
/*     */   private void removeShards(int amount) {
/* 605 */     this.plugin.getShardManager().removeShards(this.player, amount);
/*     */   }
/*     */   
/*     */   public void open() {
/* 609 */     this.plugin.getInventoryListener().registerGUI(this.player.getUniqueId(), this);
/* 610 */     this.player.openInventory(this.inventory);
/*     */   }
/*     */   
/*     */   public Inventory getInventory() {
/* 614 */     return this.inventory;
/*     */   }
/*     */ }


/* Location:              C:\Users\Jacob\Desktop\original-ShardPlugin-1.0-SNAPSHOT.jar!\com\example\shardplugin\gui\ArmorShopGUI.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */