package nl.gjorgdy.flashcarts.config;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
import org.jspecify.annotations.Nullable;

public interface IConfig {

	float default_poweredRailBoostPercentage = 0.12f;
	double default_haltSpeedThreshold = 0.05;
	double default_haltSpeedMultiplier = 0.3;

	boolean default_emptyUseExperimentalPhysics = true;
	int default_emptyMaxSpeed = 8;

	boolean default_mobUseExperimentalPhysics = true;
	int default_mobMaxSpeed = 8;

	boolean default_playerUseExperimentalPhysics = true;
	int default_playerMaxSpeed = 64;

	boolean default_tntUseExperimentalPhysics = true;
	int default_tntMaxSpeed = 32;

	/**
	 * Get the configuration for empty minecarts.
	 * @return the configuration for empty minecarts.
	 */
	ICartConfig getEmptyMinecartConfig();

	/**
	 * Get the configuration for minecarts with mobs in them (excluding players).
	 * @return the configuration for mob minecarts.
	 */
	ICartConfig getMobMinecartConfig();

	/**
	 * Get the configuration for minecarts with players in them.
	 * @return the configuration for player minecarts.
	 */
	ICartConfig getPlayerMinecartConfig();

	/**
	 * Get the configuration for TNT minecarts.
	 * @return the configuration for TNT minecarts.
	 */
	ICartConfig getTntMinecartConfig();

	@Nullable
	default ICartConfig getConfigForMinecart(AbstractMinecart minecart) {
		if (minecart instanceof MinecartTNT) return getTntMinecartConfig();
		if (minecart instanceof Minecart) {
			if (minecart.getPassengers().isEmpty()) return getEmptyMinecartConfig();
			if (minecart.getFirstPassenger() instanceof Player) return getPlayerMinecartConfig();
			if (!minecart.getPassengers().isEmpty()) return getMobMinecartConfig();
		}
		return null;
	}

	/**
	 * Get the percentage of boost a powered rail should give.
	 * @return the percentage of boost a powered rail should give.
	 */
	float getPoweredRailBoostPercentage();

	/**
	 * Get the speed threshold below which a minecart is considered halted.
	 * @return the speed threshold below which a minecart is considered halted.
	 */
	double getHaltSpeedThreshold();

	/**
	 * Get the multiplier applied to speed when a minecart is being halted.
	 * @return the multiplier applied to speed when a minecart is being halted.
	 */
	double getHaltSpeedMultiplier();

}
