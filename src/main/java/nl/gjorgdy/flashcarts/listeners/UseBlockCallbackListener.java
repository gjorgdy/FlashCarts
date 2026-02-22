package nl.gjorgdy.flashcarts.listeners;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.phys.BlockHitResult;
import nl.gjorgdy.flashcarts.Flashcarts;
import nl.gjorgdy.flashcarts.interfaces.ISelectionHolder;
import nl.gjorgdy.flashcarts.utils.ItemUtils;
import nl.gjorgdy.flashcarts.utils.RailUtils;
import org.jspecify.annotations.NonNull;

public class UseBlockCallbackListener implements UseBlockCallback {

    @Override
    public @NonNull InteractionResult interact(@NonNull Player player, @NonNull Level level, @NonNull InteractionHand interactionHand, @NonNull BlockHitResult blockHitResult) {
        if (level.isClientSide() || !ItemUtils.isRails(player.getItemInHand(interactionHand)) ) return InteractionResult.PASS;
        if (level.getBlockState(blockHitResult.getBlockPos()).getBlock() instanceof BaseRailBlock) {
            // rail selection
            if (Flashcarts.config.getBuildConfig().isRailSelectionBuildingEnabled() && !player.isCrouching()) {
                if (player instanceof ISelectionHolder selectionHolder) {
                    selectionHolder.flashCarts$setStartPoint(blockHitResult.getBlockPos(), level);
                    player.displayClientMessage(Component.literal("Start point set! Now select the end point."), true);
                }
            // rail extension
            } else if (Flashcarts.config.getBuildConfig().isRailExtendBuildingEnabled()) {
                var itemStack = player.getItemInHand(interactionHand);
                var blockPos = blockHitResult.getBlockPos();
                var blockState = level.getBlockState(blockPos);
                if (player instanceof ServerPlayer serverPlayer && RailUtils.place(serverPlayer, itemStack, blockState, blockPos)) {
                    player.swing(interactionHand, true);
                    return InteractionResult.SUCCESS;
                }
            }
        } else {
            // rail selection
            if (!Flashcarts.config.getBuildConfig().isRailSelectionBuildingEnabled()) return InteractionResult.PASS;
            if (player instanceof ServerPlayer serverPlayer && player instanceof ISelectionHolder selectionHolder && selectionHolder.flashCarts$isStartPointSet()) {
                if (player.isCrouching()) {
                    player.displayClientMessage(Component.literal("§aCleared selection"), true);
                    selectionHolder.flashCarts$clearStartPoint();
                    return InteractionResult.PASS;
                }
                if (selectionHolder.flashCarts$getStartPointLevel() != level) {
                    player.displayClientMessage(Component.literal("§cStart point is in a different world!"), true);
                    return InteractionResult.FAIL;
                }
                var startPos = selectionHolder.flashCarts$getStartPointPos();
                assert startPos != null;
                var path = RailUtils.getRailPath(level, startPos, blockHitResult.getBlockPos().above());

                if (path == null) {
                    player.displayClientMessage(Component.literal("§cNo valid rail path found!"), true);
                    return InteractionResult.FAIL;
                } else {
                    path.forEach(pos ->
                        ItemUtils.place(player.getItemInHand(interactionHand), player, pos, SoundEvents.METAL_PLACE)
                    );
                }

                player.displayClientMessage(Component.literal("§aStart point found; " + selectionHolder.flashCarts$getStartPointPos()), true);
                selectionHolder.flashCarts$clearStartPoint();
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

}
