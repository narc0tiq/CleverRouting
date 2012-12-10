package crs;

import net.minecraft.src.Item;

public class ItemGeneric extends Item {
    public ItemGeneric(String name, int itemID, int iconIndex) {
        super(itemID);

        this.setItemName("crs." + name);
        this.setTextureFile(ClientProxy.ITEMS_PNG);
        this.setIconIndex(iconIndex);
    }
}
