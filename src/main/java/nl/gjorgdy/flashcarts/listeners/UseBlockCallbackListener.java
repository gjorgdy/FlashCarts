package nl.gjorgdy.flashcarts.listeners;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.phys.BlockHitResult;
import nl.gjorgdy.flashcarts.Flashcarts;
import nl.gjorgdy.flashcarts.utils.ItemUtils;
import nl.gjorgdy.flashcarts.utils.RailUtils;
import org.jspecify.annotations.NonNull;

public class UseBlockCallbackListener implements UseBlockCallback {

    @Override
    public @NonNull InteractionResult interact(@NonNull Player player, @NonNull Level level, @NonNull InteractionHand interactionHand, @NonNull BlockHitResult blockHitResult) {
        if (level.isClientSide()) return InteractionResult.PASS;
        if (ItemUtils.isRails(player.getItemInHand(interactionHand)) && level.getBlockState(blockHitResult.getBlockPos()).getBlock() instanceof BaseRailBlock) {
            // rail selection
            if (Flashcarts.config.getBuildConfig().isRailSelectionBuildingEnabled() && !player.isCrouching()) {
                player.displayClientMessage(Component.literal("Rail selection is currently not implemented, sorry!"), true);
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
        }
        return InteractionResult.PASS;
    }

}
