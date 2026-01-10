package nl.gjorgdy.flashcarts.config;

public interface iConfig {

	boolean shouldIncreaseTntMinecartSpeed();

	int getMaxSpeedBlocksPerSecond();

	float getBlockSpeedFactorRails();

	double getHaltSpeedThreshold();

	double getHaltSpeedMultiplier();

}
