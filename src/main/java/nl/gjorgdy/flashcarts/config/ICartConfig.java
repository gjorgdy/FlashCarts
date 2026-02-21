package nl.gjorgdy.flashcarts.config;

import java.io.Serializable;

public interface ICartConfig extends Serializable {
    boolean shouldUseExperimentalPhysics();
    int getMaxSpeed();
}