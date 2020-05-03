package editmobdrops.proxies;

import editmobdrops.commands.ReloadConfigCommand;
import editmobdrops.handlers.ConfigHandler;
import editmobdrops.handlers.LivingDropsEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.File;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.init(new File(event.getSuggestedConfigurationFile().getParentFile() + "/editmobdrops/editmobdrops.cfg"));
	}

	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new LivingDropsEventHandler());
	}

	public void postInit(FMLPostInitializationEvent event) {
	}

	public void onServerStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new ReloadConfigCommand());
	}
}
