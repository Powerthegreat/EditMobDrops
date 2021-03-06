package editmobdrops.handlers;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LivingDropsEventHandler {
	Random random = new Random();

	// A method to handle LivingDropsEvents
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onMobDrops(LivingDropsEvent event) {
		// Setting up variables to make things easier
		EntityLivingBase entityKilled = (EntityLivingBase) event.entity;
		List<ItemStack> itemsToDrop = new ArrayList<>();

		// If debug mode is active, print the class name of the entity that was killed
		if (ConfigHandler.debugMode && event.source.getSourceOfDamage() instanceof EntityPlayer) {
			System.out.println("[EditMobDrops]: Mob killed: " + entityKilled.getClass().getSimpleName());

			for (int i = 0; i < event.drops.size(); i++) {
				System.out.println("[EditMobDrops]: Mob normally drops " + event.drops.get(i).getEntityItem().getDisplayName());
			}
		}

		for (String mobToClear : ConfigHandler.mobsToClear) {
			if (entityKilled.getClass().getSimpleName().equals(mobToClear))
				event.drops.clear();
		}

		for (String itemToAdd : ConfigHandler.itemsToAdd) {
			// Storing the data of the current item in a few different variables
			List<String> currentItem = new ArrayList<>(Arrays.asList(itemToAdd.split("\\s*:\\s*")));
			if (currentItem.size() < 7) {
				currentItem.add("");
				currentItem.add("");
				currentItem.add("");
				currentItem.add("");
				currentItem.add("");
				currentItem.add("");
				currentItem.add("");
			}
			String modid = currentItem.get(0);
			String name = currentItem.get(1);
			int metadata = 0;
			try {
				metadata = Integer.parseInt(currentItem.get(2));
			} catch (NumberFormatException ignored) {

			}
			// Getting the chances for each mob group into a list
			List<Double> currentItemChances = new ArrayList<>();
			for (int i = 6; i < currentItem.size(); i++) {
				try {
					currentItemChances.add(Double.parseDouble(currentItem.get(i)));
				} catch (NumberFormatException e) {
					currentItemChances.add(0.0);
				}
			}

			// Making sure there's enough data for the universal, monster, and boss chances
			if (currentItemChances.size() < 3) {
				currentItemChances.add(0.0);
				currentItemChances.add(0.0);
				currentItemChances.add(0.0);
			}

			// Universal chance
			if (random.nextDouble() * 100 < currentItemChances.get(0)) {
				// Adding the item
				ItemStack itemstack = new ItemStack(GameRegistry.findItem(modid, name), randomStackSize(currentItem.get(4), currentItem.get(5)), metadata);
				parseNBTFile(itemstack, currentItem.get(3));
				itemsToDrop.add(itemstack);
			}

			// Monster chance
			if (entityKilled instanceof EntityMob || entityKilled instanceof EntityDragon || entityKilled instanceof EntityGhast || entityKilled instanceof EntitySlime) {
				if (random.nextDouble() * 100 < currentItemChances.get(1)) {
					// Adding the item
					ItemStack itemstack = new ItemStack(GameRegistry.findItem(modid, name), randomStackSize(currentItem.get(4), currentItem.get(5)), metadata);
					parseNBTFile(itemstack, currentItem.get(3));
					itemsToDrop.add(itemstack);
				}
			}

			// Boss Chance
			if (entityKilled instanceof IBossDisplayData) {
				if (random.nextDouble() * 100 < currentItemChances.get(2)) {
					// Adding the item
					ItemStack itemstack = new ItemStack(GameRegistry.findItem(modid, name), randomStackSize(currentItem.get(4), currentItem.get(5)), metadata);
					parseNBTFile(itemstack, currentItem.get(3));
					itemsToDrop.add(itemstack);
				}
			}

			// If the mob is in any of the groups, run the chance for that group
			for (int i = 0; i < ConfigHandler.mobGroups.length; i++) {
				List<String> mobGroup = new ArrayList<String>(Arrays.asList(ConfigHandler.mobGroups[i].split("\\s*:\\s*")));
				for (String mob : mobGroup) {
					if (entityKilled.getClass().getSimpleName().equals(mob)) {
						if (i + 3 < currentItemChances.size()) {
							if (random.nextDouble() * 100 < currentItemChances.get(i + 3)) {
								// Adding the item
								if (ConfigHandler.debugMode)
									System.out.println("Adding " + modid + ":" + name);
								ItemStack itemstack = new ItemStack(GameRegistry.findItem(modid, name), randomStackSize(currentItem.get(4), currentItem.get(5)), metadata);
								parseNBTFile(itemstack, currentItem.get(3));
								itemsToDrop.add(itemstack);
							}
						}
					}
				}
			}
		}

		// Single mob items
		for (int i = 0; i < ConfigHandler.singleMobItems.length; i++) {
			List<String> singleMobItem = new ArrayList<>(Arrays.asList(ConfigHandler.singleMobItems[i].split("\\s*:\\s*")));
			if (entityKilled.getClass().getSimpleName().equals(singleMobItem.get(0))) {
				int metadata = 0;
				try {
					metadata = Integer.parseInt(singleMobItem.get(3));
				} catch (NumberFormatException ignored) {

				}
				double chance = 0.0;
				try {
					chance = Double.parseDouble(singleMobItem.get(7));
				} catch (NumberFormatException ignored) {

				}

				if (random.nextDouble() * 100 < chance) {
					// Adding the item
					if (ConfigHandler.debugMode)
						System.out.println("Adding " + singleMobItem.get(1) + ":" + singleMobItem.get(2));
					ItemStack itemstack = new ItemStack(GameRegistry.findItem(singleMobItem.get(1), singleMobItem.get(2)), randomStackSize(singleMobItem.get(5), singleMobItem.get(6)), metadata);
					parseNBTFile(itemstack, singleMobItem.get(4));
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

	private ItemStack parseNBTFile(ItemStack itemstack, String nbtFile) {
		if (!nbtFile.isEmpty()) {
			try {
				File file = new File(ConfigHandler.config.getConfigFile().getParentFile(), nbtFile + ".json");
				if (file.exists() && file.canRead()) {
					String fileContents = new String(Files.readAllBytes(Paths.get(file.toString())), StandardCharsets.US_ASCII);
					itemstack.setTagCompound((NBTTagCompound) JsonToNBT.func_150315_a(fileContents));
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

	private int randomStackSize(String minStackSize, String maxStackSize) {
		int minStack = 1;
		int maxStack = 1;

		try {
			minStack = Integer.parseInt(minStackSize);
		} catch (NumberFormatException ignored) {

		}

		try {
			maxStack = Integer.parseInt(maxStackSize);
		} catch (NumberFormatException ignored) {

		}

		if (minStack < maxStack) {
			return random.nextInt(1 + maxStack - minStack) + minStack;
		} else {
			return maxStack;
		}
	}
}
