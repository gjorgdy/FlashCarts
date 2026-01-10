package nl.gjorgdy.flashcarts;

public class DefaultConfig implements iConfig {

	@Override
	public boolean shouldIncreaseTntMinecartSpeed() {
		return true;
	}

	@Override
	public int getMaxSpeedBlocksPerSecond() {
		return 100;
	}

	@Override
	public float getBlockSpeedFactorRails() {
		return 16f;
	}

	@Override
	public double getHaltSpeedThreshold() {
		return 0.02;
	}

	@Override
	public double getHaltSpeedMultiplier() {
		return 0.35;
	}
}
