package power.editmobdrops.handlers;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import power.editmobdrops.Reference;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LivingDropsEventHandler {
	private static final Random random = new Random();

	// A method to handle LivingDropsEvents
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onMobDrops(LivingDropsEvent event) {
		// Setting up variables to make things easier
		LivingEntity entityKilled = event.getEntityLiving();
		List<ItemStack> itemsToDrop = new ArrayList<>();

		// If debug mode is active, print the class name of the entity that was killed
		if (ConfigHandler.debugMode && event.getSource().getTrueSource() instanceof PlayerEntity) {
			System.out.println("[EditMobDrops]: Mob killed: " + entityKilled.getClass().getSimpleName());

			Iterator<ItemEntity> iterator = event.getDrops().iterator();
			iterator.forEachRemaining(itemEntity -> System.out.println("[EditMobDrops]: Mob normally drops " + itemEntity.getItem().getDisplayName()));
		}

		for (String mobToClear : ConfigHandler.mobsToClear) {
			if (entityKilled.getClass().getSimpleName().equals(mobToClear))
				event.getDrops().clear();
		}

		for (String itemToAdd : ConfigHandler.itemsToAdd) {
			// Storing the data of the current item in a few different variables
			List<String> currentItem = new ArrayList<>(Arrays.asList(itemToAdd.split("\\s*:\\s*")));
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
			// Getting the chances for each mob group into a list
			List<Double> currentItemChances = new ArrayList<>();
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
				ItemStack itemstack = new ItemStack(GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(modid, name)), randomStackSize(currentItem.get(3), currentItem.get(4)));
				parseNBTFile(itemstack, currentItem.get(2));
				itemsToDrop.add(itemstack);
			}

			// Monster chance
			if (entityKilled instanceof MobEntity) {
				if (random.nextDouble() * 100 < currentItemChances.get(1)) {
					// Adding the item
					ItemStack itemstack = new ItemStack(GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(modid, name)), randomStackSize(currentItem.get(3), currentItem.get(4)));
					parseNBTFile(itemstack, currentItem.get(2));
					itemsToDrop.add(itemstack);
				}
			}

			// Boss Chance
			if (!entityKilled.isNonBoss()) {
				if (random.nextDouble() * 100 < currentItemChances.get(2)) {
					// Adding the item
					ItemStack itemstack = new ItemStack(GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(modid, name)), randomStackSize(currentItem.get(3), currentItem.get(4)));
					parseNBTFile(itemstack, currentItem.get(2));
					itemsToDrop.add(itemstack);
				}
			}

			// If the mob is in any of the groups, run the chance for that group
			for (int i = 0; i < ConfigHandler.mobGroups.size(); i++) {
				List<String> mobGroup = new ArrayList<>(Arrays.asList(ConfigHandler.mobGroups.get(i).split("\\s*:\\s*")));
				for (String mob : mobGroup) {
					if (entityKilled.getClass().getSimpleName().equals(mob)) {
						if (i + 3 < currentItemChances.size()) {
							if (random.nextDouble() * 100 < currentItemChances.get(i + 3)) {
								// Adding the item
								if (ConfigHandler.debugMode)
									System.out.println("Adding " + modid + ":" + name);
								ItemStack itemstack = new ItemStack(GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(modid, name)), randomStackSize(currentItem.get(3), currentItem.get(4)));
								parseNBTFile(itemstack, currentItem.get(2));
								itemsToDrop.add(itemstack);
							}
						}
					}
				}
			}
		}
		// Single mob items
		for (int i = 0; i < ConfigHandler.singleMobItems.size(); i++) {
			List<String> singleMobItem = new ArrayList<>(Arrays.asList(ConfigHandler.singleMobItems.get(i).split("\\s*:\\s*")));
			if (singleMobItem.size() >= 7) {
				if (entityKilled.getClass().getSimpleName().equals(singleMobItem.get(0))) {
					double chance = 0.0;
					try {
						chance = Double.parseDouble(singleMobItem.get(6));
					} catch (NumberFormatException ignored) {

					}

					if (random.nextDouble() * 100 < chance) {
						// Adding the item
						if (ConfigHandler.debugMode)
							System.out.println("Adding " + singleMobItem.get(1) + ":" + singleMobItem.get(2));
						ItemStack itemstack = new ItemStack(GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(singleMobItem.get(1), singleMobItem.get(2))), randomStackSize(singleMobItem.get(4), singleMobItem.get(5)));
						parseNBTFile(itemstack, singleMobItem.get(3));
						itemsToDrop.add(itemstack);
					}
				}
			}
		}

		// Adding the items
		if (itemsToDrop.size() > 0 && ConfigHandler.debugMode)
			System.out.println("[EditMobDrops]: Adding items");
		for (ItemStack item : itemsToDrop) {
			if (item != null)
				event.getDrops().add(new ItemEntity(event.getEntityLiving().getEntityWorld(), entityKilled.getPosX(), entityKilled.getPosY(), entityKilled.getPosZ(), item));
		}
	}

	private static void parseNBTFile(ItemStack itemstack, String nbtFile) {
		if (!nbtFile.isEmpty()) {
			try {
				File file = new File(Reference.CONFIG_PATH, nbtFile + ".json");
				if (file.exists() && file.canRead()) {
					String fileContents = new String(Files.readAllBytes(Paths.get(file.toString())), StandardCharsets.US_ASCII);
					itemstack.setTag(JsonToNBT.getTagFromJson(fileContents));
				} else {
					System.out.println("NBT file " + nbtFile + ".json not found");
				}
			} catch (CommandSyntaxException e) {
				System.out.println("NBT file " + nbtFile + ".json not valid");
			} catch (IOException e) {
				System.out.println("Couldn't read from NBT file " + nbtFile + ".json");
			}
		}
	}

	private static int randomStackSize(String minStackSize, String maxStackSize) {
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
