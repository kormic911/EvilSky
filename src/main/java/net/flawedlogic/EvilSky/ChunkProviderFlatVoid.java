package net.flawedlogic.EvilSky;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandom.Item;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChunkProviderFlatVoid extends ChunkProviderFlat
{
    private World world;
    private final int r;

    public ChunkProviderFlatVoid(World world)
    {
        super(world, world.getSeed(), false, null);
        this.world = world;
        r = 1;
    }

    @Override 
    public Chunk loadChunk(int x, int z)
    { 
    	return this.provideChunk(x, z); 
    }
    
    @Override 
    public void populate(IChunkProvider par1IChunkProvider, int par2, int par3)
    {
    	
    }

    @Override 
    public Chunk provideChunk(int chunkX, int chunkZ)
    {
    	Block[] ablock = new Block[65536];
    	byte[] abyte = new byte[65536];
    	
        for (int i = 0 ; i < 65536 ; i++) {
            ablock[i] = null;
        }

        for (int i = 0 ; i < 65536 ; i++) {
            abyte[i] = 0;
        }
        
        for (int cx = -r ; cx <= r ; cx++) {
            for (int cz = -r ; cz <= r ; cz++) {

                Random random = new Random((world.getSeed() + (chunkX+cx)) * 113 + (chunkZ+cz) * 31 + 77);
                random.nextFloat();

                if (random.nextFloat() < .05f) {
                    int x = cx * 16 + random.nextInt(16);
                    int y = 40 + random.nextInt(40);
                    int z = cz * 16 + random.nextInt(16);
                    int radius = random.nextInt(10) + (2);
                    fillSphere(ablock, abyte, x, y, z, radius);
                }
            }
        }        
    	
    	Chunk chunk = new Chunk(world, ablock, abyte, chunkX, chunkZ);
        BiomeGenBase[] biomes = world.getWorldChunkManager().loadBlockGeneratorData(null, chunkX * 16, chunkZ * 16, 16, 16);
        byte[] ids = chunk.getBiomeArray();

        for (int i = 0; i < ids.length; ++i)
        {
            ids[i] = (byte)biomes[i].biomeID;
        }

        chunk.generateSkylightMap();
        return chunk;
    }
    
    private void fillSphere(Block[] ablock, byte[] ameta, int centerx, int centery, int centerz, int radius) 
    {
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
                    	ablock[index + y] = bwm.block;
                    	ameta[index + y] = (byte)bwm.meta;
                    }
                }
            }
        }
    }
}
