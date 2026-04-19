package nl.gjorgdy.flashcarts.mixins;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior;
import net.minecraft.world.phys.Vec3;
import nl.gjorgdy.flashcarts.interfaces.IMinecartLerpContainer;
import nl.gjorgdy.flashcarts.utils.RailUtils;
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

	@Inject(method = "moveAlongTrack", at = @At("RETURN"))
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
		if (this.steps.isEmpty()) addStep();
		var stepsToReturn = ImmutableList.copyOf(this.steps);
		if (this.steps.size() > 1) resetSteps();
		return stepsToReturn;
	}

	@Unique
	private void addStep() {
		var movement = this.steps.isEmpty() ? Vec3.ZERO : minecart.position().subtract(this.steps.getLast().position());
		// movement since first step
		if (movement.length() < 0.1) {
			if (this.steps.isEmpty()) {
				this.steps.add(calculateStillStep(movement));
			}
			return;
		}
		this.steps.add(calculateMovingStep(movement));
	}

	@Unique
	private NewMinecartBehavior.MinecartStep calculateMovingStep(Vec3 movement) {
		var distance = movement.length();

		// horizontal rotation
		float yRot = minecart.getYRot() * -1;

		// vertical rotation
		float xRot = 0f;
		if (movement.y > 0.1) {
			xRot = 45f;
		} else if (movement.y < -0.1) {
			xRot = -45f;
		}
		xRot *= minecart.isFlipped() ? -1.0F : 1.0F;

		return new NewMinecartBehavior.MinecartStep(
			minecart.position(),
			movement,
			yRot, xRot,
			(float) distance
		);
	}

	@Unique
	private NewMinecartBehavior.MinecartStep calculateStillStep(Vec3 movement) {
		var block = level().getBlockState(minecart.blockPosition());
		var railShape = RailUtils.getRailShape(block);

		// horizontal rotation
		float yRot = switch (railShape) {
			case NORTH_EAST, SOUTH_WEST -> -135F;
			case ASCENDING_NORTH, ASCENDING_SOUTH, NORTH_SOUTH -> 90F;
			case SOUTH_EAST, NORTH_WEST -> 45F;
			case ASCENDING_EAST, ASCENDING_WEST, EAST_WEST -> 0.0F;
		};
		
		// vertical rotation
		float xRot = switch (railShape) {
			case ASCENDING_EAST, ASCENDING_WEST, ASCENDING_SOUTH, ASCENDING_NORTH -> 45F;
			default -> 0F;
		};
		xRot *= minecart.isFlipped() ? -1.0F : 1.0F;

		return new NewMinecartBehavior.MinecartStep(
			minecart.position(),
			movement,
			yRot, xRot,
			1.0F
		);
	}

}
