import javafx.scene.image.Image;

import java.util.Optional;

/**
 * Represents a tile on a grid in the game world. A tile can be
 * occupied by an actor, have specific properties like being
 * destroyable, and may have interactions with actors.
 * This is an abstract class meant to be extended by specific types of tiles.
 *
 * @author Sam
 */
public abstract class Tile {
    private Actor occupier;
    private final int x, y;
    private final boolean destroyable;
    private TileType type;

    /**
     * Constructs a new Tile.
     *
     * @param destroyable whether the tile can be destroyed.
     * @param x the x-coordinate of the tile.
     * @param y the y-coordinate of the tile.
     * @param type the type of the tile.
     */
    public Tile(final boolean destroyable, final int x,
                final int y, final TileType type) {
        this.destroyable = destroyable;
        this.x = x;
        this.y = y;
        this.type = type;
    }

    /**
     * Gets the x-coordinate of this tile.
     *
     * @return the x-coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of this tile.
     *
     * @return the y-coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the image associated with this tile.
     *
     * @return the image representing the tile.
     */
    public abstract Image getImage();

    /**
     * Checks if a given actor can walk on this tile.
     *
     * @param actor the actor attempting to walk on this tile.
     * @return true if the actor can walk on this tile; false otherwise.
     */
    public abstract boolean actorCanWalkOn(Actor actor);


    /**
     * Checks if this tile is currently occupied by an actor.
     *
     * @return true if the tile has an occupier; false otherwise.
     */
    public boolean hasOccupier() {
        return occupier != null;
    }

    /**
     * Sets the actor occupying this tile.
     *
     * @param occupier the actor to set as the occupier of this tile.
     */
    public void setOccupier(final Actor occupier) {
        this.occupier = occupier;
    }

    /**
     * Gets the actor currently occupying this tile.
     *
     * @return the actor occupying this tile, or null if unoccupied.
     */
    Actor getOccupier() {
        return occupier;
    }

    /**
     * Checks if this tile is destroyable.
     *
     * @return true if the tile is destroyable; false otherwise.
     */
    public boolean isDestroyable() {
        return destroyable;
    }

    /**
     * Handles interactions between the tile and a given actor.
     *
     * @param actor the actor interacting with the tile.
     * @param grid the grid on which the tile resides.
     */
    public abstract void interactWith(Actor actor, Grid grid);

    /**
     * Indicates if the tile wants to change to another type,
     * such as when it is destroyed or transformed.
     * @return an Optional containing the new TileType if a change is desired,
     * or an empty Optional otherwise.
     */
    public Optional<TileType> wantsToChange() {
        return Optional.empty();
    }

    /**
     * Populates the tile's state from a text representation.
     *
     * @param text the text to parse and populate the tile's state.
     */
    public abstract void fromText(String text);

    /**
     * Generates a text representation of the tile's state.
     *
     * @return a string representation of the tile.
     */
    public abstract String toText();

    /**
     * Returns a string representation of the tile, including its type
     * and coordinates.
     * @return a string representation of this tile.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": x = " + x + ", y = " + y;
    }

    /**
     * Gets the type of this tile.
     *
     * @return the tile's type.
     */
    public TileType getType() {
        return type;
    }
}
