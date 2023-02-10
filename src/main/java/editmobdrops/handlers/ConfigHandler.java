package editmobdrops.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import editmobdrops.AddedDrop;
import editmobdrops.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
	public static Configuration config;
	public static Boolean debugMode;
	public static List<AddedDrop> itemsToAdd;
	public static List<List<Class<? extends Entity>>> mobGroups;
	public static List<Class<? extends Entity>> mobsToClear;
	public static List<AddedDrop> singleMobItems;

	public static void init(File configFile) {
		if (config == null) {
			config = new Configuration(configFile);
			loadConfiguration();
		}
	}

	public static void reloadConfig() {
		config = new Configuration(config.getConfigFile());
		loadConfiguration();
	}

	private static void loadConfiguration() {
		debugMode = config.getBoolean("Debug Mode", Configuration.CATEGORY_GENERAL, false, "If debug mode is active, the mod will log when items are dropped. It will also print entity IDs (usable in /summon, and in this mod) to console");
		String[] rawItemsToAdd = config.getStringList("Items To Add", Configuration.CATEGORY_GENERAL, new String[]{}, "Items to add, in the form modid:itemName:[metadata]:[nbtfile]:minStackSize:maxStackSize:universalChance:[monsterChance]:[bossChance]:[group1Chance]:[group2Chance]...\nChances are %\nNBT File is a json file in the \"editmobdrops\" folder here in config (leave blank for none)");
		String[] rawMobGroups = config.getStringList("Mob Groups", Configuration.CATEGORY_GENERAL, new String[]{}, "Mob groups, in the form [EntityModid]:[EntityName]:[EntityModid]:[EntityName]...");
		String[] rawMobsToClear = config.getStringList("Mobs to Clear", Configuration.CATEGORY_GENERAL, new String[]{}, "Mobs to clear existing drops from");
		String[] rawSingleMobItems = config.getStringList("Single Mob Items", Configuration.CATEGORY_GENERAL, new String[]{}, "Single mob items, in the form EntityModid:EntityName:modid:itemName:[metadata]:[nbtfile]:minStackSize:maxStackSize:chance");

		itemsToAdd = new ArrayList<>();
		for (String itemToAdd : rawItemsToAdd) {
			AddedDrop newDrop = new AddedDrop(itemToAdd, false);
			if (newDrop.valid) {
				itemsToAdd.add(newDrop);
			}
		}

		mobGroups = new ArrayList<>();
		for (String rawMobGroup : rawMobGroups) {
			List<String> rawMobGroupSplit = new ArrayList<>(Arrays.asList(rawMobGroup.split("\\s*:\\s*")));
			List<Class<? extends Entity>> currentGroup = new ArrayList<>();
			for (int i = 0; i < rawMobGroupSplit.size() - 1; i += 2) {
				try {
					if (rawMobGroupSplit.get(i).equals("minecraft")) {
						currentGroup.add((Class<? extends Entity>) EntityList.stringToClassMapping.get(rawMobGroupSplit.get(i + 1)));
					} else {
						currentGroup.add((Class<? extends Entity>) EntityList.stringToClassMapping.get(String.format("%s.%s", rawMobGroupSplit.get(i), rawMobGroupSplit.get(i + 1))));
					}
				} catch (Exception ignored) {

				}
			}
			mobGroups.add(currentGroup);
		}

		mobsToClear = new ArrayList<>();
		for (String rawMobToClear : rawMobsToClear) {
			List<String> rawMobToClearSplit = new ArrayList<>(Arrays.asList(rawMobToClear.split("\\s*:\\s*")));
			for (int i = 0; i < rawMobToClearSplit.size() - 1; i += 2) {
				try {
					if (rawMobToClearSplit.get(i).equals("minecraft")) {
						mobsToClear.add((Class<? extends Entity>) EntityList.stringToClassMapping.get(rawMobToClearSplit.get(i + 1)));
					} else {
						mobsToClear.add((Class<? extends Entity>) EntityList.stringToClassMapping.get(String.format("%s.%s", rawMobToClearSplit.get(i), rawMobToClearSplit.get(i + 1))));
					}
				} catch (Exception ignored) {

				}
			}
		}

		singleMobItems = new ArrayList<>();
		for (String itemToAdd : rawSingleMobItems) {
			AddedDrop newDrop = new AddedDrop(itemToAdd, true);
			if (newDrop.valid) {
				singleMobItems.add(newDrop);
			}
		}

		if (config.hasChanged()) {
			config.save();
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.modID.equalsIgnoreCase(Reference.MODID)) {
			loadConfiguration();
		}
	}
}
