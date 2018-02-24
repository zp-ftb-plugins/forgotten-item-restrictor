package com.forgottenislands.itemrestrictor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class ItemRestrictor extends JavaPlugin {
	
	//This HashMap is in the form (Item ID, Data Value) for the items that need to be blocked
	public HashMap<Integer, Integer> blockedItems = new HashMap<Integer, Integer>();
	public long averageTime = 50000;
	public long amountOfTimes = 1;
	public final Logger logger = Logger.getLogger("Minecraft");
	
	public void onEnable(){
		
		startSchedulers();
		initializeConfig();
		//new EventListeners(this);
		
		logger.info("ItemRestrictor Version 1.0 Enabled!");
		
	}
	
	public void onDisable(){
		
		logger.info("ItemRestrictor Version 1.0 Disabled!");
	}
	
	//This starts the schedulers running
	public void startSchedulers(){
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new ItemRemoverAlert(this), 10800, 12000);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new ItemRemoverCheck(this), 600, 600);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new PlayerChecker(this), 4, 4);
	}
	
	//This initializes the config
	public void initializeConfig(){
		
		final FileConfiguration config = this.getConfig();
		config.options().header("Choose What Items to Block");
		String[] blockedItems = {"10:1", "20:2"};
		config.addDefault("ItemRestrictor.Blocked", Arrays.asList(blockedItems));
		config.options().copyDefaults(true);
		saveConfig();
		
		populateVariables();
		
	}
	
	//This will populate the HashMap items that contains all the ids and data values for items not allowed in the worlds
	public void populateVariables(){
		
		System.out.println("Populating....");
		System.out.println(this.getConfig().getStringList("ItemRestrictor.Blocked"));
			
		for(String item : this.getConfig().getStringList("ItemRestrictor.Blocked")){
				
			String[] split = item.split(":");
			Integer id = Integer.parseInt(split[0]);
			Integer data = Integer.parseInt(split[1]);
				
			blockedItems.put(id, data);
			
			System.out.println(id + ":" + data);
				
			continue;
		}
	}
	
	/*Searches the players inventory for items that aren't allowed in the creative world
	 * and removes them from the players inventory
	 */
	public void searchInventoryCreative(Player player, String worldname){
			
		for(ItemStack item : player.getInventory().getContents()){
			
			int id = item.getTypeId();
			int data = item.getDurability();
			
			if(blockedItems.containsKey(id)){
				if(blockedItems.get(id).equals(data)){
					
					player.getInventory().remove(item);
					
					continue;
				}
			}
		}
	}
	
	//Returns true if the player is in an enemy town
	public Boolean isInEnemyTown(Player player){
		
		if(this.isInTown(player)){
		
			if(!this.isInOwnTown(player)){
			
				return true;
			}
		}
		return false;
	}
	
	public boolean isNearEnemyTown(Player player){
		
		if(isInEnemyTown(player)) return true;
		
		if(isInOwnTown(player)) return false;
			
		Location location = player.getLocation();
		Integer x = 0;
		Integer z = 0;
		
		for (x = -100; x <= 100; x = x + 8){
		    for (z = -100; z <= 100; z = z + 8) {
		    	
		    	TownBlock townBlock = TownyUniverse.getTownBlock(location.add(x,0,z));
		    	try {
					if(townBlock != null && !townBlock.getTown().hasResident(player.getName())) return true;
				} catch (NotRegisteredException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
		return false;
	}
	
	//Returns true if the player is in any town
	public Boolean isInTown(Player player){
		
		Location location = player.getLocation();
		
		TownBlock townBlock = TownyUniverse.getTownBlock(location);
		
		if(townBlock != null){
			
			return true;
		}
		
		return false;
	}
	
	//Returns true if the player is in their own town and false if (isn't in town, doesn't have a town or is in enemy town)
	public Boolean isInOwnTown(Player player){
		
		Location location = player.getLocation();
		
		TownBlock townBlock = TownyUniverse.getTownBlock(location);
		
		Resident resident = null;
		try {
			resident = TownyUniverse.getDataSource().getResident(player.getName());
		} catch (NotRegisteredException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		try {
			if(resident.getTown() == null) return false;
		} catch (NotRegisteredException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			
			if(townBlock.getTown() != null && townBlock.getTown().hasResident(player.getName())){
						
				return true;
			} else {
				return false;
			}
		} catch (NotRegisteredException e) {
			
			e.printStackTrace();
		}
		return false;
	}
	
	//Returns the slots in the inventory that contain items that are allowed
	public ArrayList<Integer> getAllowedItems(Player player){
		
		ArrayList<Integer> allowedItems = new ArrayList<Integer>();
		
		for(ItemStack item : player.getInventory().getContents()){
			
			if(item == null) continue;
			
			int id = item.getTypeId();
			int data = item.getDurability();
			
			if(!blockedItems.containsKey(id) || (blockedItems.containsKey(id) && !blockedItems.get(id).equals(data))){
				
				for(Integer slot : player.getInventory().all(item).keySet()){
					
					if(slot > 8){
						
						allowedItems.add(slot);
					}
				}
			}
		}
		
		return allowedItems;
	}
	
	//Returns the slot id's for 
	public ArrayList<Integer> getDisallowedHotbar(Player player){
		
		ArrayList<Integer> disallowedSlots = new ArrayList<Integer>();
		
		for(ItemStack item : player.getInventory().getContents()){
			
			if(item == null) continue;
			
			int id = item.getTypeId();
			int data = item.getDurability();
			
			if(blockedItems.containsKey(id) && blockedItems.get(id).equals(data)){
				
				for(Integer slot : player.getInventory().all(item).keySet()){
					
					if(slot >= 0 && slot <=9){
					
						disallowedSlots.add(slot);
					}
				}
			}
		}
		return disallowedSlots;
	}
	
	//Teleport the player in the direction away from the town
	public void teleportAwayFromTown(Player player){
		
		player.teleport(player.getBedSpawnLocation());
	}
	
	public void checkInventory(Player player){
		
		String worldName = player.getWorld().getName();
		
		//if(worldName.equals("Creative")){
			
			//searchInventoryCreative(player, worldName);
			
		/*} else if(worldName.equals("Market")){
			
			if(plugin.searchInventoryMarket(player, worldName)){
				
				player.teleport(player.getBedSpawnLocation());
			}*/
		if(worldName.equals("Survival")){
			
			if(isNearEnemyTown(player)){
			
				ArrayList<Integer> allowedItems = getAllowedItems(player);
				
				ArrayList<Integer> disallowedItems = getDisallowedHotbar(player);
				
				if(allowedItems.size() < disallowedItems.size()){
					
					teleportAwayFromTown(player);
					
				} else {
					
					for(Integer slot : disallowedItems){
						
						PlayerInventory invent = player.getInventory();
						
						ItemStack toMoveOut = invent.getItem(slot);
						Integer inSlot = allowedItems.get(0);
						ItemStack toMoveIn = invent.getItem(inSlot);
						
						invent.clear(slot);
						invent.setItem(slot, toMoveIn);
						invent.clear(inSlot);
						invent.setItem(inSlot, toMoveOut);
					}
				}
			}
		}
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
	    if(label.equalsIgnoreCase("restrictor") && args.length == 1 && args[0].equals("stats")){
	        if(sender instanceof Player){
	        	Player player = (Player) sender;
	        	if(sender.hasPermission("itemrestrictor.stats")){
	        		player.sendMessage(ChatColor.RED + "----[Per Player Stats]----");
	        		player.sendMessage("The average time is " + averageTime + " nano seconds");
	        		Double averageCPU = ((averageTime / 200000000D) * 100);
	        		player.sendMessage(String.format("The average CPU time is %.2f%%", averageCPU));
	        		player.sendMessage(ChatColor.RED + "----[Server Stats]----");
	        		player.sendMessage("The average time is " + averageTime * Bukkit.getOnlinePlayers().length + " nano seconds");
	        		averageCPU = ((averageTime / 200000000D) * 100 * Bukkit.getOnlinePlayers().length);
	        		player.sendMessage(String.format("The average CPU time is %.2f%%", averageCPU));
	        		
	        		return true;
	        	}
	    	    sender.sendMessage(ChatColor.RED + "Sorry... You don't have permission to do this");
	    	    return true;
			}
	    }
	    sender.sendMessage(ChatColor.RED + "Incorrect Syntax");
		return true;
	}
}
	
/*	
	public boolean searchInventoryMarket(Player player, String worldname){
		
		if(items.isEmpty()){
			
			for(String item : this.getConfig().getStringList("ItemRestrictor.BannedItems")){
				
				String[] split = item.split(":");
				Integer id = Integer.parseInt(split[0]);
				Integer data = Integer.parseInt(split[1]);
				
				items.put(id, data);
				
				continue;
			}
			
			for(ItemStack item : player.getInventory().getContents()){
				
				Integer id = item.getTypeId();
				Integer data = Integer.parseInt(Short.toString(item.getDurability()));
				
				if(items.containsKey(id)){
					if(items.get(id).equals(data)){
						
						player.sendMessage(ChatColor.RED + "You cannot enter the market with blocked items! ItemID: " + id + ":" + data);
						
						return true;
					}
				}
			}
		}
		return false;
	} */