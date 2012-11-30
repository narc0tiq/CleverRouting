package crs;

import net.minecraftforge.common.ForgeDirection;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class TileEntityTube extends TileEntity {
    public ForgeDirection[] connections;

    public TileEntityTube() {
        super();

        this.blockType = CommonProxy.tubeBlock;
        // HAX: Debug connections.
        this.connections = new ForgeDirection[]{ ForgeDirection.WEST, ForgeDirection.UNKNOWN };
    }

    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
    }

    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
    }

    public boolean canUpdate() {
        return false; // should not need to. Flip if necessary.
    }
}
