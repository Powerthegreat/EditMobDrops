package editmobdrops.handlers;

import editmobdrops.Reference;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

public class ConfigHandler {
	public static Configuration config;
	public static Boolean debugMode;
	public static String[] itemsToAdd;
	public static String[] mobGroups;

	public static void init(File configFile) {
		if (config == null) {
			config = new Configuration(configFile);
			loadConfiguration();
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equalsIgnoreCase(Reference.MODID)) {
			loadConfiguration();
		}
	}

	public static void reloadConfig() {
		loadConfiguration();
	}

	private static void loadConfiguration() {
		debugMode = config.getBoolean("Debug Mode", Configuration.CATEGORY_GENERAL, false, "If debug mode is active, the mod will print the name of any mob killed by the player to console");
		itemsToAdd = config.getStringList("Items To Add", Configuration.CATEGORY_GENERAL, new String[]{}, "Items to add, in the form modid:itemName:[minStackSize]:[maxStackSize]:[metadata]:[universalChance]:[monsterChance]:[bossChance]:[group1Chance]:[group2Chance]...\nChances are %");
		mobGroups = config.getStringList("Mob Groups", Configuration.CATEGORY_GENERAL, new String[]{}, "Mob groups, in the form [EntityName]:[EntityName]...");
		if (config.hasChanged()) {
			config.save();
		}
	}
}
