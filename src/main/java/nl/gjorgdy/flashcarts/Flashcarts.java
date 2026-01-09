package nl.gjorgdy.flashcarts;

import net.fabricmc.api.ModInitializer;

public class Flashcarts implements ModInitializer {

	public static final int MAX_SPEED_BLOCKS_PER_SECOND = 100;

	public static final float BLOCK_SPEED_FACTOR_RAILS = 16.0F;

	public static final float HALT_SPEED_THRESHOLD = 0.3F;

	public static final float MINECART_HALT_SPEED_MULTIPLIER = 0.85F;

	@Override
	public void onInitialize() {
	}
}
