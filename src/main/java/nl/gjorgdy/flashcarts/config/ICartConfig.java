package nl.gjorgdy.flashcarts.config;

import java.io.Serializable;

public interface ICartConfig extends Serializable {
    boolean useExperimentalPhysics();
    int maxSpeed();
}