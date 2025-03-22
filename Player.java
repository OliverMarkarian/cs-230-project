import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Represents the player character in the game, managing player interactions, movements,
 * and inventory such as keys and diamonds. The player can interact with various objects
 * in the game world, including tiles and other actors.
 *
 * @author Jess
 */
public class Player extends Actor {
    private static final Image PLAYER_ICON = new Image("NewLilGuy.png");
    private Grid grid;
    private KeyEvent event;
    private HashSet<Integer> collectedKeys; //keys for doors
    private int diamondCount;

    /**
     * Constructs a new Player instance with the specified position, grid reference, and collected keys.
     *
     * @param x            the initial x-coordinate of the player.
     * @param y            the initial y-coordinate of the player.
     * @param grid         the grid the player is located in.
     * @param collectedKeys the set of keys the player has collected.
     */
    public Player(int x, int y, Grid grid, HashSet<Integer> collectedKeys) {
        super(2, x, y, ActorType.PLAYER);
        this.grid = grid;
        this.collectedKeys = collectedKeys;
        event = null;
        diamondCount = 0;
    }

    /**
     * Returns the number of diamonds collected by the player.
     *
     * @return the diamond count.
     */
    public int getDiamondCount() {
        return diamondCount;
    }

    /**
     * Returns the set of keys collected by the player.
     *
     * @return a {@link HashSet} of key IDs.
     */
    public HashSet<Integer> getCollectedKeys() {
        return collectedKeys;
    }

    /**
     * Updates the player's collected keys.
     *
     * @param collectedKeys the new set of keys to assign to the player.
     */
    public void setCollectedKeys(HashSet<Integer> collectedKeys) {
        this.collectedKeys = collectedKeys;
    }

    /**
     * Defines the interaction when another actor interacts with the player.
     *
     * @param interactor the actor that interacts with the player.
     */
    @Override
    public void onInteract(Actor interactor) {
    }

    /**
     * Parses the player's state from a textual representation.
     *
     * @param text the text representation of the player's state.
     */
    @Override
    public void fromText(String text) {

    }

    /**
     * Converts the player's current state to a textual representation.
     *
     * @return the textual representation of the player's state.
     */
    @Override
    public String toText() {
        StringBuilder builder = new StringBuilder();
        collectedKeys.forEach(id -> builder.append(id).append(" "));
        return String.format("%d %d %d { %s}\n", getX(), getY(), diamondCount, builder.toString());
    }

    /**
     * Returns the image associated with the player.
     *
     * @return the {@link Image} representing the player.
     */
    @Override
    protected Image getImage() {
        return PLAYER_ICON;
    }

    /**
     * Checks whether the player can walk on another actor.
     *
     * @param actor the actor to check.
     * @return false, as the player cannot walk on another actor.
     */
    @Override
    public boolean playerCanWalkOn(Actor actor) {
        return false;
    }


    /**
     * Updates the player's position and state based on the current input event.
     *
     * @param grid the game grid where the player resides.
     * @throws IOException    if an I/O error occurs during the update.
     * @throws ParseException if a parsing error occurs during the update.
     */
    public void update(Grid grid) throws IOException, ParseException {
        if (event == null) {
            return;
        }

        //tryMove() updates your x and y if its successful (it's javadocced now).
        switch (event.getCode()) {
            case LEFT:
                handleLeftMove(grid);
                break;
            case RIGHT:
                handleRightMove(grid);
                break;
            case UP:
                checkActorInteraction(grid, getX(), getY() - 1);
                grid.tryMove(this, getX(), getY() - 1);
                break;
            case DOWN:
                checkActorInteraction(grid, getX(), getY() + 1);
                grid.tryMove(this, getX(), getY() + 1);
                break;
            default:
                return;
        }

        event = null; //once we've dealt with event, reset it
    }

    /**
     * Checks and handles interactions with actors or tiles at a specified location.
     *
     * @param grid the game grid.
     * @param newX the x-coordinate of the location to check.
     * @param newY the y-coordinate of the location to check.
     * @throws IOException    if an I/O error occurs.
     * @throws ParseException if a parsing error occurs.
     */
    private void checkActorInteraction(Grid grid, int newX, int newY) throws IOException, ParseException {
        //System.out.println("player position: " + newX + ", " + newY);
        if (newX >= 0 && newX < grid.getWidth() && newY >= 0 && newY < grid.getHeight()) {
            Tile tile = grid.getTile(newX, newY);
            Actor actor = tile.getOccupier();

            // Interact with the actor
            if (actor != null) {
                actor.onInteract(this);
            }

            // Pick up diamond
            if (actor != null && actor.getType() == ActorType.DIAMOND) {
                Diamond diamond = (Diamond) actor;
                grid.removeActor(newX, newY);
                diamondCount++;
            }

            // Pick up key
            if (tile.getType() == TileType.KEY) {
                Key key = (Key) tile;
                collectedKeys.add(key.getKeyID());
                grid.removeTile(newX, newY);
            }

            // Check for exit
            if (tile.getType() == TileType.EXIT) {
                if (diamondCount >= Level.LEVEL_DIAMOND_COUNT) {
                    grid.tryExit();
                }
            }

            // Check for door
            if (tile.getType() == TileType.DOOR) {
                assert tile instanceof Door;
                Door door = (Door) tile;
                if (collectedKeys.contains(door.getDoorID())) {
                    grid.removeTile(tile.getX(), tile.getY());
                }
            }
        }
    }

    /**
     * Handles the player's movement to the left.
     *
     * @param grid the game grid.
     * @throws IOException    if an I/O error occurs.
     * @throws ParseException if a parsing error occurs.
     */
    private void handleLeftMove(Grid grid) throws IOException, ParseException {
        if (getX() - 1 >= 0) {
            Actor leftActor = grid.getTile(getX() - 1, getY()).getOccupier();
            if (leftActor != null && leftActor.getType() == ActorType.BOULDER) {
                Boulder boulder = (Boulder) leftActor;
                if (boulder.canBePushedLeft(grid)) {
                    boulder.pushLeft(grid);
                }
            }
        }
        checkActorInteraction(grid, getX() - 1, getY());
        grid.tryMove(this, getX() - 1, getY());
    }

    /**
     * Handles the player's movement to the right.
     *
     * @param grid the game grid.
     * @throws IOException    if an I/O error occurs.
     * @throws ParseException if a parsing error occurs.
     */
    private void handleRightMove(Grid grid) throws IOException, ParseException {
        if (getX() + 1 < grid.getWidth()) {
            Actor rightActor = grid.getTile(getX() + 1, getY()).getOccupier();
            if (rightActor != null && rightActor.getType() == ActorType.BOULDER) {
                Boulder boulder = (Boulder) rightActor;
                if (boulder.canBePushedRight(grid)) {
                    boulder.pushRight(grid);
                }
            }
        }
        checkActorInteraction(grid, getX() + 1, getY());
        grid.tryMove(this, getX() + 1, getY());
    }

    /**
     * Captures and stores the player's input event for processing.
     *
     * @param event the {@link KeyEvent} to capture.
     */
    public void takeInput(final KeyEvent event) {
        this.event = event;
    }

    /**
     * Checks if the player is currently on a tile with a key and collects it.
     */
    public void lookForKeys() {
        if (grid.getTile(getX(), getY()).getType() == TileType.KEY && grid.getTile(getX(), getY()) instanceof Key) {
            Key key = (Key) grid.getTile(getX(), getY()); //Not a copy, a reference to original addressed as subclass
            collectedKeys.add(key.getKeyID());
        }   //still need to replace key with dirt in grid
    }

}
