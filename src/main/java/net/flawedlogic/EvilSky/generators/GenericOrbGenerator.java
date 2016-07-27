package net.flawedlogic.EvilSky.generators;

import java.util.Random;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLLog;
import net.flawedlogic.EvilSky.EvilSky;
import net.flawedlogic.EvilSky.util.Generator;
import net.minecraft.block.Block;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public class GenericOrbGenerator {
	
	public World world;
	private SolidOrbGenerator solidOrbGenerator;
	private LiquidOrbGenerator liquidOrbGenerator;
	private SolidMultiOrbGenerator solidMultiOrbGenerator;

	public GenericOrbGenerator(World world) {
		this.world = world;
		solidOrbGenerator = new SolidOrbGenerator(this);
		liquidOrbGenerator = new LiquidOrbGenerator(this);
		solidMultiOrbGenerator = new SolidMultiOrbGenerator(this);
	}
	
	public void generate(int chunkX, int chunkZ, Block[] ablock, byte[] abyte) {
		//FMLLog.log(Level.INFO, "[EvilSky] generate called");
		if(EvilSky.instance.dimensionGenerators.get(world.provider.dimensionId).isEmpty()) {
			//FMLLog.log(Level.INFO, "[EvilSky] no dimension generators available, bailing");
			return;
		}
		
		int r = 1;
        Generator generator;
        
        for (int cx = -r ; cx <= r ; cx++) {
            for (int cz = -r ; cz <= r ; cz++) {

                Random random = new Random((world.getSeed() + (chunkX+cx)) * 113 + (chunkZ+cz) * 31 + 77);
                random.nextFloat();

                Random genRandom = new Random();
                generator = (Generator) WeightedRandom.getRandomItem(genRandom, EvilSky.instance.dimensionGenerators.get(world.provider.dimensionId));
                //FMLLog.log(Level.INFO, "[EvilSky] Attempting to use generator: '" + generator.generator + "'");
                
                if (random.nextFloat() < .05f) {
                    int x = cx * 16 + random.nextInt(16);
                    int y = 40 + random.nextInt(40);
                    int z = cz * 16 + random.nextInt(16);
                    int radius = random.nextInt(10) + (2);
                    //fillSphere(ablock, abyte, x, y, z, radius);
                    //FMLLog.log(Level.INFO, "[EvilSky] checking generator type");
                    if(generator.generator.equals("solidOrb")) {
                    	//FMLLog.log(Level.INFO, "[EvilSky] Generating solid orb");
                    	solidOrbGenerator.fillSphere(ablock, abyte, x, y, z, radius);
                    } else if(generator.generator.equals("liquidOrb")) {
                    	//FMLLog.log(Level.INFO, "[EvilSky] Generating liquid orb");
                    	liquidOrbGenerator.fillSphere(ablock, abyte, x, y, z, radius);
                    } else if(generator.generator.equals("solidMultiOrb")) {
                    	//FMLLog.log(Level.INFO, "[EvilSky] Generating solid multi orb");
                    	solidMultiOrbGenerator.fillSphere(ablock, abyte, x, y, z, radius);
                    } else {
                    	FMLLog.log(Level.INFO, "[EvilSky] Dont know how to generate " + generator.generator);
                    }
                }
            }
        }		
		
	}
}
