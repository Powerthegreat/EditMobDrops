package editmobdrops;

import editmobdrops.commands.ReloadConfigCommand;
import editmobdrops.proxies.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class EditMobDrops {
	// Instance of the mod used by forge
	@Mod.Instance
	public static EditMobDrops instance;

	// Proxies for combined client, and dedicated server
	@SidedProxy(clientSide = "editmobdrops.proxies.CombinedClientProxy", serverSide = "editmobdrops.proxies.DedicatedServerProxy")
	public static CommonProxy proxy;

	// PreInit, Load, and PostInit init the mod in the correct order
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	@Mod.EventHandler
	public void onServerStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new ReloadConfigCommand());
	}
}
