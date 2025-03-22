import javafx.scene.image.Image;

/**
 * Represents a titanium wall tile in the game. Titanium walls are
 * indestructible and block movement for all actors. They serve
 * as permanent obstacles in the grid.
 * @author Yassine
 */
public class TitaniumWall extends Tile {


    private static final Image IMAGE = new Image("TitaniumWall.jpg");

    /**
     * Constructs a new TitaniumWall tile at the specified coordinates.
     *
     * @param x the x-coordinate of the tile on the grid.
     * @param y the y-coordinate of the tile on the grid.
     */
    public TitaniumWall(final int x, final int y) {
        super(false, x, y, TileType.TITANIUM_WALL);
    }

    /**
     * Retrieves the image associated with the titanium wall.
     *
     * @return the {@link Image} representing the titanium wall.
     */
    @Override
    public Image getImage() {

        return IMAGE;
    }

    /**
     * Determines if an actor can walk on the titanium wall.
     * Titanium walls block movement for all actors.
     *
     * @param actor the actor attempting to walk on the tile.
     * @return {@code false}, as titanium walls are not walkable.
     */
    @Override
    public boolean actorCanWalkOn(final Actor actor) {
        return false;
    }

    /**
     * Handles interactions with an actor.
     * Titanium walls do not allow any interaction.
     *
     * @param actor the actor interacting with the tile.
     * @param grid the grid containing this tile.
     */
    @Override
    public void interactWith(final Actor actor, final Grid grid) {

    }

    /**
     * A method to populate a titanium wall tile's properties from
     * its textual representation.
     *
     * @param text the text data used to initialize this tile.
     */
    @Override
    public void fromText(final String text) {
    }

    /**
     * Provides a text representation of the titanium wall tile.
     *
     * @return the string "T", representing a titanium wall.
     */
    @Override
    public String toText() {
        return "T";
    }
}
