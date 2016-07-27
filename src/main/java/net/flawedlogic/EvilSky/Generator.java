package net.flawedlogic.EvilSky;

import net.minecraft.util.WeightedRandom;

public class Generator extends WeightedRandom.Item {
	public final String generator;
	
	public Generator(String generator) {
		this(generator, 1);
	}
	
	public Generator(String generator, Integer weight) {
		super(weight);
		this.generator = generator;
	}
}
