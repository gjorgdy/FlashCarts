package nl.gjorgdy.flashcarts.mixins;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior;
import nl.gjorgdy.flashcarts.interfaces.IMinecartLerpContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;
import java.util.List;

@Mixin(OldMinecartBehavior.class)
public abstract class OldMinecartBehaviorMixin extends MinecartBehavior implements IMinecartLerpContainer {

	@Unique
	private final List<NewMinecartBehavior.MinecartStep> steps = new LinkedList<>();

	protected OldMinecartBehaviorMixin(AbstractMinecart abstractMinecart) {
		super(abstractMinecart);
		addStep();
	}

	@Inject(method = "moveAlongTrack", at = @At("HEAD"))
	public void onMoveAlongTrack(CallbackInfo ci) {
		addStep();
	}

	@Unique
	private void resetSteps() {
		var lastStep = this.steps.isEmpty() ? null : this.steps.getLast();
		this.steps.clear();
		if (lastStep != null) {
			this.steps.add(
				new NewMinecartBehavior.MinecartStep(lastStep.position(), lastStep.movement(), lastStep.yRot(), lastStep.xRot(), 0.0f)
			);
		}
	}

	@Override
	public List<NewMinecartBehavior.MinecartStep> flashCarts$popSteps() {
		if (this.steps.isEmpty()) return ImmutableList.of();
		var stepsToReturn = ImmutableList.copyOf(this.steps);
		resetSteps();
		return stepsToReturn;
	}

	@Unique
	private void addStep() {
		var movement = minecart.position().subtract(this.steps.isEmpty() ? this.minecart.position() : this.steps.getLast().position());
		var distance = (float) movement.length();
		if (!this.steps.isEmpty() && distance == 0) return;
		// horizontal rotation
		float yRot = minecart.getYRot() * -1.0F + 180F;
		if (movement.x > 0.3 ^ movement.z > 0.3) {
			if (yRot % 90f != 0) {
				yRot = Math.round(yRot / 90f) * 90f;
			}
		}
		// vertical rotation
		float xRot = 0f;
		if (movement.y > 0.1) {
			xRot = -45f;
		} else if (movement.y < -0.1) {
			xRot = 45f;
		}
		xRot *= minecart.isFlipped() ? -1.0F : 1.0F;
		// lerp step
		this.steps.add(
			new NewMinecartBehavior.MinecartStep(
				minecart.position(),
				minecart.getDeltaMovement(),
				yRot, xRot, distance
			)
		);
	}

}
