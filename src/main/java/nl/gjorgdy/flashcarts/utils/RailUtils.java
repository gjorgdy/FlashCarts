package nl.gjorgdy.flashcarts.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import nl.gjorgdy.flashcarts.Flashcarts;
import nl.gjorgdy.flashcarts.mixins.BaseRailBlockInvoker;
import nl.gjorgdy.flashcarts.objects.RailPath;

import java.util.LinkedList;
import java.util.List;

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

    public static RailPath getRailPath(Level level, BlockPos startPos, BlockPos endPos) {
        var dist = startPos.distChessboard(endPos);
        int maxDist = Flashcarts.config.getBuildConfig().getRailSelectionBuildingMaxDistance();
        if (startPos.equals(endPos)) return new RailPath(startPos, endPos, List.of(), false);

        Vec3i step;
        if (startPos.getX() != endPos.getX()) {
            step = new Vec3i(Integer.signum(endPos.getX() - startPos.getX()), 0, 0);
        } else {
            step = new Vec3i(0, 0, Integer.signum(endPos.getZ() - startPos.getZ()));
        }
        var path = new LinkedList<Vec3i>();
        var currentPos = new BlockPos(startPos);
        var _step = new Vec3i(step.getX(), step.getY(), step.getZ());
        while (!currentPos.equals(endPos)) {
            currentPos = currentPos.offset(step);
            maxDist--;
            var currentDist = startPos.distChessboard(currentPos);
            if (currentDist > dist || currentDist > maxDist) return new RailPath(startPos, endPos, path, false);

            if (isRail(level, currentPos)) {
                _step = _step.offset(step);
                continue;
            }
            if (!canBePlaced(level, currentPos)) {
                if (canBePlaced(level, currentPos.above())) {
                    currentPos = currentPos.above();
                    path.add(_step.above());
                } else if (isRail(level, currentPos.above())) {
                    currentPos = currentPos.above();
                    _step = _step.offset(step).above();
                    continue;
                } else if (canBePlaced(level, currentPos.below())) {
                    currentPos = currentPos.below();
                    path.add(_step.below());
                } else if (isRail(level, currentPos.below())) {
                    currentPos = currentPos.below();
                    _step = _step.offset(step).below();
                    continue;
                } else return new RailPath(startPos, endPos, path, false);
            } else {
                path.add(_step);
            }
            if (maxDist < 0) return new RailPath(startPos, endPos, path, false);
            _step = new Vec3i(step.getX(), step.getY(), step.getZ());
        }

        return new RailPath(startPos, endPos, path, true);
    }

    public static RailShape getRailShape(Vec3i vec) {
        if (vec.getY() == 0 || (vec.getX() != vec.getY() && vec.getZ() != vec.getY())) {
            if (vec.getX() != 0) {
                return RailShape.EAST_WEST;
            } else {
                return RailShape.NORTH_SOUTH;
            }
        } else {
            if ((vec.getY() > 0 && vec.getX() > 0) || (vec.getY() < 0 && vec.getX() < 0)) {
                return RailShape.ASCENDING_EAST;
            } else if ((vec.getY() > 0 && vec.getX() < 0) || (vec.getY() < 0 && vec.getX() > 0)) {
                return RailShape.ASCENDING_WEST;
            } else if ((vec.getY() > 0 && vec.getZ() > 0) || (vec.getY() < 0 && vec.getZ() < 0)) {
                return RailShape.ASCENDING_SOUTH;
            } else {
                return RailShape.ASCENDING_NORTH;
            }
        }
    }

    private static boolean isRail(Level level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() instanceof BaseRailBlock;
    }

    private static boolean canBePlaced(Level level, BlockPos pos) {
        if (Blocks.RAIL instanceof BaseRailBlockInvoker invoker) {
            return level.getBlockState(pos).canBeReplaced()
                    && invoker.flashCarts$canSurvive(Blocks.RAIL.defaultBlockState(), level, pos);
        }
        return false;
    }

    private static boolean isXAxis(RailShape shape) {
        return (shape == RailShape.EAST_WEST) || (shape == RailShape.ASCENDING_EAST) || (shape == RailShape.ASCENDING_WEST);
    }

    private static RailShape getRailShape(BlockState state) {
        return state.getValueOrElse(PoweredRailBlock.SHAPE, state.getValueOrElse(RailBlock.SHAPE, RailShape.NORTH_SOUTH));
    }

}
