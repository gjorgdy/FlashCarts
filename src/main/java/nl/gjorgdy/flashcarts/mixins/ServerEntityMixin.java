package nl.gjorgdy.flashcarts.mixins;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.util.Mth;
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

	@Shadow
	private byte lastSentYHeadRot;

	@Shadow
	private int tickCount;

	@Shadow
	@Final
	private VecDeltaCodec positionCodec;

	@Shadow
	private byte lastSentYRot;

	@Shadow
	private byte lastSentXRot;

	@WrapOperation(method = "sendChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerEntity$Synchronizer;sendToTrackingPlayers(Lnet/minecraft/network/protocol/Packet;)V"))
	private void cancelUpdate(ServerEntity.Synchronizer instance, Packet<? super ClientGamePacketListener> packet, Operation<Void> original) {
		// Cancel move and motion packets if minecart with old physics
		if (this.entity instanceof Minecart minecart
				&& minecart.getBehavior() instanceof OldMinecartBehavior
				&& (packet instanceof ClientboundMoveEntityPacket
				|| packet instanceof ClientboundSetEntityMotionPacket
		)
		) {
			return;
		}
		original.call(instance, packet);
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

				this.lastSentYRot = Mth.packDegrees(this.entity.getYRot());
				this.lastSentXRot = Mth.packDegrees(this.entity.getXRot());
				this.positionCodec.setBase(this.entity.position());
				return false;
			}
			return true;
		}
		return false;
	}

}
