package nl.gjorgdy.flashcarts.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import nl.gjorgdy.flashcarts.Flashcarts;
import nl.gjorgdy.flashcarts.handlers.BlockDisplayEntityHandler;
import nl.gjorgdy.flashcarts.interfaces.ISelectionHolder;
import nl.gjorgdy.flashcarts.utils.ItemUtils;
import nl.gjorgdy.flashcarts.utils.RailUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements ISelectionHolder {

    @Shadow
    public abstract @NonNull Level level();

    @Shadow
    public ServerGamePacketListenerImpl connection;
    @Unique
    @Nullable
    private BlockPos startPointPos;

    @Unique
    @Nullable
    private Level startPointLevel;

    @Unique
    @Nullable
    private BlockPos lookingAtPos;

    @Unique
    @Nullable
    private List<BlockPos> currentPath;

    @Unique
    @Nullable
    private BlockDisplayEntityHandler blockDisplayEntityHandler;

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Override
    public @Nullable BlockPos flashCarts$getStartPointPos() {
        return startPointPos;
    }

    @Override
    public @Nullable Level flashCarts$getStartPointLevel() {
        return startPointLevel;
    }

    @Override
    public void flashCarts$setStartPoint(BlockPos pos, Level level) {
        startPointPos = pos;
        startPointLevel = level;
    }

    @Override
    public boolean flashCarts$isStartPointSet() {
        return startPointPos != null && startPointLevel != null;
    }

    @Override
    public void flashCarts$clearStartPoint() {
        startPointPos = null;
        startPointLevel = null;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        if (level().isClientSide()
                || tickCount % 5 != 0
                || !Flashcarts.config.getBuildConfig().shouldShowSelection()
        ) return;

        if (blockDisplayEntityHandler == null) {
            blockDisplayEntityHandler = new BlockDisplayEntityHandler((ServerPlayer)(Object)this);
        }

        if (!flashCarts$isStartPointSet() || !ItemUtils.isRails(getActiveItem())) {
            blockDisplayEntityHandler.reset();
            return;
        }

        Vec3 start = getEyePosition(1.0F);
        Vec3 lookVec = getViewVector(1.0F);
        Vec3 end = start.add(lookVec.multiply(8, 8, 8));
        ClipContext context = new ClipContext(
                start,
                end,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                this
        );

        BlockHitResult blockHit = level().clip(context);
        var startPos = flashCarts$getStartPointPos();
        assert startPos != null;

        var endPos = blockHit.getBlockPos().relative(blockHit.getDirection());

        if (endPos.equals(lookingAtPos) && currentPath != null) {
            return;
        }

        lookingAtPos = endPos;
        currentPath = RailUtils.getRailPath(level(), startPos, lookingAtPos);
        assert blockDisplayEntityHandler != null;
        blockDisplayEntityHandler.reset();

        boolean pathValid = currentPath != null && !currentPath.isEmpty() && currentPath.getLast().equals(lookingAtPos);

        var delta = startPos.subtract(endPos);
        boolean zAxis = Math.abs(delta.getZ()) > Math.abs(delta.getX());

        var poweredRail = Blocks.POWERED_RAIL.defaultBlockState()
            .setValue(PoweredRailBlock.SHAPE, zAxis ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
        var rail = Blocks.RAIL.defaultBlockState()
            .setValue(RailBlock.SHAPE, zAxis ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);

        if (blockDisplayEntityHandler != null) {
            blockDisplayEntityHandler.add(
                level().getBlockState(startPos),
                startPos,
                0x44AA44
            );
        }

        int i = 1;
        int prf = Flashcarts.config.getBuildConfig().getPoweredRailFrequency();
        for (var pos : currentPath) {
            var railBlockState = (prf != 0 && i % prf == 0) ? poweredRail : rail;
            if (blockDisplayEntityHandler != null) {
                blockDisplayEntityHandler.add(
                    railBlockState,
                    pos,
                    pathValid ? 0xFFFFFF : 0xFF0000
                );
            }
            i++;
        }
    }

}
