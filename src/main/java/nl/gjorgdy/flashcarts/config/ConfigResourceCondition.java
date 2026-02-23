package nl.gjorgdy.flashcarts.config;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import nl.gjorgdy.flashcarts.Flashcarts;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ConfigResourceCondition implements ResourceCondition {

    public static final MapCodec<ConfigResourceCondition> CODEC = MapCodec.unit(ConfigResourceCondition::new);

    @Override
    public @NonNull ResourceConditionType<?> getType() {
        return ResourceConditionType.create(Identifier.parse("flash_carts:config"), CODEC);
    }

    @Override
    public boolean test(RegistryOps.@Nullable RegistryInfoLookup registryInfoLookup) {
        return Flashcarts.config.areCheaperRecipesEnabled();
    }

}
