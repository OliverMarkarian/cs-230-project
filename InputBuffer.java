import javafx.scene.input.KeyEvent;

import java.security.Key;
import java.util.Optional;

/**
 * A class to store input events until they are allowed to be used, following the game's functional spec.
 * This class provides mechanisms for querying and receiving input events for the player.
 *
 * @author Sam
 * @see Game
 * @see KeyEvent
 */
public class InputBuffer {
    private final int tickrate;
    private int tickAccumulator;
    private KeyEvent event;

    /**
     * Constructs an InputBuffer with a specified tickrate.
     *
     * @param tickrate The number of ticks before an input event is allowed to be read.
     */
    InputBuffer(int tickrate) {
        this.tickrate = tickrate;
        event = null;
        tickAccumulator = 0;
    }

    /**
     * Called on each game tick to check if an input event is ready to be read.
     * It checks if enough ticks have passed to allow reading the event, and if so, returns the event.
     *
     * @return An Optional containing the input event if it is ready to be processed; otherwise, an empty Optional.
     */
    Optional<KeyEvent> tryRead() {
        tickAccumulator++;
        if (tickAccumulator >= tickrate) {
            tickAccumulator = 0;
            if (event == null) {
                return Optional.empty();
            }

            KeyEvent temp = event;
            event = null;
            return Optional.of(temp);
        }
        return Optional.empty();
    }

    /**
     * Receives a key event, storing it until it is allowed to be read based on the tickrate.
     * This method is called when a KeyEvent is triggered by the player (or other input source).
     *
     * @param newEvent The key event that is being received and stored.
     */
    void receiveEvent(KeyEvent newEvent) {
        event = newEvent;
        newEvent.consume();
    }
}
