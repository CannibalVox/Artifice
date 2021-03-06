package shukaro.artifice.block.frame;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import shukaro.artifice.ArtificeConfig;
import shukaro.artifice.ArtificeCore;
import shukaro.artifice.net.Packets;
import shukaro.artifice.render.connectedtexture.ConnectedTextureBase;
import shukaro.artifice.render.connectedtexture.ConnectedTextures;
import shukaro.artifice.render.connectedtexture.schemes.TransparentConnectedTexture;
import shukaro.artifice.util.BlockCoord;
import shukaro.artifice.util.ChunkCoord;
import shukaro.artifice.util.PacketWrapper;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFrameGlassWall extends BlockFrame
{
    private ConnectedTextureBase basic = new TransparentConnectedTexture(ConnectedTextures.BasicGlassWall);
    private ConnectedTextureBase reinforced = new TransparentConnectedTexture(ConnectedTextures.ReinforcedGlassWall);
    private ConnectedTextureBase industrial = new TransparentConnectedTexture(ConnectedTextures.IndustrialGlassWall);
    private ConnectedTextureBase advanced = new TransparentConnectedTexture(ConnectedTextures.AdvancedGlassWall);
    
    public BlockFrameGlassWall(int id)
    {
        super(id);
        setUnlocalizedName("artifice.glasswall");
    }
    
    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
    {
        int meta = world.getBlockMetadata(x, y, z);
        return this.getResistance(meta);
    }
    
    public float getResistance(int meta)
    {
        switch (meta)
        {
        case 0:
            return 15.0F;
        case 1:
            return 25.0F;
        case 2:
            return 45.0F;
        case 3:
            return 75.0F;
        default:
            return 10.0F;
        }
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    public ConnectedTextureBase getTextureRenderer(int side, int meta)
    {
        switch (meta)
        {
        case 0:
            return basic;
        case 1:
            return reinforced;
        case 2:
            return industrial;
        case 3:
            return advanced;
        default:
            return null;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister reg)
    {
    	ArtificeConfig.registerConnectedTextures(reg);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int meta)
    {
        return this.getTextureRenderer(side, meta).texture.textureList[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getBlockTexture(IBlockAccess block, int x, int y, int z, int side)
    {
    	Integer worldID = Minecraft.getMinecraft().thePlayer.worldObj.provider.dimensionId;
    	BlockCoord coord = new BlockCoord(x, y, z);
    	ChunkCoord chunk = new ChunkCoord(coord);
    	int meta = coord.getMeta(block);
    	
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

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side)
	{
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return 0;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        int i1 = par1IBlockAccess.getBlockId(par2, par3, par4);
        return i1 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
    }
}
