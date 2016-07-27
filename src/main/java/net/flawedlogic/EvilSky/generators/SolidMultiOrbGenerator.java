package net.flawedlogic.EvilSky.generators;

import java.util.Random;

import net.flawedlogic.EvilSky.BlockWithMeta;
import net.flawedlogic.EvilSky.EvilSky;
import net.minecraft.block.Block;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public class SolidMultiOrbGenerator {
	
	private World world;
	
	public SolidMultiOrbGenerator(GenericOrbGenerator orbGenerator) {
		this.world = orbGenerator.world;
	}
	
	public void fillSphere(Block[] ablock, byte[] ameta, int centerx, int centery, int centerz, int radius) {
		if(EvilSky.instance.dimensionBlocks.get(world.provider.dimensionId).isEmpty()) {
			return;
		}
    	BlockWithMeta bwm;
    	Random random = new Random();


        double sqradius = radius * radius;

        for (int x = 0 ; x < 16 ; x++) {
            double dxdx = (x-centerx) * (x-centerx);
            for (int z = 0 ; z < 16 ; z++) {
                double dzdz = (z-centerz) * (z-centerz);
                int index = (x * 16 + z) * 256;
                for (int y = centery-radius ; y <= centery+radius ; y++) {
                    double dydy = (y-centery) * (y-centery);
                    double sqdist = dxdx + dydy + dzdz;
                    if (sqdist <= sqradius) {
                    	bwm = (BlockWithMeta) WeightedRandom.getRandomItem(random, EvilSky.instance.dimensionBlocks.get(world.provider.dimensionId));
                    	int test = (index + y);
                    	if(test > ablock.length) {
                    		test = ablock.length - 1;
                    	}
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
