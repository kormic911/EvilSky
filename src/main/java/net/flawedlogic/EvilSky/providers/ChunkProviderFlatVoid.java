package net.flawedlogic.EvilSky.providers;

import net.flawedlogic.EvilSky.BlockWithMeta;
import net.flawedlogic.EvilSky.EvilSky;
import net.flawedlogic.EvilSky.Generator;
import net.flawedlogic.EvilSky.generators.GenericOrbGenerator;
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
import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLLog;


public class ChunkProviderFlatVoid extends ChunkProviderFlat
{
    private World world;
    private final int r;
    private GenericOrbGenerator genericOrbGenerator;

    public ChunkProviderFlatVoid(World world)
    {
        super(world, world.getSeed(), false, null);
        this.world = world;
        r = 1;
        genericOrbGenerator = new GenericOrbGenerator(this.world);
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
        
        genericOrbGenerator.generate(chunkX, chunkZ, ablock, abyte);

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
}
