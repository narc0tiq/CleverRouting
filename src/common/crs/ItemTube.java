package crs;

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
}
