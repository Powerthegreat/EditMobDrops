package editmobdrops;

import java.io.File;

import editmobdrops.commands.ReloadConfigCommand;
import editmobdrops.handlers.ConfigHandler;
import editmobdrops.handlers.LivingDropsEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, acceptableRemoteVersions = "*")
public class EditMobDrops {
	// Instance of the mod used by forge
	@Mod.Instance
	public static EditMobDrops instance;
	public static File suggestedConfigFile;

	// PreInit, Load, and PostInit init the mod in the correct order
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		suggestedConfigFile = event.getSuggestedConfigurationFile();
	}

	@Mod.EventHandler
	public void load(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new LivingDropsEventHandler());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		ConfigHandler.init(new File(suggestedConfigFile.getParentFile() + "/editmobdrops/editmobdrops.cfg"));
	}

	@Mod.EventHandler
	public void onServerStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new ReloadConfigCommand());
	}
}
