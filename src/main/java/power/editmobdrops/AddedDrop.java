package power.editmobdrops;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddedDrop {
	public boolean valid = false;
	public Item item;
	public CompoundTag nbtTag;
	public int minStack = 1;
	public int maxStack = 1;
	public List<Double> chances = new ArrayList<>();
	public EntityType<?> entityFrom;

	public AddedDrop(String rawDropData, boolean singleDrop) {
		List<String> dropData = new ArrayList<>(Arrays.asList(rawDropData.split("\\s*:\\s*")));
		if (dropData.size() < 6) {
			return;
		}
		if (dropData.size() < 8 && singleDrop) {
			return;
		}

		String itemModid, itemName;
		// Entity data for single mob drops
		if (singleDrop) {
			entityFrom = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(dropData.get(0), dropData.get(1)));
		}

		// Item Data
		itemModid = dropData.get(singleDrop ? 2 : 0);
		itemName = dropData.get(singleDrop ? 3 : 1);

		item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemModid, itemName));
		nbtTag = AddedDrop.parseNBTFile(dropData.get(singleDrop ? 4 : 2));

		// Stack size
		String minStackSize = dropData.get(singleDrop ? 5 : 3);
		String maxStackSize = dropData.get(singleDrop ? 6 : 4);

		try {
			minStack = Integer.parseInt(minStackSize);
		} catch (NumberFormatException ignored) {

		}

		try {
			maxStack = Integer.parseInt(maxStackSize);
		} catch (NumberFormatException ignored) {

		}

		// Chances
		for (int i = singleDrop ? 7 : 5; i < dropData.size(); i++) {
			try {
				chances.add(Double.parseDouble(dropData.get(i)));
			} catch (NumberFormatException e) {
				chances.add(0.0);
			}
		}

		if (!singleDrop && chances.size() < 3) {
			chances.add(0.0);
			chances.add(0.0);
			chances.add(0.0);
		}

		valid = true;
	}

	private static CompoundTag parseNBTFile(String nbtFile) {
		if (!nbtFile.isEmpty()) {
			try {
				File file = new File(Reference.CONFIG_PATH, nbtFile + ".json");
				if (file.exists() && file.canRead()) {
					String fileContents = Files.readString(Paths.get(file.toString()), StandardCharsets.US_ASCII);
					return TagParser.parseTag(fileContents);
				} else {
					System.out.println("NBT file " + nbtFile + ".json not found");
				}
			} catch (CommandSyntaxException e) {
				System.out.println("NBT file " + nbtFile + ".json not valid");
			} catch (IOException e) {
				System.out.println("Couldn't read from NBT file " + nbtFile + ".json");
			}
		}
		return null;
	}
}
