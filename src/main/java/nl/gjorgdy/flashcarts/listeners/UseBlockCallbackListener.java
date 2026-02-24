package nl.gjorgdy.flashcarts.listeners;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.phys.BlockHitResult;
import nl.gjorgdy.flashcarts.Flashcarts;
import nl.gjorgdy.flashcarts.interfaces.ISelectionHolder;
import nl.gjorgdy.flashcarts.utils.ItemUtils;
import nl.gjorgdy.flashcarts.utils.PlayerUtils;
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
                    if (player instanceof ServerPlayer splayer) {
                        splayer.displayClientMessage(Component.literal("§aStart point set, place a rail to build"), true);
                        PlayerUtils.playDirectSound(splayer, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS);
                        splayer.swing(interactionHand);
                    }
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
                    serverPlayer.displayClientMessage(Component.literal("§6Cleared selection"), true);
                    PlayerUtils.playDirectSound(serverPlayer, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS);
                    serverPlayer.swing(interactionHand);
                    selectionHolder.flashCarts$clearStartPoint();
                    return InteractionResult.PASS;
                }
                if (selectionHolder.flashCarts$getStartPointLevel() != level) {
                    player.displayClientMessage(Component.literal("§cNo valid rail path found!"), true);
                    return InteractionResult.FAIL;
                }
                var startPos = selectionHolder.flashCarts$getStartPointPos();
                assert startPos != null;
                var endPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
                var path = RailUtils.getRailPath(level, startPos, endPos);

                if (path.isEmpty() || !path.getLast().equals(endPos)) {
                    player.displayClientMessage(Component.literal("§cNo valid rail path found!"), true);
                    return InteractionResult.FAIL;
                } else {
                    int i = 1;
                    int prf = Flashcarts.config.getBuildConfig().getPoweredRailFrequency();
                    for (BlockPos blockPos : path) {
                        var rail = (prf != 0 && i % prf == 0) ? Items.POWERED_RAIL : Items.RAIL;
                        boolean placed = ItemUtils.place(rail, player, blockPos, SoundEvents.METAL_PLACE);
                        if (!placed) break;
                        i++;
                    }
                }

                if (player instanceof ServerPlayer splayer) {
                    PlayerUtils.playDirectSound(splayer, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS);
                    splayer.swing(interactionHand);
                }
                selectionHolder.flashCarts$clearStartPoint();
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

}
