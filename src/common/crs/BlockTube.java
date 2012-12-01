package crs;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import net.minecraft.src.IBlockAccess;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

import net.minecraftforge.common.ForgeDirection;

import java.util.Random;

public class BlockTube extends BlockGeneric {
    public BlockTube(int blockID) {
        super(blockID);
        this.setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
    }

    //TODO: Need to override getSelectedBoundingBoxFromPool later

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        // Default state.
        this.setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);

        TileEntity te = world.getBlockTileEntity(x, y, z);
        if(!(te instanceof TileEntityTube)) {
            return; // what do?
        }
        TileEntityTube tube = (TileEntityTube)te;

        for(int i = 0; i < tube.connections.length; i++) {
            switch(tube.connections[i]){
                case EAST:  this.maxX = 1.0F; break;
                case WEST:  this.minX = 0.0F; break;
                case UP:    this.maxY = 1.0F; break;
                case DOWN:  this.minY = 0.0F; break;
                case SOUTH: this.maxZ = 1.0F; break;
                case NORTH: this.minZ = 0.0F; break;
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType() {
        return ClientProxy.tubeRenderer.getRenderId();
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityTube();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int data) {
        return this.createNewTileEntity(world);
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune) {
        if(CleverRouting.getSide() == Side.CLIENT) {
            return;
        }

        // TODO: Get the Tube entity, ask it to drop contents. Also ask it its item ID or stack.

        int maxDrops = quantityDropped(meta, fortune, world.rand);
        for(int i = 0; i < maxDrops; i++) {
            if(world.rand.nextFloat() > chance) {
                continue;
            }

            // TODO: Use the real tube's item, not tubeStone
            dropBlockAsItem_do(world, x, y, z, CommonProxy.tubeStone.copy());
        }
    }

    @Override
    public int idDropped(int meta, Random rand, int dmg) {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int idPicked(World world, int x, int y, int z) {
        // TODO: Use the real tube's item, not tubeStone
        return CommonProxy.tubeStone.itemID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
        return 0;
    }
}
