package power.editmobdrops.handlers;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import power.editmobdrops.Reference;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ConfigHandler {
	public static final EditMobDropsConfig CONFIG;
	public static final ForgeConfigSpec CONFIG_SPEC;
	public static Boolean debugMode;
	public static List<String> itemsToAdd;
	public static List<String> mobGroups;
	public static List<String> mobsToClear;
	public static List<String> singleMobItems;

	static {
		final Pair<EditMobDropsConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(EditMobDropsConfig::new);
		CONFIG_SPEC = specPair.getRight();
		CONFIG = specPair.getLeft();
	}

	public static void bakeConfig() {
		debugMode = CONFIG.debugMode.get();
		itemsToAdd = CONFIG.itemsToAdd.get();
		mobGroups = CONFIG.mobGroups.get();
		mobsToClear = CONFIG.mobsToClear.get();
		singleMobItems = CONFIG.singleMobItems.get();
	}

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
		if (configEvent.getConfig().getSpec() == CONFIG_SPEC) {
			bakeConfig();
		}
	}


	public static class EditMobDropsConfig {
		public final ForgeConfigSpec.BooleanValue debugMode;
		public final ForgeConfigSpec.ConfigValue<List<String>> itemsToAdd;
		public final ForgeConfigSpec.ConfigValue<List<String>> mobGroups;
		public final ForgeConfigSpec.ConfigValue<List<String>> mobsToClear;
		public final ForgeConfigSpec.ConfigValue<List<String>> singleMobItems;

		public EditMobDropsConfig(ForgeConfigSpec.Builder builder) {
			debugMode = builder
					.comment("If debug mode is active, the mod will print the name of any mob killed by the player to console, and will log when items are dropped")
					.translation(Reference.MODID + ".config.debugMode")
					.define("debugMode", false);

			itemsToAdd = builder
					.comment("Items to add, in the form modid:itemName:[nbtfile]:[minStackSize]:[maxStackSize]:[universalChance]:[monsterChance]:[bossChance]:[group1Chance]:[group2Chance]...\nChances are %\nNBT File is a json file in the \"editmobdrops\" folder here in config (leave blank for none)")
					.translation(Reference.MODID + ".config.itemsToAdd")
					.define("itemsToAdd", new ArrayList<>());

			mobGroups = builder
					.comment("Mob groups, in the form [EntityName]:[EntityName]...")
					.translation(Reference.MODID + ".config.mobGroups")
					.define("mobGroups", new ArrayList<>());

			mobsToClear = builder
					.comment("Mobs to clear existing drops from")
					.translation(Reference.MODID + ".config.mobsToClear")
					.define("mobsToClear", new ArrayList<>());

			singleMobItems = builder
					.comment("Single mob items, in the form EntityName:modid:itemName:[nbtfile]:[minStackSize]:[maxStackSize]:[chance]")
					.translation(Reference.MODID + ".config.singleMobItems")
					.define("singleMobItems", new ArrayList<>());
		}
	}

	/*public static void init(File configFile) {
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
		if (event.getModID().equalsIgnoreCase(Reference.MODID)) {
			loadConfiguration();
		}
	}*/
}
