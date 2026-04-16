package nl.gjorgdy.flashcarts.mixins;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundMoveMinecartPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior;
import nl.gjorgdy.flashcarts.interfaces.IMinecartLerpContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {

	@Final
	@Shadow
	private ServerEntity.Synchronizer synchronizer;

	@Shadow
	@Final
	private Entity entity;

	@Inject(method = "sendChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;trackingPosition()Lnet/minecraft/world/phys/Vec3;"), cancellable = true)
	public void cancelUpdate(CallbackInfo ci) {
		if (entity instanceof AbstractMinecart minecart && minecart.getBehavior() instanceof OldMinecartBehavior) {
			ci.cancel();
		}
	}

	@Definition(id = "AbstractMinecart", type = AbstractMinecart.class)
	@Expression("? instanceof AbstractMinecart")
	@WrapOperation(method = "sendChanges()V", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean fakeNewPackets(Object object, Operation<Boolean> original) {
		if (object instanceof AbstractMinecart minecart) {
			if (minecart.getBehavior() instanceof IMinecartLerpContainer lerpContainer) {
				this.synchronizer.sendToTrackingPlayers(
					new ClientboundMoveMinecartPacket(
						minecart.getId(),
						lerpContainer.flashCarts$popSteps()
					)
				);
				return false;
			}
			return true;
		}
		return false;
	}

}
