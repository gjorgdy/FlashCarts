package nl.gjorgdy.flashcarts.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

public abstract class RailUtils {

    public static boolean place(ServerPlayer player, ItemStack railItem, BlockState blockState, BlockPos pos) {
        Level world = player.level();
        RailShape shape = getRailShape(blockState);
        var playerDirection = player.getDirection();
        boolean xAxisRails = isXAxis(shape);
        boolean xAxisPlayer = (playerDirection == Direction.EAST) || (playerDirection == Direction.WEST);
        return xAxisRails == xAxisPlayer &&
                forward(world, player, playerDirection.getUnitVec3i(), railItem, pos, xAxisRails, 8, false);
    }

    private static boolean forward(Level world, Player player, Vec3i vec, ItemStack railItem, BlockPos pos, boolean xAxisRails, int depth, boolean movedVertically) {
        if (depth == 0) return false;
        var _blockState = world.getBlockState(pos);
        // if there is place
        if (_blockState.isAir()) {
            var _blockStateDown = world.getBlockState(pos.below());
            if ((_blockStateDown.isAir() || _blockStateDown.getBlock() instanceof BaseRailBlock) && !movedVertically) {
                pos = pos.below();
                movedVertically = true;
                depth++;
            } else {
                return ItemUtils.place(railItem, player, pos, SoundEvents.METAL_PLACE);
            }
        }
        // if block in way
        if (!_blockState.isAir() && !(_blockState.getBlock() instanceof BaseRailBlock) && !movedVertically) {
            pos = pos.above();
            movedVertically = true;
            depth++;
        }
        // if rails and on same axis
        if (_blockState.getBlock() instanceof BaseRailBlock && isXAxis(getRailShape(_blockState)) == xAxisRails) {
            pos = pos.offset(vec);
            movedVertically = false;
        }
        return forward(world, player, vec, railItem, pos, xAxisRails, depth - 1, movedVertically);
    }

    private static boolean isXAxis(RailShape shape) {
        return (shape == RailShape.EAST_WEST) || (shape == RailShape.ASCENDING_EAST) || (shape == RailShape.ASCENDING_WEST);
    }

    private static RailShape getRailShape(BlockState state) {
        return state.getValueOrElse(PoweredRailBlock.SHAPE, state.getValueOrElse(RailBlock.SHAPE, RailShape.NORTH_SOUTH));
    }

}
