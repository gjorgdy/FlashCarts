package nl.gjorgdy.flashcarts.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NewMinecartBehavior.class)
public abstract class NewMinecartBehaviorMixin extends MinecartBehavior {

	@Unique
	private final static float MAX_SPEED = 100.0F;

	protected NewMinecartBehaviorMixin(AbstractMinecart abstractMinecart) {
		super(abstractMinecart);
	}

	@Inject(at = @At("HEAD"), method = "getMaxSpeed", cancellable = true)
	public void overrideMaxSpeed(ServerLevel level, CallbackInfoReturnable<Double> cir) {
		cir.setReturnValue(MAX_SPEED * (this.minecart.isInWater() ? 0.5 : 1.0) / 20.0);
		cir.cancel();
	}

}
