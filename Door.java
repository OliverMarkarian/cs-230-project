import javafx.scene.image.Image;

/**
 * Represents a Door tile in the game.
 *
 * @author Yassine
 */
public class Door extends Tile {

    private static final Image RED_DOOR = new Image("DoorIsRed.png");
    private static final Image GREEN_DOOR = new Image("DoorIsGreen.png");
    private static final Image BLUE_DOOR = new Image("DoorIsBlue.png");
    private static final Image YELLOW_DOOR = new Image("DoorIsYellow.png");
    private final int doorID;
    private final KeyDoorColour colour;

    /**
     * Constructs a new Door at the specified position with the given
     * door ID and color.
     * @param x The x-coordinate of the Door tile.
     * @param y The y-coordinate of the Door tile.
     * @param doorID The unique ID associated with the Door.
     * @param colour The color of the Door (used to determine its image).
     */
    public Door(int x, int y, int doorID, KeyDoorColour colour) {
        super(true, x, y, TileType.DOOR);
        this.doorID = doorID;
        this.colour = colour;
    }

    /**
     * Gets the unique ID of this Door.
     *
     * @return The door's unique ID.
     */
    public int getDoorID() {
        return doorID;
    }

    /**
     * Returns the image representation of this Door based on its color.
     *
     * @return The image of the Door tile, based on its color (Green, Red, Blue, Yellow).
     */
    @Override
    public Image getImage() {
        return switch (colour) {
            case GREEN -> GREEN_DOOR;
            case RED -> RED_DOOR;
            case YELLOW -> YELLOW_DOOR;
            case BLUE -> BLUE_DOOR;
        };
    }

    /**
     * Determines whether the specified actor can walk on this Door.
     * Doors are not passable by actors.
     *
     * @param actor The actor attempting to walk on the Door tile.
     * @return {@code false}, as actors cannot walk on a Door tile.
     */
    @Override
    public boolean actorCanWalkOn(Actor actor) {
        return false;
    }

    /**
     * Defines the behavior when an actor interacts with this Door.
     *
     * @param actor The actor interacting with the Door.
     * @param grid The grid where the Door is located.
     */
    @Override
    public void interactWith(Actor actor, Grid grid) {

    }

    /**
     * Converts the Door tile into its string representation.
     * This string is used for storing or serializing the state of the Door.
     *
     * @param text The text to convert (not used in this case).
     */
    @Override
    public void fromText(String text) {

    }

    /**
     * Returns the text representation of the Door tile.
     *
     * @return A string representing the Door tile, including its door ID.
     */
    @Override
    public String toText() {
        return "Do" + " " + doorID + " " + switch (colour) {
            case GREEN -> "G";
            case RED -> "R";
            case YELLOW -> "Y";
            case BLUE -> "B";
        };
    }

}
