import java.util.ArrayList;
import java.util.List;

/**
 * Generates a map for the game which fits the required specifications.
 *
 * @author Adrian
 */
public class MapGenerator {
    //Width and Height
    private static int columns;
    private static int rows;

    //main grid
    private float[][] field;

    //cave identification grid
    private final float[][] caveMap;
    private int caveCount;
    private final List<int[]> caveOrigin;

    //block thresholds
    private static final float STONE_THRESHOLD = 0.5f;
    private static final float DIRT_THRESHOLD = 0.6f;
    private static final float FLOOR_THRESHOLD = 1f;
    private static final float KEY_DOOR_THRESHOLD = 100f;
    private static final float DOOR_CALC_VAL = 0.5f;
    private static final float KEY_CALC_VAL = 0f;

    //amount of [object] to spawn
    private static final int PLAYER_COUNT = 1;
    private static final int BOULDER_COUNT = 40;
    private static final int DIAMOND_COUNT = 40;
    private static final int FROG_COUNT = 6;
    private static final int BUTTERFLY_COUNT = 12;
    private static final int AMOEBA_COUNT = 5;
    private static final int MAGIC_WALL_COUNT = 8;
    private static final int KEY_AND_DOOR_COUNT = 1;
    private static final int FIREFLY_COUNT = 12;
    private static final int EXIT_COUNT = 1;
    private static final String[] DOOR_COLOUR = {"R"};
    private static final int COLOUR_INDEX = 0;


    //IDs of all the objects to place so far
    private static final int TITANIUM_WALL = 1;
    private static final int PLAYER_ID = 2;
    private static final int BOULDER_ID = 3;
    private static final int DIAMOND_ID = 4;
    private static final int FROG_ID = 5;
    private static final int BUTTERFLY_ID = 6;
    private static final int AMOEBA_ID = 7;
    private static final int MAGIC_WALL_ID = 8;
    private static final int FIREFLY_ID = 9;
    private static final int EXIT_ID = 10;

    //Probabilities
    private static final float FLY_SPAWNING_CHANCE = 0.5f;
    private static final float STONE_SPAWNING_CHANCE = 0.45f;

    //encoding
    private static final boolean RUN_LENGTH_ENCODE = false;

    //blurring values
    private static final int BLUR_ITERATIONS = 10;
    private static final int BLUR_CELLS_AMOUNT = 5;

    /**
     * Constructor for the MapGenerator class that initializes...
     * ...grid size and creates the required field arrays.
     *
     * @param canvasWidth the width of the canvas
     * @param canvasHeight the height of the canvas
     */
    public MapGenerator(final int canvasWidth, final int canvasHeight) {
        int tileSize = Level.DRAW_ENTITY_SIZE;
        columns = canvasHeight / tileSize;
        rows = canvasWidth / tileSize;
        field = new float[columns][rows];
        caveMap = new float[columns][rows];
        caveCount = 0;
        caveOrigin = new ArrayList<>();
    }

    /**
     * Generates a map in the level file format as a string.
     * This method processes the map and encodes it in a...
     * ...specific format for further use.
     *
     * @return the map in string format
     */
    public String createMap() {
        //calls the process to run all the steps in
        //creating a randomly generated map

        mapProcess();

        //pre grid and actor format stuff
        String gameSectionInfo = "Level\n";
        gameSectionInfo += "1.0\n"; //amoeba rate
        gameSectionInfo += "30\n"; //AMOEBA MAX
        gameSectionInfo += "230\n"; //completionTime
        gameSectionInfo += "12\n"; //diamond threshold



        //this is the string building stuff
        String stringTileHeader = "Grid\n";
        StringBuilder stringTileGrid = new StringBuilder();
        StringBuilder stringPlayerHeader = new StringBuilder("Player\n");
        StringBuilder stringActorGrid = new StringBuilder("Actor\n");
        stringTileHeader += rows
                + " " + columns + "\n";

        //file format rules
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                //Grid Part
                if (field[i][j] < STONE_THRESHOLD) {
                    stringTileGrid.append("W"); //Wall
                } else if (field[i][j] >= STONE_THRESHOLD
                        && field[i][j] < DIRT_THRESHOLD) {
                    stringTileGrid.append("D"); //Dirt
                } else if (field[i][j] >= DIRT_THRESHOLD
                        && field[i][j] < FLOOR_THRESHOLD) {
                    stringTileGrid.append("P"); //Path
                } else if (field[i][j] == TITANIUM_WALL) {
                    stringTileGrid.append("T"); //Titanium Wall
                } else if (field[i][j] == MAGIC_WALL_ID) {
                    stringTileGrid.append("M"); // Magic wall
                } else if (field[i][j] >= KEY_DOOR_THRESHOLD
                        && field[i][j] % 1 == KEY_CALC_VAL) { //if key
                    stringTileGrid.append("K" + " ").append((int) field[i][j])
                            .append(" ").append(DOOR_COLOUR[COLOUR_INDEX]);
                } else if (field[i][j] >= KEY_DOOR_THRESHOLD
                        && field[i][j] % 1 == DOOR_CALC_VAL) { //if key
                    stringTileGrid.append("Do" + " ").append((int) (field[i][j]
                            - DOOR_CALC_VAL)).append(" ")
                            .append(DOOR_COLOUR[COLOUR_INDEX]);

                }else if (field[i][j] == EXIT_ID) {
                    stringTileGrid.append("E 0");
                    //players part
                } else if (field[i][j] == PLAYER_ID) {
                    stringPlayerHeader.append(j).append(" ")
                            .append(i).append(" 0 { 0 }\n");
                    stringTileGrid.append("P");
                    //Actors part
                } else if (field[i][j] == DIAMOND_ID) {
                    stringActorGrid.append("Di" + " ")
                            .append(j).append(" ").append(i).append(",");
                    //Diamond
                    stringTileGrid.append("P");
                } else if (field[i][j] == BOULDER_ID) {
                    stringActorGrid.append("B" + " ")
                            .append(j).append(" ")
                            .append(i).append(","); //Boulder
                    stringTileGrid.append("P");
                } else if (field[i][j] == BUTTERFLY_ID) {
                    stringActorGrid.append("Bu" + " ")
                            .append(j).append(" ").append(i).append(" ")
                            .append(Math.random()
                                    > FLY_SPAWNING_CHANCE ? "L" : "R")
                            .append(","); //Butterfly
                    stringTileGrid.append("P");
                } else if (field[i][j] == FIREFLY_ID) {
                    stringActorGrid.append("Fi" + " ").append(j).append(" ")
                            .append(i).append(" ")
                            .append(Math.random()
                                    > FLY_SPAWNING_CHANCE ? "L" : "R")
                            .append(","); //FIREFLY
                    stringTileGrid.append("P");
                } else if (field[i][j] == AMOEBA_ID) {
                    stringActorGrid.append("A" + " ").append(j)
                            .append(" ").append(i).append(","); //Amoeba
                    stringTileGrid.append("P");
                } else if (field[i][j] == FROG_ID) {
                    stringActorGrid.append("F" + " ").append(j)
                            .append(" ").append(i).append(","); //Frog
                    stringTileGrid.append("P");
                } else {
                    stringTileGrid.append("P");
                }

                stringTileGrid.append(",");
            }
            stringTileGrid.append("\n");
        }

        if (RUN_LENGTH_ENCODE) {
            stringTileGrid = new StringBuilder(stringTileHeader
                    + runLengthEncode(stringTileGrid.toString()));
        }

        //final format is a concatenation of all the
        //...different sections of the level file format
        String finalFormat = gameSectionInfo + stringTileHeader
                + stringTileGrid + stringPlayerHeader + stringActorGrid;

        return finalFormat;
    }

    /**
     * Performs run-length encoding on the grid for optimization.
     *
     * @param stringGrid the string representation of the grid
     * @return the run-length encoded grid string
     */
    private String runLengthEncode(final String stringGrid) {

        //the overall grid as a string
        StringBuilder finalGrid = new StringBuilder();

        //runs through every line of the grid
        for (String line : stringGrid.split("\n")) {

            //removes any commas added so far
            //basically added so I can turn runLength encode on and off
            String[] tiles = line.split(",");

            int count = 1;
            String currentTile = tiles[0];
            StringBuilder curLine = new StringBuilder();

            for (int i = 1; i < tiles.length; i++) {

                //if its seen the same value again
                if (tiles[i].equals(currentTile)) {
                    count++;

                    //otherwise it appends to the line
                    //how many times it saw the currentValue
                } else {
                    curLine.append(count).append(" ")
                            .append(currentTile).append(",");
                    currentTile = tiles[i];
                    count = 1;
                }

            }
            curLine.append(count).append(" ").append(currentTile).append(",");
            finalGrid.append(curLine).append("\n");

        }
        //System.out.println(finalGrid);
        return finalGrid.toString();
    }


    /**
     * Runs all the steps necessary to generate...
     * ...a random map and return it as a 2D array
     */
    private void mapProcess() {
        createField();
        field = blur(field);
        identifyCaves();
        connectCaves();


        //space to add more random objects
        CaveItemPlacer newCave =
                new CaveItemPlacer(field, rows, columns, STONE_THRESHOLD,
                        DIRT_THRESHOLD, FLOOR_THRESHOLD);
        field = newCave.mapProcess(PLAYER_COUNT, BOULDER_COUNT,
                DIAMOND_COUNT, FROG_COUNT,
                BUTTERFLY_COUNT, AMOEBA_COUNT,
                MAGIC_WALL_COUNT, KEY_AND_DOOR_COUNT,
                FIREFLY_COUNT,EXIT_COUNT);
    }

    /**
     * Initializes the map with random noise to create the base for the field.
     */
    private void createField() {
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {

                //---------------------IMPORTANT INFO-------------------
                //ratio between stone and walkable objects drawn is decided here
                //0 is stone
                //1 is walkable objects
                //Math.random() < 1 ? 0 : 1 = fully stone map
                //Math.random() < "X chance of stone" : walkableObject : stone;
                //try changing the X value and seeing how it affects the map
                field[i][j] = Math.random() < STONE_SPAWNING_CHANCE ? 0 : 1;
                //-------------------------------------------------------
            }
        }
    }

    /**
     * Applies a blurring algorithm to smooth...
     * ...out jagged map edges and create caves.
     *
     * @param grid the grid to be blurred
     * @return the blurred grid
     */
    private float[][] blur(final float[][] grid) {
        float[][] localGrid = grid;
        float left;
        float up;

        //for every cell in the grid, average its gird value from 4 around it
        //this is the blurring process
        for (int h = 0; h < BLUR_ITERATIONS; h++) {
            float[][] newGrid = new float[columns][rows];
            for (int i = 1; i < columns; i++) {
                for (int j = 1; j < rows; j++) {

                    //gathering blocks around the cell being checked
                    //taking borders into account
                    if (i - 1 < 1) {
                        left = localGrid[columns - 1][j];
                    } else {
                        left = localGrid[i - 1][j];
                    }
                    float right = localGrid[(i + 1) % columns][j];
                    if (j - 1 < 1) {
                        up = localGrid[i][rows - 1];
                    } else {
                        up = localGrid[i][j - 1];
                    }
                    float down = localGrid[i][(j + 1) % rows];
                    float middle = localGrid[i][j];

                    //creating a new value for the current cell
                    float average = (left + right + up + down + middle)
                            / BLUR_CELLS_AMOUNT;
                    newGrid[i][j] = average;
                }
            }
            //finally updating the gird with the values in each cell
            localGrid = newGrid;
        }

        //adding a stone border around the map
        for (int i = 0; i < columns; i++) {
            localGrid[i][0] = 0;
            localGrid[i][rows - 1] = 0;
        }
        for (int j = 0; j < rows; j++) {
            localGrid[0][j] = 0;
            localGrid[0][columns - 1] = 0;
        }
        return localGrid;
    }

    /**
     * Identifies all the caves in the grid and assigns them unique IDs.
     */
    private void identifyCaves() {
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {

                //if it's not stone (therefore a cave) AND the map where its...
                // ...going to be stored hasn't seen this cell before
                //then add the cells group into the caveMap
                if (field[i][j] >= STONE_THRESHOLD
                        && field[i][j] < FLOOR_THRESHOLD
                        && caveMap[i][j] == 0) {
                    //updates amount of caves
                    caveCount++;
                    //flood fill
                    addToGroup(i, j, caveCount);
                    //keeping track of the origin cell
                    //that started the flood fill
                    caveOrigin.add(new int[]{i, j});
                }
            }
        }
    }

    /**
     * Recursively adds cells to a cave group by performing a flood fill.
     *
     * @param x the x-coordinate of the starting cell
     * @param y the y-coordinate of the starting cell
     * @param caveID the unique ID of the cave
     */
    private void addToGroup(final int x, final int y, final int caveID) {

        //if the cell is being checked is stone
        //OR its already seen this cell before
        if (field[x][y] < STONE_THRESHOLD || caveMap[x][y] != 0) {
            return;
        }

        caveMap[x][y] = caveID;
        addToGroup(x + 1, y, caveID);
        addToGroup(x - 1, y, caveID);
        addToGroup(x, y + 1, caveID);
        addToGroup(x, y - 1, caveID);
    }

    /**
     * Connects the caves by carving tunnels...
     * ...between the origin points of each cave.
     */
    private void connectCaves() {
        for (int i = 0; i < caveOrigin.size() - 1; i++) {
            int[] start = caveOrigin.get(i);
            int[] end = caveOrigin.get(i + 1);
            carveTunnel(start[0], start[1], end[0], end[1]);
        }
    }

    /**
     * Carves a tunnel between two points in the grid.
     *
     * @param startX the x-coordinate of the starting point
     * @param startY the y-coordinate of the starting point
     * @param endX the x-coordinate of the end point
     * @param endY the y-coordinate of the end point
     */
    private void carveTunnel(final int startX, final int startY,
                             final int endX, final int endY) {
        int localStartX = startX;
        int localStartY = startY;
        //carving a horizontal tunnel
        while (localStartX != endX) {
            field[startX][startY] = DIRT_THRESHOLD;
            localStartX += (endX > localStartX) ? 1 : -1;
        }
        //carving a vertical tunnel
        while (localStartY != endY) {
            field[startX][startY] = DIRT_THRESHOLD;
            localStartY += (endY > localStartY) ? 1 : -1;
        }
    }
}
