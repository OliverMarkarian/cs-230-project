import javafx.scene.image.Image;

/**
 * Represents a Path tile in a grid-based game. Inherits from the {@link Tile} class.
 * This class provides functionality specific to Path tiles, such as image representation
 * and the ability to interact with actors that walk on it.
 *
 * @author Sam
 */
public class Path extends Tile {

    private static final Image IMAGE = new Image("path.png");

    /**
     * Constructs a new Path tile at the specified coordinates.
     *
     * @param x The x-coordinate of the Path tile in the grid.
     * @param y The y-coordinate of the Path tile in the grid.
     */
    public Path(int x, int y) {
        super(true, x, y, TileType.PATH);
    }

    /**
     * Returns the image associated with this Path tile.
     *
     * @return The image for the Path tile.
     */
    @Override
    public Image getImage() {
        return IMAGE;
    }

    /**
     * Determines whether the given actor can walk on this Path tile.
     * In this case, all actors are allowed to walk on a Path tile.
     *
     * @param actor The actor to check.
     * @return {@code true}, as actors can always walk on a Path tile.
     */
    @Override
    public boolean actorCanWalkOn(Actor actor) {
        return true;
    }

    /**
     * Defines the interaction between the given actor and this Path tile.
     *
     * @param actor The actor interacting with the tile.
     * @param grid The grid in which the actor and tile exist.
     */
    @Override
    public void interactWith(Actor actor, Grid grid) {

    }

    /**
     * Populates the tile from a textual representation.
     *
     * @param text The text representation of the tile.
     */
    @Override
    public void fromText(String text) {

    }

    /**
     * Converts this Path tile into its textual representation.
     *
     * @return The character representing the Path tile ("P").
     */
    @Override
    public String toText() {
        return "P";
    }
}
