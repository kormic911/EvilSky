package net.flawedlogic.EvilSky.generators;

import java.util.Random;

import net.flawedlogic.EvilSky.EvilSky;
import net.flawedlogic.EvilSky.util.BlockWithMeta;
import net.minecraft.block.Block;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public class LiquidOrbGenerator {

	private World world;
	
	public LiquidOrbGenerator(GenericOrbGenerator orbGenerator) {
		this.world = orbGenerator.world;
	}
	
	public void fillSphere(Block[] ablock, byte[] ameta, int centerx, int centery, int centerz, int radius) {
		Random random = new Random();
		if(EvilSky.instance.dimensionBlocks.get(world.provider.dimensionId).isEmpty() || EvilSky.instance.dimensionLiquidBlocks.get(world.provider.dimensionId).isEmpty()) {
			return;
		}
		
    	BlockWithMeta bwm = (BlockWithMeta) WeightedRandom.getRandomItem(random, EvilSky.instance.dimensionBlocks.get(world.provider.dimensionId));
    	BlockWithMeta lwm = (BlockWithMeta) WeightedRandom.getRandomItem(random, EvilSky.instance.dimensionLiquidBlocks.get(world.provider.dimensionId));

        double sqradius = radius * radius;
        double liquidradius = (((double) radius) - 1.5f) * (((double) radius) - 1.5f);

        for (int x = 0 ; x < 16 ; x++) {
            double dxdx = (x-centerx) * (x-centerx);
            for (int z = 0 ; z < 16 ; z++) {
                double dzdz = (z-centerz) * (z-centerz);
                int index = (x * 16 + z) * 256;
                for (int y = centery-radius ; y <= centery+radius ; y++) {
                    double dydy = (y-centery) * (y-centery);
                    double sqdist = dxdx + dydy + dzdz;
                    
                	int test = (index + y);
                	if(test > ablock.length) {
                		test = ablock.length - 1;
                	}
                    if (sqdist < liquidradius) {
                    	if(ablock[test] == null) {
                    		ablock[test] = lwm.block;
                    		ameta[test] = (byte)lwm.meta;
                    	}
                    } else if (sqdist <= sqradius) {
                    	if(ablock[test] == null) { 
                    		ablock[test] = bwm.block;
                    		ameta[test] = (byte)bwm.meta;
                    	}
                    }
                }
            }
        }
	}
}
