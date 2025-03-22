import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.IOException;
import java.text.ParseException;

/**
 * The class provides a framework for actors that have position,
 * type and interact with Grid.
 *
 * @author Sam
 */
public abstract class Actor {
    /**
     * The number of ticks between updates.
     */
    private final int tickRate;
    /**
     * The number of ticks since the last update.
     */
    private int tickCount;
    /**
     * The type of the actor.
     */
    private ActorType type;
    /**
     * The x-coordinate of the actor.
     */
    private int x;
    /**
     * The y-coordinate of the actor.
     */
    private int y;
    /**
     * Whether the actor is alive.
     */
    private boolean isAlive;

    /**
     * Constructs an {@code Actor} with specified parameters.
     *
     * @param tickRate the number of ticks between updates
     * @param x        the initial x-coordinate of the actor
     * @param y        the initial y-coordinate of the actor
     * @param type     the type of the actor
     */
    public Actor(final int tickRate, final int x,
                 final int y, final ActorType type) {
        this.tickRate = tickRate;
        tickCount = 0;
        this.x = x;
        this.y = y;
        isAlive = true;
        this.type = type;
    }

    /**
     * Constructs a temporary {@code Actor} with default values.
     */
    public Actor() {
        tickRate = 2;
        tickCount = 0;
        isAlive = true;
    }

    /**
     * Gets the actor's y-coordinate.
     *
     * @return the y-coordinate of the actor
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the actor's x-coordinate.
     *
     * @return the x-coordinate of the actor
     */
    public int getX() {
        return x;
    }

    /**
     * Marks the actor as dead.
     */
    public void kill() {
        isAlive = false;
    }



    /**
     * Gets the type of the actor.
     *
     * @return the actor's type
     */
    public ActorType getType() {
        return type;
    }

    /**
     * Handles interaction between this actor and another.
     *
     * @param interactor the actor interacting with this actor
     */
    public abstract void onInteract(Actor interactor);

    /**
     * Checks if the actor is alive.
     *
     * @return {@code true} if the actor is alive; {@code false} otherwise
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Restores the actor's state from a text representation.
     *
     * @param text the text representation of the actor
     */
    public abstract void fromText(String text);

    /**
     * Converts the actor's state to a text representation.
     *
     * @return a string representing the actor's state
     */
    public abstract String toText();

    /**
     * Determines whether the actor should update based on its tick rate.
     *
     * @return {@code true} if the actor should update; {@code false} otherwise
     */
    public boolean shouldUpdate() {
        if (!isAlive) {
            return false;
        }
        tickCount++;
        if (tickCount >= tickRate) {
            tickCount = 0;
            return true;
        }
        return false;
    }

    /**
     * Updates the actor's state within the given grid.
     *
     * @param grid the grid in which the actor is located
     * @throws IOException    if an I/O error occurs
     * @throws ParseException if parsing data fails during the update
     */
    public abstract void update(Grid grid) throws IOException, ParseException;

    /**
     * Sets the actor's x-coordinate.
     *
     * @param x the new x-coordinate
     */
    public void setX(final int x) {
        this.x = x;
    }

    /**
     * Sets the actor's y-coordinate.
     *
     * @param y the new y-coordinate
     */
    public void setY(final int y) {
        this.y = y;
    }

    /**
     * Renders the actor on a graphics context.
     *
     * @param gc the graphics context for rendering
     */
    public void draw(final GraphicsContext gc) {
        gc.drawImage(getImage(), (x + 1) * Level.DRAW_ENTITY_SIZE,
                (y + 1) * Level.DRAW_ENTITY_SIZE,
                Level.DRAW_ENTITY_SIZE, Level.DRAW_ENTITY_SIZE);
    }

    /**
     * Gets the image representing the actor.
     *
     * @return the image of the actor
     */
    protected abstract Image getImage();

    /**
     * Determines if a player can walk on this actor's position.
     *
     * @param actor the actor attempting to walk on this position
     * @return {@code true} if player can walk on actor; {@code false} otherwise
     */
    public abstract boolean playerCanWalkOn(Actor actor);
}
