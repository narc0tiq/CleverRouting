package crs;

import cpw.mods.fml.common.registry.LanguageRegistry;

import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {
    public static void init() {
        CommonProxy.init();

        preloadTextures();
        initLanguage();
    }

    public static void preloadTextures() {
        MinecraftForgeClient.preloadTexture("/crs-gfx/blocks.png");
        MinecraftForgeClient.preloadTexture("/crs-gfx/blocks.png");
    }

    public static void initLanguage() {
        LanguageRegistry.addName(CommonProxy.tubeBlock, "Pneumatic Tube Block");
        LanguageRegistry.addName(CommonProxy.tubeStone, "Stone Pneumatic Tube");
    }
}
