package nl.gjorgdy.flashcarts.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import nl.gjorgdy.flashcarts.Flashcarts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NewMinecartBehavior.class)
public abstract class NewMinecartBehaviorMixin extends MinecartBehavior {

	protected NewMinecartBehaviorMixin(AbstractMinecart abstractMinecart) {
		super(abstractMinecart);
	}

	@Inject(at = @At("HEAD"), method = "getMaxSpeed", cancellable = true)
	public void overrideMaxSpeed(ServerLevel level, CallbackInfoReturnable<Double> cir) {
		cir.setReturnValue(Flashcarts.config.getMaxSpeedBlocksPerSecond() * (this.minecart.isInWater() ? 0.5 : 1.0) / 20.0);
		cir.cancel();
	}

	@ModifyConstant(
		method = "calculateHaltTrackSpeed(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/phys/Vec3;",
		constant = @Constant(doubleValue = 0.03)
	)
	private double replaceHaltThreshold(double constant) {
		return Flashcarts.config.getHaltSpeedThreshold();
	}

	@ModifyArg(
			method = "calculateHaltTrackSpeed(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/phys/Vec3;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;"),
			index = 0
	)
	private double replaceHaltMultiplier(double d) {
		return Flashcarts.config.getHaltSpeedMultiplier();
	}

}
