package nl.gjorgdy.flashcarts.config;

public interface iConfig {

	boolean shouldIncreaseTntMinecartSpeed();

	int getMaxSpeedBlocksPerSecond();

	float getPoweredRailBoostPercentage();

	double getHaltSpeedThreshold();

	double getHaltSpeedMultiplier();

}
