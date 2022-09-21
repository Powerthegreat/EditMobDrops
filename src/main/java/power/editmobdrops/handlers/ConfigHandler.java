package power.editmobdrops.handlers;

import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import power.editmobdrops.AddedDrop;
import power.editmobdrops.Reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ConfigHandler {
	public static final EditMobDropsConfig CONFIG;
	public static final ForgeConfigSpec CONFIG_SPEC;
	public static Boolean debugMode;
	public static List<AddedDrop> itemsToAdd;
	public static List<List<EntityType<?>>> mobGroups;
	public static List<EntityType<?>> mobsToClear;
	public static List<AddedDrop> singleMobItems;

	static {
		final Pair<EditMobDropsConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(EditMobDropsConfig::new);
		CONFIG_SPEC = specPair.getRight();
		CONFIG = specPair.getLeft();
	}

	public static void bakeConfig() {
		debugMode = CONFIG.debugMode.get();
		List<String> rawItemsToAdd = CONFIG.itemsToAdd.get();
		List<String> rawMobGroups = CONFIG.mobGroups.get();
		List<String> rawMobsToClear = CONFIG.mobsToClear.get();
		List<String> rawSingleMobItems = CONFIG.singleMobItems.get();

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
			List<EntityType<?>> currentGroup = new ArrayList<>();
			for (int i = 0; i < rawMobGroupSplit.size() - 1; i += 2) {
				currentGroup.add(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(rawMobGroupSplit.get(i), rawMobGroupSplit.get(i + 1))));
			}
			mobGroups.add(currentGroup);
		}

		mobsToClear = new ArrayList<>();
		for (String rawMobToClear : rawMobsToClear) {
			List<String> rawMobToClearSplit = new ArrayList<>(Arrays.asList(rawMobToClear.split("\\s*:\\s*")));
			for (int i = 0; i < rawMobToClearSplit.size() - 1; i += 2) {
				mobsToClear.add(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(rawMobToClearSplit.get(i), rawMobToClearSplit.get(i + 1))));
			}
		}

		singleMobItems = new ArrayList<>();
		for (String itemToAdd : rawSingleMobItems) {
			AddedDrop newDrop = new AddedDrop(itemToAdd, true);
			if (newDrop.valid) {
				singleMobItems.add(newDrop);
			}
		}
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
					.comment("Items to add, in the form modid:itemName:[nbtfile]:minStackSize:maxStackSize:universalChance:[monsterChance]:[bossChance]:[group1Chance]:[group2Chance]...\nChances are %\nNBT File is a json file in the \"editmobdrops\" folder here in config (leave blank for none)")
					.translation(Reference.MODID + ".config.itemsToAdd")
					.define("itemsToAdd", new ArrayList<>());

			mobGroups = builder
					.comment("Mob groups, in the form [EntityModid]:[EntityName]:[EntityModid]:[EntityName]...")
					.translation(Reference.MODID + ".config.mobGroups")
					.define("mobGroups", new ArrayList<>());

			mobsToClear = builder
					.comment("Mobs to clear existing drops from")
					.translation(Reference.MODID + ".config.mobsToClear")
					.define("mobsToClear", new ArrayList<>());

			singleMobItems = builder
					.comment("Single mob items, in the form EntityModid:EntityName:modid:itemName:[nbtfile]:minStackSize:maxStackSize:chance")
					.translation(Reference.MODID + ".config.singleMobItems")
					.define("singleMobItems", new ArrayList<>());
		}
	}
}
