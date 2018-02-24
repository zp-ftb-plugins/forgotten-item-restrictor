package com.forgottenislands.itemrestrictor;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerChecker implements Runnable {
	
	public static ItemRestrictor plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	
	public PlayerChecker(ItemRestrictor instance){
		
		plugin = instance;
	}
	
	@Override
	public void run(){
		
		for(Player player : Bukkit.getOnlinePlayers()){
			
			long begin = System.nanoTime();
			
			//System.out.println("Checking " + player.getName() + "... Time is " + begin);
			
			plugin.checkInventory(player);
			
			//System.out.println("Finished Checking " + player.getName() + "... Time is " + System.nanoTime());
			
			long timeTaken = System.nanoTime() - begin;
			
			//System.out.println("Time Taken for " + player.getName() + " was " + (timeTaken));
			
			plugin.averageTime = (((plugin.averageTime * plugin.amountOfTimes) + timeTaken) / (plugin.amountOfTimes++));
		}
	}
}