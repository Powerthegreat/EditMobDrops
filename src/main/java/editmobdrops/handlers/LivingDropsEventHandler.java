package editmobdrops.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import editmobdrops.AddedDrop;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.WorldEvent.Load;

public class LivingDropsEventHandler {
	
	// Refreshes configs on world load in order to capture items that weren't available during FMLPostInitializationEvent
	@SubscribeEvent
	public void onWorldLoad(Load event) {
		ConfigHandler.reloadConfig();
	}
	
	// A method to handle LivingDropsEvents
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onMobDrops(LivingDropsEvent event) {
		// Setting up variables to make things easier
		EntityLivingBase entityKilled = event.entityLiving;
		List<ItemStack> itemsToDrop = new ArrayList<>();

		// If debug mode is active, print the class name of the entity that was killed
		if (ConfigHandler.debugMode && event.source.getSourceOfDamage() instanceof EntityPlayer) {
			String[] splitEntityKilled = EntityList.getEntityString(entityKilled).split("\\s\\.\\s");
			System.out.println("[EditMobDrops]: Mob killed: " + EntityList.getEntityString(entityKilled));
			if (splitEntityKilled.length < 2) {
				System.out.println("[EditMobDrops]: Use minecraft:" + splitEntityKilled[0] + " for this mod");
			} else {
				System.out.println("[EditMobDrops]: Use " + splitEntityKilled[0] + ":" + splitEntityKilled[1] + " for this mod");
			}
		}

		for (Class<? extends Entity> mobToClear : ConfigHandler.mobsToClear) {
			if (entityKilled.getClass().equals(mobToClear))
				event.drops.clear();
		}

		for (AddedDrop itemToAdd : ConfigHandler.itemsToAdd) {
			// Universal chance
			if (event.entityLiving.worldObj.rand.nextDouble() * 100 < itemToAdd.chances.get(0)) {
				// Adding the item
				if (ConfigHandler.debugMode) {
					System.out.println("Adding " + itemToAdd.item);
				}
				ItemStack itemstack = new ItemStack(itemToAdd.item, randomStackSize(itemToAdd, event.entityLiving.worldObj.rand), itemToAdd.metadata);
				if (itemToAdd.nbtTag != null) {
					itemstack.setTagCompound(itemToAdd.nbtTag);
				}
				itemsToDrop.add(itemstack);
			}

			// Monster chance
			if (entityKilled instanceof EntityMob || entityKilled instanceof EntityDragon || entityKilled instanceof EntityGhast || entityKilled instanceof EntitySlime) {
				if (event.entityLiving.worldObj.rand.nextDouble() * 100 < itemToAdd.chances.get(1)) {
					// Adding the item
					if (ConfigHandler.debugMode) {
						System.out.println("Adding " + itemToAdd.item);
					}
					ItemStack itemstack = new ItemStack(itemToAdd.item, randomStackSize(itemToAdd, event.entityLiving.worldObj.rand), itemToAdd.metadata);
					if (itemToAdd.nbtTag != null) {
						itemstack.setTagCompound(itemToAdd.nbtTag);
					}
					itemsToDrop.add(itemstack);
				}
			}

			// Boss Chance
			if (entityKilled instanceof IBossDisplayData) {
				if (event.entityLiving.worldObj.rand.nextDouble() * 100 < itemToAdd.chances.get(2)) {
					// Adding the item
					if (ConfigHandler.debugMode) {
						System.out.println("Adding " + itemToAdd.item);
					}
					ItemStack itemstack = new ItemStack(itemToAdd.item, randomStackSize(itemToAdd, event.entityLiving.worldObj.rand), itemToAdd.metadata);
					if (itemToAdd.nbtTag != null) {
						itemstack.setTagCompound(itemToAdd.nbtTag);
					}
					itemsToDrop.add(itemstack);
				}
			}

			// If the mob is in any of the groups, run the chance for that group
			for (int i = 0; i < ConfigHandler.mobGroups.size(); i++) {
				if (ConfigHandler.mobGroups.get(i).contains(entityKilled.getClass())) {
					if (i + 3 < itemToAdd.chances.size()) {
						if (event.entityLiving.worldObj.rand.nextDouble() * 100 < itemToAdd.chances.get(i + 3)) {
							// Adding the item
							if (ConfigHandler.debugMode) {
								System.out.println("Adding " + itemToAdd.item);
							}
							ItemStack itemstack = new ItemStack(itemToAdd.item, randomStackSize(itemToAdd, event.entityLiving.worldObj.rand), itemToAdd.metadata);
							if (itemToAdd.nbtTag != null) {
								itemstack.setTagCompound(itemToAdd.nbtTag);
							}
							itemsToDrop.add(itemstack);
						}
					}
				}
			}
		}

		// Single mob items
		for (AddedDrop singleMobItem : ConfigHandler.singleMobItems) {
			if (entityKilled.getClass().equals(singleMobItem.entityFrom)) {
				if (event.entityLiving.worldObj.rand.nextDouble() * 100 < singleMobItem.chances.get(0)) {
					// Adding the item
					if (ConfigHandler.debugMode)
						System.out.println("Adding " + singleMobItem.item);
					ItemStack itemstack = new ItemStack(singleMobItem.item, randomStackSize(singleMobItem, event.entityLiving.worldObj.rand), singleMobItem.metadata);
					if (singleMobItem.nbtTag != null) {
						itemstack.setTagCompound(singleMobItem.nbtTag);
					}
					itemsToDrop.add(itemstack);
				}
			}
		}

		// Adding the items
		if (itemsToDrop.size() > 0 && ConfigHandler.debugMode)
			System.out.println("[EditMobDrops]: Adding items");
		for (ItemStack item : itemsToDrop) {
			event.drops.add(new EntityItem(event.entityLiving.worldObj, entityKilled.posX, entityKilled.posY, entityKilled.posZ, item));
		}
	}

	private int randomStackSize(AddedDrop drop, Random random) {
		if (drop.minStack < drop.maxStack) {
			return random.nextInt(1 + drop.maxStack - drop.minStack) + drop.minStack;
		} else {
			return drop.maxStack;
		}
	}
}
