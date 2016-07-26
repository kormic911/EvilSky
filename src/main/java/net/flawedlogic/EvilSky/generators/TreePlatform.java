package net.flawedlogic.EvilSky.generators;

import net.minecraft.world.World;
import net.minecraft.init.Blocks;

public class TreePlatform implements IPlatformGenerator {
	
	@Override
	public void generate(World world, int x, int y, int z)
	{
		if(world.provider.dimensionId == 0) {
			buildTree(world, x, y, z);
		}
	}
	
	private void buildTree(World world, int x, int y, int z)
	{
		world.setBlock(x, y, z, Blocks.bedrock);
	}
}
