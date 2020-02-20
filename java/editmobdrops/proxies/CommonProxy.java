package editmobdrops.proxies;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import editmobdrops.commands.ReloadConfigCommand;
import editmobdrops.handlers.ConfigHandler;
import editmobdrops.handlers.LivingDropsEventHandler;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.init(new File(event.getSuggestedConfigurationFile().getParentFile() + "\\editmobdrops\\editmobdrops.cfg"));
	}

	public void load(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new LivingDropsEventHandler());
	}

	public void postInit(FMLPostInitializationEvent event) {
	}

	public void onServerStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new ReloadConfigCommand());
	}
}
