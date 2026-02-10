package nl.gjorgdy.flashcarts.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Entity.class)
public class EntityMixin {

	@Inject(method = "setOnGroundWithMovement(ZZLnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"))
	public void onPush(boolean bl, boolean horizontalCollision, Vec3 vec3, CallbackInfo ci) {
		if (!horizontalCollision) {
			return;
		}
		if ((((Object) this) instanceof AbstractMinecart minecart)) {
			var collisionBox = minecart.getBoundingBox().inflate(1.0E-7);
			List<Entity> list = minecart.level().getEntities(minecart, collisionBox, EntitySelector.pushableBy(minecart));
			if (!list.isEmpty()) {
				for (Entity entity : list) {
					if (entity instanceof AbstractMinecart colliderMinecart) {
						this.onCollide(colliderMinecart, minecart);
					}
				}
			}
		}
	}

	@Unique
	private void onCollide(AbstractMinecart collider, AbstractMinecart self) {
		if (collider.getKnownMovement().length() == self.getKnownMovement().length()) {
			return;
		}
		if (collider.getKnownMovement().length() < self.getKnownMovement().length()) {
			if (self.countPlayerPassengers() >= 1 && collider.countPlayerPassengers() == 0 && collider instanceof VehicleEntityInvoker colliderMinecart) {
				colliderMinecart.flash_cart$destroy((ServerLevel) collider.level(), colliderMinecart.flash_cart$getDropItem());
			}
		} else {
			if (collider.countPlayerPassengers() >= 1 && self.countPlayerPassengers() == 0 && self instanceof VehicleEntityInvoker selfMinecart) {
				selfMinecart.flash_cart$destroy((ServerLevel) self.level(), selfMinecart.flash_cart$getDropItem());
			}
		}
	}

}
