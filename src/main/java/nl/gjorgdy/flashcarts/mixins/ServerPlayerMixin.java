package nl.gjorgdy.flashcarts.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import nl.gjorgdy.flashcarts.interfaces.ISelectionHolder;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements ISelectionHolder {

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
}
