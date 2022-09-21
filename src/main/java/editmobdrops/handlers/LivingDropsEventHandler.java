package editmobdrops.handlers;

import editmobdrops.AddedDrop;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LivingDropsEventHandler {
	Random random = new Random();

	// A method to handle LivingDropsEvents
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onMobDrops(LivingDropsEvent event) {
		// Setting up variables to make things easier
		EntityLivingBase entityKilled = event.getEntityLiving();
		List<ItemStack> itemsToDrop = new ArrayList<>();

		for (Class<? extends Entity> mobToClear : ConfigHandler.mobsToClear) {
			if (entityKilled.getClass().equals(mobToClear))
				event.getDrops().clear();
		}

		for (AddedDrop itemToAdd : ConfigHandler.itemsToAdd) {
			// Universal chance
			if (random.nextDouble() * 100 < itemToAdd.chances.get(0)) {
				// Adding the item
				if (ConfigHandler.debugMode) {
					System.out.println("Adding " + itemToAdd.item);
				}
				ItemStack itemstack = new ItemStack(itemToAdd.item, randomStackSize(itemToAdd), itemToAdd.metadata);
				itemstack.setTagCompound(itemToAdd.nbtTag);
				itemsToDrop.add(itemstack);
			}

			// Monster chance
			if (entityKilled instanceof EntityMob || entityKilled instanceof EntityDragon || entityKilled instanceof EntityGhast || entityKilled instanceof EntitySlime) {
				if (random.nextDouble() * 100 < itemToAdd.chances.get(1)) {
					// Adding the item
					if (ConfigHandler.debugMode) {
						System.out.println("Adding " + itemToAdd.item);
					}
					ItemStack itemstack = new ItemStack(itemToAdd.item, randomStackSize(itemToAdd), itemToAdd.metadata);
					itemstack.setTagCompound(itemToAdd.nbtTag);
					itemsToDrop.add(itemstack);
				}
			}

			// Boss Chance
			if (!entityKilled.isNonBoss()) {
				if (random.nextDouble() * 100 < itemToAdd.chances.get(2)) {
					// Adding the item
					if (ConfigHandler.debugMode) {
						System.out.println("Adding " + itemToAdd.item);
					}
					ItemStack itemstack = new ItemStack(itemToAdd.item, randomStackSize(itemToAdd), itemToAdd.metadata);
					itemstack.setTagCompound(itemToAdd.nbtTag);
					itemsToDrop.add(itemstack);
				}
			}

			// If the mob is in any of the groups, run the chance for that group
			for (int i = 0; i < ConfigHandler.mobGroups.size(); i++) {
				if (ConfigHandler.mobGroups.get(i).contains(entityKilled.getClass())) {
					if (i + 3 < itemToAdd.chances.size()) {
						if (random.nextDouble() * 100 < itemToAdd.chances.get(i + 3)) {
							// Adding the item
							if (ConfigHandler.debugMode) {
								System.out.println("Adding " + itemToAdd.item);
							}
							ItemStack itemstack = new ItemStack(itemToAdd.item, randomStackSize(itemToAdd), itemToAdd.metadata);
							itemstack.setTagCompound(itemToAdd.nbtTag);
							itemsToDrop.add(itemstack);
						}
					}
				}
			}
		}

		// Single mob items
		for (AddedDrop singleMobItem : ConfigHandler.singleMobItems) {
			if (entityKilled.getClass().equals(singleMobItem.entityFrom)) {
				if (random.nextDouble() * 100 < singleMobItem.chances.get(0)) {
					// Adding the item
					if (ConfigHandler.debugMode)
						System.out.println("Adding " + singleMobItem.item);
					ItemStack itemstack = new ItemStack(singleMobItem.item, randomStackSize(singleMobItem), singleMobItem.metadata);
					itemstack.setTagCompound(singleMobItem.nbtTag);
					itemsToDrop.add(itemstack);
				}
			}
		}

		// Adding the items
		if (itemsToDrop.size() > 0 && ConfigHandler.debugMode)
			System.out.println("[EditMobDrops]: Adding items");
		for (ItemStack item : itemsToDrop) {
			event.getDrops().add(new EntityItem(event.getEntityLiving().getEntityWorld(), entityKilled.posX, entityKilled.posY, entityKilled.posZ, item));
		}
	}

	private ItemStack parseNBTFile(ItemStack itemstack, String nbtFile) {
		if (!nbtFile.isEmpty()) {
			try {
				File file = new File(ConfigHandler.config.getConfigFile().getParentFile(), nbtFile + ".json");
				if (file.exists() && file.canRead()) {
					String fileContents = new String(Files.readAllBytes(Paths.get(file.toString())), StandardCharsets.US_ASCII);
					itemstack.setTagCompound(JsonToNBT.getTagFromJson(fileContents));
				} else {
					System.out.println("NBT file " + nbtFile + ".json not found");
				}
			} catch (NBTException e) {
				System.out.println("NBT file " + nbtFile + ".json not valid");
			} catch (IOException e) {
				System.out.println("Couldn't read from NBT file " + nbtFile + ".json");
			}
		}

		return itemstack;
	}

	private int randomStackSize(AddedDrop drop) {
		if (drop.minStack < drop.maxStack) {
			return random.nextInt(1 + drop.maxStack - drop.minStack) + drop.minStack;
		} else {
			return drop.maxStack;
		}
	}
}
