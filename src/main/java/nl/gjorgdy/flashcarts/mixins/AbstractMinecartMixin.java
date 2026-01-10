package nl.gjorgdy.flashcarts.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.entity.vehicle.minecart.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import nl.gjorgdy.flashcarts.Flashcarts;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends VehicleEntity {

	@Mutable
	@Final
	@Shadow
	private MinecartBehavior behavior;

	@Unique
	private final AbstractMinecart self = (AbstractMinecart) (Object) this;

	public AbstractMinecartMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(at = @At("HEAD"), method = "getBlockSpeedFactor", cancellable = true)
	private void _getBlockSpeedFactor(CallbackInfoReturnable<Float> cir) {
		if (self.getBehavior() instanceof OldMinecartBehavior) return;
		BlockState blockState = this.level().getBlockState(this.blockPosition());
		cir.setReturnValue(blockState.is(BlockTags.RAILS) ? Flashcarts.BLOCK_SPEED_FACTOR_RAILS : super.getBlockSpeedFactor());
		cir.cancel();
	}

	@Inject(at = @At("HEAD"), method = "tick")
	public void _tick(CallbackInfo ci) {
		getPassengers().forEach(p -> {
			if (p instanceof ServerPlayer player) {
				var speed = getSpeedBlocksPerSecond();
				if (speed > Flashcarts.HALT_SPEED_THRESHOLD) { // TODO: base on breaking speed
					player.sendSystemMessage(Component.literal(String.format("%.2f", speed) + " b/s"), true);
				} else {
					player.sendSystemMessage(Component.empty(), true);
				}
			}
		});
		if ((self instanceof Minecart || (self instanceof MinecartTNT && Flashcarts.INCREASE_TNT_MINECART_SPEED))
				&& this.behavior instanceof OldMinecartBehavior
		) {
			this.behavior = new NewMinecartBehavior(self);
		}
	}

	@Unique
	private double getSpeedBlocksPerSecond() {
		return Math.min(this.getKnownSpeed().length() * 20, 100);
	}

}
