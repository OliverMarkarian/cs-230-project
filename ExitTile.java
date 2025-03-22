import javafx.scene.image.Image;

/**
 * Represents an exit tile in the game.
 *
 * @author Sam
 */
public class ExitTile extends Tile {

    private static Image image = new Image("ExitNether.jpg");
    private int diamondThreshold;

    /**
     * Constructs an ExitTile with the specified diamond threshold and
     * coordinates.
     * @param diamondThreshold the number of diamonds needed to exit the level
     * @param x the x-coordinate of the exit tile
     * @param y the y-coordinate of the exit tile
     */
    public ExitTile(int diamondThreshold, int x, int y) {
        super(true, x, y, TileType.EXIT);
        this.diamondThreshold = diamondThreshold;
    }

    /**
     * Returns the image associated with this ExitTile.
     *
     * @return the Image representing the exit tile
     */
    @Override
    public Image getImage() {
        return image;
    }

    /**
     * Determines if an actor can walk on this ExitTile.
     * Since exit tiles are meant for interaction and not for walking on, this always returns false.
     *
     * @param actor the actor attempting to walk on the tile
     * @return false since actors cannot walk on the exit tile
     */
    @Override
    public boolean actorCanWalkOn(Actor actor) {
        return false;
    }

    /**
     * Handles the interaction between an actor and this ExitTile.
     *
     * @param actor the actor interacting with the tile
     * @param grid the game grid in which the interaction occurs
     */
    @Override
    public void interactWith(Actor actor, Grid grid) {

    }

    /**
     * This method would deserialize a tile from a text representation.
     *
     * @param text the text representation of the tile
     */
    @Override
    public void fromText(String text) {

    }

    /**
     * Returns the text representation of the ExitTile.
     * The text representation is a single character "E".
     *
     * @return the string "E", representing the ExitTile
     */
    @Override
    public String toText() {
        return "E" + " " + diamondThreshold;
    }

}
