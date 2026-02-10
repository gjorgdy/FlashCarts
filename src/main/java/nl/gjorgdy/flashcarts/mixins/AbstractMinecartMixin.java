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
	public void _tick(CallbackInfo ci) {
		getPassengers().forEach(p -> {
			if (p instanceof ServerPlayer player) {
				var speed = getSpeedBlocksPerSecond();
				if (speed > Flashcarts.config.getHaltSpeedThreshold()) {
					player.sendSystemMessage(Component.literal(String.format("%.2f", speed) + " b/s"), true);
				} else {
					player.sendSystemMessage(Component.empty(), true);
				}
			}
		});
		if ((self instanceof Minecart || (self instanceof MinecartTNT && Flashcarts.config.shouldIncreaseTntMinecartSpeed()))
					&& this.behavior instanceof OldMinecartBehavior
		) {
			this.behavior = new NewMinecartBehavior(self);
		}
	}

	@Unique
	private double getSpeedBlocksPerSecond() {
		return Math.min(this.getKnownSpeed().length() * 20, Flashcarts.config.getMaxSpeed());
	}

}
