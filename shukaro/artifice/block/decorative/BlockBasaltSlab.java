package shukaro.artifice.block.decorative;

import java.util.List;
import java.util.Random;
import java.util.Locale;

import net.minecraft.block.BlockHalfSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import shukaro.artifice.ArtificeBlocks;
import shukaro.artifice.ArtificeConfig;
import shukaro.artifice.ArtificeCore;
import shukaro.artifice.gui.ArtificeCreativeTab;
import shukaro.artifice.net.Packets;
import shukaro.artifice.render.IconHandler;
import shukaro.artifice.render.connectedtexture.ConnectedTextureBase;
import shukaro.artifice.render.connectedtexture.ConnectedTextures;
import shukaro.artifice.render.connectedtexture.schemes.SlabConnectedTexture;
import shukaro.artifice.util.BlockCoord;
import shukaro.artifice.util.ChunkCoord;
import shukaro.artifice.util.PacketWrapper;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBasaltSlab extends BlockHalfSlab
{
    private final String[] types = { "basaltBrick", "basaltCobble", "basaltPaver", "basaltAntipaver" };

    private Icon paverSide;

    private ConnectedTextureBase paver = new SlabConnectedTexture(ConnectedTextures.BasaltPaver);
    private ConnectedTextureBase antipaver = new SlabConnectedTexture(ConnectedTextures.BasaltAntipaver);

    private final boolean isDouble;

    public BlockBasaltSlab(int id, boolean isDouble)
    {
        super(id, isDouble, Material.rock);
        setCreativeTab(ArtificeCreativeTab.main);
        setLightOpacity(0);
        setHardness(1.5F);
        setResistance(10.0F);
        this.isDouble = isDouble;
    }

    @Override
    public int idDropped(int id, Random rand, int meta)
    {
        return ArtificeBlocks.blockBasaltSlab.blockID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(int id, CreativeTabs tab, List list)
    {
        if (id != ArtificeBlocks.blockBasaltDoubleSlab.blockID)
        {
            for (int i = 0; i < types.length; i++)
                list.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public String getFullSlabName(int meta)
    {
        meta = meta > 7 ? meta - 8 : meta;
        if (meta >= types.length)
            meta = 0;
        return "tile.artifice.slab." + types[meta].toLowerCase(Locale.ENGLISH);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister reg)
    {
    	ArtificeConfig.registerConnectedTextures(reg);
        paverSide = IconHandler.registerSingle(reg, "paverside", "basalt");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int meta)
    {
        meta = meta & 7;
        if (meta == 2 || meta == 3)
        {
            if (side == 0 || side == 1)
            	return this.getTextureRenderer(side, meta).texture.textureList[0];
            else
                return this.paverSide;
        }
        if (meta == 0)
            meta = 2;
        return ArtificeBlocks.blockBasalt.getIcon(side, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getBlockTexture(IBlockAccess block, int x, int y, int z, int side)
    {
        int t = block.getBlockMetadata(x, y, z) & 7;
        int meta = block.getBlockMetadata(x, y, z);
        if (t == 2 || t == 3)
        {
            if (side == 0 || side == 1)
            {
            	Integer worldID = Minecraft.getMinecraft().thePlayer.worldObj.provider.dimensionId;
            	BlockCoord coord = new BlockCoord(x, y, z);
            	ChunkCoord chunk = new ChunkCoord(coord);

            	if (!ArtificeCore.textureCache.contains(worldID, chunk, coord))
            	{
            		int[] indices = new int[6];
            		for (int i=0; i<indices.length; i++)
            			indices[i] = this.getTextureRenderer(i, meta).getTextureIndex(block, x, y, z, i);
            		ArtificeCore.textureCache.add(worldID, chunk, coord, indices);
            	}

            	if (ArtificeCore.textureCache.get(worldID, chunk, coord) == null)
            		return this.getIcon(side, meta);
            	return this.getTextureRenderer(side, meta).texture.textureList[ArtificeCore.textureCache.get(worldID, chunk, coord)[side]];
            }
            else
                return this.paverSide;
        }
        if (t == 0)
            t = 2;
        return ArtificeBlocks.blockBasalt.getIcon(side, t);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborID)
    {
    	Integer worldID = world.provider.dimensionId;
    	BlockCoord coord = new BlockCoord(x, y, z);
    	ChunkCoord chunk = new ChunkCoord(coord);
    	int meta = coord.getMeta(world);

    	int[] old = ArtificeCore.textureCache.get(worldID, chunk, coord);
    	int[] indices = new int[6];
		for (int i=0; i<indices.length; i++)
			indices[i] = this.getTextureRenderer(i, meta).getTextureIndex(world, x, y, z, i);
		ArtificeCore.textureCache.add(worldID, chunk, coord, indices);
    }

    public ConnectedTextureBase getTextureRenderer(int side, int meta)
    {
        meta = meta & 7;
        if (meta == 2)
            return this.paver;
        if (meta == 3)
            return this.antipaver;
        return null;
    }
}
