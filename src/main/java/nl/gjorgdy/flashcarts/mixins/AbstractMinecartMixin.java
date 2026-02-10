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
			if (p instanceof ServerPlayer player) {
				var speed = getSpeedBlocksPerSecond();
				if (speed > Flashcarts.config.getHaltSpeedThreshold()) {
					int bars = (int) Math.round(speed / Flashcarts.config.getMaxSpeed() * 10);
					String barString = "§a" + "▮".repeat(Math.min(bars, 7))
							   + "§e" + "▮".repeat(Math.min(Math.max(bars - 7, 0), 2))
							   + "§c" + "▮".repeat(Math.max(bars - 9, 0))
							   + "§7" + "▮".repeat(10 - bars);
					player.sendSystemMessage(Component.literal(barString + String.format(" %.2f", speed) + " b/s"), true);
				} else {
					String barString = "§7" + "▮".repeat(10);
					player.sendSystemMessage(Component.literal(barString + " 0.00 b/s"), true);
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
