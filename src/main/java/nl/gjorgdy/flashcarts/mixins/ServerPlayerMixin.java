package nl.gjorgdy.flashcarts.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import nl.gjorgdy.flashcarts.Flashcarts;
import nl.gjorgdy.flashcarts.handlers.BlockDisplayEntityHandler;
import nl.gjorgdy.flashcarts.interfaces.ISelectionHolder;
import nl.gjorgdy.flashcarts.objects.RailPath;
import nl.gjorgdy.flashcarts.utils.ItemUtils;
import nl.gjorgdy.flashcarts.utils.ListUtils;
import nl.gjorgdy.flashcarts.utils.PlayerUtils;
import nl.gjorgdy.flashcarts.utils.RailUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements ISelectionHolder {

    @Shadow
    public abstract @NonNull Level level();

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
    private RailPath currentPath;

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
        currentPath = null;
        lookingAtPos = null;
        if (blockDisplayEntityHandler != null) {
            blockDisplayEntityHandler.reset();
        }
    }

    @Override
    public boolean flashCarts$isStartPointSet() {
        return startPointPos != null && startPointLevel != null;
    }

    @Override
    public void flashCarts$clearStartPoint() {
        startPointPos = null;
        startPointLevel = null;
        currentPath = null;
        lookingAtPos = null;
        if (blockDisplayEntityHandler != null) {
            blockDisplayEntityHandler.reset();
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        if (level().isClientSide()
                || tickCount % 2 != 0
                || !Flashcarts.config.getBuildConfig().shouldShowSelection()
        ) return;

        if (blockDisplayEntityHandler == null) {
            blockDisplayEntityHandler = new BlockDisplayEntityHandler((ServerPlayer)(Object)this);
        }

        if (!flashCarts$isStartPointSet() || !ItemUtils.isRails(getActiveItem())) {
            blockDisplayEntityHandler.reset();
            return;
        }

        var startPos = flashCarts$getStartPointPos();
        assert startPos != null;
        if (!(level().getBlockState(startPos).getBlock() instanceof BaseRailBlock)) {
            blockDisplayEntityHandler.reset();
            flashCarts$clearStartPoint();
            return;
        }

        BlockHitResult blockHit = PlayerUtils.rayCast(this, blockInteractionRange());
        var targetBlockState = level().getBlockState(blockHit.getBlockPos());
        var endPos = targetBlockState.canBeReplaced() || RailUtils.isRail(targetBlockState)
                ? blockHit.getBlockPos()
                : blockHit.getBlockPos().relative(blockHit.getDirection());

        if (currentPath != null && (endPos.equals(lookingAtPos) || (targetBlockState.isAir() && !currentPath.isValid() || !startPos.equals(currentPath.start())))) return;

        lookingAtPos = endPos;
        currentPath = RailUtils.getRailPath(level(), startPos, lookingAtPos);

        assert blockDisplayEntityHandler != null;
        blockDisplayEntityHandler.resetCounter();
        blockDisplayEntityHandler.add(
            level().getBlockState(startPos),
            startPos,
            0x44AA44
        );

        AtomicInteger i = new AtomicInteger(0);
        AtomicReference<BlockPos> pos = new AtomicReference<>(startPos);
        int prf = Flashcarts.config.getBuildConfig().getPoweredRailFrequency();

        boolean pathValid = currentPath.isValid() && !targetBlockState.isAir();

        ListUtils.biIterate(currentPath.path(), (vec, nextVec) -> {
            pos.set(pos.get().offset(vec));
            i.getAndAdd(vec.getX() + vec.getZ());
            blockDisplayEntityHandler.add(
                RailUtils.getBlockState(
                    prf,
                    i.get(),
                    getActiveItem().getItem(),
                    RailUtils.getRailShape(vec, nextVec)
                ),
                pos.get(),
                pathValid ? 0xFFFFFF : 0xFF0000
            );
        });

        blockDisplayEntityHandler.removeOld();
    }

}
