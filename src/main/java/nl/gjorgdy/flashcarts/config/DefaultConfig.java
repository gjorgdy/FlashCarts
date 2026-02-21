package nl.gjorgdy.flashcarts.config;

public class DefaultConfig implements IConfig {

	private DefaultConfig() {}

	public static DefaultConfig load() {
		return new DefaultConfig();
	}

	private static final ICartConfig default_emptyMinecartConfig = new ICartConfig() {
		@Override
		public boolean useExperimentalPhysics() {
			return default_emptyUseExperimentalPhysics;
		}

		@Override
		public int maxSpeed() {
			return default_emptyMaxSpeed;
		}
	};

	private static final ICartConfig default_mobMinecartConfig = new ICartConfig() {
		@Override
		public boolean useExperimentalPhysics() {
			return default_mobUseExperimentalPhysics;
		}

		@Override
		public int maxSpeed() {
			return default_mobMaxSpeed;
		}
	};

	private static final ICartConfig default_playerMinecartConfig = new ICartConfig() {
		@Override
		public boolean useExperimentalPhysics() {
			return default_playerUseExperimentalPhysics;
		}

		@Override
		public int maxSpeed() {
			return default_playerMaxSpeed;
		}
	};

	private static final ICartConfig default_tntMinecartConfig = new ICartConfig() {
		@Override
		public boolean useExperimentalPhysics() {
			return default_tntUseExperimentalPhysics;
		}

		@Override
		public int maxSpeed() {
			return default_tntMaxSpeed;
		}
	};

	@Override
	public ICartConfig emptyMinecartConfig() {
		return default_emptyMinecartConfig;
	}

	@Override
	public ICartConfig mobMinecartConfig() {
		return default_mobMinecartConfig;
	}

	@Override
	public ICartConfig playerMinecartConfig() {
		return default_playerMinecartConfig;
	}

	@Override
	public ICartConfig tntMinecartConfig() {
		return default_tntMinecartConfig;
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
