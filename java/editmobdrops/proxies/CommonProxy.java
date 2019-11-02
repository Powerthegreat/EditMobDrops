package editmobdrops.proxies;

import editmobdrops.handlers.ConfigHandler;
import editmobdrops.handlers.LivingDropsEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.init(event.getSuggestedConfigurationFile());
	}

	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new LivingDropsEventHandler());
	}

	public void postInit(FMLPostInitializationEvent event) {
	}
}
