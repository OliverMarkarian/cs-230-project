import java.util.Random;

/**
 * Handles the placement of various items within a grid-based system.
 * Items are distributed randomly according to specified rules and constraints.
 *
 * @author Adrian
 */
public class CaveItemPlacer {

    private final int row;
    private final int col;

    private final Random rand = new Random();

    //cave identification grid
    private float[][] field;
    private final float[][] caveMap;
    private int caveCount;

    //block threshold used for determining what a
    //block is based on its value in field[][]
    private final float stoneThreshold;
    private final float dirtThreshold;
    private final float floorThreshold;

    //IDs of all the objects to place so far
    private static final int PLAYER_ID = 2;
    private static final int BOULDER_ID = 3;
    private static final int DIAMOND_ID = 4;
    private static final int FROG_ID = 5;
    private static final int BUTTERFLY_ID = 6;
    private static final int AMOEBA_ID = 7;
    private static final int MAGIC_WALL_ID = 8;
    private static final int FIREFLY_ID = 9;
    private static final int EXIT_ID = 10;
    private static final int KEY_GENERATOR_START = 100;
    private static int keyIDGenerator = KEY_GENERATOR_START;
    private static final float DOOR_CALC_VAL = 0.5f;

    /**
     * Constructs a CaveItemPlacer instance with...
     * ...the specified grid dimensions, thresholds, and field.
     *
     * @param aField the 2D array representing the field
     * @param aRow the number of rows in the field
     * @param aCol the number of columns in the field
     * @param aStoneThreshold the threshold value for identifying stone blocks
     * @param aDirtThreshold the threshold value for identifying dirt blocks
     * @param aFloorThreshold the threshold value for identifying floor blocks
     */
    public CaveItemPlacer(final float[][] aField, final int aRow,
                          final int aCol, final float aStoneThreshold,
                          final float aDirtThreshold,
                          final float aFloorThreshold) {
        field = aField;
        row = aRow;
        col = aCol;
        caveMap = new float[aCol][aRow];
        stoneThreshold = aStoneThreshold;
        dirtThreshold = aDirtThreshold;
        floorThreshold = aFloorThreshold;
    }

    /**
     * Processes the field to place various items randomly and add a border.
     *
     * @param playerCount number of players to place
     * @param boulderCount number of boulders to place
     * @param diamondCount number of diamonds to place
     * @param frogCount number of frogs to place
     * @param butterflyCount number of butterflies to place
     * @param amoebaCount number of amoebas to place
     * @param magicWallCount number of magic walls to place
     * @param keyAndDoorCount number of key-door pairs to place
     * @param aFireflyCount number of fireflies to place
     * @param aExitCount number of exits to place
     * @return the processed field with items placed
     */
    public float[][] mapProcess(final int playerCount, final int boulderCount,
                                final int diamondCount, final int frogCount,
                                final int butterflyCount, final int amoebaCount,
                                final int magicWallCount,
                                final int keyAndDoorCount,
                                final int aFireflyCount,
                                final int aExitCount) {

        identifyCaves();
        field = sprinkleItems(playerCount, boulderCount, diamondCount,
                frogCount, butterflyCount, amoebaCount, magicWallCount,
                keyAndDoorCount, aFireflyCount, aExitCount);

        addTitaniumBorder();

        return field;
    }

    /**
     * Identifies all caves in the field by performing a flood-fill operation.
     */
    private void identifyCaves() {
        for (int i = 0; i < col; i++) {
            for (int j = 0; j < row; j++) {

                //if it's not stone (therefore a cave) AND the map where its...
                // ...going to be stored hasn't seen this cell before
                //then add the cells group into the caveMap
                if (field[i][j] >= stoneThreshold && caveMap[i][j] == 0) {
                    //updates amount of caves
                    caveCount++;
                    //flood fill
                    addToGroup(i, j, caveCount);
                }
            }
        }
    }

    /**
     * Performs a flood-fill operation to group cells into caves.
     *
     * @param y the current row index
     * @param x the current column index
     * @param caveID the ID of the cave being formed
     */
    private void addToGroup(final int y, final int x, final int caveID) {

        //if the cell is being checked is stone
        //OR its already seen this cell before
        if (field[y][x] < stoneThreshold || caveMap[y][x] != 0) {
            return;
        }

        caveMap[y][x] = caveID;
        addToGroup(y + 1, x, caveID);
        addToGroup(y - 1, x, caveID);
        addToGroup(y, x + 1, caveID);
        addToGroup(y, x - 1, caveID);
    }

    /**
     * Places items of various types in the field based on specified counts.
     *
     * @param playerCount number of players to place
     * @param boulderCount number of boulders to place
     * @param diamondCount number of diamonds to place
     * @param frogCount number of frogs to place
     * @param butterflyCount number of butterflies to place
     * @param amoebaCount number of amoebas to place
     * @param magicWallCount number of magic walls to place
     * @param keyAndDoorCount number of key-door pairs to place
     * @param fireflyCount number of fireflies to place
     * @param exitCount number of exits to place
     * @return the updated field
     */
    private float[][] sprinkleItems(final int playerCount,
                                    final int boulderCount,
                                    final int diamondCount, final int frogCount,
                                    final int butterflyCount,
                                    final int amoebaCount,
                                    final int magicWallCount,
                                    final int keyAndDoorCount,
                                    final int fireflyCount,
                                    final int exitCount) {


        //tries to place the amount of items
        //...specified using their individual IDs
        placePlayer(playerCount);

        placeBoulderDiamond(BOULDER_ID, boulderCount);
        placeBoulderDiamond(DIAMOND_ID, diamondCount);

        placeItem(FROG_ID, frogCount);
        placeFly(BUTTERFLY_ID, butterflyCount);

        placeItem(AMOEBA_ID, amoebaCount);
        placeItem(MAGIC_WALL_ID, magicWallCount);
        placeKeyAndDoor(keyAndDoorCount);
        placeFly(FIREFLY_ID, fireflyCount);
        placeItem(EXIT_ID, exitCount);

        return field;
    }

    /**
     * Places a specified number of objects...
     * ...of a given type randomly in the field.
     *
     * @param objectID the ID of the object to place
     * @param objectAmount the number of objects to place
     */
    private void placeBoulderDiamond(final int objectID,
                                     final int objectAmount) {
        for (int i = 0; i < objectAmount; i++) {
            boolean keepTrying = true;

            while (keepTrying) {
                //random x and y position to try place an object at
                int x = rand.nextInt(row);
                int y = rand.nextInt(col);

                //IF the x and y we're trying to place the object...
                // at is an open space...AND this x and y is part...
                // of a cave that its seen before
                //AND it follows the rules specified in canPlaceItem(x,y)
                //Then set object in the random x and y position
                if (field[y][x] >= dirtThreshold
                        && field[y][x] < floorThreshold
                        && caveMap[y][x] >= 0
                        && canPlaceBoulderDiamond(y, x)) {
                    field[y][x] = objectID;
                    keepTrying = false;

                }
            }
        }
    }

    /**
     * Places a specified number of objects of...
     * ...a given type randomly in the field.
     *
     * @param objectAmount the number of objects to place
     */
    private void placePlayer(final int objectAmount) {
        for (int i = 0; i < objectAmount; i++) {
            boolean keepTrying = true;

            while (keepTrying) {
                //random x and y position to try place an object at
                int x = rand.nextInt(row);
                int y = rand.nextInt(col);

                //IF the x and y we're trying to place the...
                // object at is an open space AND this x and y...
                //...is part of a cave that its seen before
                //AND it follows the rules specified in canPlaceItem(x,y)
                //Then set object in the random x and y position
                if (field[y][x] >= dirtThreshold
                        && field[y][x] < floorThreshold
                        && caveMap[y][x] >= 0
                        && canPlacePlayer(y, x)) {
                    field[y][x] = PLAYER_ID;
                    keepTrying = false;

                }
            }
        }
    }

    /**
     * Determines if a flying object can be...
     * ...placed at a given position without blocking paths.
     *
     * @param y the row index
     * @param x the column index
     * @return true if the flying object can be placed, false otherwise
     */
    private boolean canPlaceBoulderDiamond(final int y, final int x) {
        //if x and y are out of bounds return false
        if (x < 0 || x >= row || y < 0 || y >= col) {
            return false;
        }
        //if for some reason x and y are a stone block return false
        if (caveMap[y][x] < dirtThreshold) {
            return false;
        }

        int numOpNeighbours = 0;

        if (y > 0 && field[y - 1][x] <= dirtThreshold
                && field[y - 1][x] > 0) {
            numOpNeighbours++;
        }
        if (y < col - 1 && field[y + 1][x] <= dirtThreshold
                && field[y + 1][x] > 0) {
            numOpNeighbours++;
        }
        if (x > 0 && field[y][x - 1] <= dirtThreshold
                && field[y][x - 1] > 0) {
            numOpNeighbours++;
        }
        if (x < row - 1 && field[y][x + 1] <= dirtThreshold
                && field[y][x + 1] > 0) {
            numOpNeighbours++;
        }

        boolean playerBeneath = false;
        for (int j = y; j <= col - 1; j++) {
            System.out.println(j);
            if (field[j][x] == 2) {
                playerBeneath = true;
            }
        }

        //if it has available neighbours return true
        return numOpNeighbours > 0 && !playerBeneath;
    }

    /**
     * Determines if a flying object can...
     * ...be placed at a given position without blocking paths.
     *
     * @param y the row index
     * @param x the column index
     * @return true if the flying object can be placed, false otherwise
     */
    private boolean canPlacePlayer(final int y, final int x) {

        //if x and y are out of bounds return false
        if (x < 0 || x >= row || y < 0 || y >= col) {
            return false;
        }
        //if for some reason x and y are a stone block return false
        if (caveMap[y][x] < dirtThreshold) {
            return false;
        }

        //keeps track of how many blocks around the fly are walls
        int numOpNeighbours = 0;


        if (y > 0 && field[y - 1][x] <= dirtThreshold
                && field[y - 1][x] > 0) {
            numOpNeighbours++;
        }
        if (y < col - 1 && field[y + 1][x] <= dirtThreshold
                && field[y + 1][x] > 0) {
            numOpNeighbours++;
        }
        if (x > 0 && field[y][x - 1] <= dirtThreshold
                && field[y][x - 1] > 0) {
            numOpNeighbours++;
        }
        if (x < row - 1 && field[y][x + 1] <= dirtThreshold
                && field[y][x + 1] > 0) {
            numOpNeighbours++;
        }

        //if it has available neighbours return true
        return numOpNeighbours > 0;
    }

    /**
     * Places a specified number of objects...
     * ...of a given type randomly in the field.
     *
     * @param objectID the ID of the object to place
     * @param objectAmount the number of objects to place
     */
    private void placeItem(final int objectID, final int objectAmount) {
        for (int i = 0; i < objectAmount; i++) {
            boolean keepTrying = true;

            while (keepTrying) {
                //random x and y position to try place an object at
                int x = rand.nextInt(row);
                int y = rand.nextInt(col);

                //IF the x and y we're trying to place...
                //...the object at is an open space...
                //...AND this x and y is part of a cave that its seen before
                //AND it follows the rules specified in canPlaceItem(x,y)
                //Then set object in the random x and y position
                if (field[y][x] >= dirtThreshold
                        && field[y][x] < floorThreshold
                        && caveMap[y][x] >= 0
                        && canPlaceItem(y, x)) {
                    field[y][x] = objectID;
                    keepTrying = false;

                }
            }
        }
    }

    /**
     * Places a specified number of flying...
     * ...objects of a given type randomly in the field.
     *
     * @param objectID the ID of the object to place
     * @param objectAmount the number of objects to place
     */
    private void placeFly(final int objectID, final int objectAmount) {
        for (int i = 0; i < objectAmount; i++) {
            boolean keepTrying = true;

            while (keepTrying) {
                //random x and y position to try place an object at
                int x = rand.nextInt(row);
                int y = rand.nextInt(col);

                //IF the x and y we're trying to...
                //...place the object at is an open space...
                //...AND this x and y is part of a cave that its seen before
                //AND it follows the rules specified in canPlaceItem(x,y)
                //Then set object in the random x and y position
                if (field[y][x] >= dirtThreshold
                        && field[y][x] < floorThreshold
                        && caveMap[y][x] >= 0 && canPlaceFly(y, x)) {
                    field[y][x] = objectID;
                    keepTrying = false;

                }
            }
        }
    }


    /**
     * Determines if a flying object can be...
     * ...placed at a given position without blocking paths.
     *
     * @param y the row index
     * @param x the column index
     * @return true if the flying object can be placed, false otherwise
     */
    private boolean canPlaceFly(final int y, final int x) {

        //if x and y are out of bounds return false
        if (x < 0 || x >= row
                || y < 0
                || y >= col) {
            return false;
        }
        //if for some reason x and y are a stone block return false
        if (caveMap[y][x] < dirtThreshold) {
            return false;
        }

        //keeps track of how many blocks around the fly are walls
        int numOpNeighbours = 0;


        if (y > 0 && field[y - 1][x] <= dirtThreshold
                && field[y - 1][x] > 0) {
            numOpNeighbours++;
        }
        if (y < col - 1 && field[y + 1][x] <= dirtThreshold
                && field[y + 1][x] > 0) {
            numOpNeighbours++;
        }
        if (x > 0 && field[y][x - 1] <= dirtThreshold
                && field[y][x - 1] > 0) {
            numOpNeighbours++;
        }
        if (x < row - 1 && field[y][x + 1] <= dirtThreshold
                && field[y][x + 1] > 0) {
            numOpNeighbours++;
        }

        //if it has available neighbours return true
        return numOpNeighbours > 0;
    }

    /**
     * Places a specified number of key-door pairs randomly in the field.
     *
     * @param keyAndDoorAmount the number of key-door pairs to place
     */
    private void placeKeyAndDoor(final int keyAndDoorAmount) {
        for (int i = 0; i < keyAndDoorAmount; i++) {

            boolean keepTrying = true;

            while (keepTrying) {
                int x = rand.nextInt(row);
                int y = rand.nextInt(col);
                int z = rand.nextInt(row);
                int w = rand.nextInt(col);

                //IF the x and y we're trying to place...
                //...the object at is an open space...
                //...AND this x and y is part of a cave that its seen before
                //AND it follows the rules specified in canPlaceItem(x,y)
                //Then set object in the random x and y position
                //in this case it is applied for both the key and the door
                if (field[y][x] >= dirtThreshold
                        && field[y][x] < floorThreshold
                        && caveMap[y][x] >= 0
                        && canPlaceItem(y, x)
                        && field[w][z] >= dirtThreshold
                        && field[w][z] < floorThreshold
                        && caveMap[w][z] >= 0
                        && canPlaceItem(w, z)) {
                    field[y][x] = keyIDGenerator; //key
                    System.out.println("key " + field[y][x]);
                    field[w][z] = keyIDGenerator + DOOR_CALC_VAL; //door
                    System.out.println("door " + field[w][z]);

                    keyIDGenerator++;
                    keepTrying = false;

                }
            }
        }
    }


    /**
     * Determines if an item can be placed...
     * ...at a given position without blocking paths.
     *
     * @param y the row index
     * @param x the column index
     * @return true if the item can be placed, false otherwise
     */
    private boolean canPlaceItem(final int y, final int x) {

        //if x and y are out of bounds return false
        if (x < 0 || x >= row || y < 0 || y >= col) {
            return false;
        }
        //if for some reason x and y are a stone block return false
        if (caveMap[y][x] < dirtThreshold) {
            return false;
        }

        //keeps track of how many of its blocks around the object are available
        int numOpNeighbours = 0;

        //checking if the object would block a tunnel...
        //...making the game impossible to complete
        //this is part of some of the ruleset
        if (y > 0 && field[y - 1][x] >= dirtThreshold
                && field[y - 1][x] < floorThreshold) {
            numOpNeighbours++;
        }
        if (y < col - 1 && field[y + 1][x] >= dirtThreshold
                && field[y + 1][x] < floorThreshold) {
            numOpNeighbours++;
        }
        if (x > 0 && field[y][x - 1] >= dirtThreshold
                && field[y][x - 1] < floorThreshold) {
            numOpNeighbours++;
        }
        if (x < row - 1 && field[y][x + 1] >= dirtThreshold
                && field[y][x + 1] < floorThreshold) {
            numOpNeighbours++;
        }

        //if it has available neighbours return true
        return numOpNeighbours > 2;
    }

    /**
     * Adds a border of stone blocks around the entire field.
     *
     */
    private void addTitaniumBorder() {
        //adding a stone border around the map
        for (int i = 0; i < col; i++) {
            field[i][0] = 1;
            field[i][row - 1] = 1;
        }
        for (int j = 0; j < row; j++) {
            field[0][j] = 1;
            field[col - 1][j] = 1;
        }
    }
}
