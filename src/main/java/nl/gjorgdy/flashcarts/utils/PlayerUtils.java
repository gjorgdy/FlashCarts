package nl.gjorgdy.flashcarts.utils;

import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public abstract class PlayerUtils {

    public static void playDirectSound(ServerPlayer player, SoundEvent soundEvent, SoundSource soundCategory) {
        playDirectSound(player, soundEvent, soundCategory, 1.0f, 1.0f);
    }

    public static void playDirectSound(ServerPlayer player, SoundEvent soundEvent, SoundSource soundCategory, float volume, float pitch) {
        var packet = new ClientboundSoundEntityPacket(
                Holder.direct(soundEvent),
                soundCategory,
                player,
                volume,
                pitch,
                player.getRandom().nextLong()
        );
        player.connection.send(packet);
    }

}
