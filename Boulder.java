import javafx.scene.image.Image;
import javafx.util.Pair;

import java.util.Random;

/**
 * * The Boulder class represents a boulder actor in the game.
 * It extends the Actor class and provides specific
 * behavior for the boulder, such as falling, rolling, and being pushed.
 *
 * @author Oliver
 */
public class Boulder extends Actor {

    private static final int BOULDER_TICK_RATE = 3;
    private static final Image IMAGE = new Image("boulder.png");
    private boolean hasFallen = false;

    /**
     * Constructs a Boulder object with the given x and y coordinates.
     *
     * @param x The x coordinate of the boulder.
     * @param y The y coordinate of the boulder.
     */
    public Boulder(final int x, final int y) {
        super(BOULDER_TICK_RATE, x, y, ActorType.BOULDER);
    }

    /**
     * Handles interactions with another actor.
     *
     * @param interactor The actor interacting with this boulder.
     */
    @Override
    public void onInteract(final Actor interactor) {
    }

    /**
     * Parses the boulder's state from a textual representation.
     *
     * @param text The textual representation of the boulder.
     */
    @Override
    public void fromText(final String text) {
    }

    /**
     * Converts the boulder's state to a textual representation.
     *
     * @return A string representing the boulder in text format.
     */
    @Override
    public String toText() {
        return "B " + getX() + " " + getY();
    }

    /**
     * Updates the boulder's state in the game world. The boulder may fall,
     * roll, or interact with special tiles (e.g., a magic wall).
     *
     * @param grid The grid containing the game world.
     */
    @Override
    public void update(final Grid grid) {
        if (getY() + 1 < grid.getHeight()) {
            Tile mWall = grid.getTile(getX(), getY() + 1);
            if (mWall.getType() == TileType.MAGIC_WALL) {
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
     * Checks if the boulder can fall to the tile below.
     *
     * @param grid The grid containing the game world.
     * @return True if the boulder can fall, false otherwise.
     */
    private boolean canFall(final Grid grid) {
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
     * Causes the boulder to fall to the tile below, interacting with any
     * objects it lands on.
     * @param grid The grid containing the game world.
     */
    private void fall(final Grid grid) {
        if (getY() + 1 < grid.getHeight()) {
            Actor occupier = grid.getTile(getX(), getY() + 1).getOccupier();
            if (occupier == null) {
                grid.tryMove(this, getX(), getY() + 1);
                hasFallen = true;
            } else if (hasFallen
                    && (occupier.getType() != ActorType.DIAMOND
                    && occupier.getType() != ActorType.AMOEBA
                    && occupier.getType() != ActorType.BOULDER
                    && occupier.getType() != ActorType.FIREFLY
                    && occupier.getType() != ActorType.BUTTERFLY)) {
                grid.placeExplosion(new Pair<>(getX(), getY()));
                hasFallen = false;
            } else {
                hasFallen = false;
            }
            Actor newOccupier = grid.getTile(getX(), getY() + 1).getOccupier();
            if (newOccupier != null) {
                if (hasFallen && (newOccupier.getType() != ActorType.DIAMOND
                        && newOccupier.getType() != ActorType.AMOEBA
                        && newOccupier.getType() != ActorType.BOULDER
                        && newOccupier.getType() != ActorType.FIREFLY
                        && newOccupier.getType() != ActorType.BUTTERFLY)) {
                    grid.placeExplosion(new Pair<>(getX(), getY()));
                    hasFallen = false;
                }
            }
        } else {
            hasFallen = false;
        }

    }

    /**
     * Checks if the boulder can roll to the left.
     *
     * @param grid The grid containing the game world.
     * @return True if the boulder can roll left, false otherwise.
     */
    private boolean canRollLeft(final Grid grid) {
        return getX() - 1 >= 0 && getY() + 1 < grid.getHeight()
                && grid.getTile(getX() - 1, getY()).getType() == TileType.PATH
                &&  grid.getTile(getX() - 1, getY()).getOccupier() == null
                && grid.getTile(getX() - 1, getY() + 1).getType() == TileType.PATH
                && grid.getTile(getX() - 1, getY() + 1).getOccupier() == null
                && grid.getTile(getX(), getY() + 1).getType() != TileType.MAGIC_WALL;
    }

    /**
     * Rolls the boulder to the left.
     *
     * @param grid The grid containing the game world.
     */
    private void rollLeft(final Grid grid) {
        if (getX() - 1 >= 0 && getY() + 1 < grid.getHeight()) {
            grid.tryMove(this, getX() - 1, getY());
        }
    }

    /**
     * Checks if the boulder can roll to the right.
     *
     * @param grid The grid containing the game world.
     * @return True if the boulder can roll right, false otherwise.
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
     * Rolls the boulder to the right.
     *
     * @param grid The grid containing the game world.
     */
    private void rollRight(Grid grid) {
        if (getX() + 1 < grid.getWidth() && getY() + 1 < grid.getHeight()) {
            grid.tryMove(this, getX() + 1, getY());
        }
    }

    /**
     * Checks if the boulder can be pushed to the right.
     *
     * @param grid The grid containing the game world.
     * @return True if the boulder can be pushed right, false otherwise.
     */
    public boolean canBePushedRight(final Grid grid) {
        return getX() + 1 < grid.getWidth()
                && grid.getTile(getX() + 1, getY()).getType() == TileType.PATH
                && grid.getTile(getX() + 1, getY()).getOccupier() == null;
    }

    /**
     * Checks if the boulder can be pushed to the left.
     *
     * @param grid The grid containing the game world.
     * @return True if the boulder can be pushed left, false otherwise.
     */
    public boolean canBePushedLeft(final Grid grid) {
        return getX() - 1 >= 0
                && grid.getTile(getX() - 1, getY()).getType() == TileType.PATH
                && grid.getTile(getX() - 1, getY()).getOccupier() == null;
    }

    /**
     * Pushes the boulder to the right if possible.
     *
     * @param grid The grid containing the game world.
     */
    public void pushRight(final Grid grid) {
        if (canBePushedRight(grid)) {
            grid.tryMove(this, getX() + 1, getY());
        }
    }

    /**
     * Pushes the boulder to the left if possible.
     *
     * @param grid The grid containing the game world.
     */
    public void pushLeft(final Grid grid) {
        if (canBePushedLeft(grid)) {
            grid.tryMove(this, getX() - 1, getY());
        }
    }

    /**
     * Determines if the player can walk on the boulder's tile.
     *
     * @param actor The actor attempting to walk on the boulder.
     * @return False, as players cannot walk on boulders.
     */
    public boolean playerCanWalkOn(Actor actor) {
        return false;
    }

    /**
     * Retrieves the image representation of the boulder.
     *
     * @return The image of the boulder.
     */
    @Override
    public Image getImage() {
        return IMAGE;
    }
}
