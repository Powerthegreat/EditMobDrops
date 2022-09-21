package power.editmobdrops.handlers;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import power.editmobdrops.AddedDrop;
import power.editmobdrops.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LivingDropsEventHandler {
	private static final Random random = new Random();

	// A method to handle LivingDropsEvents
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onMobDrops(LivingDropsEvent event) {
		// Setting up variables to make things easier
		LivingEntity entityKilled = event.getEntityLiving();
		List<ItemStack> itemsToDrop = new ArrayList<>();

		for (EntityType<?> mobToClear : ConfigHandler.mobsToClear) {
			if (entityKilled.getType().equals(mobToClear)) {
				event.getDrops().clear();
				System.out.println(event.getDrops());
			}
		}

		for (AddedDrop itemToAdd : ConfigHandler.itemsToAdd) {
			// Universal chance
			if (random.nextDouble() * 100 < itemToAdd.chances.get(0)) {
				// Adding the item
				if (ConfigHandler.debugMode) {
					System.out.println("Adding " + itemToAdd.item);
				}
				ItemStack itemstack = new ItemStack(itemToAdd.item, randomStackSize(itemToAdd));
				itemstack.setTag(itemToAdd.nbtTag);
				itemsToDrop.add(itemstack);
			}

			// Monster chance
			if (entityKilled instanceof MobEntity) {
				if (random.nextDouble() * 100 < itemToAdd.chances.get(1)) {
					// Adding the item
					if (ConfigHandler.debugMode) {
						System.out.println("Adding " + itemToAdd.item);
					}
					ItemStack itemstack = new ItemStack(itemToAdd.item, randomStackSize(itemToAdd));
					itemstack.setTag(itemToAdd.nbtTag);
					itemsToDrop.add(itemstack);
				}
			}

			// Boss Chance
			if (!entityKilled.canChangeDimensions()) { // Includes fishing bobber, renamed from isNonBoss
				if (random.nextDouble() * 100 < itemToAdd.chances.get(2)) {
					// Adding the item
					if (ConfigHandler.debugMode) {
						System.out.println("Adding " + itemToAdd.item);
					}
					ItemStack itemstack = new ItemStack(itemToAdd.item, randomStackSize(itemToAdd));
					itemstack.setTag(itemToAdd.nbtTag);
					itemsToDrop.add(itemstack);
				}
			}

			// If the mob is in any of the groups, run the chance for that group
			for (int i = 0; i < ConfigHandler.mobGroups.size(); i++) {
				if (ConfigHandler.mobGroups.get(i).contains(entityKilled.getType())) {
					if (i + 3 < itemToAdd.chances.size()) {
						if (random.nextDouble() * 100 < itemToAdd.chances.get(i + 3)) {
							// Adding the item
							if (ConfigHandler.debugMode) {
								System.out.println("Adding " + itemToAdd.item);
							}
							ItemStack itemstack = new ItemStack(itemToAdd.item, randomStackSize(itemToAdd));
							itemstack.setTag(itemToAdd.nbtTag);
							itemsToDrop.add(itemstack);
						}
					}
				}
			}
		}

		// Single mob items
		for (AddedDrop singleMobItem : ConfigHandler.singleMobItems) {
			if (entityKilled.getType().equals(singleMobItem.entityFrom)) {
				if (random.nextDouble() * 100 < singleMobItem.chances.get(0)) {
					// Adding the item
					if (ConfigHandler.debugMode)
						System.out.println("Adding " + singleMobItem.item);
					ItemStack itemstack = new ItemStack(singleMobItem.item, randomStackSize(singleMobItem));
					itemstack.setTag(singleMobItem.nbtTag);
					itemsToDrop.add(itemstack);
				}
			}
		}

		// Adding the items
		if (itemsToDrop.size() > 0 && ConfigHandler.debugMode)
			System.out.println("[EditMobDrops]: Adding items");
		for (ItemStack item : itemsToDrop) {
			if (item != null)
				event.getDrops().add(new ItemEntity(entityKilled.level, entityKilled.position().x(), entityKilled.position().y(), entityKilled.position().z(), item));
		}
	}

	private static int randomStackSize(AddedDrop drop) {
		if (drop.minStack < drop.maxStack) {
			return random.nextInt(1 + drop.maxStack - drop.minStack) + drop.minStack;
		} else {
			return drop.maxStack;
		}
	}
}
