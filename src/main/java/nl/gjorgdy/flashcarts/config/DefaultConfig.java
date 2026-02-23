package nl.gjorgdy.flashcarts.config;

public class DefaultConfig implements IConfig {

	private DefaultConfig() {}

	public static DefaultConfig load() {
		return new DefaultConfig();
	}

	private static final IBuildConfig default_buildConfig = new IBuildConfig() {
		@Override
		public boolean isRailSelectionBuildingEnabled() {
			return default_railSelectionBuildingEnabled;
		}

		@Override
		public int getRailSelectionBuildingMaxDistance() {
			return default_railSelectionBuildingMaxDistance;
		}

		@Override
		public boolean isRailExtendBuildingEnabled() {
			return default_railExtendBuildingEnabled;
		}

		@Override
		public int getRailExtendBuildingMaxDistance() {
			return default_railExtendBuildingMaxDistance;
		}
	};

	@Override
	public IBuildConfig getBuildConfig() {
		return default_buildConfig;
	}

	private static final ICartConfig default_emptyMinecartConfig = new ICartConfig() {
		@Override
		public boolean shouldUseExperimentalPhysics() {
			return default_emptyUseExperimentalPhysics;
		}

		@Override
		public int getMaxSpeed() {
			return default_emptyMaxSpeed;
		}
	};

	private static final ICartConfig default_mobMinecartConfig = new ICartConfig() {
		@Override
		public boolean shouldUseExperimentalPhysics() {
			return default_mobUseExperimentalPhysics;
		}

		@Override
		public int getMaxSpeed() {
			return default_mobMaxSpeed;
		}
	};

	private static final ICartConfig default_playerMinecartConfig = new ICartConfig() {
		@Override
		public boolean shouldUseExperimentalPhysics() {
			return default_playerUseExperimentalPhysics;
		}

		@Override
		public int getMaxSpeed() {
			return default_playerMaxSpeed;
		}
	};

	private static final ICartConfig default_tntMinecartConfig = new ICartConfig() {
		@Override
		public boolean shouldUseExperimentalPhysics() {
			return default_tntUseExperimentalPhysics;
		}

		@Override
		public int getMaxSpeed() {
			return default_tntMaxSpeed;
		}
	};

	@Override
	public ICartConfig getEmptyMinecartConfig() {
		return default_emptyMinecartConfig;
	}

	@Override
	public ICartConfig getMobMinecartConfig() {
		return default_mobMinecartConfig;
	}

	@Override
	public ICartConfig getPlayerMinecartConfig() {
		return default_playerMinecartConfig;
	}

	@Override
	public ICartConfig getTntMinecartConfig() {
		return default_tntMinecartConfig;
	}

	@Override
	public boolean areCheaperRecipesEnabled() {
		return default_cheaperRecipes;
	}

	@Override
	public boolean shouldShowSpeedometer() {
		return default_showSpeedometer;
	}

	@Override
	public boolean shouldShowSpeedBar() {
		return default_showSpeedBar;
	}

	@Override
	public boolean shouldShowStationTitle() {
		return default_showStationTitle;
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
