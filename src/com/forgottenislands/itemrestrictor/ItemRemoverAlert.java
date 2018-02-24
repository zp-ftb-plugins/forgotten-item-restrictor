package com.forgottenislands.itemrestrictor;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ItemRemoverAlert implements Runnable {
	
	public static ItemRestrictor plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	
	public ItemRemoverAlert(ItemRestrictor instance){
		
		plugin = instance;
	}
	
	@Override
	public void run(){
		
		Bukkit.broadcastMessage(ChatColor.RED + "Items on ground will be removed in 1 minute!");
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new ItemRemover(plugin), 1200);
	}
}
