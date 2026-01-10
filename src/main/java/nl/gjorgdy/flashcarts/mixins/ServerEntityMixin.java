package nl.gjorgdy.flashcarts.mixins;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.protocol.game.ClientboundMoveMinecartPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior;
import net.minecraft.world.level.block.BaseRailBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {

	@Final
	@Shadow
	private ServerEntity.Synchronizer synchronizer;
	@Final
	@Shadow
	private ServerLevel level;

	@Unique
	private NewMinecartBehavior.MinecartStep previousStep = null;

	@Definition(id = "AbstractMinecart", type = AbstractMinecart.class)
	@Expression("? instanceof AbstractMinecart")
	@WrapOperation(method = "sendChanges()V", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean fakeNewPackets(Object object, Operation<Boolean> original) {
		if (object instanceof AbstractMinecart minecart) {
			if (minecart.getBehavior() instanceof OldMinecartBehavior) {
				float xRot = 0f;
				var block = this.level.getBlockState(minecart.getOnPos());
				if (block.getBlock() instanceof BaseRailBlock rail && block.getValue(rail.getShapeProperty()).isSlope()) {
					xRot = switch (block.getValue(rail.getShapeProperty())) {
						case ASCENDING_NORTH, ASCENDING_EAST -> minecart.getYRot() <= 0 ? -45f : 45f;
						case ASCENDING_SOUTH, ASCENDING_WEST -> minecart.getYRot() <= 0 ? 45f : -45f;
						default -> 0f;
					};
				}
				var step = new NewMinecartBehavior.MinecartStep(minecart.position(), minecart.getDeltaMovement(), minecart.getYRot() * -1, xRot, 1.0F);
				var steps = previousStep == null ? List.of(step) : List.of(previousStep, step);
				this.synchronizer.sendToTrackingPlayers(new ClientboundMoveMinecartPacket(minecart.getId(), steps));
				this.previousStep = new NewMinecartBehavior.MinecartStep(step.position(), step.movement(), step.yRot(), step.xRot(), 0F);
				return false;
			}
			return true;
		}
		return false;
	}

}
