import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * The {@code Amoeba} class represents an amoeba actor in the game.
 * Amoebas grow, interact with their environment, and transform into other
 * entities (e.g., diamonds or boulders) based on game logic.
 *
 * @author Sam
 */
public class Amoeba extends Actor {
    /**
     * The image of the amoeba.
     */
    private static final Image IMG = new Image("amoeba.png");
    /**
     * The default tick rate for amoebas.
     */
    private static final int AMOEBA_DEFAULT_TICK_RATE = 10;
    /**
     * The tick rate for amoebas.
     */
    private static int tickRate = AMOEBA_DEFAULT_TICK_RATE;
    /**
     * The maximum group size for amoebas.
     */
    private static int maxGroupSize;
    /**
     * The number of ticks since the last update.
     */
    private int lastUpdateWasNTicksAgo = 0;

    /**
     * Adjusts the amoeba tick rate based on the provided rate multiplier.
     *
     * @param rate the multiplier to adjust the tick rate
     *             (higher values mean slower updates).
     */
    public static void setAmoebaRate(final float rate) {
        tickRate = (int) (AMOEBA_DEFAULT_TICK_RATE / rate);
    }

    /**
     * Sets the maximum group size for amoebas.
     *
     * @param size the maximum number of amoebas allowed in a group.
     */
    public static void setMaxGroupSize(final int size) {
        maxGroupSize = size;
    }

    /**
     * Constructs an {@code Amoeba} at the specified coordinates.
     *
     * @param x the x-coordinate of the amoeba
     * @param y the y-coordinate of the amoeba
     */
    public Amoeba(final int x, final int y) {
        super(1, x, y, ActorType.AMOEBA);
    }

    @Override
    public void onInteract(final Actor interactor) {
    }

    @Override
    public void fromText(final String text) {
    }

    /**
     * @return the serialized form of the amoeba.
     */
    @Override
    public String toText() {
        return String.format("A %d %d", getX(), getY());
    }

    /**
     * Updates the amoeba based on the game logic.
     * @param grid the grid in which the actor is located
     */
    @Override
    public void update(final Grid grid) {
        if (lastUpdateWasNTicksAgo++ != tickRate) {
            return;
        }

        getGroup(grid).stream()
                .map(Tile::getOccupier)
                .filter(occupier -> occupier instanceof Amoeba)
                .map(occupier -> (Amoeba) occupier)
                .forEach(Amoeba::markUpdatedThisTick);

        final HashSet<ActorType> enemies = new HashSet<>();
        enemies.add(ActorType.BUTTERFLY);
        enemies.add(ActorType.FROG);
        enemies.add(ActorType.FIREFLY);

        final ArrayList<Tile> adjacents = grid.getAdjacentTiles(this);

        adjacents.stream()
                .filter(Tile::hasOccupier)
                .filter(tile -> enemies.contains(tile.getOccupier().getType()))
                .forEach(tile -> {
                    tile.getOccupier().kill();
                    tile.setOccupier(null);
                });

        final ArrayList<Tile> possibleGrowthAreas = adjacents.stream()
                .filter(tile -> tile.getType() == TileType.DIRT
                        ||
                        (tile.getType() == TileType.PATH
                                && !tile.hasOccupier()))
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<Tile> group = getGroup(grid);

        if (group.size() == maxGroupSize) {
            turnToBoulders(group, grid);
            return;
        }

        final boolean inGroup = !adjacents.stream()
                .filter(Tile::hasOccupier)
                .map(Tile::getOccupier)
                .map(Actor::getType)
                .filter(type -> type == ActorType.AMOEBA)
                .toList().isEmpty();

        if (inGroup) {
            trySpread(group, grid);
        } else if (!possibleGrowthAreas.isEmpty()) {
            Random random = new Random();
            final int index = (possibleGrowthAreas.size() == 1)
                    ? 0 : random.nextInt(0, possibleGrowthAreas.size() - 1);
            Tile spawnAt = possibleGrowthAreas.get(index);

            grid.addActor(spawnAt.getX(), spawnAt.getY(),
                    new Amoeba(spawnAt.getX(), spawnAt.getY()));

            group.add(spawnAt);
        } else {
            turnToDiamonds(group, grid);
        }

        tellGroupUpdated(group);
    }

    private void markUpdatedThisTick() {
        lastUpdateWasNTicksAgo = 0;
    }

    private ArrayList<Tile> getGroup(final Grid grid) {
        HashSet<Tile> seen = new HashSet<>();
        Queue<Tile> tiles = new LinkedList<>();
        tiles.offer(grid.getTile(getX(), getY()));

        while (!tiles.isEmpty()) {
            final Tile front = tiles.poll();

            grid.getAdjacentTiles(front).stream()
                    .filter(tile -> !seen.contains(tile))
                    .filter(Tile::hasOccupier)
                    .filter(tile -> tile.getOccupier().getType()
                            == ActorType.AMOEBA)
                    .forEach(tiles::offer);

            seen.add(front);
        }

        return new ArrayList<>(seen);
    }

    private void trySpread(final ArrayList<Tile> amoeba, final Grid grid) {
        Queue<Tile> tiles = new LinkedList<>(amoeba);

        boolean hasSpread = false;

        while (!tiles.isEmpty() && !hasSpread) {
            final Tile front = tiles.poll();

            ArrayList<Tile> adjacents = grid.getAdjacentTiles(front);

            for (Tile adjacent : adjacents) {
                if (canSpreadTo(adjacent) && !hasSpread) {
                    performSpread(adjacent, amoeba, grid);
                    hasSpread = true;
                }
            }
        }

        if (!hasSpread) {
            turnToDiamonds(amoeba, grid);
        } else {
            tellGroupUpdated(amoeba);
        }
    }

    private boolean canSpreadTo(final Tile tile) {
        return !tile.hasOccupier()
                && (tile.getType() == TileType.PATH
                || tile.getType() == TileType.DIRT);
    }

    private void performSpread(final Tile tile, final ArrayList<Tile> amoeba,
                               final Grid grid) {
        grid.addActor(tile.getX(), tile.getY(),
                new Amoeba(tile.getX(), tile.getY()));
        amoeba.add(grid.getTile(tile.getX(), tile.getY()));
    }

    private void tellGroupUpdated(final ArrayList<Tile> amoeba) {
        amoeba.stream()
                .map(Tile::getOccupier)
                .filter(actor -> actor instanceof Amoeba)
                .map(actor -> (Amoeba) actor)
                .forEach(Amoeba::markUpdatedThisTick);
    }

    private void turnToDiamonds(final ArrayList<Tile> group, final Grid grid) {
        group.stream()
                .map(Tile::getOccupier)
                .filter(occupier -> occupier.getType() != ActorType.DIAMOND)
                .forEach(tile -> {
                    grid.removeActor(tile.getX(), tile.getY());
                    grid.removeTile(tile.getX(), tile.getY());
                    grid.addActor(tile.getX(), tile.getY(),
                            new Diamond(tile.getX(), tile.getY()));
                });
        tellGroupUpdated(group);
    }

    private void turnToBoulders(final ArrayList<Tile> group, final Grid grid) {
        group.stream()
                .map(Tile::getOccupier)
                .filter(occupier -> occupier.getType() != ActorType.BOULDER)
                .forEach(tile -> {
                    grid.removeActor(tile.getX(), tile.getY());
                    grid.removeTile(tile.getX(), tile.getY());
                    grid.addActor(tile.getX(), tile.getY(),
                            new Boulder(tile.getX(), tile.getY()));
                });
        tellGroupUpdated(group);
    }

    /**
     * @return the image of the amoeba.
     */
    @Override
    protected Image getImage() {
        return IMG;
    }

    /**
     * @param actor the actor attempting to walk on this position
     * @return whether the player can walk on this actor's position.
     */
    @Override
    public boolean playerCanWalkOn(final Actor actor) {
        return false;
    }
}
