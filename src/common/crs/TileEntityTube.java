package crs;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.common.io.ByteArrayDataInput;

import net.minecraftforge.common.ForgeDirection;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;

public class TileEntityTube extends TileEntity {
    // Please be sure and keep this in sort() order.
    public ForgeDirection[] connections;

    public static final byte MATERIAL_STONE = 0;
    public static final byte MATERIAL_GOLD  = 1;
    public static final byte MATERIAL_BRASS = 2;
    public byte material = MATERIAL_STONE;

    public TileEntityTube() {
        super();

        this.blockType = CommonProxy.tubeBlock;
        this.connections = new ForgeDirection[]{ ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN };
    }

    public void initialize(ItemTube item) {
        this.material = item.material;
    }

    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        if(tag.hasKey("material")) {
            this.material = tag.getByte("material");
        }
        if(tag.hasKey("connection1")) {
            connections[0] = ForgeDirection.getOrientation(tag.getByte("connection1"));
        }
        if(tag.hasKey("connection2")) {
            connections[1] = ForgeDirection.getOrientation(tag.getByte("connection2"));
        }
    }

    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setByte("material", this.material);
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

    public void invalidateConnections() {
        // Do we need to invalidate an existing connection?
        for(int i = 0; i < 2; i++) {
            ForgeDirection dir = connections[i];

            int x = this.xCoord + dir.offsetX;
            int y = this.yCoord + dir.offsetY;
            int z = this.zCoord + dir.offsetZ;

            if(worldObj.getBlockId(x, y, z) != CommonProxy.tubeBlock.blockID) {
                connections[i] = ForgeDirection.UNKNOWN;
            }
        }
        Arrays.sort(connections);
    }

    public ArrayList<ForgeDirection> findConnections() {
        ArrayList<ForgeDirection> potentialConnections = new ArrayList<ForgeDirection>();
        for(ForgeDirection dir: ForgeDirection.VALID_DIRECTIONS) {
            if(dir == connections[0]) {
                continue; // Skip it, we're already there.
            }

            int x = this.xCoord + dir.offsetX;
            int y = this.yCoord + dir.offsetY;
            int z = this.zCoord + dir.offsetZ;

            if(worldObj.getBlockId(x, y, z) == CommonProxy.tubeBlock.blockID) {
                TileEntityTube other = (TileEntityTube)worldObj.getBlockTileEntity(x, y, z);
                if(other.connections[1] == ForgeDirection.UNKNOWN) {
                    potentialConnections.add(dir);
                }
                else if((other.connections[0] == dir.getOpposite())
                     || (other.connections[1] == dir.getOpposite())) {
                    // it's connected to us
                    potentialConnections.add(dir);
                }
            }
        }
        return potentialConnections;
   }

    public void updateConnections() {
        invalidateConnections();
        if(connections[1] != ForgeDirection.UNKNOWN) {
            return; // No update necessary.
        }

        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

        ArrayList<ForgeDirection> potentialConnections = findConnections();
        if(potentialConnections.size() < 1) {
            return; // Ain't nothin' there, man.
        }

        if(connections[0] == ForgeDirection.UNKNOWN) {
            connections[0] = potentialConnections.get(0);
            if(potentialConnections.size() > 1) {
                connections[1] = potentialConnections.get(1);
                return;
            }
        }
        else { // already had a connection, get a second one
            connections[1] = potentialConnections.get(0);
        }
        Arrays.sort(connections);
    }

    @Override
    public Packet getDescriptionPacket() {
        return (new PacketTubeDescriptor(this)).getPacket250();
    }

    public void writeData(DataOutputStream data) throws IOException {
        data.writeByte((byte)connections[0].ordinal());
        data.writeByte((byte)connections[1].ordinal());
        data.writeByte(material);
    }

    public void readData(ByteArrayDataInput data) throws IOException {
        connections[0] = ForgeDirection.getOrientation(data.readByte());
        connections[1] = ForgeDirection.getOrientation(data.readByte());
        material = data.readByte();
    }
}
