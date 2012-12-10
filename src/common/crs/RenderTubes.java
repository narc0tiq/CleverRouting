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
    public double tubeMaterialOffset = 0.0D; // in texels, which are the same as pixels for a 256x256px texture

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
        if(tube.material == TileEntityTube.MATERIAL_STONE) {
            this.tubeMaterialOffset = 0.0D;
        }
        else if(tube.material == TileEntityTube.MATERIAL_GOLD) {
            this.tubeMaterialOffset = 16.0D;
        }
        else if(tube.material == TileEntityTube.MATERIAL_BRASS) {
            this.tubeMaterialOffset = 32.0D;
        }

        int brightness = block.getMixedBrightnessForBlock(world, x, y, z);
        ForgeHooksClient.bindTexture(ClientProxy.BLOCKS_PNG, 0);
        if(tube.connections[0] == ForgeDirection.UNKNOWN) {
            renderStandaloneTube(world, x, y, z, block, brightness, render);
        }
        else if(tube.connections[1] == ForgeDirection.UNKNOWN) {
            renderExitTube(tube, x, y, z, brightness);
        }
        else if(tube.connections[0].getOpposite() == tube.connections[1]) {
            renderStraightTube(tube, x, y, z, brightness);
        }
        else {
            renderElbowTube(tube, x, y, z, brightness);
        }

        return true;
    }

    public boolean shouldRender3DInInventory() {
        return true;
    }

    public int getRenderId() {
        return this.renderID;
    }
// }

    public void renderStandaloneTube(IBlockAccess world, int x, int y, int z, Block block, int brightness, RenderBlocks render) {
        render.renderStandardBlock(block, x, y, z); // First the outer faces...

        int texIndex = block.getBlockTexture(world, x, y, z, 0);
        Tessellator tess = Tessellator.instance;
        for(ForgeDirection side: ForgeDirection.VALID_DIRECTIONS) {
            switch(side) { // ...then the inner faces.
                case UP:    render.renderTopFace   (block, x, y - 0.5D, z, texIndex); break;
                case DOWN:  render.renderBottomFace(block, x, y + 0.5D, z, texIndex); break;
                case NORTH: render.renderNorthFace (block, x + 0.5D, y, z, texIndex); break;
                case SOUTH: render.renderSouthFace (block, x - 0.5D, y, z, texIndex); break;
                case EAST:  render.renderEastFace  (block, x, y, z + 0.5D, texIndex); break;
                case WEST:  render.renderWestFace  (block, x, y, z - 0.5D, texIndex); break;
            }
        }
    }

    public boolean renderStraightTube(TileEntityTube tube, int x, int y, int z, int brightness) {
        switch(tube.connections[0]) {
            case NORTH:
            case SOUTH:
                renderStraightTubeFace(x, y, z, ForgeDirection.EAST, tube.connections[0], true, brightness);
                renderStraightTubeFace(x, y, z, ForgeDirection.WEST, tube.connections[0], true, brightness);
                renderStraightTubeFace(x, y, z, ForgeDirection.UP,   tube.connections[0], true, brightness);
                renderStraightTubeFace(x, y, z, ForgeDirection.DOWN, tube.connections[0], true, brightness);
                break;
            case UP:
            case DOWN:
                renderStraightTubeFace(x, y, z, ForgeDirection.EAST,  tube.connections[0], true, brightness);
                renderStraightTubeFace(x, y, z, ForgeDirection.WEST,  tube.connections[0], true, brightness);
                renderStraightTubeFace(x, y, z, ForgeDirection.NORTH, tube.connections[0], true, brightness);
                renderStraightTubeFace(x, y, z, ForgeDirection.SOUTH, tube.connections[0], true, brightness);
                break;
            case EAST:
            case WEST:
                renderStraightTubeFace(x, y, z, ForgeDirection.NORTH, tube.connections[0], true, brightness);
                renderStraightTubeFace(x, y, z, ForgeDirection.SOUTH, tube.connections[0], true, brightness);
                renderStraightTubeFace(x, y, z, ForgeDirection.UP,    tube.connections[0], true, brightness);
                renderStraightTubeFace(x, y, z, ForgeDirection.DOWN,  tube.connections[0], true, brightness);
                break;
        }

        return true;
    }

    public boolean renderExitTube(TileEntityTube tube, int x, int y, int z, int brightness) {
        renderTubeExitFace(x, y, z, tube.connections[0].getOpposite(), brightness);

        if((tube.connections[0] == ForgeDirection.NORTH) || 
           (tube.connections[0] == ForgeDirection.SOUTH)) {
            renderStraightTubeFace(x, y, z, ForgeDirection.EAST, tube.connections[0], false, brightness);
            renderStraightTubeFace(x, y, z, ForgeDirection.WEST, tube.connections[0], false, brightness);
            renderStraightTubeFace(x, y, z, ForgeDirection.UP,   tube.connections[0], false, brightness);
            renderStraightTubeFace(x, y, z, ForgeDirection.DOWN, tube.connections[0], false, brightness);
        }
        else if((tube.connections[0] == ForgeDirection.UP) || 
                (tube.connections[0] == ForgeDirection.DOWN)) {
            renderStraightTubeFace(x, y, z, ForgeDirection.EAST,  tube.connections[0], false, brightness);
            renderStraightTubeFace(x, y, z, ForgeDirection.WEST,  tube.connections[0], false, brightness);
            renderStraightTubeFace(x, y, z, ForgeDirection.NORTH, tube.connections[0], false, brightness);
            renderStraightTubeFace(x, y, z, ForgeDirection.SOUTH, tube.connections[0], false, brightness);
        }
        else if((tube.connections[0] == ForgeDirection.EAST) || 
                (tube.connections[0] == ForgeDirection.WEST)) {
            renderStraightTubeFace(x, y, z, ForgeDirection.NORTH, tube.connections[0], false, brightness);
            renderStraightTubeFace(x, y, z, ForgeDirection.SOUTH, tube.connections[0], false, brightness);
            renderStraightTubeFace(x, y, z, ForgeDirection.UP,    tube.connections[0], false, brightness);
            renderStraightTubeFace(x, y, z, ForgeDirection.DOWN,  tube.connections[0], false, brightness);
        }

        return true;
    }

    public boolean renderElbowTube(TileEntityTube tube, int x, int y, int z, int brightness) {
        renderStraightTubeFace(x, y, z, tube.connections[0].getOpposite(),
                                        tube.connections[1],  false, brightness);
        renderStraightTubeFace(x, y, z, tube.connections[1].getOpposite(),
                                        tube.connections[0],  false, brightness);
        renderElbowInnerFace(x, y, z, tube.connections[0], tube.connections[1], brightness);
        renderElbowInnerFace(x, y, z, tube.connections[1], tube.connections[0], brightness);

        ForgeDirection elbowSide = tube.connections[0].getRotation(tube.connections[1]);
        renderElbowTubeFace(x, y, z, elbowSide, tube.connections, brightness);
        renderElbowTubeFace(x, y, z, elbowSide.getOpposite(), tube.connections, brightness);

        return true;
    }

    protected void renderStraightTubeFace(int x, int y, int z, ForgeDirection side, ForgeDirection pipeDirection, boolean completeTube, int brightness) {
        double texTop = (tubeMaterialOffset + 0.0D) / texSize;
        double texBottom = (tubeMaterialOffset + 16.0D) / texSize;
        double texLeft = 52.0D / texSize;
        double texRight = 60.0D / texSize;

        double[] topRight    = new double[3];
        double[] bottomRight = new double[3];
        double[] bottomLeft  = new double[3];
        double[] topLeft     = new double[3];

        if((side == ForgeDirection.EAST) || (side == ForgeDirection.WEST)) {
            if(side == ForgeDirection.EAST) {
                topRight[0] = bottomRight[0] = bottomLeft[0] = topLeft[0] = x + 0.75D;
            }
            else {
                topRight[0] = bottomRight[0] = bottomLeft[0] = topLeft[0] = x + 0.25D;
            }

            if((pipeDirection == ForgeDirection.NORTH) || (pipeDirection == ForgeDirection.SOUTH)){
                topLeft[1] = bottomLeft[1] = y + 0.25D;
                topRight[1] = bottomRight[1] = y + 0.75D;

                topLeft[2] = topRight[2] = z + 0.0D;
                bottomLeft[2] = bottomRight[2] = z + 1.0D;

                if(!completeTube) {
                    if(pipeDirection == ForgeDirection.NORTH) {
                        bottomRight[2] = bottomLeft[2] = z + 0.75D;
                        texBottom = (tubeMaterialOffset + 12.0D) / texSize;
                    }
                    else {
                        topRight[2] = topLeft[2] = z + 0.25D;
                        texTop = (tubeMaterialOffset + 4.0D) / texSize;
                    }
                }
            }
            else if((pipeDirection == ForgeDirection.UP) || (pipeDirection == ForgeDirection.DOWN)){
                topLeft[2] = bottomLeft[2] = z + 0.25D;
                topRight[2] = bottomRight[2] = z + 0.75D;

                topLeft[1] = topRight[1] = y + 0.0D;
                bottomLeft[1] = bottomRight[1] = y + 1.0D;

                if(!completeTube) {
                    if(pipeDirection == ForgeDirection.UP) {
                        topRight[1] = topLeft[1] = y + 0.25D;
                        texTop = (tubeMaterialOffset + 4.0D) / texSize;
                    }
                    else {
                        bottomRight[1] = bottomLeft[1] = y + 0.75D;
                        texBottom = (tubeMaterialOffset + 12.0D) / texSize;
                    }
                }
            }
        }
        else if((side == ForgeDirection.NORTH) || (side == ForgeDirection.SOUTH)) {
            if(side == ForgeDirection.SOUTH) {
                topRight[2] = bottomRight[2] = bottomLeft[2] = topLeft[2] = z + 0.75D;
            }
            else {
                topRight[2] = bottomRight[2] = bottomLeft[2] = topLeft[2] = z + 0.25D;
            }

            if((pipeDirection == ForgeDirection.WEST) || (pipeDirection == ForgeDirection.EAST)){
                topLeft[1] = bottomLeft[1] = y + 0.25D;
                topRight[1] = bottomRight[1] = y + 0.75D;

                topLeft[0] = topRight[0] = x + 0.0D;
                bottomLeft[0] = bottomRight[0] = x + 1.0D;

                if(!completeTube) {
                    if(pipeDirection == ForgeDirection.WEST) {
                        bottomRight[0] = bottomLeft[0] = x + 0.75D;
                        texBottom = (tubeMaterialOffset + 12.0D) / texSize;
                    }
                    else {
                        topRight[0] = topLeft[0] = x + 0.25D;
                        texTop = (tubeMaterialOffset + 4.0D) / texSize;
                    }
                }
            }
            else if((pipeDirection == ForgeDirection.UP) || (pipeDirection == ForgeDirection.DOWN)){
                topLeft[0] = bottomLeft[0] = x + 0.25D;
                topRight[0] = bottomRight[0] = x + 0.75D;

                topLeft[1] = topRight[1] = y + 0.0D;
                bottomLeft[1] = bottomRight[1] = y + 1.0D;

                if(!completeTube) {
                    if(pipeDirection == ForgeDirection.UP) {
                        topRight[1] = topLeft[1] = y + 0.25D;
                        texTop = (tubeMaterialOffset + 4.0D) / texSize;
                    }
                    else {
                        bottomRight[1] = bottomLeft[1] = y + 0.75D;
                        texBottom = (tubeMaterialOffset + 12.0D) / texSize;
                    }
                }
            }
        }
        else if((side == ForgeDirection.UP) || (side == ForgeDirection.DOWN)) {
            if(side == ForgeDirection.UP) {
                topRight[1] = bottomRight[1] = bottomLeft[1] = topLeft[1] = y + 0.75D;
            }
            else {
                topRight[1] = bottomRight[1] = bottomLeft[1] = topLeft[1] = y + 0.25;
            }

            if((pipeDirection == ForgeDirection.NORTH) || (pipeDirection == ForgeDirection.SOUTH)) {
                topLeft[0] = bottomLeft[0] = x + 0.25D;
                topRight[0] = bottomRight[0] = x + 0.75D;

                topLeft[2] = topRight[2] = z + 0.0D;
                bottomLeft[2] = bottomRight[2] = z + 1.0D;

                if(!completeTube) {
                    if(pipeDirection == ForgeDirection.NORTH) {
                        bottomRight[2] = bottomLeft[2] = z + 0.75D;
                        texBottom = (tubeMaterialOffset + 12.0D) / texSize;
                    }
                    else {
                        topRight[2] = topLeft[2] = z + 0.25D;
                        texTop = (tubeMaterialOffset + 4.0D) / texSize;
                    }
                }
            }
            else
            if((pipeDirection == ForgeDirection.EAST) || (pipeDirection == ForgeDirection.WEST)) {
                topLeft[2] = bottomLeft[2] = z + 0.25D;
                topRight[2] = bottomRight[2] = z + 0.75D;

                topLeft[0] = topRight[0] = x + 0.0D;
                bottomLeft[0] = bottomRight[0] = x + 1.0D;

                if(!completeTube) {
                    if(pipeDirection == ForgeDirection.WEST) {
                        bottomRight[0] = bottomLeft[0] = x + 0.75D;
                        texBottom = (tubeMaterialOffset + 12.0D) / texSize;
                    }
                    else {
                        topRight[0] = topLeft[0] = x + 0.25D;
                        texTop = (tubeMaterialOffset + 4.0D) / texSize;
                    }
                }
            }
        }

        Tessellator tess = Tessellator.instance;
        tess.setBrightness(brightness);
        float colorMult = getColorMultForFace(side);
        tess.setColorOpaque_F(colorMult, colorMult, colorMult);

        tess.addVertexWithUV(   topRight[0],    topRight[1],    topRight[2], texRight, texTop);
        tess.addVertexWithUV(bottomRight[0], bottomRight[1], bottomRight[2], texRight, texBottom);
        tess.addVertexWithUV( bottomLeft[0],  bottomLeft[1],  bottomLeft[2], texLeft,  texBottom);
        tess.addVertexWithUV(    topLeft[0],     topLeft[1],     topLeft[2], texLeft,  texTop);
        tess.addVertexWithUV(    topLeft[0],     topLeft[1],     topLeft[2], texLeft,  texTop);
        tess.addVertexWithUV( bottomLeft[0],  bottomLeft[1],  bottomLeft[2], texLeft,  texBottom);
        tess.addVertexWithUV(bottomRight[0], bottomRight[1], bottomRight[2], texRight, texBottom);
        tess.addVertexWithUV(   topRight[0],    topRight[1],    topRight[2], texRight, texTop);
    }

    public void renderTubeExitFace(int x, int y, int z, ForgeDirection side, int brightness) {
        double texTop = (tubeMaterialOffset + 4.0D) / texSize;
        double texBottom = (tubeMaterialOffset + 12.0D) / texSize;
        double texLeft = 20.0D / texSize;
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
        tess.setBrightness(brightness);
        float colorMult = getColorMultForFace(side);
        tess.setColorOpaque_F(colorMult, colorMult, colorMult);

        tess.addVertexWithUV(   topRight[0],    topRight[1],    topRight[2], texRight, texTop);
        tess.addVertexWithUV(bottomRight[0], bottomRight[1], bottomRight[2], texRight, texBottom);
        tess.addVertexWithUV( bottomLeft[0],  bottomLeft[1],  bottomLeft[2], texLeft,  texBottom);
        tess.addVertexWithUV(    topLeft[0],     topLeft[1],     topLeft[2], texLeft,  texTop);
        tess.addVertexWithUV(    topLeft[0],     topLeft[1],     topLeft[2], texLeft,  texTop);
        tess.addVertexWithUV( bottomLeft[0],  bottomLeft[1],  bottomLeft[2], texLeft,  texBottom);
        tess.addVertexWithUV(bottomRight[0], bottomRight[1], bottomRight[2], texRight, texBottom);
        tess.addVertexWithUV(   topRight[0],    topRight[1],    topRight[2], texRight, texTop);
    }

    protected void renderElbowTubeFace(int x, int y, int z, ForgeDirection side, ForgeDirection[] pipeDirections, int brightness) {
        double texTop = (tubeMaterialOffset + 0.0D) / texSize;
        double texBottom = (tubeMaterialOffset + 12.0D) / texSize;
        double texLeft = 32.0D / texSize;
        double texRight = 44.0D / texSize;

        double[] topRight    = new double[3];
        double[] bottomRight = new double[3];
        double[] bottomLeft  = new double[3];
        double[] topLeft     = new double[3];

        if((side == ForgeDirection.UP) || (side == ForgeDirection.DOWN)) {
            if(side == ForgeDirection.UP) {
                topRight[1] = bottomRight[1] = bottomLeft[1] = topLeft[1] = y + 0.75D;
            }
            else {
                topRight[1] = bottomRight[1] = bottomLeft[1] = topLeft[1] = y + 0.25D;
            }

            if((pipeDirections[0] == ForgeDirection.NORTH) &&
               (pipeDirections[1] == ForgeDirection.EAST)) {
                topRight[0] = topLeft[0] = x + 1.0D;
                bottomRight[0] = bottomLeft[0] = x + 0.25D;

                topLeft[2] = bottomLeft[2] = z + 0.0D;
                topRight[2] = bottomRight[2] = z + 0.75D;
            }
            else if((pipeDirections[0] == ForgeDirection.SOUTH) &&
               (pipeDirections[1] == ForgeDirection.WEST)) {
                topRight[0] = topLeft[0] = x + 0.0D;
                bottomRight[0] = bottomLeft[0] = x + 0.75D;

                topLeft[2] = bottomLeft[2] = z + 1.0D;
                topRight[2] = bottomRight[2] = z + 0.25D;
            }
            else if((pipeDirections[0] == ForgeDirection.NORTH) &&
               (pipeDirections[1] == ForgeDirection.WEST)) {
                topRight[2] = topLeft[2] = z + 0.0D;
                bottomRight[2] = bottomLeft[2] = z + 0.75D;

                topLeft[0] = bottomLeft[0] = x + 0.0D;
                topRight[0] = bottomRight[0] = x + 0.75D;
            }
            else if((pipeDirections[0] == ForgeDirection.SOUTH) &&
               (pipeDirections[1] == ForgeDirection.EAST)) {
                topRight[2] = topLeft[2] = z + 1.0D;
                bottomRight[2] = bottomLeft[2] = z + 0.25D;

                topLeft[0] = bottomLeft[0] = x + 1.0D;
                topRight[0] = bottomRight[0] = x + 0.25D;
            }
        }
        else if((side == ForgeDirection.NORTH) || (side == ForgeDirection.SOUTH)) {
            if(side == ForgeDirection.NORTH) {
                topRight[2] = bottomRight[2] = bottomLeft[2] = topLeft[2] = z + 0.25D;
            }
            else {
                topRight[2] = bottomRight[2] = bottomLeft[2] = topLeft[2] = z + 0.75D;
            }

            if((pipeDirections[0] == ForgeDirection.UP) &&
               (pipeDirections[1] == ForgeDirection.EAST)) {
                topRight[0] = topLeft[0] = x + 1.0D;
                bottomRight[0] = bottomLeft[0] = x + 0.25D;

                topLeft[1] = bottomLeft[1] = y + 1.0D;
                topRight[1] = bottomRight[1] = y + 0.25D;
            }
            else if((pipeDirections[0] == ForgeDirection.DOWN) &&
               (pipeDirections[1] == ForgeDirection.WEST)) {
                topRight[0] = topLeft[0] = x + 0.0D;
                bottomRight[0] = bottomLeft[0] = x + 0.75D;

                topLeft[1] = bottomLeft[1] = y + 0.0D;
                topRight[1] = bottomRight[1] = y + 0.75D;
            }
            else if((pipeDirections[0] == ForgeDirection.UP) &&
               (pipeDirections[1] == ForgeDirection.WEST)) {
                topRight[1] = topLeft[1] = y + 1.0D;
                bottomRight[1] = bottomLeft[1] = y + 0.25D;

                topLeft[0] = bottomLeft[0] = x + 0.0D;
                topRight[0] = bottomRight[0] = x + 0.75D;
            }
            else if((pipeDirections[0] == ForgeDirection.DOWN) &&
               (pipeDirections[1] == ForgeDirection.EAST)) {
                topRight[1] = topLeft[1] = y + 0.0D;
                bottomRight[1] = bottomLeft[1] = y + 0.75D;

                topLeft[0] = bottomLeft[0] = x + 1.0D;
                topRight[0] = bottomRight[0] = x + 0.25D;
            }
        }
        else if((side == ForgeDirection.EAST) || (side == ForgeDirection.WEST)) {
            if(side == ForgeDirection.EAST) {
                topRight[0] = bottomRight[0] = bottomLeft[0] = topLeft[0] = x + 0.75D;
            }
            else {
                topRight[0] = bottomRight[0] = bottomLeft[0] = topLeft[0] = x + 0.25D;
            }

            if((pipeDirections[0] == ForgeDirection.UP) &&
               (pipeDirections[1] == ForgeDirection.NORTH)) {
                topRight[2] = topLeft[2] = z + 0.0D;
                bottomRight[2] = bottomLeft[2] = z + 0.75D;

                topLeft[1] = bottomLeft[1] = y + 1.0D;
                topRight[1] = bottomRight[1] = y + 0.25D;
            }
            else if((pipeDirections[0] == ForgeDirection.DOWN) &&
               (pipeDirections[1] == ForgeDirection.SOUTH)) {
                topRight[2] = topLeft[2] = z + 1.0D;
                bottomRight[2] = bottomLeft[2] = z + 0.25D;

                topLeft[1] = bottomLeft[1] = y + 0.0D;
                topRight[1] = bottomRight[1] = y + 0.75D;
            }
            else if((pipeDirections[0] == ForgeDirection.UP) &&
               (pipeDirections[1] == ForgeDirection.SOUTH)) {
                topRight[2] = topLeft[2] = z + 1.0D;
                bottomRight[2] = bottomLeft[2] = z + 0.25D;

                topLeft[1] = bottomLeft[1] = y + 1.0D;
                topRight[1] = bottomRight[1] = y + 0.25D;
            }
            else if((pipeDirections[0] == ForgeDirection.DOWN) &&
               (pipeDirections[1] == ForgeDirection.NORTH)) {
                topRight[2] = topLeft[2] = z + 0.0D;
                bottomRight[2] = bottomLeft[2] = z + 0.75D;

                topLeft[1] = bottomLeft[1] = y + 0.0D;
                topRight[1] = bottomRight[1] = y + 0.75D;
            }
        }

        Tessellator tess = Tessellator.instance;
        tess.setBrightness(brightness);
        float colorMult = getColorMultForFace(side);
        tess.setColorOpaque_F(colorMult, colorMult, colorMult);

        tess.addVertexWithUV(   topRight[0],    topRight[1],    topRight[2], texRight, texTop);
        tess.addVertexWithUV(bottomRight[0], bottomRight[1], bottomRight[2], texRight, texBottom);
        tess.addVertexWithUV( bottomLeft[0],  bottomLeft[1],  bottomLeft[2], texLeft,  texBottom);
        tess.addVertexWithUV(    topLeft[0],     topLeft[1],     topLeft[2], texLeft,  texTop);
        tess.addVertexWithUV(    topLeft[0],     topLeft[1],     topLeft[2], texLeft,  texTop);
        tess.addVertexWithUV( bottomLeft[0],  bottomLeft[1],  bottomLeft[2], texLeft,  texBottom);
        tess.addVertexWithUV(bottomRight[0], bottomRight[1], bottomRight[2], texRight, texBottom);
        tess.addVertexWithUV(   topRight[0],    topRight[1],    topRight[2], texRight, texTop);
    }

    protected void renderElbowInnerFace(int x, int y, int z, ForgeDirection side, ForgeDirection pipeDirection, int brightness) {
        double texTop = (tubeMaterialOffset + 0.0D) / texSize;
        double texBottom = (tubeMaterialOffset + 4.0D) / texSize;
        double texLeft = 52.0D / texSize;
        double texRight = 60.0D / texSize;

        double[] topRight    = new double[3];
        double[] bottomRight = new double[3];
        double[] bottomLeft  = new double[3];
        double[] topLeft     = new double[3];

        if((side == ForgeDirection.NORTH) || (side == ForgeDirection.SOUTH)) {
            if(side == ForgeDirection.NORTH) {
                topRight[2] = bottomRight[2] = bottomLeft[2] = topLeft[2] = z + 0.25D;
            }
            else {
                topRight[2] = bottomRight[2] = bottomLeft[2] = topLeft[2] = z + 0.75D;
            }

            if((pipeDirection == ForgeDirection.WEST) || (pipeDirection == ForgeDirection.EAST)) {
                topLeft[1] = bottomLeft[1] = y + 0.75D;
                topRight[1] = bottomRight[1] = y +  0.25D;

                if(pipeDirection == ForgeDirection.EAST) {
                    topLeft[0] = topRight[0] = x + 1.0D;
                    bottomLeft[0] = bottomRight[0] = x + 0.75D;
                }
                else {
                    topLeft[0] = topRight[0] = x + 0.0D;
                    bottomLeft[0] = bottomRight[0] = x + 0.25D;
                }
            }
            else
            if((pipeDirection == ForgeDirection.UP) || (pipeDirection == ForgeDirection.DOWN)) {
                topLeft[0] = bottomLeft[0] = x + 0.75D;
                topRight[0] = bottomRight[0] = x + 0.25D;

                if(pipeDirection == ForgeDirection.UP) {
                    topLeft[1] = topRight[1] = y + 1.0D;
                    bottomLeft[1] = bottomRight[1] = y + 0.75D;
                }
                else {
                    topLeft[1] = topRight[1] = y + 0.0D;
                    bottomLeft[1] = bottomRight[1] = y + 0.25D;
                }
            }
        }
        else if((side == ForgeDirection.EAST) || (side == ForgeDirection.WEST)) {
            if(side == ForgeDirection.EAST) {
                topRight[0] = bottomRight[0] = bottomLeft[0] = topLeft[0] = x + 0.75D;
            }
            else {
                topRight[0] = bottomRight[0] = bottomLeft[0] = topLeft[0] = x + 0.25D;
            }

            if((pipeDirection == ForgeDirection.NORTH) || (pipeDirection == ForgeDirection.SOUTH)) {
                topLeft[1] = bottomLeft[1] = y + 0.75D;
                topRight[1] = bottomRight[1] = y + 0.25D;

                if(pipeDirection == ForgeDirection.SOUTH) {
                    topLeft[2] = topRight[2] = z + 1.0D;
                    bottomLeft[2] = bottomRight[2] = z + 0.75D;
                }
                else {
                    topLeft[2] = topRight[2] = z + 0.0D;
                    bottomLeft[2] = bottomRight[2] = z + 0.25D;
                }
            }
            else
            if((pipeDirection == ForgeDirection.UP) || (pipeDirection == ForgeDirection.DOWN)) {
                topLeft[2] = bottomLeft[2] = z + 0.75D;
                topRight[2] = bottomRight[2] = z +  0.25D;

                if(pipeDirection == ForgeDirection.UP) {
                    topLeft[1] = topRight[1] = y + 1.0D;
                    bottomLeft[1] = bottomRight[1] = y + 0.75D;
                }
                else {
                    topLeft[1] = topRight[1] = y + 0.0D;
                    bottomLeft[1] = bottomRight[1] = y + 0.25D;
                }
            }
        }
        else if((side == ForgeDirection.UP) || (side == ForgeDirection.DOWN)) {
            if(side == ForgeDirection.UP) {
                topRight[1] = bottomRight[1] = bottomLeft[1] = topLeft[1] = y + 0.75D;
            }
            else {
                topRight[1] = bottomRight[1] = bottomLeft[1] = topLeft[1] = y + 0.25D;
            }

            if((pipeDirection == ForgeDirection.NORTH) || (pipeDirection == ForgeDirection.SOUTH)) {
                topLeft[0] = bottomLeft[0] = x + 0.75D;
                topRight[0] = bottomRight[0] = x + 0.25D;

                if(pipeDirection == ForgeDirection.SOUTH) {
                    topLeft[2] = topRight[2] = z + 1.0D;
                    bottomLeft[2] = bottomRight[2] = z + 0.75D;
                }
                else {
                    topLeft[2] = topRight[2] = z + 0.0D;
                    bottomLeft[2] = bottomRight[2] = z + 0.25D;
                }
            }
            else
            if((pipeDirection == ForgeDirection.WEST) || (pipeDirection == ForgeDirection.EAST)) {
                topLeft[2] = bottomLeft[2] = z + 0.75D;
                topRight[2] = bottomRight[2] = z +  0.25D;

                if(pipeDirection == ForgeDirection.EAST) {
                    topLeft[0] = topRight[0] = x + 1.0D;
                    bottomLeft[0] = bottomRight[0] = x + 0.75D;
                }
                else {
                    topLeft[0] = topRight[0] = x + 0.0D;
                    bottomLeft[0] = bottomRight[0] = x + 0.25D;
                }
            }
        }

        Tessellator tess = Tessellator.instance;
        tess.setBrightness(brightness);
        float colorMult = getColorMultForFace(side);
        tess.setColorOpaque_F(colorMult, colorMult, colorMult);

        tess.addVertexWithUV(   topRight[0],    topRight[1],    topRight[2], texRight, texTop);
        tess.addVertexWithUV(bottomRight[0], bottomRight[1], bottomRight[2], texRight, texBottom);
        tess.addVertexWithUV( bottomLeft[0],  bottomLeft[1],  bottomLeft[2], texLeft,  texBottom);
        tess.addVertexWithUV(    topLeft[0],     topLeft[1],     topLeft[2], texLeft,  texTop);
        tess.addVertexWithUV(    topLeft[0],     topLeft[1],     topLeft[2], texLeft,  texTop);
        tess.addVertexWithUV( bottomLeft[0],  bottomLeft[1],  bottomLeft[2], texLeft,  texBottom);
        tess.addVertexWithUV(bottomRight[0], bottomRight[1], bottomRight[2], texRight, texBottom);
        tess.addVertexWithUV(   topRight[0],    topRight[1],    topRight[2], texRight, texTop);
    }

    public float getColorMultForFace(ForgeDirection face) {
        if(face == ForgeDirection.UP) {
            return 0.9F;
        }
        else if(face == ForgeDirection.DOWN) {
            return 0.7F;
        }
        else {
            return 0.8F;
        }
    }
}
