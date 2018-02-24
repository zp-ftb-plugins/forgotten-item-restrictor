package com.forgottenislands.itemrestrictor;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

public class ItemRemoverCheck implements Runnable {
	
	public static ItemRestrictor plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	
	public ItemRemoverCheck(ItemRestrictor instance){
		
		plugin = instance;
	}
	
	@Override
	public void run(){
		
		for(World world : Bukkit.getWorlds()){
			
			ArrayList<Entity> items = new ArrayList<Entity>();
			
			for(Entity entity : world.getEntities()){
				
				if(entity instanceof Item){
					
					items.add(entity);
				}
			}
			
			if(items.size() > 10000){
				
				Bukkit.broadcastMessage(ChatColor.RED + "Items on ground will be removed in 30 seconds as there are more than 10000!");
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new ItemRemover(plugin), 600);
			}
		}
	}
}
