package com.forgottenislands.itemrestrictor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class EventListeners implements Listener {
	
	public static ItemRestrictor plugin;
	
	public EventListeners(ItemRestrictor instance){
		plugin = instance;
		Bukkit.getServer().getPluginManager().registerEvents(this, instance);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void changeWorld(PlayerChangedWorldEvent event){
		
		Player player = event.getPlayer();
		
		String worldName = player.getWorld().getName();
		
		if(worldName.equals("Creative")){
			
			plugin.searchInventoryCreative(player, worldName);
			
		/*} else if(worldName.equals("Market")){
			
			if(plugin.searchInventoryMarket(player, worldName)){
				
				player.teleport(player.getBedSpawnLocation());
				
			}*/
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void closeInventory(InventoryCloseEvent event){
		
		if(event.getPlayer() instanceof Player) plugin.checkInventory((Player) event.getPlayer());
	}
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void inventClick(InventoryClickEvent event){
		
		if(event.getInventory().getHolder() instanceof Player) plugin.checkInventory((Player) event.getInventory().getHolder());
	}
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void pickupItem(InventoryPickupItemEvent event){
		
		if(event.getInventory().getHolder() instanceof Player) plugin.checkInventory((Player) event.getInventory().getHolder());
	}
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void moveItem(InventoryMoveItemEvent event){
		
		if(event.getInitiator().getHolder() instanceof Player) plugin.checkInventory((Player) event.getInitiator().getHolder());
		
		if(event.getDestination().getHolder() instanceof Player) plugin.checkInventory((Player) event.getDestination().getHolder());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void dropItems(ItemSpawnEvent event){
		
		String worldName = event.getEntity().getLocation().getWorld().getName();
		
		if(worldName.equals("Creative")){
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void playerDropItems(PlayerDropItemEvent event){
		
		String worldName = event.getPlayer().getName();
		
		if(worldName.equals("Creative")){
			
			event.setCancelled(true);
		}
	}	
}