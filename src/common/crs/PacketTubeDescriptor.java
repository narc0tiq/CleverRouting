package crs;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class PacketTubeDescriptor {
    public TileEntityTube tube;

    public PacketTubeDescriptor(TileEntityTube tube) {
        this.tube = tube;
    }

    public Packet getPacket250() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(bytes);

        try {
            data.writeByte(CommonProxy.tubePacketID);
            data.writeInt(tube.xCoord);
            data.writeInt(tube.yCoord);
            data.writeInt(tube.zCoord);
            tube.writeData(data);
        }
        catch(IOException e) {
            // and ignore the living daylights out of it
        }

        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = CommonProxy.channelName;
        packet.data = bytes.toByteArray();
        packet.length = bytes.size();
        packet.isChunkDataPacket = true;

        return packet;
    }

    public static void readPacket250(ByteArrayDataInput data) throws IOException {
        int x = data.readInt();
        int y = data.readInt();
        int z = data.readInt();

        World world = CleverRouting.proxy.getClientWorld();
        assert world != null: "Thewe's somethin' scwewy going on hewe!";
        TileEntity te = world.getBlockTileEntity(x, y, z);

        if(!(te instanceof TileEntityTube)) {
            return; // Dafuq?
        }

        ((TileEntityTube)te).readData(data);
    }
}
