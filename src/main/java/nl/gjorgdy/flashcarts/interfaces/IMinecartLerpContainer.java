package nl.gjorgdy.flashcarts.interfaces;

import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;

import java.util.List;

public interface IMinecartLerpContainer {

	List<NewMinecartBehavior.MinecartStep> flashCarts$popSteps();

}
