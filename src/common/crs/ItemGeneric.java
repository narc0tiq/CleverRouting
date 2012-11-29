package crs;

import net.minecraft.src.Item;

public class ItemGeneric extends Item {
    public ItemGeneric(String name, int itemID, int iconIndex) {
        super(itemID);

        this.setItemName(name);
        this.setTextureFile("/crs-gfx/items.png");
        this.setIconIndex(iconIndex);
    }
}
