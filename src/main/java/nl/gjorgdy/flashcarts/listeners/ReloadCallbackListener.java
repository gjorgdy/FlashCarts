package nl.gjorgdy.flashcarts.listeners;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import nl.gjorgdy.flashcarts.Flashcarts;
import org.jspecify.annotations.NonNull;

public class ReloadCallbackListener implements ServerLifecycleEvents.StartDataPackReload {

    @Override
    public void startDataPackReload(@NonNull MinecraftServer minecraftServer, @NonNull CloseableResourceManager closeableResourceManager) {
        Flashcarts.loadConfig();
    }

}
