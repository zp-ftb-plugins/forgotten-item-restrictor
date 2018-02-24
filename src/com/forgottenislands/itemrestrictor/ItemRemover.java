package com.forgottenislands.itemrestrictor;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

public class ItemRemover implements Runnable {
	
	public static ItemRestrictor plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	
	public ItemRemover(ItemRestrictor instance){
		
		plugin = instance;
	}
	
	@Override
	public void run(){
		
		for(World world : Bukkit.getWorlds()){
			
			for(Entity entity : world.getEntities()){
				
				if(entity instanceof Item){
					
					entity.remove();
				}
			}
		}
	}
}
