package editmobdrops.handlers;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import editmobdrops.Reference;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigHandler {
	public static Configuration config;
	public static Boolean debugMode;
	public static String[] itemsToAdd;
	public static String[] mobGroups;
	public static String[] mobsToClear;
	public static String[] singleMobItems;

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
		debugMode = config.getBoolean("Debug Mode", Configuration.CATEGORY_GENERAL, false, "If debug mode is active, the mod will print the name of any mob killed by the player to console, and will log when items are dropped");
		itemsToAdd = config.getStringList("Items To Add", Configuration.CATEGORY_GENERAL, new String[]{}, "Items to add, in the form modid:itemName:[metadata]:[nbtfile]:[minStackSize]:[maxStackSize]:[universalChance]:[monsterChance]:[bossChance]:[group1Chance]:[group2Chance]...\nChances are %\nNBT File is a json file in the \"editmobdrops\" folder here in config (leave blank for none)");
		mobGroups = config.getStringList("Mob Groups", Configuration.CATEGORY_GENERAL, new String[]{}, "Mob groups, in the form [EntityName]:[EntityName]...");
		mobsToClear = config.getStringList("Mobs to Clear", Configuration.CATEGORY_GENERAL, new String[]{}, "Mobs to clear existing drops from");
		singleMobItems = config.getStringList("Single Mob Items", Configuration.CATEGORY_GENERAL, new String[]{}, "Single mob items, in the form EntityName:modid:itemName:[metadata]:[nbtfile]:[minStackSize]:[maxStackSize]:[chance]");
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
