package com.example.shardplugin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import com.example.shardplugin.ShardPlugin;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class BlockBreakListener implements Listener {
    
    private final ShardPlugin plugin;
    private final Random random;
    
    public BlockBreakListener(final ShardPlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }
    
    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final ConfigurationSection blockSection = this.plugin.getConfigManager().getConfig().getConfigurationSection("settings.shard_blocks");
        
        if (blockSection != null) {
            final String blockType = event.getBlock().getType().name();
            
            if (blockSection.contains(blockType)) {
                final ConfigurationSection blockConfig = blockSection.getConfigurationSection(blockType);
                
                if (blockConfig != null) {
                    final int baseShards = blockConfig.getInt("amount");
                    
                    // Get multipliers from both armor and pickaxe
                    final double armorMultiplier = this.plugin.getArmorManager().getArmorMultiplier(player);
                    final double pickaxeMultiplier = this.plugin.getPickaxeManager().getPickaxeMultiplier(player);
                    final double totalMultiplier = armorMultiplier * pickaxeMultiplier;
                    
                    final int totalShards = (int) Math.round(baseShards * totalMultiplier);
                    
                    // Give shards to player
                    this.plugin.getShardManager().addShards(player, totalShards);
                    
                    // Create and give physical shard items
                    final ItemStack shardItem = this.createShardItem(totalShards);
                    player.getInventory().addItem(shardItem);
                    
                    // Send individual shard messages with delay
                    new BukkitRunnable() {
                        private int count = 0;
                        
                        @Override
                        public void run() {
                            if (count < totalShards) {
                                player.sendMessage("§a+" + 1 + " shard!");
                                count++;
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 2L); // Run every 2 ticks (0.1 seconds)
                    
                    // Get the total number of rolls (armor + pickaxe)
                    final double armorRolls = this.plugin.getArmorManager().getArmorRolls(player);
                    final double pickaxeRolls = this.plugin.getPickaxeManager().getPickaxeRolls(player);
                    final double totalRolls = armorRolls + pickaxeRolls;
                    
                    // Calculate guaranteed rolls and chance for an extra roll
                    final int guaranteedRolls = (int) Math.floor(totalRolls);
                    final double extraRollChance = totalRolls - guaranteedRolls;
                    
                    // Execute commands for guaranteed rolls
                    final List<String> commands = blockConfig.getStringList("commands");
                    for (int i = 0; i < guaranteedRolls; i++) {
                        executeCommands(commands, player);
                    }
                    
                    // Check for extra roll based on the fractional part
                    if (extraRollChance > 0 && random.nextDouble() < extraRollChance) {
                        executeCommands(commands, player);
                    }
                }
            }
        }
    }
    
    private void executeCommands(final List<String> commands, final Player player) {
        for (final String command : commands) {
            final String formattedCommand = command.replace("%player%", player.getName());
            this.plugin.getServer().dispatchCommand(
                this.plugin.getServer().getConsoleSender(),
                formattedCommand
            );
        }
    }
    
    private ItemStack createShardItem(final int amount) {
        final ConfigurationSection currencySection = this.plugin.getConfigManager().getConfig().getConfigurationSection("settings.currency");
        final ItemStack shardItem = new ItemStack(
            Material.valueOf(currencySection.getString("material", "AMETHYST_SHARD")),
            amount
        );
        final ItemMeta meta = shardItem.getItemMeta();
        
        if (meta != null) {
            meta.setCustomModelData(currencySection.getInt("custom_model_data", 1001));
            shardItem.setItemMeta(meta);
        }
        
        return shardItem;
    }
}