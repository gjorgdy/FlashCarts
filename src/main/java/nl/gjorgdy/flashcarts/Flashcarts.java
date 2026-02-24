package nl.gjorgdy.flashcarts;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.loader.api.FabricLoader;
import nl.gjorgdy.flashcarts.config.ConfigResourceCondition;
import nl.gjorgdy.flashcarts.config.DefaultConfig;
import nl.gjorgdy.flashcarts.config.FzzyConfig;
import nl.gjorgdy.flashcarts.config.IConfig;
import nl.gjorgdy.flashcarts.listeners.ReloadCallbackListener;
import nl.gjorgdy.flashcarts.listeners.UseBlockCallbackListener;
import nl.gjorgdy.flashcarts.listeners.UseItemCallbackListener;
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
		UseBlockCallback.EVENT.register(new UseBlockCallbackListener());
		UseItemCallback.EVENT.register(new UseItemCallbackListener());
		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(new ReloadCallbackListener());
		ResourceConditions.register(new ConfigResourceCondition().getType());

		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(mod -> {
			VERSION = mod.getMetadata().getVersion().getFriendlyString();
		});

		loadConfig();
	}

	public static void loadConfig() {
		if (FabricLoader.getInstance().isModLoaded("fzzy_config")) {
			config = FzzyConfig.load();
		} else {
			config = DefaultConfig.load();
			LOGGER.log(Level.INFO, "Fzzy Config not found, using default settings.");
		}
	}

}
