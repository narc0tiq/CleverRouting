package crs;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class CommonProxy {
    public static Block tubeBlock;
    public static ItemStack tubeStone;
    public static ItemStack tubeGold;
    public static ItemStack tubeBrass;

    public void init() {
        initBlocks(CleverRouting.config);
        initItems(CleverRouting.config);
    }

    public void initBlocks(Configuration config) {
        Property tubeID = config.getBlock("generic.tube", 512);
        tubeID.comment = "All tubes use this block ID. Don't change it once in it's in place!";
        tubeBlock = new BlockTube(tubeID.getInt());

        GameRegistry.registerBlock(tubeBlock);
        GameRegistry.registerTileEntity(TileEntityTube.class, "crs.tube.entity");
    }

    public void initItems(Configuration config) {
        Property stoneTubeID = config.getItem("tube.stone", 21010);
        Item stoneTubeItem = new ItemGeneric("crs.tube.stone", stoneTubeID.getInt(), 0);
        tubeStone = new ItemStack(stoneTubeItem, 1);
    }
}
