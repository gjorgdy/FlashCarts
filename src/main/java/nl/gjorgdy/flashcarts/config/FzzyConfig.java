package nl.gjorgdy.flashcarts.config;

import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.annotations.IgnoreVisibility;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber;
import net.minecraft.resources.Identifier;
import nl.gjorgdy.flashcarts.Flashcarts;
import org.jspecify.annotations.NonNull;

@IgnoreVisibility
public class FzzyConfig extends Config implements IConfig {

	public static FzzyConfig load() {
		return ConfigApiJava.registerAndLoadConfig(FzzyConfig::new);
	}

	private FzzyConfig() {
		super(Identifier.fromNamespaceAndPath(Flashcarts.MOD_ID, "config"));
	}

	@Comment("Whether to show the speedometer when in minecart (current speed in blocks per second), vanilla: false")
	private boolean showSpeedometer = default_showSpeedometer;

	@Comment("Whether to show the speed bar when in minecart (bars up to max speed), vanilla: false")
	private boolean showSpeedBar = default_showSpeedBar;

	@Comment("Whether to show the station title (title when standing still at a block with a sign under it), vanilla: false")
	private boolean showStationTitle = default_showStationTitle;

	@Comment("Percentage of boost a powered rail should give, vanilla: 0.06 (6%)")
	private ValidatedFloat poweredRailBoostPercentage = new ValidatedFloat( default_poweredRailBoostPercentage, 0.99f, 0.01f, ValidatedNumber.WidgetType.SLIDER);

	@Comment("How slow a minecart has to be, to be considered halted, vanilla: 0.03")
	private ValidatedDouble haltSpeedThreshold = new ValidatedDouble(default_haltSpeedThreshold, 0.99, 0.01, ValidatedNumber.WidgetType.SLIDER);

	@Comment("Multiplier applied to speed when minecart is considered halted, vanilla: 0.5")
	private ValidatedDouble haltSpeedMultiplier = new ValidatedDouble(default_haltSpeedMultiplier, 0.9, 0.1, ValidatedNumber.WidgetType.SLIDER);

	@Comment("Configuration for empty minecarts")
	private ICartConfig emptyMinecart = new FzzyCartConfig(default_emptyUseExperimentalPhysics, default_emptyMaxSpeed);

	@Comment("Configuration for minecarts with a mob in them")
	private ICartConfig mobMinecart = new FzzyCartConfig(default_mobUseExperimentalPhysics, default_mobMaxSpeed);

	@Comment("Configuration for minecarts with a player in them")
	private ICartConfig playerMinecart = new FzzyCartConfig(default_playerUseExperimentalPhysics, default_playerMaxSpeed);

	@Comment("Configuration for TNT minecarts")
	private ICartConfig tntMinecart = new FzzyCartConfig(default_tntUseExperimentalPhysics, default_tntMaxSpeed);

	@Override
	public ICartConfig getEmptyMinecartConfig() {
		return emptyMinecart;
	}

	@Override
	public ICartConfig getMobMinecartConfig() {
		return mobMinecart;
	}

	@Override
	public ICartConfig getPlayerMinecartConfig() {
		return playerMinecart;
	}

	@Override
	public ICartConfig getTntMinecartConfig() {
		return tntMinecart;
	}

	@Override
	public boolean shouldShowSpeedometer() {
		return showSpeedometer;
	}

	@Override
	public boolean shouldShowSpeedBar() {
		return showSpeedBar;
	}

	@Override
	public boolean shouldShowStationTitle() {
		return showStationTitle;
	}

	@Override
	public float getPoweredRailBoostPercentage() {
		return poweredRailBoostPercentage.get();
	}

	@Override
	public double getHaltSpeedThreshold() {
		return haltSpeedThreshold.get();
	}

	@Override
	public double getHaltSpeedMultiplier() {
		return haltSpeedMultiplier.get();
	}

	@Override
	public @NonNull SaveType saveType() {
		return SaveType.SEPARATE;
	}

	@IgnoreVisibility
	public static class FzzyCartConfig extends ConfigSection implements ICartConfig {

		@Comment("Should these minecarts make use of the increased speed and experimental physics, vanilla: false")
		private boolean useExperimentalPhysics;

		@Comment("Maximum speed for these minecarts in blocks per second, vanilla: 8")
		private ValidatedInt maxSpeedBlocksPerSecond;

		private FzzyCartConfig(boolean default_useExperimentalPhysics, int default_maxSpeed) {
			useExperimentalPhysics = default_useExperimentalPhysics;
			maxSpeedBlocksPerSecond = new ValidatedInt(default_maxSpeed, 256, 8, ValidatedNumber.WidgetType.SLIDER);
		}

		@Override
		public boolean shouldUseExperimentalPhysics() {
			return useExperimentalPhysics;
		}

		@Override
		public int getMaxSpeed() {
			return maxSpeedBlocksPerSecond.get();
		}
	}

}
