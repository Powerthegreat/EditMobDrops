package power.editmobdrops;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import power.editmobdrops.handlers.ConfigHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Mod(Reference.MODID)
public class EditMobDrops {
	private static final Logger LOGGER = LogManager.getLogger();

	public EditMobDrops() {
		Reference.CONFIG_PATH = FMLPaths.CONFIGDIR.get() + Reference.CONFIG_PATH;
		if (!Files.exists(Paths.get(Reference.CONFIG_PATH))) {
			try {
				Files.createDirectories(Paths.get(Reference.CONFIG_PATH));
			} catch (IOException e) {
				LOGGER.error("Could not create editmobdrops config directory");
			}
		}

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.CONFIG_SPEC, Reference.CONFIG_PATH + "editmobdrops.toml");
	}
}
