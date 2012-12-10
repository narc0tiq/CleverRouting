package crs;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;

import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

public class RenderTubeItem implements IItemRenderer {
    // Happily borrowed from buildcraft.transport.render.PipeItemRenderer
    public void renderTube(RenderBlocks render, ItemStack stack, float translateX, float translateY, float translateZ) {
        int textureOffset = ((ItemTube)stack.getItem()).getTextureOffset();

        Tessellator tess = Tessellator.instance;

        Block block = CommonProxy.tubeBlock;
        block.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
        block.setBlockBoundsForItemRender();
        render.func_83018_a(block); // setRenderBoundsForBlockRender(block)

        GL11.glTranslatef(translateX, translateY, translateZ);

        tess.startDrawingQuads();
        tess.setNormal(0.0F, -1.0F, 0.0F);
        render.renderBottomFace(block, 0.0D, 0.0D, 0.0D, textureOffset + 1);
        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(0.0F, 1.0F, 0.0F);
        render.renderTopFace(block, 0.0D, 0.0D, 0.0D, textureOffset + 1);
        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(0.0F, 0.0F, -1.0F);
        render.renderEastFace(block, 0.0D, 0.0D, 0.0D, textureOffset + 3);
        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(0.0F, 0.0F, 1.0F);
        render.renderWestFace(block, 0.0D, 0.0D, 0.0D, textureOffset + 3);
        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(-1.0F, 0.0F, 0.0F);
        render.renderNorthFace(block, 0.0D, 0.0D, 0.0D, textureOffset + 3);
        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(1.0F, 0.0F, 0.0F);
        render.renderSouthFace(block, 0.0D, 0.0D, 0.0D, textureOffset + 3);
        tess.draw();

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

//public interface IItemRenderer {
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        if((type == ItemRenderType.ENTITY) || (type == ItemRenderType.EQUIPPED) || (type == ItemRenderType.INVENTORY)) {
            return true;
        }

        return false;
    }

    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        switch(type) {
            case ENTITY:
                renderTube((RenderBlocks) data[0], item, -0.5F, -0.5F, -0.5F);
                break;
            case EQUIPPED:
                renderTube((RenderBlocks) data[0], item, -0.4F, 0.5F, 0.35F);
                break;
            case INVENTORY:
                renderTube((RenderBlocks) data[0], item, -0.5F, -0.5F, -0.5F);
                break;
            default:
                break; // Huh, what?
        }
    }
//}
}
