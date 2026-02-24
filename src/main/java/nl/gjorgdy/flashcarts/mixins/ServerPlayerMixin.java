package nl.gjorgdy.flashcarts.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import nl.gjorgdy.flashcarts.Flashcarts;
import nl.gjorgdy.flashcarts.interfaces.ISelectionHolder;
import nl.gjorgdy.flashcarts.utils.RailUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
                || tickCount % 10 != 0
                || !flashCarts$isStartPointSet()
                || !Flashcarts.config.getBuildConfig().shouldShowSelectionParticles()
        ) return;

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
        var path = RailUtils.getRailPath(level(), startPos, endPos);

        boolean pathValid = !path.isEmpty() && path.getLast().equals(endPos);
        if (level() instanceof ServerLevel level) {
            for (var pos : path) {
                level.sendParticles(
                    pathValid ? ParticleTypes.HAPPY_VILLAGER : ParticleTypes.WAX_OFF,
                    pos.getX() + 0.5,
                    pos.getY() + 0.25,
                    pos.getZ() + 0.5,
                    1, 0, 0, 0, 0
                );
            }
        }
    }

}
