package editmobdrops.handlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
		EntityLivingBase entityKilled = event.getEntityLiving();
		List<ItemStack> itemsToDrop = new ArrayList<ItemStack>();

		// If debug mode is active, print the class name of the entity that was killed
		if (ConfigHandler.debugMode && event.getSource().getTrueSource() instanceof EntityPlayer) {
			System.out.println("[EditMobDrops]: Mob killed: " + entityKilled.getClass().getSimpleName());
		}

		System.out.println("[EditMobDrops]: Adding items");
		for (String itemToAdd : ConfigHandler.itemsToAdd) {
			// Storing the data of the current item in a few different variables
			List<String> currentItem = new ArrayList<String>(Arrays.asList(itemToAdd.split("\\s*:\\s*")));
			if (currentItem.size() < 6) {
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
				metadata = Integer.parseInt(currentItem.get(4));
			} catch (NumberFormatException ignored) {

			}
			// Getting the chances for each mob group into a list
			List<Double> currentItemChances = new ArrayList<Double>();
			for (int i = 5; i < currentItem.size(); i++) {
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
				itemsToDrop.add(new ItemStack(GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(modid, name)), randomStackSize(currentItem.get(2), currentItem.get(3)), metadata));
			}

			// Monster chance
			if (entityKilled instanceof EntityMob || entityKilled instanceof EntityDragon || entityKilled instanceof EntityGhast || entityKilled instanceof EntitySlime) {
				if (random.nextDouble() * 100 < currentItemChances.get(1)) {
					// Adding the item
					itemsToDrop.add(new ItemStack(GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(modid, name)), randomStackSize(currentItem.get(2), currentItem.get(3)), metadata));
				}
			}

			// Boss Chance
			if (!entityKilled.isNonBoss()) {
				if (random.nextDouble() * 100 < currentItemChances.get(2)) {
					// Adding the item
					itemsToDrop.add(new ItemStack(GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(modid, name)), randomStackSize(currentItem.get(2), currentItem.get(3)), metadata));
				}
			}

			// If the mob is in any of the groups, run the chance for that group
			for (int i = 0; i < ConfigHandler.mobGroups.length; i++) {
				List<String> mobGroup = new ArrayList<>(Arrays.asList(ConfigHandler.mobGroups[i].split("\\s*:\\s*")));
				for (String mob : mobGroup) {
					if (entityKilled.getClass().getSimpleName().equals(mob)) {
						if (i + 3 < currentItemChances.size()) {
							if (random.nextDouble() * 100 < currentItemChances.get(i + 3)) {
								// Adding the item
								System.out.println("Adding " + modid + ":" + name);
								itemsToDrop.add(new ItemStack(GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(modid, name)), randomStackSize(currentItem.get(2), currentItem.get(3)), metadata));
							}
						}
					}
				}
			}
		}

		// Adding the items
		for (ItemStack item : itemsToDrop) {
			event.getDrops().add(new EntityItem(event.getEntityLiving().getEntityWorld(), entityKilled.posX, entityKilled.posY, entityKilled.posZ, item));
		}
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
