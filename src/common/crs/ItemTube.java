package crs;

import cpw.mods.fml.common.Side;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

import net.minecraftforge.common.ForgeDirection;

public class ItemTube extends ItemGeneric {
    public byte material = 0;

    public ItemTube(String ident, int itemID, byte material) {
        super("tube." + ident, itemID, (material * 16));

        this.material = material;
    }

    @Override
    public String getTextureFile() {
        return ClientProxy.BLOCKS_PNG;
    }

    public int getTextureOffset() {
        if(material == TileEntityTube.MATERIAL_STONE) {
            return 0;
        }
        else if(material == TileEntityTube.MATERIAL_GOLD) {
            return 16;
        }
        else if(material == TileEntityTube.MATERIAL_BRASS) {
            return 32;
        }

        return 0; // this should never happen. ...yeah...
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if(stack.stackSize <= 0) {
            return false;
        }

        if(world.getBlockId(x, y, z) == Block.snow.blockID) {
            side = 0;
        }
        else {
            ForgeDirection direction = ForgeDirection.getOrientation(side);
            x += direction.offsetX;
            y += direction.offsetY;
            z += direction.offsetZ;
        }

        int blockID = CommonProxy.tubeBlock.blockID;
        if(player.canCurrentToolHarvestBlock(x, y, z)
                && world.canPlaceEntityOnSide(blockID, x, y, z, false, side, player)) {

            if(placeTube(world, x, y, z)) {
                CommonProxy.tubeBlock.onBlockPlacedBy(world, x, y, z, player);
                stack.stackSize--;
            }
            return true;
        }
        else {
            return false;
        }
    }

    public boolean placeTube(World world, int x, int y, int z) {
        boolean placed = world.setBlockAndMetadataWithNotify(x, y, z, CommonProxy.tubeBlock.blockID, 0);

        if(placed) {
            TileEntityTube tube = (TileEntityTube)world.getBlockTileEntity(x, y, z);
            tube.initialize(this);
        }

        return placed;
    }
}
