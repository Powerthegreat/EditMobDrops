package editmobdrops;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import editmobdrops.handlers.ConfigHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class AddedDrop {
	public boolean valid = false;
	public Item item;
	public NBTTagCompound nbtTag;
	public int minStack = 1;
	public int maxStack = 1;
	public int metadata = 0;
	public List<Double> chances = new ArrayList<>();
	public Class<? extends Entity> entityFrom;

	public AddedDrop(String rawDropData, boolean singleDrop) {
		List<String> dropData = new ArrayList<>(Arrays.asList(rawDropData.split("\\s*:\\s*")));
		if (dropData.size() < 7) {
			return;
		}
		if (dropData.size() < 9 && singleDrop) {
			return;
		}

		String itemModid, itemName;
		// Entity data for single mob drops
		if (singleDrop) {
			try {
				if (dropData.get(0).equals("minecraft")) {
					entityFrom = (Class<? extends Entity>) EntityList.stringToClassMapping.get(dropData.get(1));
				} else {
					entityFrom = (Class<? extends Entity>) EntityList.stringToClassMapping.get(String.format("%s.%s", dropData.get(0), dropData.get(1)));
				}
			} catch (NullPointerException e) {
				System.out.println("Failed to find entity " + dropData.get(0) + "." + dropData.get(1));
				return;
			}
		}

		// Item Data
		itemModid = dropData.get(singleDrop ? 2 : 0);
		itemName = dropData.get(singleDrop ? 3 : 1);

		item = GameRegistry.findItem(itemModid, itemName);
		try {
			metadata = Integer.parseInt(dropData.get(singleDrop ? 4 : 2));
		} catch (NumberFormatException ignored) {

		}
		nbtTag = parseNBTFile(dropData.get(singleDrop ? 5 : 3));

		// Stack size
		String minStackSize = dropData.get(singleDrop ? 6 : 4);
		String maxStackSize = dropData.get(singleDrop ? 7 : 5);

		try {
			minStack = Integer.parseInt(minStackSize);
		} catch (NumberFormatException ignored) {

		}

		try {
			maxStack = Integer.parseInt(maxStackSize);
		} catch (NumberFormatException ignored) {

		}

		// Chances
		for (int i = singleDrop ? 8 : 6; i < dropData.size(); i++) {
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

	private static NBTTagCompound parseNBTFile(String nbtFile) {
		if (!nbtFile.isEmpty()) {
			try {
				File file = new File(ConfigHandler.config.getConfigFile().getParentFile(), nbtFile + ".json");
				if (file.exists() && file.canRead()) {
					String fileContents = new String(Files.readAllBytes(Paths.get(file.toString())), StandardCharsets.US_ASCII);
					System.out.println(fileContents);
					System.out.println(JsonToNBT.func_150315_a(fileContents));
					return (NBTTagCompound) JsonToNBT.func_150315_a(fileContents);
				} else {
					System.out.println("NBT file " + nbtFile + ".json not found");
				}
			} catch (NBTException e) {
				System.out.println("NBT file " + nbtFile + ".json not valid");
			} catch (IOException e) {
				System.out.println("Couldn't read from NBT file " + nbtFile + ".json");
			}
		}
		return null;
	}
}
