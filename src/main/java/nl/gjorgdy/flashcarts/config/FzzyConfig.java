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
	private boolean increaseTntMinecartSpeed = true;

	@Comment("Maximum speed for minecarts in blocks per second, vanilla: 8")
	private ValidatedInt maxSpeedBlocksPerSecond = new ValidatedInt(100, 256, 8, ValidatedNumber.WidgetType.SLIDER);

	@Comment("Speed factor for minecarts on rails (higher is faster), vanilla: 1.0")
	private ValidatedFloat blockSpeedFactorRails = new ValidatedFloat( 16f, 100f, 1f, ValidatedNumber.WidgetType.SLIDER);

	@Comment("How slow a minecart has to be, to be considered halted, vanilla: 0.03")
	private ValidatedDouble haltSpeedThreshold = new ValidatedDouble(0.02, 0.99, 0.01, ValidatedNumber.WidgetType.SLIDER);

	@Comment("Multiplier applied to speed when minecart is considered halted, vanilla: 0.5")
	private ValidatedDouble haltSpeedMultiplier = new ValidatedDouble(0.35, 0.9, 0.1, ValidatedNumber.WidgetType.SLIDER);

	@Override
	public boolean shouldIncreaseTntMinecartSpeed() {
		return increaseTntMinecartSpeed;
	}

	@Override
	public int getMaxSpeedBlocksPerSecond() {
		return maxSpeedBlocksPerSecond.get();
	}

	@Override
	public float getBlockSpeedFactorRails() {
		return blockSpeedFactorRails.get();
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
