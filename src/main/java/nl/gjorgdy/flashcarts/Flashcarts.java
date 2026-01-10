package nl.gjorgdy.flashcarts;

import net.fabricmc.api.ModInitializer;

public class Flashcarts implements ModInitializer {

	public static final boolean INCREASE_TNT_MINECART_SPEED = true;

	public static final int MAX_SPEED_BLOCKS_PER_SECOND = 100;

	public static final float BLOCK_SPEED_FACTOR_RAILS = 16.0F;

	public static final double HALT_SPEED_THRESHOLD = 0.02;

	public static final double HALT_SPEED_MULTIPLIER = 0.35;

	@Override
	public void onInitialize() {
	}
}
