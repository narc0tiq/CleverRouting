package crs;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

@Mod(
        modid = "CleverRouting",
        version = "%conf:VERSION%",
        useMetadata = true,
        dependencies = ""
    )
@NetworkMod(
        clientSideRequired = true,
        versionBounds = "%conf:VERSION_BOUNDS%"
    )
public class CleverRouting {
    public static boolean DEBUG_NETWORK = true;

    @Mod.Instance("CleverRouting")
    public static CleverRouting instance;

    @SidedProxy(clientSide = "crs.ClientProxy", serverSide = "crs.CommonProxy")
    public static CommonProxy proxy;

    public static Configuration config;

    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
    }

    @Mod.Init
    public void init(FMLInitializationEvent event) {
        try {
            config.load();
        }
        catch (RuntimeException e) { /* and ignore it */ }

        config.save();

        proxy.init();
    }

    @Mod.PostInit
    public void postInit(FMLPostInitializationEvent event) {
        //loadIntegration("whatever");
    }

    @SuppressWarnings("unchecked")
    private static boolean loadIntegration(String name) {
        System.out.println("CleverRouting: Loading " + name + " integration...");

        try {
            Class t = CleverRouting.class.getClassLoader().loadClass("crs.integration." + name);
            return ((Boolean)t.getMethod("init", new Class[0]).invoke((Object)null, new Object[0])).booleanValue();
        }
        catch (Throwable e) {
            System.out.println("CleverRouting: Did not load " + name + " integration: " + e);
            return false;
        }
    }

    public static Side getSide() {
        return FMLCommonHandler.instance().getEffectiveSide();
    }
}
