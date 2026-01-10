package nl.gjorgdy.flashcarts;

public interface iConfig {

	boolean shouldIncreaseTntMinecartSpeed();

	int getMaxSpeedBlocksPerSecond();

	float getBlockSpeedFactorRails();

	double getHaltSpeedThreshold();

	double getHaltSpeedMultiplier();

}
