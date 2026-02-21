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

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {

	@Final
	@Shadow
	private ServerEntity.Synchronizer synchronizer;

	@Shadow
	@Final
	private Entity entity;

	@WrapOperation(method = "sendChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerEntity$Synchronizer;sendToTrackingPlayers(Lnet/minecraft/network/protocol/Packet;)V"))
	private void cancelUpdate(ServerEntity.Synchronizer instance, Packet<? super ClientGamePacketListener> packet, Operation<Void> original) {
		if (this.entity instanceof Minecart minecart && minecart.getBehavior() instanceof OldMinecartBehavior) {
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
				return false;
			}
			return true;
		}
		return false;
	}

}
