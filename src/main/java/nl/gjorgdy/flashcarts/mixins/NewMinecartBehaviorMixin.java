package nl.gjorgdy.flashcarts.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import nl.gjorgdy.flashcarts.Flashcarts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NewMinecartBehavior.class)
public abstract class NewMinecartBehaviorMixin extends MinecartBehavior {

	protected NewMinecartBehaviorMixin(AbstractMinecart abstractMinecart) {
		super(abstractMinecart);
	}

	@Inject(at = @At("HEAD"), method = "getMaxSpeed", cancellable = true)
	public void overrideMaxSpeed(ServerLevel level, CallbackInfoReturnable<Double> cir) {
		cir.setReturnValue(Flashcarts.MAX_SPEED_BLOCKS_PER_SECOND * (this.minecart.isInWater() ? 0.5 : 1.0) / 20.0);
		cir.cancel();
	}

}
