package crs;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

import net.minecraftforge.client.MinecraftForgeClient;

import net.minecraft.src.World;

public class ClientProxy extends CommonProxy {
    public static RenderTubes tubeRenderer;
    public static RenderTubeItem tubeItemRenderer;

    public static final String BLOCKS_PNG = "/crs-gfx/blocks.png";
    public static final String ITEMS_PNG  = "/crs-gfx/items.png";

    @Override
    public void init() {
        super.init();

        preloadTextures();
        initRenderers();
    }

    public void preloadTextures() {
        MinecraftForgeClient.preloadTexture(BLOCKS_PNG);
        MinecraftForgeClient.preloadTexture(ITEMS_PNG);
    }

    public void initRenderers() {
        tubeRenderer = new RenderTubes(RenderingRegistry.getNextAvailableRenderId());
        RenderingRegistry.registerBlockHandler(tubeRenderer);

        tubeItemRenderer = new RenderTubeItem();
        MinecraftForgeClient.registerItemRenderer(CommonProxy.tubeStone.itemID, tubeItemRenderer);
        MinecraftForgeClient.registerItemRenderer(CommonProxy.tubeGold.itemID,  tubeItemRenderer);
        MinecraftForgeClient.registerItemRenderer(CommonProxy.tubeBrass.itemID, tubeItemRenderer);
    }

    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance().getClient().theWorld;
    }
}
