import javafx.scene.image.Image;

/**
 * Represents a Dirt tile in the game.
 *
 * @author Sam
 * @author Ashley
 */
public class Dirt extends Tile {
    private boolean isPickedUp;
    private static final Image IMAGE = new Image("crateHL.jpg");

    /**
     * Constructs a new Dirt tile at the specified coordinates.
     *
     * @param x The x-coordinate of the Dirt tile.
     * @param y The y-coordinate of the Dirt tile.
     */
    public Dirt(int x, int y) {
        super(true, x, y, TileType.DIRT);
        this.isPickedUp = false;
    }

    /**
     * Returns the pickup state of the Dirt tile.
     *
     * @return {@code true} if the Dirt tile has been picked up,
     * {@code false} if not picked up.
     */
    public boolean isPickedUp() {
        return isPickedUp;

    }

    /**
     * Sets the pickup state of the Dirt tile.
     *
     * @param pickedUp {@code true} to mark the Dirt tile as picked up,
     * {@code false} to mark it as not picked up.
     */
    public void setPickedUp(final boolean pickedUp) {
        isPickedUp = pickedUp;

    }


    /**
     * Returns the image representing this Dirt tile.
     *
     * @return The image of the Dirt tile.
     */
    @Override
    public Image getImage() {
        return IMAGE;
    }

    /**
     * Determines whether the specified actor can walk on this Dirt tile.
     * Players and Amoebas are allowed to walk on it.
     * @param actor The actor attempting to walk on the tile.
     * @return {@code true} if the actor can walk on the Dirt tile,
     * {@code false} otherwise.
     */
    @Override
    public boolean actorCanWalkOn(final Actor actor) {
        return actor.getType() == ActorType.PLAYER
                || actor.getType() == ActorType.AMOEBA;
    }

    /**
     * Defines the behavior of the Dirt tile when interacted with by an actor.
     *
     * @param actor The actor interacting with the tile.
     * @param grid The grid where the tile exists.
     */
    @Override
    public void interactWith(Actor actor, Grid grid) {

    }

    /**
     * Converts the Dirt tile into its string representation.
     *
     * @param text The text to convert (not used in this method).
     */
    @Override
    public void fromText(String text) {

    }

    /**
     * Returns the text representation of the Dirt tile.
     *
     * @return A string representing the Dirt tile ("D").
     */
    @Override
    public String toText() {
        return "D";
    }
}
