package nl.gjorgdy.flashcarts.config;

public class DefaultConfig implements IConfig {

	private DefaultConfig() {}

	public static DefaultConfig load() {
		return new DefaultConfig();
	}

	private static final ICartConfig defaultMinecartConfig = new ICartConfig() {
		@Override
		public boolean useExperimentalPhysics() {
			return true;
		}

		@Override
		public int maxSpeed() {
			return default_maxSpeed;
		}
	};

	@Override
	public ICartConfig emptyMinecartConfig() {
		return defaultMinecartConfig;
	}

	@Override
	public ICartConfig mobMinecartConfig() {
		return defaultMinecartConfig;
	}

	@Override
	public ICartConfig playerMinecartConfig() {
		return defaultMinecartConfig;
	}

	@Override
	public ICartConfig tntMinecartConfig() {
		return defaultMinecartConfig;
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
