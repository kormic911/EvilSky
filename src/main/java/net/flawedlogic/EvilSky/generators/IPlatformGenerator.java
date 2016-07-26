package net.flawedlogic.EvilSky.generators;

import net.minecraft.world.World;

public interface IPlatformGenerator {
	void generate(World world, int x, int y, int z);
}
