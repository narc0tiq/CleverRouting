package crs;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;
import net.minecraft.src.TileEntity;

import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeDirection;

public class RenderTubes implements ISimpleBlockRenderingHandler {
    public int renderID = -1;
    public double texSize = 256.0D;

    public RenderTubes(int renderID) {
        this.renderID = renderID;
    }

// public interface ISimpleBlockRenderingHandler {
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks render) {
        // No-op: our tubes are items, you should not have a block in your inventory.
    }

    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks render) {
        if(!(block instanceof BlockTube)) {
            return false; // what do?
        }
        block.setBlockBoundsBasedOnState(world, x, y, z);
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if(!(te instanceof TileEntityTube)) {
            return false; // dafuq?
        }
        TileEntityTube tube = (TileEntityTube)te;

        ForgeHooksClient.bindTexture("/crs-gfx/blocks.png", 0);
        if(tube.connections[0].getOpposite() == tube.connections[1]) {
            return renderStraightTube(tube, x, y, z);
        }
        else if(tube.connections[1] == ForgeDirection.UNKNOWN) {
            return renderExitTube(tube, x, y, z);
        }

        render.renderStandardBlock(block, x, y, z);
        return true;
    }

    public boolean shouldRender3DInInventory() {
        return true;
    }

    public int getRenderId() {
        return this.renderID;
    }
// }

    public boolean renderStraightTube(TileEntityTube tube, int x, int y, int z) {
        switch(tube.connections[0]) {
            case NORTH:
            case SOUTH:
                renderStraightTubeFace(x, y, z, ForgeDirection.EAST, tube.connections[0], true);
                renderStraightTubeFace(x, y, z, ForgeDirection.WEST, tube.connections[0], true);
                renderStraightTubeFace(x, y, z, ForgeDirection.UP,   tube.connections[0], true);
                renderStraightTubeFace(x, y, z, ForgeDirection.DOWN, tube.connections[0], true);
                break;
            case UP:
            case DOWN:
                renderStraightTubeFace(x, y, z, ForgeDirection.EAST,  tube.connections[0], true);
                renderStraightTubeFace(x, y, z, ForgeDirection.WEST,  tube.connections[0], true);
                renderStraightTubeFace(x, y, z, ForgeDirection.NORTH, tube.connections[0], true);
                renderStraightTubeFace(x, y, z, ForgeDirection.SOUTH, tube.connections[0], true);
                break;
            case EAST:
            case WEST:
                renderStraightTubeFace(x, y, z, ForgeDirection.NORTH, tube.connections[0], true);
                renderStraightTubeFace(x, y, z, ForgeDirection.SOUTH, tube.connections[0], true);
                renderStraightTubeFace(x, y, z, ForgeDirection.UP,    tube.connections[0], true);
                renderStraightTubeFace(x, y, z, ForgeDirection.DOWN,  tube.connections[0], true);
                break;
        }

        return true;
    }

    public boolean renderExitTube(TileEntityTube tube, int x, int y, int z) {
        renderTubeExitFace(x, y, z, tube.connections[0].getOpposite());

        if((tube.connections[0] == ForgeDirection.NORTH) || 
           (tube.connections[0] == ForgeDirection.SOUTH)) {
            renderStraightTubeFace(x, y, z, ForgeDirection.EAST, tube.connections[0], false);
            renderStraightTubeFace(x, y, z, ForgeDirection.WEST, tube.connections[0], false);
            renderStraightTubeFace(x, y, z, ForgeDirection.UP,   tube.connections[0], false);
            renderStraightTubeFace(x, y, z, ForgeDirection.DOWN, tube.connections[0], false);
        }
        else if((tube.connections[0] == ForgeDirection.UP) || 
                (tube.connections[0] == ForgeDirection.DOWN)) {
            renderStraightTubeFace(x, y, z, ForgeDirection.EAST,  tube.connections[0], false);
            renderStraightTubeFace(x, y, z, ForgeDirection.WEST,  tube.connections[0], false);
            renderStraightTubeFace(x, y, z, ForgeDirection.NORTH, tube.connections[0], false);
            renderStraightTubeFace(x, y, z, ForgeDirection.SOUTH, tube.connections[0], false);
        }
        else if((tube.connections[0] == ForgeDirection.EAST) || 
                (tube.connections[0] == ForgeDirection.WEST)) {
            renderStraightTubeFace(x, y, z, ForgeDirection.NORTH, tube.connections[0], false);
            renderStraightTubeFace(x, y, z, ForgeDirection.SOUTH, tube.connections[0], false);
            renderStraightTubeFace(x, y, z, ForgeDirection.UP,    tube.connections[0], false);
            renderStraightTubeFace(x, y, z, ForgeDirection.DOWN,  tube.connections[0], false);
        }

        return true;
    }

    protected void renderStraightTubeFace(int x, int y, int z, ForgeDirection side, ForgeDirection pipeDirection, boolean completeTube) {
        double texTop = 0.0D / texSize;
        double texLeft = 68.0D / texSize;
        double texBottom = 16.0D / texSize;
        double texRight = 76.0D / texSize;

        double[] topRight    = new double[0];
        double[] bottomRight = new double[0];
        double[] bottomLeft  = new double[0];
        double[] topLeft     = new double[0];

        Tessellator tess = Tessellator.instance;

        if(side == ForgeDirection.EAST) {
            if((pipeDirection == ForgeDirection.NORTH) || (pipeDirection == ForgeDirection.SOUTH)){
                topRight    = new double[]{ x + 0.75D, y + 0.75D, z + 0.0D};
                bottomRight = new double[]{ x + 0.75D, y + 0.75D, z + 1.0D};
                bottomLeft  = new double[]{ x + 0.75D, y + 0.25D, z + 1.0D};
                topLeft     = new double[]{ x + 0.75D, y + 0.25D, z + 0.0D};

                if(!completeTube) {
                    if(pipeDirection == ForgeDirection.NORTH) {
                        bottomRight[2] = bottomLeft[2] = z + 0.75D;
                        texBottom = 12.0D / texSize;
                    }
                    else {
                        topRight[2] = topLeft[2] = z + 0.25D;
                        texTop = 4.0D / texSize;
                    }
                }
            }
            else {
                topRight    = new double[]{ x + 0.75D, y + 1.0D, z + 0.25D};
                bottomRight = new double[]{ x + 0.75D, y + 0.0D, z + 0.25D};
                bottomLeft  = new double[]{ x + 0.75D, y + 0.0D, z + 0.75D};
                topLeft     = new double[]{ x + 0.75D, y + 1.0D, z + 0.75D};

                if(!completeTube) {
                    if(pipeDirection == ForgeDirection.UP) {
                        bottomRight[1] = bottomLeft[1] = y + 0.25D;
                        texBottom = 12.0D / texSize;
                    }
                    else {
                        topRight[1] = topLeft[1] = y + 0.75D;
                        texTop = 4.0D / texSize;
                    }
                }
            }
        }
        else if(side == ForgeDirection.WEST) {
            if((pipeDirection == ForgeDirection.NORTH) || (pipeDirection == ForgeDirection.SOUTH)){
                topRight    = new double[]{ x + 0.25D, y + 0.75D, z + 0.0D};
                bottomRight = new double[]{ x + 0.25D, y + 0.75D, z + 1.0D};
                bottomLeft  = new double[]{ x + 0.25D, y + 0.25D, z + 1.0D};
                topLeft     = new double[]{ x + 0.25D, y + 0.25D, z + 0.0D};

                if(!completeTube) {
                    if(pipeDirection == ForgeDirection.NORTH) {
                        bottomRight[2] = bottomLeft[2] = z + 0.75D;
                        texBottom = 12.0D / texSize;
                    }
                    else {
                        topRight[2] = topLeft[2] = z + 0.25D;
                        texTop = 4.0D / texSize;
                    }
                }
            }
            else {
                topRight    = new double[]{ x + 0.25D, y + 1.0D, z + 0.25D};
                bottomRight = new double[]{ x + 0.25D, y + 0.0D, z + 0.25D};
                bottomLeft  = new double[]{ x + 0.25D, y + 0.0D, z + 0.75D};
                topLeft     = new double[]{ x + 0.25D, y + 1.0D, z + 0.75D};

                if(!completeTube) {
                    if(pipeDirection == ForgeDirection.UP) {
                        bottomRight[1] = bottomLeft[1] = y + 0.25D;
                        texBottom = 12.0D / texSize;
                    }
                    else {
                        topRight[1] = topLeft[1] = y + 0.75D;
                        texTop = 4.0D / texSize;
                    }
                }
            }
        }
        else if(side == ForgeDirection.NORTH) {
            if((pipeDirection == ForgeDirection.WEST) || (pipeDirection == ForgeDirection.EAST)){
                topRight    = new double[]{ x + 0.0D, y + 0.75D, z + 0.25D};
                bottomRight = new double[]{ x + 1.0D, y + 0.75D, z + 0.25D};
                bottomLeft  = new double[]{ x + 1.0D, y + 0.25D, z + 0.25D};
                topLeft     = new double[]{ x + 0.0D, y + 0.25D, z + 0.25D};

                if(!completeTube) {
                    if(pipeDirection == ForgeDirection.WEST) {
                        bottomRight[0] = bottomLeft[0] = x + 0.75D;
                        texBottom = 12.0D / texSize;
                    }
                    else {
                        topRight[0] = topLeft[0] = x + 0.25D;
                        texTop = 4.0D / texSize;
                    }
                }
            }
            else {
                topRight    = new double[]{ x + 0.25D, y + 1.0D, z + 0.25D};
                bottomRight = new double[]{ x + 0.25D, y + 0.0D, z + 0.25D};
                bottomLeft  = new double[]{ x + 0.75D, y + 0.0D, z + 0.25D};
                topLeft     = new double[]{ x + 0.75D, y + 1.0D, z + 0.25D};

                if(!completeTube) {
                    if(pipeDirection == ForgeDirection.UP) {
                        bottomRight[1] = bottomLeft[1] = y + 0.25D;
                        texBottom = 12.0D / texSize;
                    }
                    else {
                        topRight[1] = topLeft[1] = y + 0.75D;
                        texTop = 4.0D / texSize;
                    }
                }
            }
        }
        else if(side == ForgeDirection.SOUTH) {
            if((pipeDirection == ForgeDirection.WEST) || (pipeDirection == ForgeDirection.EAST)){
                topRight    = new double[]{ x + 0.0D, y + 0.75D, z + 0.75D};
                bottomRight = new double[]{ x + 1.0D, y + 0.75D, z + 0.75D};
                bottomLeft  = new double[]{ x + 1.0D, y + 0.25D, z + 0.75D};
                topLeft     = new double[]{ x + 0.0D, y + 0.25D, z + 0.75D};

                if(!completeTube) {
                    if(pipeDirection == ForgeDirection.WEST) {
                        bottomRight[0] = bottomLeft[0] = x + 0.75D;
                        texBottom = 12.0D / texSize;
                    }
                    else {
                        topRight[0] = topLeft[0] = x + 0.25D;
                        texTop = 4.0D / texSize;
                    }
                }
            }
            else {
                topRight    = new double[]{ x + 0.25D, y + 1.0D, z + 0.75D};
                bottomRight = new double[]{ x + 0.25D, y + 0.0D, z + 0.75D};
                bottomLeft  = new double[]{ x + 0.75D, y + 0.0D, z + 0.75D};
                topLeft     = new double[]{ x + 0.75D, y + 1.0D, z + 0.75D};

                if(!completeTube) {
                    if(pipeDirection == ForgeDirection.UP) {
                        bottomRight[1] = bottomLeft[1] = y + 0.25D;
                        texBottom = 12.0D / texSize;
                    }
                    else {
                        topRight[1] = topLeft[1] = y + 0.75D;
                        texTop = 4.0D / texSize;
                    }
                }
            }
        }
        else if(side == ForgeDirection.UP) {
            if((pipeDirection == ForgeDirection.NORTH) || (pipeDirection == ForgeDirection.SOUTH)) {
                topRight    = new double[]{ x + 0.75D, y + 0.75D, z + 0.0D};
                bottomRight = new double[]{ x + 0.75D, y + 0.75D, z + 1.0D};
                bottomLeft  = new double[]{ x + 0.25D, y + 0.75D, z + 1.0D};
                topLeft     = new double[]{ x + 0.25D, y + 0.75D, z + 0.0D};

                if(!completeTube) {
                    if(pipeDirection == ForgeDirection.NORTH) {
                        bottomRight[2] = bottomLeft[2] = z + 0.75D;
                        texBottom = 12.0D / texSize;
                    }
                    else {
                        topRight[2] = topLeft[2] = z + 0.25D;
                        texTop = 4.0D / texSize;
                    }
                }
            }
            else {
                topRight    = new double[]{ x + 0.0D, y + 0.75D, z + 0.75D};
                bottomRight = new double[]{ x + 1.0D, y + 0.75D, z + 0.75D};
                bottomLeft  = new double[]{ x + 1.0D, y + 0.75D, z + 0.25D};
                topLeft     = new double[]{ x + 0.0D, y + 0.75D, z + 0.25D};

                if(!completeTube) {
                    if(pipeDirection == ForgeDirection.WEST) {
                        bottomRight[0] = bottomLeft[0] = x + 0.75D;
                        texBottom = 12.0D / texSize;
                    }
                    else {
                        topRight[0] = topLeft[0] = x + 0.25D;
                        texTop = 4.0D / texSize;
                    }
                }
            }
        }
        else if(side == ForgeDirection.DOWN) {
            if((pipeDirection == ForgeDirection.NORTH) || (pipeDirection == ForgeDirection.SOUTH)) {
                topRight    = new double[]{ x + 0.75D, y + 0.25D, z + 0.0D};
                bottomRight = new double[]{ x + 0.75D, y + 0.25D, z + 1.0D};
                bottomLeft  = new double[]{ x + 0.25D, y + 0.25D, z + 1.0D};
                topLeft     = new double[]{ x + 0.25D, y + 0.25D, z + 0.0D};

                if(!completeTube) {
                    if(pipeDirection == ForgeDirection.NORTH) {
                        bottomRight[2] = bottomLeft[2] = z + 0.75D;
                        texBottom = 12.0D / texSize;
                    }
                    else {
                        topRight[2] = topLeft[2] = z + 0.25D;
                        texTop = 4.0D / texSize;
                    }
                }
            }
            else {
                topRight    = new double[]{ x + 0.0D, y + 0.25D, z + 0.75D};
                bottomRight = new double[]{ x + 1.0D, y + 0.25D, z + 0.75D};
                bottomLeft  = new double[]{ x + 1.0D, y + 0.25D, z + 0.25D};
                topLeft     = new double[]{ x + 0.0D, y + 0.25D, z + 0.25D};

                if(!completeTube) {
                    if(pipeDirection == ForgeDirection.WEST) {
                        bottomRight[0] = bottomLeft[0] = x + 0.75D;
                        texBottom = 12.0D / texSize;
                    }
                    else {
                        topRight[0] = topLeft[0] = x + 0.25D;
                        texTop = 4.0D / texSize;
                    }
                }
            }
        }

        tess.addVertexWithUV(   topRight[0],    topRight[1],    topRight[2], texRight, texTop);
        tess.addVertexWithUV(bottomRight[0], bottomRight[1], bottomRight[2], texRight, texBottom);
        tess.addVertexWithUV( bottomLeft[0],  bottomLeft[1],  bottomLeft[2], texLeft,  texBottom);
        tess.addVertexWithUV(    topLeft[0],     topLeft[1],     topLeft[2], texLeft,  texTop);
        tess.addVertexWithUV(    topLeft[0],     topLeft[1],     topLeft[2], texLeft,  texTop);
        tess.addVertexWithUV( bottomLeft[0],  bottomLeft[1],  bottomLeft[2], texLeft,  texBottom);
        tess.addVertexWithUV(bottomRight[0], bottomRight[1], bottomRight[2], texRight, texBottom);
        tess.addVertexWithUV(   topRight[0],    topRight[1],    topRight[2], texRight, texTop);
    }

    public void renderTubeExitFace(int x, int y, int z, ForgeDirection side) {
        double texTop = 4.0D / texSize;
        double texLeft = 20.0D / texSize;
        double texBottom = 12.0D / texSize;
        double texRight = 28.0D / texSize;

        double[] topRight    = new double[0];
        double[] bottomRight = new double[0];
        double[] bottomLeft  = new double[0];
        double[] topLeft     = new double[0];

        switch(side) {
            case NORTH:
                topRight    = new double[]{ x + 0.25D, y + 0.75D, z + 0.25D};
                bottomRight = new double[]{ x + 0.75D, y + 0.75D, z + 0.25D};
                bottomLeft  = new double[]{ x + 0.75D, y + 0.25D, z + 0.25D};
                topLeft     = new double[]{ x + 0.25D, y + 0.25D, z + 0.25D};
            break;
            case SOUTH:
                topRight    = new double[]{ x + 0.25D, y + 0.75D, z + 0.75D};
                bottomRight = new double[]{ x + 0.75D, y + 0.75D, z + 0.75D};
                bottomLeft  = new double[]{ x + 0.75D, y + 0.25D, z + 0.75D};
                topLeft     = new double[]{ x + 0.25D, y + 0.25D, z + 0.75D};
            break;
            case EAST:
                topRight    = new double[]{ x + 0.75D, y + 0.75D, z + 0.25D};
                bottomRight = new double[]{ x + 0.75D, y + 0.75D, z + 0.75D};
                bottomLeft  = new double[]{ x + 0.75D, y + 0.25D, z + 0.75D};
                topLeft     = new double[]{ x + 0.75D, y + 0.25D, z + 0.25D};
            break;
            case WEST:
                topRight    = new double[]{ x + 0.25D, y + 0.75D, z + 0.25D};
                bottomRight = new double[]{ x + 0.25D, y + 0.75D, z + 0.75D};
                bottomLeft  = new double[]{ x + 0.25D, y + 0.25D, z + 0.75D};
                topLeft     = new double[]{ x + 0.25D, y + 0.25D, z + 0.25D};
            break;
            case UP:
                topRight    = new double[]{ x + 0.75D, y + 0.75D, z + 0.25D};
                bottomRight = new double[]{ x + 0.75D, y + 0.75D, z + 0.75D};
                bottomLeft  = new double[]{ x + 0.25D, y + 0.75D, z + 0.75D};
                topLeft     = new double[]{ x + 0.25D, y + 0.75D, z + 0.25D};
            break;
            case DOWN:
                topRight    = new double[]{ x + 0.75D, y + 0.25D, z + 0.25D};
                bottomRight = new double[]{ x + 0.75D, y + 0.25D, z + 0.75D};
                bottomLeft  = new double[]{ x + 0.25D, y + 0.25D, z + 0.75D};
                topLeft     = new double[]{ x + 0.25D, y + 0.25D, z + 0.25D};
            break;
        }

        Tessellator tess = Tessellator.instance;

        tess.addVertexWithUV(   topRight[0],    topRight[1],    topRight[2], texRight, texTop);
        tess.addVertexWithUV(bottomRight[0], bottomRight[1], bottomRight[2], texRight, texBottom);
        tess.addVertexWithUV( bottomLeft[0],  bottomLeft[1],  bottomLeft[2], texLeft,  texBottom);
        tess.addVertexWithUV(    topLeft[0],     topLeft[1],     topLeft[2], texLeft,  texTop);
        tess.addVertexWithUV(    topLeft[0],     topLeft[1],     topLeft[2], texLeft,  texTop);
        tess.addVertexWithUV( bottomLeft[0],  bottomLeft[1],  bottomLeft[2], texLeft,  texBottom);
        tess.addVertexWithUV(bottomRight[0], bottomRight[1], bottomRight[2], texRight, texBottom);
        tess.addVertexWithUV(   topRight[0],    topRight[1],    topRight[2], texRight, texTop);
    }
}
