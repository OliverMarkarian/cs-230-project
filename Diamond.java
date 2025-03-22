import javafx.scene.image.Image;
import javafx.util.Pair;

import java.util.Random;

/**
 * Represents a diamond actor in the game, which can fall, roll, and interact with other game elements.
 * Extends the Actor class.
 *
 * @author Oliver
 */
public class Diamond extends Actor {


    private static final Image IMAGE = new Image("diamond.png");
    private boolean hasFallen = false;

    /**
     * Constructs a Diamond object with specified initial position.
     *
     * @param x the x-coordinate of the diamond.
     * @param y the y-coordinate of the diamond.
     */
    public Diamond(int x, int y) {
        super(3, x, y, ActorType.DIAMOND);
    }

    /**
     * Defines the interaction behavior when another actor interacts with this diamond.
     *
     * @param interactor the actor interacting with the diamond.
     */
    @Override
    public void onInteract(Actor interactor) {
    }

    /**
     * Parses the diamond's state from a string representation.
     *
     * @param text the string representation of the diamond.
     */
    @Override
    public void fromText(String text) {
    }

    /**
     * Converts the diamond's state to a string representation.
     *
     * @return the string representation of the diamond.
     */
    @Override
    public String toText() {
        return "Di " + getX() + " " + getY();
    }

    /**
     * Updates the state of the diamond based on its surroundings and possible actions.
     *
     * @param grid the current game grid.
     */
    @Override
    public void update(Grid grid) {
        if (getY() + 1 < grid.getHeight()){
            Tile mWall = grid.getTile(getX(), getY() + 1);
            if (grid.getTile(getX(), getY() + 1).getType() == TileType.MAGIC_WALL) {
                mWall.interactWith(this, grid);
            }
        }
        Random random = new Random();
        if (canFall(grid)) {
            fall(grid);
        } else if (random.nextBoolean()) {
            if (canRollRight(grid)) {
                rollRight(grid);
            } else if (canRollLeft(grid)) {
                rollLeft(grid);
            }
        } else {
            if (canRollLeft(grid)) {
                rollLeft(grid);
            } else if (canRollRight(grid)) {
                rollRight(grid);
            }
        }
    }

    /**
     * Checks if the diamond can fall in the current grid configuration.
     *
     * @param grid the current game grid.
     * @return true if the diamond can fall, false otherwise.
     */
    private boolean canFall(Grid grid) {
        if (getY() + 1 < grid.getHeight()
                && grid.getTile(getX(), getY() + 1).getType() == TileType.PATH
                && grid.getTile(getX(), getY() + 1).getOccupier() == null) {
            return true;
        } else {
            hasFallen = false;
            return false;
        }
    }

    /**
     * Handles the diamond falling to a new position in the grid.
     *
     * @param grid the current game grid.
     */
    private void fall(Grid grid) {
        if (getY() + 1 < grid.getHeight()) {
            Actor occupier = grid.getTile(getX(), getY() + 1).getOccupier();
            if (occupier == null) {
                grid.tryMove(this, getX(), getY() + 1);
                hasFallen = true;
            } else if (hasFallen && (occupier.getType() != ActorType.BOULDER && occupier.getType() != ActorType.AMOEBA && occupier.getType() != ActorType.DIAMOND)) { //Only explode if it hits an enemy.
                grid.placeExplosion(new Pair<>(getX(), getY()));
                hasFallen = false;
            } else {
                hasFallen = false;
            }
            Actor newOccupier = grid.getTile(getX(), getY() + 1).getOccupier();
            if (newOccupier != null) {
                if (hasFallen && (newOccupier.getType() != ActorType.BOULDER && newOccupier.getType() != ActorType.AMOEBA && newOccupier.getType() != ActorType.DIAMOND)) { //Only explode if it hits an enemy.
                    grid.placeExplosion(new Pair<>(getX(), getY()));
                    hasFallen = false;
                }
            }
        } else {
            hasFallen = false;
        }
    }

    /**
     * Checks if the diamond can roll to the left in the current grid configuration.
     *
     * @param grid the current game grid.
     * @return true if the diamond can roll left, false otherwise.
     */
    private boolean canRollLeft(Grid grid) {
        return getX() - 1 >= 0 && getY() + 1 < grid.getHeight()
                && grid.getTile(getX() - 1, getY()).getType() == TileType.PATH
                && grid.getTile(getX() - 1, getY()).getOccupier() == null
                && grid.getTile(getX() - 1, getY() + 1).getType() == TileType.PATH
                && grid.getTile(getX() - 1, getY() + 1).getOccupier() == null &&
                grid.getTile(getX(), getY() + 1).getType() != TileType.MAGIC_WALL;
    }

    /**
     * Handles the diamond rolling to the left in the grid.
     *
     * @param grid the current game grid.
     */
    private void rollLeft(Grid grid) {
        if (getX() - 1 >= 0 && getY() + 1 < grid.getHeight()) {
            grid.tryMove(this, getX() - 1, getY());
        }
    }

    /**
     * Checks if the diamond can roll to the right in the current grid configuration.
     *
     * @param grid the current game grid.
     * @return true if the diamond can roll right, false otherwise.
     */
    private boolean canRollRight(Grid grid) {
        return getX() + 1 < grid.getWidth() && getY() + 1 < grid.getHeight()
                && grid.getTile(getX() + 1, getY()).getType() == TileType.PATH
                && grid.getTile(getX() + 1, getY()).getOccupier() == null
                && grid.getTile(getX() + 1, getY() + 1).getType() == TileType.PATH
                && grid.getTile(getX() + 1, getY() + 1).getOccupier() == null
                && grid.getTile(getX(), getY() + 1).getType() != TileType.MAGIC_WALL;
    }

    /**
     * Handles the diamond rolling to the right in the grid.
     *
     * @param grid the current game grid.
     */
    private void rollRight(Grid grid) {
        if (getX() + 1 < grid.getWidth() && getY() + 1 < grid.getHeight()) {
            grid.tryMove(this, getX() + 1, getY());
        }
    }

    /**
     * Returns the image associated with the diamond.
     *
     * @return the diamond's image.
     */
    @Override
    public Image getImage() {
        return IMAGE;
    }

    /**
     * Determines if a player can walk on this diamond.
     *
     * @param actor the actor attempting to walk on the diamond.
     * @return true if the player can walk on the diamond, false otherwise.
     */
    @Override
    public boolean playerCanWalkOn(Actor actor) {
        return true;
    }
}