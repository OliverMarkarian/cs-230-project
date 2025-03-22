import javafx.scene.image.Image;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;

/**
 * Represents a frog actor in the game. The frog moves towards the player and interacts with the player if adjacent.
 * It uses pathfinding to move towards the player, or moves randomly if no path is available.
 *
 * @author Sam
 */
public class Frog extends Actor {
    private static final Image IMAGE = new Image("headCrabHL.png");
    private Actor target; //Player
    private static final int FROG_RATE = 4;

    private Queue<Pair<Integer, Integer>> path;

    /**
     * Constructs a Frog with the specified target (the player) and initial position (x, y).
     *
     * @param target the actor that the frog is targeting, typically the player
     * @param x the initial x-coordinate of the frog
     * @param y the initial y-coordinate of the frog
     */
    public Frog(Actor target, int x, int y) {
        super(FROG_RATE, x, y, ActorType.FROG);
        this.target = target;
    }

    /**
     * Defines the interaction behavior when another actor interacts
     * with the frog.
     *
     * @param interactor the actor interacting with the frog
     */
    @Override
    public void onInteract(Actor interactor) {
    }

    /**
     * Parses the frog's state from a string representation.
     *
     * @param text the string representation of the frog's state
     */
    @Override
    public void fromText(String text) {

    }

    /**
     * Converts the frog's current state to a string representation.
     *
     * @return a string representing the frog's state in the format "F x y"
     */
    @Override
    public String toText() {
        StringBuilder out = new StringBuilder();
        return out.append("F ").append(getX()).append(" ").append(getY()).toString();
    }

    /**
     * Updates the frog's state each game tick. The frog moves towards the player if a path exists.
     * If no path is available, the frog moves randomly to an adjacent tile.
     *
     * @param grid the game grid on which the frog operates
     */
    @Override
    public void update(Grid grid) {

        if (!target.isAlive()) {
            return;
        }

        final boolean adjacentToPlayer = grid.getAdjacentTiles(this)
                .stream().anyMatch(t -> t.getOccupier() == target);

        if (adjacentToPlayer) {
            target.kill();
            path = null;
            return;
        }


        if (path == null || path.isEmpty() || !grid.pathStillValid(path, this, target)) {
            path = grid.getPath(this, target);
        }


        final boolean noPath = path == null || path.isEmpty();

        if (noPath) {
            ArrayList<Tile> adj = grid.getAdjacentTiles(this); //move randomly, if possible
            if (!adj.isEmpty()) {
                Random r = new Random();
                final int idx = r.nextInt(0, adj.size() - 1);
                grid.tryMove(this, adj.get(idx).getX(), adj.get(idx).getY());
            }
        } else {
            Pair<Integer, Integer> newPos = path.poll();
            grid.tryMove(this, newPos.getKey(), newPos.getValue());
        }


    }

    /**
     * Returns the image representation of the frog.
     *
     * @return the Image object representing the frog
     */
    @Override
    protected Image getImage() {
        return IMAGE;
    }

    /**
     * Determines whether the player can walk on the current tile occupied by the frog.
     * The frog is not walkable by the player.
     *
     * @param actor the actor that is checking if they can walk on the frog's tile
     * @return false, as the player cannot walk on the frog's tile
     */
    @Override
    public boolean playerCanWalkOn(Actor actor) {
        return false;
    }
}
