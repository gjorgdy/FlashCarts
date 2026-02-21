package nl.gjorgdy.flashcarts.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.entity.vehicle.minecart.*;
import net.minecraft.world.level.Level;
import nl.gjorgdy.flashcarts.Flashcarts;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends VehicleEntity {

	@Unique
	private final AbstractMinecart self = (AbstractMinecart) (Object) this;

	@Mutable
	@Final
	@Shadow
	private MinecartBehavior behavior;

	public AbstractMinecartMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(at = @At("HEAD"), method = "tick")
	public void tick(CallbackInfo ci) {
		getPassengers().forEach(p -> {
			if (p instanceof ServerPlayer player && tickCount % 2 == 0) {
				var speedometer = Flashcarts.config.shouldShowSpeedometer();
				var speedBar = Flashcarts.config.shouldShowSpeedBar();
				if (!speedometer && !speedBar) return;
				var stringBuilder = new StringBuilder();
				var speed = getSpeedBlocksPerSecond();
				if (speed > Flashcarts.config.getHaltSpeedThreshold()) {
					if (speedBar) {
						int bars = (int) Math.floor(speed / Flashcarts.config.getPlayerMinecartConfig().getMaxSpeed() * 10);
						stringBuilder
								.append("§a")
								.append("▮".repeat(Math.min(bars, 6)))
								.append("§e")
								.append("▮".repeat(Math.clamp(bars - 6, 0, 2)))
								.append("§6")
								.append("▮".repeat(Math.clamp(bars - 8, 0, 1)))
								.append("§c")
								.append("▮".repeat(Math.clamp(bars - 9, 0, 1)))
								.append("§7")
								.append("▮".repeat(10 - bars));
					}
					if (speedometer) {
						stringBuilder.append(String.format(" %,6.2f b/s", speed));
					}
				} else {
					if (speedBar) {
						stringBuilder
							.append("§7")
							.append("▮".repeat(10));
					}
					if (speedometer) {
						stringBuilder.append(String.format(" %,6.2f b/s", 0f));
					}
				}
				player.sendSystemMessage(Component.literal(stringBuilder.toString()), true);
			}
		});
		if (tickCount % 5 != 0) return;
		var cartConfig = Flashcarts.config.getConfigForMinecart(self);
		if (cartConfig != null && cartConfig.shouldUseExperimentalPhysics()) {
			setNewMinecartBehavior();
		}
		else {
			setOldMinecartBehavior();
		}
	}

	@Unique
	private void setNewMinecartBehavior() {
		if (this.behavior instanceof OldMinecartBehavior) {
			this.behavior = new NewMinecartBehavior(self);
		}
	}

	@Unique
	private void setOldMinecartBehavior() {
		if (this.behavior instanceof NewMinecartBehavior) {
			this.behavior = new OldMinecartBehavior(self);
		}
	}

	@Unique
	private double getSpeedBlocksPerSecond() {
		var cartConfig = Flashcarts.config.getConfigForMinecart(self);
		if (cartConfig == null) {
			return self.getKnownSpeed().length() * 20;
		}
		return Math.min(this.getKnownSpeed().length() * 20, cartConfig.getMaxSpeed());
	}

}
