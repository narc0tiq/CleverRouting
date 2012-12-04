package crs;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

import net.minecraftforge.client.MinecraftForgeClient;

import net.minecraft.src.World;

public class ClientProxy extends CommonProxy {
    public static RenderTubes tubeRenderer;

    @Override
    public void init() {
        super.init();

        preloadTextures();
        initRenderers();
    }

    public void preloadTextures() {
        MinecraftForgeClient.preloadTexture("/crs-gfx/blocks.png");
        MinecraftForgeClient.preloadTexture("/crs-gfx/items.png");
    }

    public void initRenderers() {
        tubeRenderer = new RenderTubes(RenderingRegistry.getNextAvailableRenderId());
        RenderingRegistry.registerBlockHandler(tubeRenderer);
    }

    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance().getClient().theWorld;
    }
}
