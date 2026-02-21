package nl.gjorgdy.flashcarts;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import nl.gjorgdy.flashcarts.config.DefaultConfig;
import nl.gjorgdy.flashcarts.config.FzzyConfig;
import nl.gjorgdy.flashcarts.config.IConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Flashcarts implements ModInitializer {

	public static final String MOD_ID = "flash_carts";

	public static String VERSION = "unknown";

	public static final Logger LOGGER = LogManager.getLogger("flash_carts");

	public static IConfig config;

	@Override
	public void onInitialize() {
		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(mod -> {
			VERSION = mod.getMetadata().getVersion().getFriendlyString();
		});
		if (FabricLoader.getInstance().isModLoaded("fzzy_config")) {
			config = FzzyConfig.load();
		} else {
			config = DefaultConfig.load();
			LOGGER.log(Level.INFO, "Fzzy Config not found, using default settings.");
		}
	}

}
