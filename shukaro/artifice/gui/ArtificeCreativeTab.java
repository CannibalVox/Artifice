package shukaro.artifice.gui;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import shukaro.artifice.ArtificeBlocks;
import shukaro.artifice.ArtificeConfig;
import shukaro.artifice.ArtificeItems;

public class ArtificeCreativeTab extends CreativeTabs
{
    public static final ArtificeCreativeTab main = new ArtificeCreativeTab("Artifice");
    //public static final ArtificeCreativeTab documents = new ArtificeCreativeTab("Documents");
    
    public ArtificeCreativeTab(String label)
    {
        super(label);
    }
    
    @Override
    public ItemStack getIconItemStack()
    {
    	/*
    	if (this.getTabLabel() == documents.getTabLabel())
    	{
    		return new ItemStack(Item.writtenBook);
    	}
    	*/
    	if (this.getTabLabel() == main.getTabLabel())
    	{
	        if (ArtificeConfig.enableFrames.getBoolean(true))
	            return new ItemStack(ArtificeBlocks.blockFrame, 1, 0);
	        if (ArtificeConfig.enableWorldGen.getBoolean(true))
	            return new ItemStack(ArtificeBlocks.blockBasalt, 1, 0);
	        if (ArtificeConfig.enableUpgrades.getBoolean(true))
	        	return new ItemStack(ArtificeItems.itemUpgrade, 1, 0);
	        if (ArtificeConfig.enableSledges.getBoolean(true))
	            return new ItemStack(ArtificeItems.itemSledgeDiamond, 1, 0);
	        if (ArtificeConfig.enableCoins.getBoolean(true))
	        	return new ItemStack(ArtificeItems.itemCoin, 1, 2);
	        if (ArtificeConfig.enableBoxes.getBoolean(true))
	            return new ItemStack(ArtificeItems.itemBox, 1, 0);
	        if (ArtificeConfig.enableSteel.getBoolean(true))
	            return new ItemStack(ArtificeItems.itemSteelIngot, 1, 0);
	        return null;
    	}
    	else
    		return null;
    }
    
    @Override
    public String getTranslatedTabLabel()
    {
        return this.getTabLabel();
    }
}
