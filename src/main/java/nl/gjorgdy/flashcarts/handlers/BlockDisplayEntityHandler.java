package nl.gjorgdy.flashcarts.handlers;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

public class BlockDisplayEntityHandler {

    private int topId = Integer.MAX_VALUE -1;

    private final ServerPlayer player;

    public BlockDisplayEntityHandler(ServerPlayer player) {
        this.player = player;
    }

    public void reset() {
        player.connection.send(
            new ClientboundRemoveEntitiesPacket(
                IntStream.rangeClosed(topId, Integer.MAX_VALUE - 1).toArray()
            )
        );
        topId = Integer.MAX_VALUE -1;
    }

    public int count() {
        return Integer.MAX_VALUE - topId - 1;
    }

    public void add(BlockState blockState, BlockPos pos, int color) {
        var displayEntity = EntityType.BLOCK_DISPLAY.create(
                player.level(),
                EntitySpawnReason.COMMAND
        );
        if (displayEntity == null) return;

        displayEntity.setId(topId--);
        displayEntity.setUUID(UUID.randomUUID());
        displayEntity.setBlockState(blockState);

        displayEntity.setGlowingTag(true);
        displayEntity.setGlowColorOverride(color);

        player.connection.send(
                new ClientboundAddEntityPacket(
                        displayEntity,
                        displayEntity.getId(),
                        pos
                )
        );
        player.connection.send(
                new ClientboundSetEntityDataPacket(
                        displayEntity.getId(),
                        Objects.requireNonNull(displayEntity.getEntityData().getNonDefaultValues())
                )
        );
    }

}
