package crs;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.google.common.io.ByteArrayDataInput;

import net.minecraftforge.common.ForgeDirection;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;

public class TileEntityTube extends TileEntity {
    // Please be sure and keep this in sort() order.
    public ForgeDirection[] connections;

    public TileEntityTube() {
        super();

        this.blockType = CommonProxy.tubeBlock;
        this.connections = new ForgeDirection[]{ ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN };
    }

    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        if(tag.hasKey("connection1")) {
            connections[0] = ForgeDirection.getOrientation(tag.getByte("connection1"));
        }
        if(tag.hasKey("connection2")) {
            connections[1] = ForgeDirection.getOrientation(tag.getByte("connection2"));
        }
    }

    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        if(connections[0] != ForgeDirection.UNKNOWN) {
            tag.setByte("connection1", (byte)connections[0].ordinal());
        }
        if(connections[1] != ForgeDirection.UNKNOWN) {
            tag.setByte("connection2", (byte)connections[1].ordinal());
        }
    }

    public boolean canUpdate() {
        return false; // should not need to. Flip if necessary.
    }

    public void updateConnections() {
        ArrayList<ForgeDirection> potentialConnections = new ArrayList<ForgeDirection>();

        for(ForgeDirection dir: ForgeDirection.VALID_DIRECTIONS) {
            int x = this.xCoord + dir.offsetX;
            int y = this.yCoord + dir.offsetY;
            int z = this.zCoord + dir.offsetZ;

            if(worldObj.getBlockId(x, y, z) == CommonProxy.tubeBlock.blockID) {
                potentialConnections.add(dir);
            }
        }

        if(potentialConnections.size() < 1) {
            this.connections[0] = this.connections[1] = ForgeDirection.UNKNOWN;
            return;
        }

        this.connections[0] = potentialConnections.get(0);
        if(potentialConnections.size() > 1) {
            this.connections[1] = potentialConnections.get(1);
        }
        else {
            this.connections[1] = ForgeDirection.UNKNOWN;
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        return (new PacketTubeDescriptor(this)).getPacket250();
    }

    public void writeData(DataOutputStream data) throws IOException {
        data.writeByte((byte)connections[0].ordinal());
        data.writeByte((byte)connections[1].ordinal());
    }

    public void readData(ByteArrayDataInput data) throws IOException {
        connections[0] = ForgeDirection.getOrientation(data.readByte());
        connections[1] = ForgeDirection.getOrientation(data.readByte());
    }
}
