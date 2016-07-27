package net.flawedlogic.EvilSky;


import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Maps;


import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.flawedlogic.EvilSky.generators.*;
import net.flawedlogic.EvilSky.providers.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "EvilSky", name = "EvilSky", version = EvilSky.VERSION)
public class EvilSky
{
	@Instance("EvilSky")
	public static EvilSky instance;
    public static final String MODID = "EvilSky";
    public static final String VERSION = "1.1.2";
    private VoidWorldType worldType;
    
    private Map<String, IPlatformGenerator> generators = Maps.newHashMap();
    
    public Map<Integer, List<BlockWithMeta>> dimensionBlocks = Maps.newHashMap();
    public Map<Integer, List<BlockWithMeta>> dimensionLiquidBlocks = Maps.newHashMap();
    public Map<Integer, Boolean> dimensionIsVoid = Maps.newHashMap();
    public Map<Integer, List<Generator>> dimensionGenerators = Maps.newHashMap();
    
    public Boolean endHasSpikes = false;
    public Boolean netherHasFortresses = false;
    
    public static final String GENERAL = Configuration.CATEGORY_GENERAL;
    public static final String OVERWORLD = "dimension:0";
    public static final String NETHER = "dimension:-1";
    public static final String END = "dimension:1";
    
    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
    	Configuration config = null;
    	
    	
    	File cfgFile = event.getSuggestedConfigurationFile();
    	try {
    		config = new Configuration(cfgFile);

    		Boolean endHasSpikes = config.getBoolean("End has obsidian spikes?", GENERAL, true, "Enabling this will cause the obisidan spikes to spawn in the end");
    		Boolean netherHasFortresses = config.getBoolean("Nether has fortresses?", GENERAL, true, "Enabling this will cause the nether fortresses to spawn");
    		
        	// Overworld Configurations
        	config.getStringList("blocks", OVERWORLD, new String[] {"minecraft:dirt:0=20", "minecraft:stone:0=5", "minecraft:sand:0=1", "minecraft:air:0=5", "minecraft:gravel:0=1", "minecraft:clay:0=1"}, "List of blocks to use in overworld terrain generation. Use this format: modid:blockName:metaId=weight");
        	config.getStringList("liquid blocks", OVERWORLD, new String[] {"minecraft:water:0=10", "minecraft:lava:0=10"}, "List of liquid blocks to use in the overworld terrain generation. Use this format: modid:blockName:metaId=weight");
        	config.getBoolean("is void", OVERWORLD, true, "Enabling this will cause the overworld to be a void world");
        	config.getStringList("generators", OVERWORLD, new String[] {"solidOrb=50", "liquidOrb=10", "solidMultiOrb=20"}, "List of generators to use in the generation of the dimension. Use this format: generator=weight");
        	
        	// Nether Configurations
        	config.getStringList("blocks", NETHER, new String[] {"minecraft:netherrack:0=50", "minecraft:soul_sand:0=10"}, "List of blocks to use in nether terrain generation. Use this format: modid:blockName:metaId=weight");
        	config.getStringList("liquid blocks", NETHER, new String[] {"minecraft:lava:0=1"}, "List of liquid blocks to use in the nether terrain generation. Use this format: modid:blockName:metaId=weight");
        	config.getBoolean("is void", NETHER, true, "Enabling this will cause the nether to be a void world");
        	config.getStringList("generators", NETHER, new String[] {"solidOrb=50", "liquidOrb=2"}, "List of generators to use in the generation of the dimension. Use this format: generator=weight");
        	
        	// End Configurations
        	config.getStringList("blocks", END, new String[] {"minecraft:end_stone:0=1"}, "List of blocks to use in the end terrain generation. Use this format: modid:blockName:metaId=weight");
        	config.getStringList("liquid blocks", END, new String[] {}, "List of liquid blocks to use in the end terrain generation. Use this format: modid:blockName:metaId=weight");
        	config.getBoolean("is void", END, true, "Enabling this will cause the end to be a void world");
        	config.getStringList("generators", END, new String[] {"solidOrb=1"}, "List of generators to use in the generation of the dimension. Use this format: generator=weight");
        	
        	// Process the dimension configurations
        	Iterator<String> categoryNames = config.getCategoryNames().iterator();
        	String pattern = "^dimension:[0-9\\-]+";
        	Pattern p = Pattern.compile(pattern);
        	Matcher m;
        	
        	while(categoryNames.hasNext()) {
        		String categoryName = categoryNames.next();
        		m = p.matcher(categoryName);
        		if(m.find()) {
        			String[] categoryParts = categoryName.split(":");
        			FMLLog.log(Level.INFO, "[EvilSky] Loading configurations for dimension: "+ categoryParts[0]);
        			processDimensionConfigurations(new Integer(categoryParts[1]), config.getCategory(categoryName));
        		}
        	}
        	
    	} catch(Exception e) {
    		FMLLog.severe("[EvilSky] Error loading config, deleting file and resetting");
    		e.printStackTrace();
    		
    		if(cfgFile.exists()) {
    			cfgFile.delete();
    		}
    		config = new Configuration(cfgFile);
    	}
    	
    	if(config.hasChanged()) {
    		config.save();
    	}
    	
    	generators.put("tree", new TreePlatform());
    	
    	MinecraftForge.EVENT_BUS.register(this);
    }
    
    @EventHandler
    public void load(FMLInitializationEvent event) 
    {
    	FMLLog.log(Level.INFO, "[EvilSky] initialized");
    	worldType = new VoidWorldType();
    	
    	Hashtable<Integer, Class<? extends WorldProvider>> providers = ReflectionHelper.getPrivateValue(DimensionManager.class, null, "providers");
    	providers.put(0,  WorldProviderSurfaceVoid.class);
    	providers.put(1, WorldProviderEndVoid.class);
    	providers.put(-1, WorldProviderNetherVoid.class);
    }
    
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
    	//FMLLog.log(Level.INFO, "onWorldLoad event");
        if (!event.world.isRemote && event.world instanceof WorldServer)
        {
            WorldServer world = (WorldServer)event.world;
            int spawnX = (int)(event.world.getWorldInfo().getSpawnX() / world.provider.getMovementFactor() / 16);
            int spawnZ = (int)(event.world.getWorldInfo().getSpawnZ() / world.provider.getMovementFactor() / 16);
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    world.theChunkProviderServer.loadChunk(spawnX + x, spawnZ + z);
                }
            }
        }    	
    }
    
    public boolean shouldBeVoid(World world)
    {
    	// Check and see if we have a void record for the dimension in question
    	if(dimensionIsVoid.containsKey(world.provider.dimensionId)) {
    		return dimensionIsVoid.get(world.provider.dimensionId);
    	} else {
    		// If we have no record, return false (our default for now)
    		return false;
    	}
    }
    
    
    public IPlatformGenerator getPlatformType(World world)
    {
    	IPlatformGenerator ret = generators.get("tree");
    	return ret;
    }
    
    public boolean shouldGenerateSpikes(World world)
    {
    	return endHasSpikes;
    }
    
    public boolean shouldGenerateNetherFortress(World world)
    {
    	return netherHasFortresses;
    }
    
    private void processDimensionConfigurations(Integer dimensionId, ConfigCategory dimensionConfiguration )
    {
    	Map<String, Property> dimensionProperties = dimensionConfiguration.getValues();
    	
    	String[] blocks = dimensionProperties.get("blocks").getStringList();
    	FMLLog.log(Level.INFO, "[EvilSky] Loading blocks for dimension: "+dimensionId);
    	processDimensionBlocks(dimensionId, dimensionBlocks, blocks);
    	
    	FMLLog.log(Level.INFO, "[EvilSky] Loading liquid blocks for dimension: "+dimensionId);
    	String[] liquidBlocks = dimensionProperties.get("liquid blocks").getStringList();
    	processDimensionBlocks(dimensionId, dimensionLiquidBlocks, liquidBlocks);
    	
    	FMLLog.log(Level.INFO, "[EvilSky] Loading generators for dimension: "+dimensionId);
    	String[] generators = dimensionProperties.get("generators").getStringList();
    	processDimensionGenerators(dimensionId, dimensionGenerators, generators);
    	
    	Boolean isVoid = dimensionProperties.get("is void").getBoolean();
    	
    	// Handle the dimension void information
    	dimensionIsVoid.put(dimensionId, isVoid);
    }
    
    private void processDimensionGenerators(Integer dimensionId, Map<Integer, List<Generator>> generatorList, String[] generators)
    {
    	List<Generator> _generators = new ArrayList<Generator>();
    	
    	for(String generatorData : generators) {
    		String[] parts = generatorData.split("=");
    		_generators.add(new Generator(parts[0], new Integer(parts[1])));
    	}
    	generatorList.put(dimensionId, _generators);
    }
    
    private void processDimensionBlocks(Integer dimensionId, Map<Integer, List<BlockWithMeta>> blockList, String[] blocks)
    {
    	List<BlockWithMeta> _blockList = new ArrayList<BlockWithMeta>();
    	
    	Integer weight = 0;
    	Integer meta = 0;
    	String modid = null;
    	String name = null;
    	Block lookupBlock;
    	Integer count = 0;
    	
    	for(String blockData : blocks) {
    		String[] parta = blockData.split("=");
    		weight = new Integer(parta[1]);
    		String[] partb = parta[0].split(":");
    		modid = partb[0];
    		name = partb[1];
    		meta = new Integer(partb[2]);
    		lookupBlock = GameRegistry.findBlock(modid, name);
    		if(lookupBlock != null) {
    			FMLLog.log(Level.INFO, "[EvilSky] Loading "+lookupBlock.getUnlocalizedName());
    			_blockList.add(new BlockWithMeta(lookupBlock, meta, weight));
    			count++;
    		} else {
    			FMLLog.log(Level.WARN, "[EvilSky] Invalid block provided ["+blockData+"], ignoring");
    		}
    	}
    	
    	FMLLog.log(Level.INFO, "[EvilSky] Successfully loaded "+count+" blocks for dimension: "+dimensionId);
    	blockList.put(dimensionId, _blockList);   	
    }
}
