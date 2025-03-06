package com.example.shardplugin.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.Material;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import com.example.shardplugin.ShardPlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArmorShopGUI {
    private final ShardPlugin plugin;
    private final Player player;
    private Inventory inventory;
    private int currentPage;
    private final int maxPage;
    private final Map<Integer, String> slotToArmorSet;
    private final Map<Integer, String> slotToPickaxe;
    private final int enchantedShardSlot;

    public ArmorShopGUI(final ShardPlugin plugin, final Player player) {
        this.plugin = plugin;
        this.player = player;
        this.currentPage = 1;
        this.slotToArmorSet = new HashMap<>();
        this.slotToPickaxe = new HashMap<>();

        final ConfigurationSection guiConfig = this.plugin.getConfig().getConfigurationSection("settings.gui");
        this.enchantedShardSlot = guiConfig.getInt("enchanted_shard.slot", 4);

        int maxPageFound = 1;
        final ConfigurationSection armorSection = this.plugin.getConfig().getConfigurationSection("armor_sets");
        final ConfigurationSection pickaxeSection = this.plugin.getConfig().getConfigurationSection("pickaxes");

        if (armorSection != null) {
            for (final String key : armorSection.getKeys(false)) {
                final int page = armorSection.getInt(key + ".page", 1);
                maxPageFound = Math.max(maxPageFound, page);
            }
        }

        if (pickaxeSection != null) {
            for (final String key : pickaxeSection.getKeys(false)) {
                final int page = pickaxeSection.getInt(key + ".page", 1);
                maxPageFound = Math.max(maxPageFound, page);
            }
        }

        this.maxPage = maxPageFound;
        this.createInventory();
    }

    private void createInventory() {
        final ConfigurationSection guiConfig = this.plugin.getConfig().getConfigurationSection("settings.gui");
        final String title = guiConfig.getString("title", "Equipment Shop - Page %page%")
                .replace("%page%", String.valueOf(this.currentPage));
        final int size = guiConfig.getInt("size", 54);

        this.inventory = Bukkit.createInventory(null, size, title);
        this.setupGUI();
    }

    private void setupGUI() {
        this.slotToArmorSet.clear();
        this.slotToPickaxe.clear();

        // Fill GUI with filler items
        final ConfigurationSection fillerConfig = this.plugin.getConfig().getConfigurationSection("settings.gui.filler");
        if (fillerConfig != null) {
            final ItemStack filler = new ItemStack(Material.valueOf(fillerConfig.getString("material", "GRAY_STAINED_GLASS_PANE")));
            final ItemMeta fillerMeta = filler.getItemMeta();
            if (fillerMeta != null) {
                fillerMeta.setDisplayName(fillerConfig.getString("name", " "));
                filler.setItemMeta(fillerMeta);

                for (int i = 0; i < this.inventory.getSize(); i++) {
                    this.inventory.setItem(i, filler.clone());
                }
            }
        }

        // Add enchanted shard conversion option
        this.addEnchantedShardOption();

        // Add armor sets for current page
        final ConfigurationSection armorSection = this.plugin.getConfig().getConfigurationSection("armor_sets");
        if (armorSection != null) {
            for (final String setName : armorSection.getKeys(false)) {
                final int page = armorSection.getInt(setName + ".page", 1);
                if (page == this.currentPage) {
                    this.addArmorSet(setName);
                }
            }
        }

        // Add pickaxes for current page
        final ConfigurationSection pickaxeSection = this.plugin.getConfig().getConfigurationSection("pickaxes");
        if (pickaxeSection != null) {
            for (final String pickaxeName : pickaxeSection.getKeys(false)) {
                final int page = pickaxeSection.getInt(pickaxeName + ".page", 1);
                if (page == this.currentPage) {
                    this.addPickaxe(pickaxeName);
                }
            }
        }

        // Add navigation items
        this.setupNavigation();
    }

    private void addEnchantedShardOption() {
        final ConfigurationSection enchantedConfig = this.plugin.getConfig().getConfigurationSection("settings.gui.enchanted_shard");
        if (enchantedConfig == null) return;

        final Material material = Material.valueOf(enchantedConfig.getString("material", "AMETHYST_SHARD"));
        final ItemStack enchantedShard = new ItemStack(material);
        final ItemMeta meta = enchantedShard.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(enchantedConfig.getString("name", "§d✨ Enchanted Shard"));
            meta.setCustomModelData(enchantedConfig.getInt("custom_model_data", 1002));

            final List<String> lore = enchantedConfig.getStringList("lore");
            meta.setLore(lore);

            enchantedShard.setItemMeta(meta);
            this.inventory.setItem(this.enchantedShardSlot, enchantedShard);
        }
    }

    private void setupNavigation() {
        final ConfigurationSection navConfig = this.plugin.getConfig().getConfigurationSection("settings.gui.navigation");
        if (navConfig == null) return;

        // Previous page button
        if (this.currentPage > 1) {
            final ConfigurationSection prevConfig = navConfig.getConfigurationSection("previous_page");
            if (prevConfig != null) {
                final ItemStack prevButton = new ItemStack(Material.valueOf(prevConfig.getString("material", "ARROW")));
                final ItemMeta prevMeta = prevButton.getItemMeta();
                if (prevMeta != null) {
                    prevMeta.setDisplayName(prevConfig.getString("name", "§e← Previous Page"));
                    prevButton.setItemMeta(prevMeta);
                    this.inventory.setItem(prevConfig.getInt("slot", 45), prevButton);
                }
            }
        }

        // Next page button
        if (this.currentPage < this.maxPage) {
            final ConfigurationSection nextConfig = navConfig.getConfigurationSection("next_page");
            if (nextConfig != null) {
                final ItemStack nextButton = new ItemStack(Material.valueOf(nextConfig.getString("material", "ARROW")));
                final ItemMeta nextMeta = nextButton.getItemMeta();
                if (nextMeta != null) {
                    nextMeta.setDisplayName(nextConfig.getString("name", "§e→ Next Page"));
                    nextButton.setItemMeta(nextMeta);
                    this.inventory.setItem(nextConfig.getInt("slot", 53), nextButton);
                }
            }
        }

        // Page info
        final ConfigurationSection infoConfig = navConfig.getConfigurationSection("info");
        if (infoConfig != null) {
            final ItemStack infoButton = new ItemStack(Material.valueOf(infoConfig.getString("material", "BOOK")));
            final ItemMeta infoMeta = infoButton.getItemMeta();
            if (infoMeta != null) {
                infoMeta.setDisplayName(
                        infoConfig.getString("name", "§ePage %page%/%total%")
                                .replace("%page%", String.valueOf(this.currentPage))
                                .replace("%total%", String.valueOf(this.maxPage))
                );
                infoButton.setItemMeta(infoMeta);
                this.inventory.setItem(infoConfig.getInt("slot", 49), infoButton);
            }
        }
    }

    private void addArmorSet(final String setName) {
        final ConfigurationSection setSection = this.plugin.getConfig().getConfigurationSection("armor_sets." + setName);
        if (setSection == null) return;

        final ConfigurationSection guiSection = setSection.getConfigurationSection("gui");
        final ConfigurationSection costs = setSection.getConfigurationSection("cost");
        final ConfigurationSection enchantedCosts = setSection.getConfigurationSection("cost_enchanted");
        final ConfigurationSection slots = setSection.getConfigurationSection("slots");
        final ConfigurationSection pieces = setSection.getConfigurationSection("pieces");

        if (guiSection != null && slots != null && pieces != null && (costs != null || enchantedCosts != null)) {
            addArmorPiece(setName, "helmet", guiSection, costs, enchantedCosts, slots, pieces);
            addArmorPiece(setName, "chestplate", guiSection, costs, enchantedCosts, slots, pieces);
            addArmorPiece(setName, "leggings", guiSection, costs, enchantedCosts, slots, pieces);
            addArmorPiece(setName, "boots", guiSection, costs, enchantedCosts, slots, pieces);
        }
    }

    private void addArmorPiece(final String setName, final String type,
                               final ConfigurationSection guiSection,
                               final ConfigurationSection costs,
                               final ConfigurationSection enchantedCosts,
                               final ConfigurationSection slots,
                               final ConfigurationSection pieces) {
        final ConfigurationSection pieceSection = guiSection.getConfigurationSection(type);
        final ConfigurationSection pieceStats = pieces.getConfigurationSection(type);
        if (pieceSection == null || pieceStats == null) return;

        final int slot = slots.getInt(type, -1);
        if (slot < 0 || slot >= this.inventory.getSize()) return;

        final Material material = Material.valueOf(pieceSection.getString("material", "DIAMOND_" + type.toUpperCase()));
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        final String displayName = pieceSection.getString("display_name", "§6" + setName + " " + type);
        meta.setDisplayName(displayName);

        final List<String> lore = new ArrayList<>();

        // Add cost information
        if (enchantedCosts != null && enchantedCosts.contains(type)) {
            final int enchantedCost = enchantedCosts.getInt(type, 1);
            lore.add("§d§lCost: §5✨ " + enchantedCost + " Enchanted Shards");
        } else if (costs != null && costs.contains(type)) {
            final int cost = costs.getInt(type, 100);
            lore.add("§7Cost: §e" + cost + " shards");
        }

        lore.add("");

        // Add requirement information
        final String requiredSet = this.plugin.getConfig().getConfigurationSection("armor_sets." + setName).getString("requires");
        if (requiredSet != null) {
            lore.add("§c§lRequires: §7" + requiredSet + " " + type);
            lore.add("");
        }

        if (pieceSection.contains("lore")) {
            lore.addAll(pieceSection.getStringList("lore"));
            lore.add("");
        }

        // Add individual piece bonus information
        final double multiplier = pieceStats.getDouble("multiplier", 1.0);
        final int rolls = pieceStats.getInt("rolls", 1);
        lore.add("§6Piece Bonus:");
        lore.add("§e+" + (int)((multiplier - 1) * 100) + "% Shard Drop Rate");
        if (rolls > 1) {
            lore.add("§e+" + (rolls - 1) + " Extra Roll" + (rolls > 2 ? "s" : ""));
        }
        lore.add("");
        lore.add("§eClick to purchase!");

        meta.setLore(lore);

        if (pieceSection.contains("custom_model_data")) {
            meta.setCustomModelData(pieceSection.getInt("custom_model_data"));
        }

        if (meta instanceof LeatherArmorMeta && pieceSection.contains("color")) {
            final ConfigurationSection colorSection = pieceSection.getConfigurationSection("color");
            if (colorSection != null) {
                final int red = colorSection.getInt("red", 0);
                final int green = colorSection.getInt("green", 0);
                final int blue = colorSection.getInt("blue", 0);
                ((LeatherArmorMeta) meta).setColor(Color.fromRGB(red, green, blue));
            }
        }

        item.setItemMeta(meta);
        this.inventory.setItem(slot, item);
        this.slotToArmorSet.put(slot, setName + ":" + type);
    }

    private void addPickaxe(final String pickaxeName) {
        final ConfigurationSection pickaxeSection = this.plugin.getConfig().getConfigurationSection("pickaxes." + pickaxeName);
        if (pickaxeSection == null) return;

        final int slot = pickaxeSection.getInt("slot", -1);
        if (slot < 0 || slot >= this.inventory.getSize()) return;

        final ConfigurationSection guiSection = pickaxeSection.getConfigurationSection("gui");
        if (guiSection == null) return;

        final Material material = Material.valueOf(guiSection.getString("material", "IRON_PICKAXE"));
        final ItemStack pickaxe = new ItemStack(material);
        final ItemMeta meta = pickaxe.getItemMeta();
        if (meta == null) return;

        final String displayName = guiSection.getString("display_name", "§6" + pickaxeName);
        meta.setDisplayName(displayName);

        final List<String> lore = new ArrayList<>();
        final int cost = pickaxeSection.getInt("cost", 100);
        lore.add("§7Cost: §e" + cost + " shards");
        lore.add("");

        // Add requirement information
        final String requiredPickaxe = pickaxeSection.getString("requires");
        if (requiredPickaxe != null) {
            lore.add("§c§lRequires: §7" + requiredPickaxe);
            lore.add("");
        }

        if (guiSection.contains("lore")) {
            lore.addAll(guiSection.getStringList("lore"));
        }

        lore.add("");
        lore.add("§eClick to purchase!");

        meta.setLore(lore);

        if (guiSection.contains("custom_model_data")) {
            meta.setCustomModelData(guiSection.getInt("custom_model_data"));
        }

        pickaxe.setItemMeta(meta);
        this.inventory.setItem(slot, pickaxe);
        this.slotToPickaxe.put(slot, pickaxeName);
    }

    private boolean hasRequiredArmorPiece(final String requiredSet, final String type) {
        if (requiredSet == null) return true;

        final ItemStack[] inventory = player.getInventory().getContents();
        final NamespacedKey armorSetKey = new NamespacedKey(plugin, "armor_set");

        for (ItemStack item : inventory) {
            if (item == null || !item.hasItemMeta()) continue;

            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            String setName = container.get(armorSetKey, PersistentDataType.STRING);

            if (setName != null && setName.equals(requiredSet)) {
                String itemType = item.getType().name().toLowerCase();
                if (itemType.contains(type.toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasRequiredPickaxe(final String requiredPickaxe) {
        if (requiredPickaxe == null) return true;

        final ItemStack[] inventory = player.getInventory().getContents();
        final NamespacedKey pickaxeKey = new NamespacedKey(plugin, "pickaxe");

        for (ItemStack item : inventory) {
            if (item == null || !item.hasItemMeta()) continue;

            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            String pickaxeName = container.get(pickaxeKey, PersistentDataType.STRING);

            if (pickaxeName != null && pickaxeName.equals(requiredPickaxe)) {
                return true;
            }
        }

        return false;
    }

    private void removeRequiredArmorPiece(final String requiredSet, final String type) {
        if (requiredSet == null) return;

        final ItemStack[] inventory = player.getInventory().getContents();
        final NamespacedKey armorSetKey = new NamespacedKey(plugin, "armor_set");

        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];
            if (item == null || !item.hasItemMeta()) continue;

            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            String setName = container.get(armorSetKey, PersistentDataType.STRING);

            if (setName != null && setName.equals(requiredSet)) {
                String itemType = item.getType().name().toLowerCase();
                if (itemType.contains(type.toLowerCase())) {
                    player.getInventory().setItem(i, null);
                    break;
                }
            }
        }
    }

    private void removeRequiredPickaxe(final String requiredPickaxe) {
        if (requiredPickaxe == null) return;

        final ItemStack[] inventory = player.getInventory().getContents();
        final NamespacedKey pickaxeKey = new NamespacedKey(plugin, "pickaxe");

        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];
            if (item == null || !item.hasItemMeta()) continue;

            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            String pickaxeName = container.get(pickaxeKey, PersistentDataType.STRING);

            if (pickaxeName != null && pickaxeName.equals(requiredPickaxe)) {
                player.getInventory().setItem(i, null);
                break;
            }
        }
    }

    public void handleClick(final InventoryClickEvent event) {
        event.setCancelled(true);

        final ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        final int slot = event.getRawSlot();
        if (slot >= this.inventory.getSize()) return;

        // Handle enchanted shard conversion
        if (slot == this.enchantedShardSlot) {
            this.handleEnchantedShardConversion();
            return;
        }

        // Handle navigation
        final ConfigurationSection navConfig = this.plugin.getConfig().getConfigurationSection("settings.gui.navigation");
        if (navConfig != null) {
            final int prevSlot = navConfig.getInt("previous_page.slot", 45);
            final int nextSlot = navConfig.getInt("next_page.slot", 53);

            if (slot == prevSlot && this.currentPage > 1) {
                this.currentPage--;
                this.setupGUI();
                return;
            }

            if (slot == nextSlot && this.currentPage < this.maxPage) {
                this.currentPage++;
                this.setupGUI();
                return;
            }
        }

        // Only proceed with purchase logic if the item has lore
        final ItemMeta meta = clicked.getItemMeta();
        if (!meta.hasLore()) return;

        // Parse cost from lore
        final List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) return;

        try {
            final String costText = lore.get(0).replaceAll("§[0-9a-fklmnor]", "");
            boolean isEnchantedShardCost = costText.contains("Enchanted Shards");

            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(costText);
            if (!matcher.find()) {
                this.player.sendMessage("§cFailed to parse item cost!");
                return;
            }

            int cost = Integer.parseInt(matcher.group());

            // Handle armor purchase
            final String armorInfo = this.slotToArmorSet.get(slot);
            if (armorInfo != null) {
                final String[] parts = armorInfo.split(":");
                final String setName = parts[0];
                final String type = parts[1];

                // Check for required armor piece
                final ConfigurationSection armorSection = this.plugin.getConfig().getConfigurationSection("armor_sets." + setName);
                final String requiredSet = armorSection.getString("requires");

                if (!hasRequiredArmorPiece(requiredSet, type)) {
                    player.sendMessage("§cYou need the " + requiredSet + " " + type + " to purchase this!");
                    return;
                }

                if (isEnchantedShardCost) {
                    final int playerEnchantedShards = this.plugin.getShardManager().getEnchantedShards(this.player);
                    if (playerEnchantedShards < cost) {
                        this.player.sendMessage("§cYou don't have enough enchanted shards!");
                        return;
                    }
                } else {
                    final int playerShards = this.plugin.getShardManager().getShards(this.player);
                    if (playerShards < cost) {
                        this.player.sendMessage("§cYou don't have enough shards!");
                        return;
                    }
                }

                final ItemStack armor = this.plugin.getArmorManager().createArmorPiece(setName, clicked.getType());
                if (armor != null) {
                    // Remove required armor piece first
                    removeRequiredArmorPiece(requiredSet, type);

                    // Then remove shards and give new armor
                    if (isEnchantedShardCost) {
                        this.plugin.getShardManager().removeEnchantedShards(this.player, cost);
                    } else {
                        this.plugin.getShardManager().removeShards(this.player, cost);
                    }
                    this.player.getInventory().addItem(armor);
                    this.player.sendMessage("§aYou purchased " + clicked.getItemMeta().getDisplayName() + "§a!");
                }
                return;
            }

            // Handle pickaxe purchase
            final String pickaxeName = this.slotToPickaxe.get(slot);
            if (pickaxeName != null) {
                // Check for required pickaxe
                final ConfigurationSection pickaxeSection = this.plugin.getConfig().getConfigurationSection("pickaxes." + pickaxeName);
                final String requiredPickaxe = pickaxeSection.getString("requires");

                if (!hasRequiredPickaxe(requiredPickaxe)) {
                    player.sendMessage("§cYou need the " + requiredPickaxe + " pickaxe to purchase this pickaxe!");
                    return;
                }

                if (isEnchantedShardCost) {
                    final int playerEnchantedShards = this.plugin.getShardManager().getEnchantedShards(this.player);
                    if (playerEnchantedShards < cost) {
                        this.player.sendMessage("§cYou don't have enough enchanted shards!");
                        return;
                    }
                } else {
                    final int playerShards = this.plugin.getShardManager().getShards(this.player);
                    if (playerShards < cost) {
                        this.player.sendMessage("§cYou don't have enough shards!");
                        return;
                    }
                }

                final ItemStack pickaxe = this.plugin.getPickaxeManager().createPickaxe(pickaxeName);
                if (pickaxe != null) {
                    // Remove required pickaxe first
                    removeRequiredPickaxe(requiredPickaxe);

                    // Then remove shards and give new pickaxe
                    if (isEnchantedShardCost) {
                        this.plugin.getShardManager().removeEnchantedShards(this.player, cost);
                    } else {
                        this.plugin.getShardManager().removeShards(this.player, cost);
                    }
                    this.player.getInventory().addItem(pickaxe);
                    this.player.sendMessage("§aYou purchased " + clicked.getItemMeta().getDisplayName() + "§a!");
                }
            }
        } catch (Exception e) {
            this.player.sendMessage("§cAn error occurred while processing your purchase.");
            e.printStackTrace();
        }
    }

    private void handleEnchantedShardConversion() {
        final ConfigurationSection currencyConfig = this.plugin.getConfig().getConfigurationSection("settings.currency.enchanted");
        if (currencyConfig == null) return;

        final int conversionRate = currencyConfig.getInt("conversion_rate", 10);
        final int playerShards = this.plugin.getShardManager().getShards(this.player);

        if (playerShards < conversionRate) {
            this.player.sendMessage("§cYou need at least " + conversionRate + " regular shards to convert to an enchanted shard!");
            return;
        }

        // Remove regular shards and add enchanted shards
        this.plugin.getShardManager().removeShards(this.player, conversionRate);
        this.plugin.getShardManager().addEnchantedShards(this.player, 1);

        this.player.sendMessage(
                this.plugin.getConfig()
                        .getString("settings.messages.enchanted_shard_purchase", "§aYou converted %regular% regular shards into %enchanted% enchanted shards!")
                        .replace("%regular%", String.valueOf(conversionRate))
                        .replace("%enchanted%", "1")
        );
    }

    private int countShards() {
        return this.plugin.getShardManager().getShards(this.player);
    }

    private void removeShards(final int amount) {
        this.plugin.getShardManager().removeShards(this.player, amount);
    }

    public void open() {
        this.plugin.getInventoryListener().registerGUI(this.player.getUniqueId(), this);
        this.player.openInventory(this.inventory);
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}