import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import javafx.scene.canvas.GraphicsContext;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * The Grid class represents a 2D grid for managing tiles and actors in a game.
 * It supports actions like placing explosions, moving actors, pathfinding, and rendering the grid.
 *
 * @author Sam
 */
public class Grid {
    private final static int EXPLOSION_SIZE = 3; //UNIFORM SIZE nXn
    private final ArrayList<ArrayList<Tile>> tiles;
    private final int width;
    private final int height;
    private final Pathfinder pathfinder;
    private ArrayList<Actor> addQueue;
    private ArrayList<Actor> removeQueue;

    private static final int EXPLOSION_DISPLAYS_FOR_N_TICKS = 2;
    private Image small = new Image("smallboom.PNG");
    private Image medium = new Image("mediumboom.PNG");
    private Image big = new Image("bigboom.PNG");
    private int explosionTickCount;
    private ArrayList<Pair<Integer, Integer>> explosionCoords;
    private boolean levelShouldExit;

    /**
     * A constructor for the grid class. This is not intended to be called by anyone other than LevelFactory.
     *
     * @param tiles  The 2D ArrayList that makes up the grid.
     * @param width  The length of the rows.
     * @param height The length of the columns.
     * @see LevelFactory
     * @see Tile
     */
    public Grid(final ArrayList<ArrayList<Tile>> tiles, final int width, final int height) {
        this.width = width;
        this.height = height;
        this.tiles = tiles;
        pathfinder = new Pathfinder();
        addQueue = new ArrayList<>();
        explosionCoords = new ArrayList<>();
        removeQueue = new ArrayList<>();
        explosionTickCount = 0;
        levelShouldExit = false;
    }

    /**
     * A method to draw the grid and all of its tiles.
     *
     * @param gc The GraphicsContext of the Canvas the Grid should be drawn to.
     * @see javafx.scene.canvas.Canvas
     */
    public void draw(final GraphicsContext gc) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Rectangle rectangle = new Rectangle();
                ///   gc.setFill(tiles.get(y).get(x).getImage());
                //    gc.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
                gc.drawImage(tiles.get(y).get(x).getImage(), Level.DRAW_ENTITY_SIZE * (x+1), Level.DRAW_ENTITY_SIZE * (y+1), Level.DRAW_ENTITY_SIZE, Level.DRAW_ENTITY_SIZE);
            }
        }


        if (explosionTickCount++ < EXPLOSION_DISPLAYS_FOR_N_TICKS && !explosionCoords.isEmpty()) {
            explosionCoords.forEach(coords -> {
                gc.drawImage(small, (double) ((coords.getKey() + 1) * Level.DRAW_ENTITY_SIZE),
                        (double) ((coords.getValue() + 1) * Level.DRAW_ENTITY_SIZE), Level.DRAW_ENTITY_SIZE, Level.DRAW_ENTITY_SIZE);
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.seconds(0.1), event -> {
                            gc.drawImage(medium, (double) ((coords.getKey() + 1) * Level.DRAW_ENTITY_SIZE),
                                    (double) ((coords.getValue() + 1) * Level.DRAW_ENTITY_SIZE), Level.DRAW_ENTITY_SIZE, Level.DRAW_ENTITY_SIZE);
                        }),
                        new KeyFrame(Duration.seconds(0.1), event -> {
                            gc.drawImage(big, (double) ((coords.getKey() + 1) * Level.DRAW_ENTITY_SIZE),
                                    (double) ((coords.getValue() + 1) * Level.DRAW_ENTITY_SIZE), Level.DRAW_ENTITY_SIZE, Level.DRAW_ENTITY_SIZE);
                        })
                );
                timeline.setCycleCount(1);
                timeline.play();
            });
        }
    }

    /**
     * Retrieves the tile at the specified coordinates (x, y).
     *
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return The Tile at the specified coordinates.
     */
    public Tile getTile(int x, int y) {
        return tiles.get(y).get(x);
    }

    /**
     * Returns the height (number of rows) of the grid.
     *
     * @return The height of the grid.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Places an explosion of size 3x3 at the specified coordinates, affecting nearby tiles.
     * The explosion destroys any destructible tiles within its radius and kills any actors within the explosion range.
     *
     * @param center The coordinates of the explosion's center.
     */

    public void placeExplosion(final Pair<Integer, Integer> center) {
        ArrayList<Pair<Integer, Integer>> coords = getExplosionCoords(center);

        coords.stream()
                .filter(coord -> this.inBounds(coord.getKey(), coord.getValue()))
                .map(coord -> this.getTile(coord.getKey(), coord.getValue()))
                .filter(tile -> tile.getOccupier() != null)
                .map(Tile::getOccupier)
                .forEach(Actor::kill);

        coords.forEach(coord -> {
            if (this.inBounds(coord.getKey(), coord.getValue())) {
                this.changeTile(coord.getKey(), coord.getValue(), TileType.PATH);
            }
        });

        explosionCoords = coords;
        explosionTickCount = 0;
    }

    private ArrayList<Pair<Integer,Integer>> getExplosionCoords(final Pair<Integer, Integer> center) {
        final int x = center.getKey();
        final int y = center.getValue();

        final int begin = x - EXPLOSION_SIZE / 2;
        final int end = 1 + x + EXPLOSION_SIZE / 2;

        ArrayList<Pair<Integer, Integer>> coords = new ArrayList<>();
        for (int explosionY = y - 1; explosionY <= y + 1; explosionY++) {
            for (int explosionX = begin; explosionX < end; explosionX++) {
                if (getTile(explosionX, explosionY).isDestroyable()) {
                    coords.add(new Pair<>(explosionX, explosionY));
                }
            }
        }
        return coords;
    }

    /**
     * Returns the width (number of columns) of the grid.
     *
     * @return The width of the grid.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Checks if the specified coordinates (x, y) are within the grid's bounds.
     *
     * @param x The x-coordinate to check.
     * @param y The y-coordinate to check.
     * @return True if the coordinates are within bounds, otherwise false.
     */
    private boolean inBounds(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return true;
        }
        return false;
    }

    /**
     * Invokes interaction behaviour on the Actor who occupies a tile at (x, y).
     * If the tile is not occupied, this is a no-op.
     *
     * @param interactor The actor invoking the interaction.
     * @param x          The x-coordinate of the Tile the actor wants to interact with.
     * @param y          The y-coordinate of the Tile the actor wants to interact with.
     * @see Actor
     */
    public void interactWithOccupier(Actor interactor, int x, int y) {

        if (!inBounds(x, y)) {
            return;
        }
        if (tiles.get(y).get(x).getOccupier() == null) {
            return;
        }

        tiles.get(y).get(x).getOccupier().onInteract(interactor);
    }

    /**
     * Calculates the shortest path from one actor (A) to another actor (B) using the pathfinding algorithm.
     *
     * @param A The actor requesting the path.
     * @param B The target actor to reach.
     * @return A queue of coordinates representing the path from A to B.
     */
    Queue<Pair<Integer, Integer>> getPath(Actor A, Actor B) {
        return pathfinder.ShortestPath(A.getX(), A.getY(), B.getX(), B.getY(), this, A);
    }

    /**
     * A method to update the type of a tile.
     *
     * @param x    The x-coordinate of the tile.
     * @param y    The y-coordinate of the tile.
     * @param type The new type of the tile.
     */
    public void changeTile(int x, int y, TileType type) {
        if (!inBounds(x, y)) {
            throw new IndexOutOfBoundsException("Invalid bounds.");
        }

        Actor occupier = getTile(x, y).getOccupier();

        Tile tile = null;

        switch (type) {
            default -> {
                throw new IllegalArgumentException("Invalid type for now.");
            }
            case PATH -> tile = new Path(x, y);
        }

        tile.setOccupier(occupier);
        tiles.get(y).set(x, tile);
    }

    /**
     * The move method is used to determine if a given move is valid (e.g. enemies cannot walk on a Dirt tile), and if
     * so, executing that move. The Actor will have its position updated. It is important the boolean is checked.
     * Behaviour can occur after moving (e.g. Dirt to Path), and this method is responsible for that.
     *
     * @param actor The Actor who is requesting to be moved.
     * @param x     The new x-coordinate.
     * @param y     The new y-coordinate.
     * @return A boolean denoting if the move was executed.
     */
    public boolean tryMove(Actor actor, int x, int y) {
        if (!inBounds(x, y)) {
            return false;
        }
        // extract all actors except player itself from tiles player cannot walk on boulder but player can walk on diamond to pick it.
        if (!tiles.get(y).get(x).actorCanWalkOn(actor)) {
            return false;
        }
        Actor occupier = tiles.get(y).get(x).getOccupier();
        if (occupier != null && !occupier.playerCanWalkOn(actor)) {
            return false;
        }
        tiles.get(actor.getY()).get(actor.getX()).setOccupier(null); //free old tile

        tiles.get(y).get(x).setOccupier(actor);


        actor.setX(x);
        actor.setY(y);

        if (tiles.get(y).get(x).getType() == TileType.DIRT && actor.getType() == ActorType.PLAYER) {
            this.changeTile(x, y, TileType.PATH);
        }

        return true;
    }

    /**
     * A method to be called by an actor (by delegation through Grid) some time after they have received their path.
     * To be used to check if it is still valid (i.e. does the actor need to get a new path?).
     *
     * @return boolean stating whether the path is still valid.
     **/
    public boolean pathStillValid(final Queue<Pair<Integer, Integer>> queue, final Actor actor, final Actor target) {

        final ArrayList<Pair<Integer, Integer>> copy = new ArrayList<>(queue); //Copy as reference semantics mean we would invalidate the old queue.

        final Pair<Integer, Integer> targetTile = copy.removeLast();

        if (getTile(targetTile.getKey(), targetTile.getValue()).getOccupier() != target) {
            return false;
        }

        //Checks if there exist any occupied tiles
        return copy.stream()
                .map(tilePosition -> getTile(tilePosition.getKey(), tilePosition.getValue()))
                .filter(tile -> !tile.actorCanWalkOn(actor) || tile.hasOccupier())
                .toList()
                .isEmpty();
    }


    /**
     * A method to be called by tiles and actors to spawn actors in game.
     * It should be noted that the actor given will not be updated until the next tick that it should be updated on.
     *
     * @param x     The x-coordinate of the given tile.
     * @param y     The y-coordinate of the given tile.
     * @param actor The actor that the caller wishes to place on the given tile.
     */
    public void addActor(int x, int y, Actor actor) {
        if (!inBounds(x, y)) {
            throw new IndexOutOfBoundsException("Invalid coordinates.");
        }
        Tile tile = tiles.get(y).get(x);
        if (tile.hasOccupier()) {
            throw new IllegalArgumentException("Tile not empty.");
        }

        if (!tile.actorCanWalkOn(actor)) {
            throw new IllegalArgumentException("Tile cannot sustain actor.");
        }
        actor.setX(x);
        actor.setY(y);
        tile.setOccupier(actor);

        addQueue.add(actor);
    }

    /**
     * A method to remove tiles and diamond
     *
     * @param x the x-coordinate of the given tile
     * @param y the y-coordinate of the given tile
     */
    public void removeTile(int x, int y) {
        if (!inBounds(x, y)) {
            throw new IndexOutOfBoundsException("Invalid coordinates.");
        }
        Tile tile1 = new Path(x, y);
        ArrayList<Tile> arrayList = tiles.get(y);
        arrayList.set(x, tile1);
    }

    /**
     * A method to be called to remove an actor from a given tile. This method should not be called if the goal is to
     * kill the actor, instead call kill() on that actor. If there is no actor present, this method is a no-op.
     *
     * @param x The x-coordinate of the Actor.
     * @param y The y-coordinate of the Actor.
     * @see Actor
     * @see Tile
     */
    public void removeActor(int x, int y) {
        if (!inBounds(x, y)) {
            throw new IllegalArgumentException("skibidi toilet");
        }
        Tile tile = getTile(x, y);
        if (tile.hasOccupier()) {
            removeQueue.add(tile.getOccupier());
        }
        tile.setOccupier(null);
    }


    /**
     * This is a method for Level to call to add the Actors requested to be spawned to the game. It is incorrect to
     * call this if you are not Level.
     *
     * @return The ArrayList of actors to be added to the game.
     * @see Actor
     * @see Level
     */
    public ArrayList<Actor> pollNewActors() {
        ArrayList<Actor> out = new ArrayList<>(addQueue);
        addQueue.clear();

        return out;
    }

    /**
     * A method for Level to call to query all the actors that have been requested to be removed.
     * It is invalid behaviour to call this if you are not Level.
     *
     * @return The ArrayList of actors to be removed from the game.
     * @see Actor
     * @see Level
     */
    public ArrayList<Actor> pollRemovedActors() {
        ArrayList<Actor> out = new ArrayList<>(removeQueue);
        removeQueue.clear();

        return out;
    }

    /**
     * A method that saves the current state of the grid to text,
     *
     * @return A string containing the current state of the grid.
     */
    public String toText() {
        StringBuilder builder = new StringBuilder();

        builder.append(width).append(" ").append(height).append("\n");
        for (ArrayList<Tile> row : tiles) {
            for (Tile tile : row) {
                builder.append(tile.toText());
                builder.append(',');
            }
            builder.append('\n');
        }

        builder.deleteCharAt(builder.length() - 2); //extraneous comma removal.
        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    /**
     * A method to get all tiles adjacent (i.e. above, below, right, left), subject to boundary conditions.
     *
     * @param caller The Actor who is the subject of the query.
     * @return The list of tiles adjacent and in bounds.
     */
    public ArrayList<Tile> getAdjacentTiles(final Actor caller) {
        return getAdjacentTiles(caller.getX(), caller.getY());
    }

    /**
     * A method to get all tiles adjacent (i.e. above, below, right, left), subject to boundary conditions.
     *
     * @param tile The tile who is the subject of the query.
     * @return The list of tiles adjacent and in bounds.
     */
    public ArrayList<Tile> getAdjacentTiles(final Tile tile) {
        return getAdjacentTiles(tile.getX(), tile.getY());
    }

    /**
     * A method to get all tiles adjacent (i.e. above, below, right, left), subject to boundary conditions.
     *
     * @param x The x-coordinate of the subject tile.
     * @param y The y-coordinate of the subject tile.
     * @return The list of tiles adjacent and in bounds.
     */
    public ArrayList<Tile> getAdjacentTiles(final int x, final int y) {
        final ArrayList<Tile> adjacents = new ArrayList<>();

        if (inBounds(x + 1, y)) {
            adjacents.add(getTile(x + 1, y));
        }
        if (inBounds(x - 1, y)) {
            adjacents.add(getTile(x - 1, y));
        }
        if (inBounds(x, y + 1)) {
            adjacents.add(getTile(x, y + 1));
        }
        if (inBounds(x, y - 1)) {
            adjacents.add(getTile(x, y - 1));
        }
        return adjacents;
    }

    /**
     * Marks the level as complete by setting the flag to exit the level.
     */
    public void tryExit() {
        levelShouldExit = true;
    }

    /**
     * Checks if the level has been marked as complete.
     * This method returns true if the level exit flag has been set, indicating that the level is complete.
     *
     * @return True if the level should exit, otherwise false.
     */
    public boolean levelComplete() {
        return levelShouldExit;
    }
}
