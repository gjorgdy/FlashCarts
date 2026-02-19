package nl.gjorgdy.flashcarts.config;

import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.annotations.IgnoreVisibility;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber;
import net.minecraft.resources.Identifier;
import nl.gjorgdy.flashcarts.Flashcarts;
import org.jspecify.annotations.NonNull;

@IgnoreVisibility
public class FzzyConfig extends Config implements iConfig {

	public static FzzyConfig load() {
		return ConfigApiJava.registerAndLoadConfig(FzzyConfig::new);
	}

	private FzzyConfig() {
		super(Identifier.fromNamespaceAndPath(Flashcarts.MOD_ID, "config"));
	}

	@Comment("Should TNT Minecarts also make use of the increased speed and experimental physics")
	private boolean increaseTntMinecartSpeed = default_increaseTntMinecartSpeed;

	@Comment("Maximum speed for minecarts in blocks per second, vanilla: 8")
	private ValidatedInt maxSpeed = new ValidatedInt(default_maxSpeed, 256, 8, ValidatedNumber.WidgetType.SLIDER);

	@Comment("Percentage of boost a powered rail should give, vanilla: 0.06 (6%)")
	private ValidatedFloat poweredRailBoostPercentage = new ValidatedFloat( default_poweredRailBoostPercentage, 0.99f, 0.1f, ValidatedNumber.WidgetType.SLIDER);

	@Comment("How slow a minecart has to be, to be considered halted, vanilla: 0.03")
	private ValidatedDouble haltSpeedThreshold = new ValidatedDouble(default_haltSpeedThreshold, 0.99, 0.01, ValidatedNumber.WidgetType.SLIDER);

	@Comment("Multiplier applied to speed when minecart is considered halted, vanilla: 0.5")
	private ValidatedDouble haltSpeedMultiplier = new ValidatedDouble(default_haltSpeedMultiplier, 0.9, 0.1, ValidatedNumber.WidgetType.SLIDER);

	@Comment("Should empty Minecarts also make use of the increased speed and experimental physics, default: false")
	private boolean shouldIncreaseEmptyMinecartSpeed = default_shouldIncreaseEmptyMinecartSpeed;

	@Comment("Should Minecarts with a mob other than a player also make use of the increased speed and experimental physics, default: true")
	private boolean shouldIncreaseNonPlayerMinecartSpeed = default_shouldIncreaseNonPlayerMinecartSpeed;

	@Override
	public boolean shouldIncreaseTntMinecartSpeed() {
		return increaseTntMinecartSpeed;
	}

	@Override
	public boolean shouldIncreaseEmptyMinecartSpeed() {
		return shouldIncreaseEmptyMinecartSpeed;
	}

	@Override
	public boolean shouldIncreaseNonPlayerMinecartSpeed() {
		return shouldIncreaseNonPlayerMinecartSpeed;
	}

	@Override
	public int getMaxSpeed() {
		return maxSpeed.get();
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

}
