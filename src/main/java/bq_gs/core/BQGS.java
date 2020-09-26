package bq_gs.core;

import org.apache.logging.log4j.Logger;

import bq_gs.core.proxies.CommonProxy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = BQGS.MODID, version = BQGS.VERSION, name = BQGS.NAME, dependencies = "after:betterquesting;after:gamestages")
public class BQGS
{
    public static final String MODID = "bq_gs";
    public static final String VERSION = "1.0.0";
    public static final String NAME = "BQ_Gamestages";
    public static final String PROXY = "bq_gs.core.proxies";
    public static final String CHANNEL = "BQGS";
	
	@Instance(MODID)
	public static BQGS instance;
	
	@SidedProxy(clientSide = PROXY + ".ClientProxy", serverSide = PROXY + ".CommonProxy")
	public static CommonProxy proxy;
	public SimpleNetworkWrapper network;
	public static Logger logger;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	logger = event.getModLog();
    	network = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);
    	
    	if(Loader.isModLoaded("gamestages")) proxy.registerHandlers();
    	
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ModContainer modContainer = Loader.instance().getIndexedModList().get("bq_gs");
        if(modContainer != null && modContainer.getMod() instanceof BQGS)
        {
            BQGS modInstance = (BQGS)modContainer.getMod();
            // DO THINGS...
        }
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if(Loader.isModLoaded("betterquesting") && Loader.isModLoaded("gamestages"))
        {
            proxy.registerExpansion();
        }
    }
    
    @EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
    }
}
