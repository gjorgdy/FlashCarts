package nl.gjorgdy.flashcarts.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import nl.gjorgdy.flashcarts.Flashcarts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(NewMinecartBehavior.class)
public abstract class NewMinecartBehaviorMixin extends MinecartBehavior {

	protected NewMinecartBehaviorMixin(AbstractMinecart abstractMinecart) {
		super(abstractMinecart);
	}

	@WrapMethod(method = "getMaxSpeed")
	public double overrideMaxSpeed(ServerLevel serverLevel, Operation<Double> original) {
		var cartConfig = Flashcarts.config.getConfigForMinecart(minecart);
		int maxSpeed = cartConfig != null && cartConfig.shouldUseExperimentalPhysics()
				? cartConfig.getMaxSpeed()
				: 8;
		return maxSpeed * (this.minecart.isInWater() ? 0.5 : 1.0) / 20.0;
	}

	@ModifyConstant(
			method = "calculateBoostTrackSpeed(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/phys/Vec3;",
			constant = @Constant(doubleValue = 0.06)
	)
	private double replaceBoostPercentage(double constant) {
		return Flashcarts.config.getPoweredRailBoostPercentage();
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
