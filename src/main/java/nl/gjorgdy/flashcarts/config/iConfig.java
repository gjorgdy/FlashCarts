package nl.gjorgdy.flashcarts.config;

public interface iConfig {

	boolean default_increaseTntMinecartSpeed = true;
	int default_maxSpeed = 100;
	float default_poweredRailBoostPercentage = 0.12f;
	double default_haltSpeedThreshold = 0.05;
	double default_haltSpeedMultiplier = 0.3;
	boolean default_shouldIncreaseEmptyMinecartSpeed = false;
	boolean default_shouldIncreaseNonPlayerMinecartSpeed = true;

	/**
	 * Whether TNT minecarts should have increased speed.
	 * @return true if TNT minecarts should have increased speed, false otherwise.
	 */
	boolean shouldIncreaseTntMinecartSpeed();

	/**
	 * Whether empty minecarts should have increased speed.
	 * @return true if empty minecarts should have increased speed, false otherwise.
	 */
	boolean shouldIncreaseEmptyMinecartSpeed();

	/**
	 * Whether minecarts with a mob other than a player should have increased speed.
	 * @return true if minecarts with a mob other than a player should have increased speed, false otherwise.
	 */
	boolean shouldIncreaseNonPlayerMinecartSpeed();

	/**
	 * Get the maximum speed for minecarts in blocks per second.
	 * @return the maximum speed for minecarts in blocks per second.
	 */
	int getMaxSpeed();

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
