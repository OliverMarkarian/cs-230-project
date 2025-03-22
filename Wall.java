import javafx.scene.image.Image;

/**
 * Represents a wall tile in the game grid.
 * A wall is a stationary, non-walkable obstacle that blocks actor movement.
 *
 * @author Adrian
 */
public class Wall extends Tile {

    private static final Image IMAGE = new Image("neonWall.png");

    /**
     * Constructs a Wall tile at the specified grid coordinates.
     *
     * @param x the x-coordinate of the wall tile.
     * @param y the y-coordinate of the wall tile.
     */
    public Wall(final int x, final int y) {
        super(true, x, y, TileType.WALL);
    }

    /**
     * Returns the image associated with the wall tile.
     *
     * @return the {@link Image} object for the wall tile.
     */
    @Override
    public Image getImage() {
        return IMAGE;
    }

    /**
     * Determines if the specified actor can walk on this tile.
     * For wall tiles, actors cannot walk on them.
     *
     * @param actor the actor attempting to walk on the tile.
     * @return {@code false} as actors cannot walk on wall tiles.
     */
    @Override
    public boolean actorCanWalkOn(final Actor actor) {
        return false;
    }

    /**
     * Handles interaction between an actor and this wall tile.
     * Walls generally do not support any interactions.
     *
     * @param actor the actor interacting with the wall.
     * @param grid  the grid in which the interaction occurs.
     */
    @Override
    public void interactWith(final Actor actor, final Grid grid) {

    }

    /**
     * Parses a text representation of the wall tile.
     *
     * @param text the text representation of the tile.
     */
    @Override
    public void fromText(String text) {

    }

    /**
     * Returns a text representation of the wall tile.
     *
     * @return a string representing the wall tile, specifically "W".
     */
    @Override
    public String toText() {
        return "W";
    }
}
