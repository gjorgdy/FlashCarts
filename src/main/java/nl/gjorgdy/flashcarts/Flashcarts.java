package nl.gjorgdy.flashcarts;

import net.fabricmc.api.ModInitializer;

public class Flashcarts implements ModInitializer {

	public static iConfig config;

	@Override
	public void onInitialize() {
		config = new DefaultConfig();
	}

}
