package nl.gjorgdy.flashcarts.config;

public class DefaultConfig implements iConfig {

	private DefaultConfig() {}

	public static DefaultConfig load() {
		return new DefaultConfig();
	}

	@Override
	public boolean shouldIncreaseTntMinecartSpeed() {
		return default_increaseTntMinecartSpeed;
	}

	@Override
	public int getMaxSpeed() {
		return default_maxSpeed;
	}

	@Override
	public float getPoweredRailBoostPercentage() {
		return default_poweredRailBoostPercentage;
	}

	@Override
	public double getHaltSpeedThreshold() {
		return default_haltSpeedThreshold;
	}

	@Override
	public double getHaltSpeedMultiplier() {
		return default_haltSpeedMultiplier;
	}
}
