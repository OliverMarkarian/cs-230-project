import javafx.scene.image.Image;
import javafx.util.Pair;

/**
 * Represents a Butterfly actor in the game that moves in a predefined pattern,
 * interacts with the player, and explodes upon death.
 *
 * @author Adrian
 */
public class Firefly extends Actor {
    private final boolean isLeft;
    private static final Image IMAGE = new Image("fireflie.png");
    private Direction direction = Direction.SOUTH;
    private boolean begginingDirectionSet = false;
    private final Actor player;
    private Grid grid;
    private static final int TICK_RATE = 5;
    private static final int LOOP_MOD_VALUE = 4;
    private static final int CASE4_VAL = 3;
    private static final int CASE3_VAL = 2;
    private static final int CASE2_VAL = 1;


    private boolean cycleLeft = false;
    private boolean cycleRight = false;
    private boolean movingObjectPresent = false;

    private final int[][] previousPos = {
            {0, 0},
            {0, 0},
            {0, 0},
            {0, 0},
    };

    private final int[][][] cyclePatternLeft = {
            {{0, 0}, {-1, 0}, {-1, 1}, {0, 1}},
            {{0, 0}, {0, 1}, {1, 1}, {1, 0}},
            {{0, 0}, {1, 0}, {1, -1}, {0, -1}},
            {{0, 0}, {0, -1}, {-1, -1}, {-1, 0}},
    };

    private final int[][][] cyclePatternRight = {
            {{0, 0}, {0, 1}, {-1, 1}, {-1, 0}},
            {{0, 0}, {-1, 0}, {-1, -1}, {0, -1}},
            {{0, 0}, {0, -1}, {1, -1}, {1, 0}},
            {{0, 0}, {1, 0}, {1, 1}, {0, 1}},
    };

    private int posPointer = 0;

    private final Direction[][] lTDirLeft = {
            {Direction.NORTH, Direction.WEST,
                    Direction.NORTH, Direction.EAST, Direction.SOUTH},
            {Direction.SOUTH, Direction.EAST,
                    Direction.SOUTH, Direction.WEST, Direction.NORTH},
            {Direction.EAST, Direction.NORTH,
                    Direction.EAST, Direction.SOUTH, Direction.WEST},
            {Direction.WEST, Direction.SOUTH,
                    Direction.WEST, Direction.NORTH, Direction.EAST},
    };


    private final int[][][] lTCoordsLeft = {
            {{0, -1}, {-1, 0}, {0, -1}, {1, 0}, {0, 1}},
            {{0, 1}, {1, 0}, {0, 1}, {-1, 0}, {0, -1}},
            {{1, 0}, {0, -1}, {1, 0}, {0, 1}, {-1, 0}},
            {{-1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 0}},
    };


    private final Direction[][] lTDirRight = {
            {Direction.NORTH, Direction.EAST,
                    Direction.NORTH, Direction.WEST, Direction.SOUTH},
            {Direction.SOUTH, Direction.WEST,
                    Direction.SOUTH, Direction.EAST, Direction.NORTH},
            {Direction.EAST, Direction.SOUTH,
                    Direction.EAST, Direction.NORTH, Direction.WEST},
            {Direction.WEST, Direction.NORTH,
                    Direction.WEST, Direction.SOUTH, Direction.EAST},
    };

    private final int[][][] lTCoordsRight = {
            {{0, -1}, {1, 0}, {0, -1}, {-1, 0}, {0, 1}},
            {{0, 1}, {-1, 0}, {0, 1}, {1, 0}, {0, -1}},
            {{1, 0}, {0, 1}, {1, 0}, {0, -1}, {-1, 0}},
            {{-1, 0}, {0, -1}, {-1, 0}, {0, 1}, {1, 0}},
    };

    /**
     * Constructs a Butterfly object with its initial properties.
     *
     * @param aIsLeft  Whether the butterfly starts...
     *                ...with a left-hand movement pattern.
     * @param x       The initial x-coordinate of the butterfly.
     * @param y       The initial y-coordinate of the butterfly.
     * @param target  The player actor that the butterfly interacts with.
     */
    Firefly(final boolean aIsLeft, final int x,
              final int y, final Actor target) {
        super(TICK_RATE, x, y, ActorType.FIREFLY);
        this.isLeft = aIsLeft;
        player = target;
    }

    /**
     * Defines how the butterfly interacts with another actor.
     *
     * @param interactor The actor interacting with the butterfly.
     */
    @Override
    public void onInteract(final Actor interactor) {

    }

    /**
     * Loads butterfly properties from a text representation.
     *
     * @param text The string representing the butterfly's state.
     */
    @Override
    public void fromText(final String text) {

    }

    /**
     * Serializes the butterfly's state into a text representation.
     *
     * @return A string representing the butterfly's state.
     */
    @Override
    public String toText() {
        return String.format("Bu %d %d %s",
                getX(), getY(), (isLeft) ? "L" : "R");
    }


    /**
     * Updates the butterfly's state and behavior each game tick.
     *
     * @param aGrid The grid containing all game tiles and actors.
     */
    @Override
    public void update(final Grid aGrid) {
        grid = aGrid;
        //this sets the initial face of the butterfly
        if (!begginingDirectionSet) {
            if (isLeft) {
                setInitIalDirectionLeft(grid);
            } else {
                setInitIalDirectionRight(grid);
            }
            begginingDirectionSet = true;
        }


        boolean adjacentToPlayer = grid.getAdjacentTiles(this)
                .stream().anyMatch(t -> t.getOccupier() == player);


        if (adjacentToPlayer) {
            player.kill();
            grid.removeActor(player.getX(), player.getY());
            this.kill();
            grid.removeActor(this.getX(), this.getY());
        }


        if (detectBoulderDiamond(grid)) {
            grid.placeExplosion(new Pair<>(this.getX(), this.getY()));
            grid.getTile(getX(), getY() - 1).getOccupier().kill();
            grid.removeActor(getX(), getY() - 1);
            this.kill();
            grid.removeActor(this.getX(), this.getY());
            explode(grid);
        }

        if (isLeft) {
            moveLeft(grid);
        } else {
            moveRight(grid);
        }
    }

    private boolean detectBoulderDiamond(final Grid aGrid) {
        Actor occupier = aGrid.getTile(getX(), getY() - 1).getOccupier();
        if (occupier == null) {
            return false;
        } else {
            return occupier.getType() == ActorType.BOULDER
                    || occupier.getType() == ActorType.DIAMOND;
        }
    }


    /**
     * Sets the initial direction for the butterfly with a left-hand pattern.
     *
     * @param aGrid The grid containing all game tiles and actors.
     */
    private void setInitIalDirectionLeft(final Grid aGrid) {
        Tile leftB = aGrid.getTile(getX() - 1, getY());
        Tile rightB = aGrid.getTile(getX() + 1, getY());
        Tile upB = aGrid.getTile(getX(), getY() - 1);
        Tile downB = aGrid.getTile(getX(), getY() + 1);

        boolean upBl = !allowedTile(upB);
        boolean downBl = !allowedTile(downB);
        boolean rightBl = !allowedTile(rightB);
        boolean leftBl = !allowedTile(leftB);

        if (!upBl) {
            direction = Direction.EAST;
        } else if (!rightBl) {
            direction = Direction.SOUTH;
        } else if (!downBl) {
            direction = Direction.WEST;
        } else if (!leftBl) {
            direction = Direction.NORTH;
        } else {
            direction = Direction.NORTH;
        }

    }

    /**
     * Sets the initial direction for the butterfly with a right-hand pattern.
     *
     * @param aGrid The grid containing all game tiles and actors.
     */
    private void setInitIalDirectionRight(final Grid aGrid) {
        Tile leftB = aGrid.getTile(getX() - 1, getY());
        Tile rightB = aGrid.getTile(getX() + 1, getY());
        Tile upB = aGrid.getTile(getX(), getY() - 1);
        Tile downB = aGrid.getTile(getX(), getY() + 1);


        //if the down block is one of the allowed types
        if (allowedTile(downB)) {
            direction = Direction.EAST;
        } else if (allowedTile(leftB)) {
            direction = Direction.SOUTH;
        } else if (allowedTile(upB)) {
            direction = Direction.WEST;
        } else if (allowedTile(rightB)) {
            direction = Direction.NORTH;
        }
    }

    /**
     * Checks whether the given tile is an...
     * ...allowed tile for the butterfly to interact with.
     *
     * @param tile The tile to check.
     * @return True if the tile is allowed, otherwise false.
     */
    private boolean allowedTile(final Tile tile) {

        boolean allowed;
        if (tile.getOccupier() != null) {
            switch (tile.getOccupier().getType()) {
                case BUTTERFLY, FIREFLY, FROG -> {
                    movingObjectPresent = true;
                    System.out.println("Moving object detected");
                    allowed = true;
                }
                case DIAMOND, BOULDER -> allowed = true;
                default -> allowed = false;
            }
        } else {
            switch (tile.getType()) {
                case WALL, DIRT, MAGIC_WALL, EXIT,
                     TITANIUM_WALL, DOOR, KEY -> allowed = true;
                default -> allowed = false;
            }
        }
        return allowed;
    }

    /**
     * Checks whether the given tile can explode.
     *
     * @param tile The tile to check.
     * @return True if the tile is explodable, otherwise false.
     */
    private boolean allowedTileExplode(final Tile tile) {
        boolean allowed;
        if (tile.getOccupier() != null) {
            switch (tile.getOccupier().getType()) {
                case BOULDER, BUTTERFLY, FIREFLY,
                     AMOEBA, DIAMOND, FROG -> allowed = true;
                default -> allowed = false;
            }
        } else {
            switch (tile.getType()) {
                case WALL, DIRT, MAGIC_WALL, DOOR, KEY, PATH -> {
                    allowed = true;
                }
                default -> allowed = false;
            }
        }
        return allowed;
    }

    /**
     * Determines if the butterfly's movement...
     * ...pattern matches a right-handed cycle.
     */
    private void ifCycleRight() {
        boolean case1 = false;
        boolean case2 = false;
        boolean case3 = false;
        boolean case4 = false;


        for (int i = 0; i < previousPos.length; i++) {
            if (previousPos[((posPointer) % LOOP_MOD_VALUE
                    + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][0]
                    == previousPos[((posPointer) % LOOP_MOD_VALUE
                    + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][0]
                    + cyclePatternRight[i][0][0]
                    && previousPos[((posPointer) % LOOP_MOD_VALUE
                    + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][1]
                    == previousPos[((posPointer) % LOOP_MOD_VALUE
                    + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][1]
                    +  cyclePatternRight[i][0][1]) {
                case1 = true;
            }
            if (previousPos[((posPointer - CASE2_VAL)
                    % LOOP_MOD_VALUE + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][0]
                    == previousPos[((posPointer) % LOOP_MOD_VALUE
                    + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][0]
                    + cyclePatternRight[i][CASE4_VAL][0]
                    && previousPos[((posPointer - CASE2_VAL)
                    % LOOP_MOD_VALUE + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][1]
                    == previousPos[((posPointer) % LOOP_MOD_VALUE
                    + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][1]
                    + cyclePatternRight[i][CASE4_VAL][1]) {
                case2 = true;
            }
            if (previousPos[((posPointer - CASE3_VAL)
                    % LOOP_MOD_VALUE + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][0]
                    == previousPos[((posPointer) % LOOP_MOD_VALUE
                    + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][0]
                    + cyclePatternRight[i][CASE3_VAL][0]
                    && previousPos[((posPointer - CASE3_VAL) % LOOP_MOD_VALUE
                    + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][1]
                    == previousPos[((posPointer)
                    % LOOP_MOD_VALUE + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][1]
                    + cyclePatternRight[i][CASE3_VAL][1]) {
                case3 = true;
            }
            if (previousPos[((posPointer - CASE4_VAL)
                    % LOOP_MOD_VALUE + LOOP_MOD_VALUE)
                    % LOOP_MOD_VALUE][0] == previousPos[((posPointer)
                    % LOOP_MOD_VALUE + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][0]
                    + cyclePatternRight[i][CASE2_VAL][0]
                    && previousPos[((posPointer - CASE4_VAL)
                    % LOOP_MOD_VALUE + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][1]
                    == previousPos[((posPointer) % LOOP_MOD_VALUE
                    + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][1]
                    + cyclePatternRight[i][CASE2_VAL][1]) {
                case4 = true;
            }
        }

        cycleRight = case1 && case2 && case3 && case4;
    }

    /**
     * Determines if the butterfly's movement...
     * ...pattern matches a left-handed cycle.
     */
    private void ifCycleLeft() {
        boolean case1 = false;
        boolean case2 = false;
        boolean case3 = false;
        boolean case4 = false;


        for (int i = 0; i < previousPos.length; i++) {
            if (previousPos[((posPointer) % LOOP_MOD_VALUE
                    + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][0]
                    == previousPos[((posPointer) % LOOP_MOD_VALUE
                    + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][0]
                    + cyclePatternLeft[i][0][0]
                    && previousPos[((posPointer) % LOOP_MOD_VALUE
                    + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][1]
                    == previousPos[((posPointer) % LOOP_MOD_VALUE
                    + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][1]
                    +  cyclePatternLeft[i][0][1]) {
                case1 = true;
            }
            if (previousPos[((posPointer - CASE2_VAL)
                    % LOOP_MOD_VALUE + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][0]
                    == previousPos[((posPointer) % LOOP_MOD_VALUE
                    + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][0]
                    + cyclePatternLeft[i][CASE4_VAL][0]
                    && previousPos[((posPointer - CASE2_VAL)
                    % LOOP_MOD_VALUE + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][1]
                    == previousPos[((posPointer) % LOOP_MOD_VALUE
                    + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][1]
                    + cyclePatternLeft[i][CASE4_VAL][1]) {
                case2 = true;
            }
            if (previousPos[((posPointer - CASE3_VAL)
                    % LOOP_MOD_VALUE + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][0]
                    == previousPos[((posPointer) % LOOP_MOD_VALUE
                    + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][0]
                    + cyclePatternLeft[i][CASE3_VAL][0]
                    && previousPos[((posPointer - CASE3_VAL) % LOOP_MOD_VALUE
                    + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][1]
                    == previousPos[((posPointer)
                    % LOOP_MOD_VALUE + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][1]
                    + cyclePatternLeft[i][CASE3_VAL][1]) {
                case3 = true;
            }
            if (previousPos[((posPointer - CASE4_VAL)
                    % LOOP_MOD_VALUE + LOOP_MOD_VALUE)
                    % LOOP_MOD_VALUE][0] == previousPos[((posPointer)
                    % LOOP_MOD_VALUE + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][0]
                    + cyclePatternLeft[i][CASE2_VAL][0]
                    && previousPos[((posPointer - CASE4_VAL)
                    % LOOP_MOD_VALUE + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][1]
                    == previousPos[((posPointer) % LOOP_MOD_VALUE
                    + LOOP_MOD_VALUE) % LOOP_MOD_VALUE][1]
                    + cyclePatternLeft[i][CASE2_VAL][1]) {
                case4 = true;
            }
        }

        cycleLeft = case1 && case2 && case3 && case4;
    }
    /**
     * Moves the butterfly according to its left-hand movement pattern.
     *
     * @param aGrid The grid containing all game tiles and actors.
     */
    private void moveLeft(final Grid aGrid) {
        cycleLeft = false;

        previousPos[posPointer][0] = getX();
        previousPos[posPointer][1] = getY();


        Tile upB = aGrid.getTile(getX(), getY() - 1);
        Tile downB = aGrid.getTile(getX(), getY() + 1);
        Tile rightB = aGrid.getTile(getX() + 1, getY());
        Tile leftB = aGrid.getTile(getX() - 1, getY());


        boolean northBl = !allowedTile(upB);
        boolean southBl = !allowedTile(downB);
        boolean eastBl = !allowedTile(rightB);
        boolean westBl = !allowedTile(leftB);


        boolean[][] truthDirLeft = {
                {northBl, westBl, northBl, eastBl, southBl},
                {southBl, eastBl, southBl, westBl, northBl},
                {eastBl, northBl, eastBl, southBl, westBl},
                {westBl, southBl, westBl, northBl, eastBl},
        };

        int row = 0;
        while (!lTDirLeft[row][0].equals(direction)) {
            row++;
        }

        int col = 1;

        ifCycleLeft();
        if (cycleLeft && movingObjectPresent) {
            col = 2;
            movingObjectPresent = false;
        }

        boolean fini = false;
        while (!fini && col <= LOOP_MOD_VALUE) {
            if (truthDirLeft[row][col]) {
                fini = true;
                grid.tryMove(this, this.getX()
                        + lTCoordsLeft[row][col][0], this.getY()
                        + lTCoordsLeft[row][col][1]);
                this.direction = lTDirLeft[row][col];
            }
            col++;
        }

        posPointer++;
        posPointer %= previousPos.length;
    }


    /**
     * Moves the butterfly according to its right-hand movement pattern.
     *
     * @param aGrid The grid containing all game tiles and actors.
     */
    private void moveRight(final Grid aGrid) {
        cycleRight = false;
        cycleLeft = false;

        previousPos[posPointer][0] = getX();
        previousPos[posPointer][1] = getY();

        Tile leftB = aGrid.getTile(getX() - 1, getY());
        Tile rightB = aGrid.getTile(getX() + 1, getY());
        Tile upB = aGrid.getTile(getX(), getY() - 1);
        Tile downB = aGrid.getTile(getX(), getY() + 1);


        boolean leftBl = !allowedTile(leftB);
        boolean rightBl = !allowedTile(rightB);
        boolean upBl = !allowedTile(upB);
        boolean downBl = !allowedTile(downB);

        boolean[][] truthDirRight = {
                {upBl, rightBl, upBl, leftBl, downBl},
                {downBl, leftBl, downBl, rightBl, upBl},
                {rightBl, downBl, rightBl, upBl, leftBl},
                {leftBl, upBl, leftBl, downBl, rightBl},
        };


        int row = 0;
        while (!lTDirRight[row][0].equals(direction)) {
            row++;
        }

        int col = 1;

        ifCycleRight();

        if (cycleRight && movingObjectPresent) {
            col = 2;
            movingObjectPresent = false;
        }


        boolean fini = false;
        while (!fini && col <= LOOP_MOD_VALUE) {
            if (truthDirRight[row][col]) {
                fini = true;
                grid.tryMove(this, this.getX() + lTCoordsRight[row][col][0],
                        this.getY() + lTCoordsRight[row][col][1]);
                this.direction = lTDirRight[row][col];
            }
            col++;
        }


        posPointer++;
        posPointer %= previousPos.length;
    }
    /**
     * Triggers the butterfly's explosion,...
     * ...converting nearby tiles and spawning diamonds.
     *
     * @param aGrid The grid containing all game tiles and actors.
     */
    public void explode(final Grid aGrid) {
        for (int aX = getX() - 1; aX <= getX() + 1; aX++) {
            for (int aY = getY() - 1; aY <= getY() + 1; aY++) {
                if (aGrid.getTile(aX, aY).getOccupier() == null
                        && allowedTileExplode(aGrid.getTile(aX, aY))) {
                    aGrid.changeTile(aX, aY, TileType.PATH);
                    Boulder newBoulder = new Boulder(aX, aY);
                    aGrid.addActor(aX, aY, newBoulder);
                }
            }
        }
    }


    /**
     * Retrieves the image representation of the butterfly.
     *
     * @return The butterfly's image.
     */
    @Override
    protected Image getImage() {
        return IMAGE;
    }
    /**
     * Determines if the player can walk on the butterfly.
     *
     * @param actor The player actor attempting to walk on the butterfly.
     * @return Always false, as the player cannot walk on a butterfly.
     */
    @Override
    public boolean playerCanWalkOn(final Actor actor) {
        return false;
    }

}
