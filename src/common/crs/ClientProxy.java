package crs;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {
    public static RenderTubes tubeRenderer;

    public void init() {
        super.init();

        preloadTextures();
        initLanguage();
        initRenderers();
    }

    public void preloadTextures() {
        MinecraftForgeClient.preloadTexture("/crs-gfx/blocks.png");
        MinecraftForgeClient.preloadTexture("/crs-gfx/items.png");
    }

    public void initLanguage() {
        LanguageRegistry.addName(CommonProxy.tubeBlock, "Pneumatic Tube Block");
        LanguageRegistry.addName(CommonProxy.tubeStone, "Stone Pneumatic Tube");
    }

    public void initRenderers() {
        tubeRenderer = new RenderTubes(RenderingRegistry.getNextAvailableRenderId());
        RenderingRegistry.registerBlockHandler(tubeRenderer);
    }
}
