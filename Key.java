import javafx.scene.image.Image;

/**
 * Represents a Key tile in the game, which can be picked up by the player or other actors.
 * Inherits from the Tile class and provides functionality specific to a Key item, such as tracking whether it
 * has been picked up and identifying the Key by its unique keyID.
 *
 * @author Yassine
 */
public class Key extends Tile {
    private boolean isPickedUp;
    private static final Image RED_KEY = new Image("KeyRed.png");
    private static final Image GREEN_KEY = new Image("KeyGreen.png");
    private static final Image BLUE_KEY = new Image("KeyBlue.png");
    private static final Image YELLOW_KEY = new Image("KeyYellow.png");
    private final int keyID;
    private final KeyDoorColour colour;


    /**
     * Constructs a Key object at a specified (x, y) coordinate with a unique keyID.
     *
     * @param x The x-coordinate of the Key on the grid.
     * @param y The y-coordinate of the Key on the grid.
     * @param keyID The unique identifier for this Key.
     */
    public Key(int x, int y, int keyID, KeyDoorColour colour) {
        super(true, x, y, TileType.KEY);
        this.keyID = keyID;
        this.colour = colour;
    }

    /**
     * Retrieves the unique key ID of this Key.
     *
     * @return The key ID of the Key.
     */
    public int getKeyID() {
        return keyID;

    }

    /**
     * Checks if the Key has been picked up.
     *
     * @return true if the Key has been picked up, false otherwise.
     */
    public boolean isPickedUp() {
        return isPickedUp;

    }

    /**
     * Sets the picked-up status of the Key.
     *
     * @param pickedUp true if the Key has been picked up, false otherwise.
     */
    public void setPickedUp(boolean pickedUp) {
        isPickedUp = pickedUp;

    }

    /**
     * Returns the image representing the Key tile.
     *
     * @return The image associated with the Key.
     */
    @Override
    public Image getImage() {
        return switch (colour) {
            case GREEN -> GREEN_KEY;
            case RED -> RED_KEY;
            case YELLOW -> YELLOW_KEY;
            case BLUE -> BLUE_KEY;
        };
    }

    /**
     * Determines whether an actor can walk on this Key tile. For a Key, the answer is always true.
     *
     * @param actor The actor attempting to walk on the Key tile.
     * @return true, since actors can walk on Key tiles.
     */
    @Override
    public boolean actorCanWalkOn(Actor actor) {
        return true;
    }

    /**
     * Defines the interaction behavior when an actor interacts with this Key.
     *
     * @param actor The actor interacting with the Key.
     * @param grid The grid where the Key is located.
     */
    @Override
    public void interactWith(Actor actor, Grid grid) {

    }

    /**
     * Parses the given text to reconstruct a Key tile. This method does nothing in the current implementation.
     *
     * @param text The text representation of the Key tile.
     */
    @Override
    public void fromText(String text) {

    }

    /**
     * Converts the Key tile to a text representation.
     *
     * @return A string representing the Key tile in the format "K <keyID>".
     */
    @Override
    public String toText() {
        return "K" + " " + getKeyID() + " " + switch (colour) {
            case GREEN -> "G";
            case RED -> "R";
            case YELLOW -> "Y";
            case BLUE -> "B";
        };
    }

}
