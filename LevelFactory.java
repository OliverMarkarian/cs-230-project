import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.util.*;

/**
 * A factory class for creating levels, either randomly or from our level file format.
 *
 * @author Sam
 * @see Level
 */
public class LevelFactory {
    /**
     * A method to create a level from our format.
     *
     * @param format The Level in its String representation.
     * @return The Level created from the String given.
     * @throws ParseException If the String is invalid and doesn't meet our specification, an exception is thrown
     *                        denoting what the issue was and where.
     */
    public static Level createFromText(final String format) throws ParseException {
        Scanner scanner = new Scanner(format);

        if (!scanner.hasNextLine()) {
            throw new ParseException("Level file was empty", 0);
        }

        final String first = scanner.nextLine();

        if (!first.equals("Level")) {
            throw new ParseException("Level file did not start with 'Level' header", 1);
        }

        final String levelData = parseUntil("Grid", scanner);

        Scanner levelScanner = new Scanner(levelData);

        if (!levelScanner.hasNextFloat()) {
            throw new ParseException("Expected an amoeba rate (float). Was not given one.", 0);
        }
        final float amoebaRate = levelScanner.nextFloat();

        if (!levelScanner.hasNextInt()) {
            throw new ParseException("Expected an maximum amoeba size (int). Was not given one.", 0);
        }
        final int amoebaMax = levelScanner.nextInt();

        Amoeba.setAmoebaRate(amoebaRate);
        Amoeba.setMaxGroupSize(amoebaMax);

        if (!levelScanner.hasNextInt()) {
            throw new ParseException("Expected an level timer threshold (int). Was not given one.", 0);
        }
        final int levelTimer = levelScanner.nextInt();

        if (!levelScanner.hasNextInt()) {
            throw new ParseException("Expected an diamond threshold (int). Was not given one.", 0);
        }
        final int diamondThreshold = levelScanner.nextInt();

        final String gridSection = parseUntil("Player", scanner);
        final ArrayList<ArrayList<Tile>> tiles = parseGrid(gridSection);

        final Grid grid = new Grid(tiles, tiles.getFirst().size(), tiles.size());


        final String playerSection = parseUntil("Actor", scanner);

        Scanner playerScanner = new Scanner(playerSection);

        if (!playerScanner.hasNextInt()) {
            throw new ParseException("Expected an x-coordinate for the Player.", 0);
        }

        int x = playerScanner.nextInt();

        if (!playerScanner.hasNextInt()) {
            throw new ParseException("Expected n y-coordinate for the Player.", 0);
        }

        int y = playerScanner.nextInt();

        if (!playerScanner.hasNextInt()) {
            throw new ParseException("Expected a diamond count for the Player.", 0);
        }

        int diamondCount = playerScanner.nextInt();

        if (!playerScanner.hasNext()) {
            throw new ParseException("Expected a beginning brace for set of keys picked up by player.", 0);
        }
        playerScanner.next();

        HashSet<Integer> collectedKeys = new HashSet<>();
        while (playerScanner.hasNextInt()) {
            collectedKeys.add(playerScanner.nextInt());
        }

        if (!playerScanner.hasNext()) {
            throw new ParseException("Expected a closing brace for set of keys picked up by player.", 0);
        }
        playerScanner.next();

        //Deal with empty grid case etc
        Player player = new Player(x, y, grid, collectedKeys); //the player you play as in game

        StringBuilder actorBuilder = new StringBuilder(); //Actor is just the rest of the file
        while (scanner.hasNextLine()) {
            actorBuilder.append(scanner.nextLine()).append("\n");
        }

        final String actorSection = actorBuilder.toString();
        ArrayList<Actor> actors = parseActors(actorSection, grid, player);

        return new Level(player, grid, actors, amoebaRate, amoebaMax, levelTimer, diamondThreshold);
    }

    /**
     * Parses the input string until the specified endpoint is found.
     *
     * @param endpoint The endpoint string to look for in the input.
     * @param scanner The scanner to read the input.
     * @return The content between the current position and the endpoint.
     * @throws ParseException If the endpoint is not found, an exception is thrown.
     */
    private static String parseUntil(final String endpoint, Scanner scanner) throws ParseException {
        StringBuilder builder = new StringBuilder();

        int count = 0;

        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine();
            if (line.equals(endpoint)) {
                return builder.toString();
            }
            builder.append(line).append("\n");
            count++;
        }
        throw new ParseException(String.format("Tried to parse until hitting %s, scanner ran out of lines after %d iterations.", endpoint, count), count);
    }

    /**
     * Parses the grid data from the input string and creates a grid.
     *
     * @param input The input string containing grid data.
     * @return A 2D ArrayList representing the grid of tiles.
     * @throws ParseException If the grid format is invalid, an exception is thrown.
     */
    private static ArrayList<ArrayList<Tile>> parseGrid(String input) throws ParseException {

        //Gross
        final Map<String, KeyDoorColour> colourMap = new HashMap<>();
        colourMap.put("R", KeyDoorColour.RED);
        colourMap.put("G", KeyDoorColour.GREEN);
        colourMap.put("B", KeyDoorColour.BLUE);
        colourMap.put("Y", KeyDoorColour.YELLOW);

        StringBuilder validColours = new StringBuilder();
        colourMap.keySet().forEach(c -> validColours.append(c).append(", "));
        final String colourString = validColours.toString().substring(0, validColours.length() - 2);


        Scanner s = new Scanner(input);
        if (!s.hasNextInt()) {
            throw new ParseException("Expected a width for grid, was not given one.", 0);
        }
        final int width = s.nextInt();
        if (!s.hasNextInt()) {
            throw new ParseException("Expected a height for grid, was not given one.", 0);
        }
        final int height = s.nextInt();
        ArrayList<ArrayList<Tile>> tiles = new ArrayList<>();
        s.useDelimiter(",|\n| ");
        s.nextLine();
        for (int y = 0; y < height; y++) {
            ArrayList<Tile> t = new ArrayList<>();
            for (int x = 0; x < width; x++) {
                final String type = s.next();

                switch (type) {
                    case "D" -> t.add(new Dirt(x, y));
                    case "W" -> t.add(new Wall(x, y));
                    case "P" -> t.add(new Path(x, y));
                    case "M" -> t.add(new MagicWall(x, y));
                    case "E" -> {
                        if (!s.hasNextInt()) {
                            throw new ParseException(String.format("When creating exit tile, expected an int for diamond threshold." +
                                    "Was not given one at row %d, column %d", y, x), 0);
                        }
                        final int diamondThreshold = s.nextInt();
                        t.add(new ExitTile(diamondThreshold, x, y));
                    }
                    case "T" -> t.add(new TitaniumWall(x, y));
                    case "K" -> {
                        if (!s.hasNextInt()) {
                            throw new ParseException(String.format("When creating a key, expected an int for keyID." +
                                    "Was not given one at row %d, column %d", y, x), 0);
                        }
                        final int keyID = s.nextInt();

                        if (!s.hasNext()) {
                            throw new ParseException(String.format("When creating key, expected a color in {%s}. Was not given one at row %d, column %d", colourString, y, x), 0);
                        }

                        final String colourText = s.next();

                        if (!colourMap.containsKey(colourText)) {
                            throw new ParseException(String.format("When creating key, expected a color in {%s}. Was given %s at row %d, column %d", colourString, colourText, y, x), 0);
                        }

                        final KeyDoorColour colour = colourMap.get(colourText);


                        t.add(new Key(x, y, keyID, colour));
                    }
                    case "Do" -> {
                        if (!s.hasNextInt()) {
                            throw new ParseException(String.format("When creating locked door, expected an int doorID." +
                                    "Was not given one at row %d, column %d", y, x), 0);
                        }
                        final int doorID = s.nextInt();

                        if (!s.hasNext()) {
                            throw new ParseException(String.format("When creating locked door, expected a color in {%s}. Was not given one at row %d, column %d", colourString, y, x), 0);
                        }

                        final String colourText = s.next();

                        if (!colourMap.containsKey(colourText)) {
                            throw new ParseException(String.format("When creating locked door, expected a color in {%s}. Was given %s at row %d, column %d", colourString, colourText, y, x), 0);
                        }

                        final KeyDoorColour colour = colourMap.get(colourText);

                        t.add(new Door(x, y, doorID, colour));
                    }
                    default -> {
                        System.out.println("Unknown tile type: " + type);
                        throw new ParseException(String.format("Unknown tile type: %s at row %d, col %d.\n",
                                type, y, x), width);
                    }
                }
            }

            if (s.hasNextLine()) {
                s.nextLine();
            }
            tiles.add(t);
        }
        return tiles;
    }


    /**
     * Creates a random map and returns it as a level in the file format.
     * This method is still under maintenance and not fully functional.
     *
     * @param canvasWidth The width of the canvas for the map.
     * @param canvasHeight The height of the canvas for the map.
     * @return A randomly generated Level object based on the map data.
     * @throws ParseException If the random map generation fails, an exception is thrown.
     */
    public static Level createRandomMap(int canvasWidth, int canvasHeight) throws ParseException {
        MapGenerator randMapGen = new MapGenerator(canvasWidth, canvasHeight);

        //this one holds the file formatted string
        String randMap = randMapGen.createMap();

        //placeholder return----------------------------------------------------------------------------
        return createFromText(randMap);
    }

    /**
     * Parses the actors data from the input string and creates a list of actors.
     *
     * @param data The input string containing actors data.
     * @param grid The grid where the actors will be placed.
     * @param player The player object to be added to the list of actors.
     * @return A list of Actor objects created from the data.
     * @throws ParseException If the actors data is invalid, an exception is thrown.
     */
    private static ArrayList<Actor> parseActors(final String data, Grid grid, Actor player) throws ParseException {
        ArrayList<Actor> actors = new ArrayList<>();
        Scanner s = new Scanner(data);
        actors.add(player);
        grid.getTile(player.getX(), player.getY()).setOccupier(player);
        s.useDelimiter(",");

        HashSet<String> valid = new HashSet<>();
        valid.add("Bu");
        valid.add("B");
        valid.add("F");
        valid.add("A");
        valid.add("Di");
        valid.add("Fi");


        int actorCount = 0; //used for debugging parse errors.
        while (s.hasNext()) {
            final String next = s.next();
            if (!next.matches("(\n)+")) { //should read
                Scanner thisLine = new Scanner(next);
                thisLine.useDelimiter(" | ,|\n");

                if (!thisLine.hasNext()) {
                    throw new ParseException(String.format("Actor with index %d did not have a type", actorCount), actorCount);
                }

                final String actorType = thisLine.next();
                if (!valid.contains(actorType)) {
                    throw new ParseException(String.format("Actor with index %d had type %s which is not in valid set.", actorCount, actorType), actorCount);
                }

                if (!thisLine.hasNextInt()) {
                    throw new ParseException(String.format("Actor with index %d and type %s did not have an X coordinate (you may have forgotten to add a comma after their x-coordinate?)", actorCount, actorType), actorCount);
                }

                final int x = thisLine.nextInt();

                if (!thisLine.hasNextInt()) {
                    throw new ParseException(String.format("Actor with index %d and type %s did not have an Y coordinate (you may have forgotten to add a comma after their y-coordinate?)", actorCount, actorType), actorCount);
                }

                final int y = thisLine.nextInt();


                Actor actor = null;

                //PLAN: Factor the base stuff (x, y) out to an actor parse each subclass calls when scanning
                //This is getting bloated and I think subclasses should parse their data (makes it less centralised
                //anyway).
                if (actorType.equals("B")) { //will need to sub-scan for X Y etc
                    actor = new Boulder(x, y);
                } else if (actorType.equals("Di")) {
                    actor = new Diamond(x, y);
                } else if (actorType.equals("A")) {
                    actor = new Amoeba(x, y);
                } else if (actorType.equals("F")) {
                    actor = new Frog(player, x, y); //Frog needs a reference to the Player
                } else if (actorType.equals("Bu")) {
                    final String direction = thisLine.next();
                    boolean isLeft = false;
                    if (direction.equals("L")) {
                        isLeft = true;
                    } else if (direction.equals("R")) {
                        isLeft = false;
                    } else {
                        throw new ParseException(String.format("Expected L|R for butterfly direction, was given %s. Actor index: ", direction, actorType), actorCount);
                    }
                    actor = new Butterfly(isLeft, x, y, player); //this is probably not how we are doing it, jst testing forwarding
                } else if (actorType.equals("Fi")) {
                    final String direction = thisLine.next();
                    boolean isLeft = false;
                    if (direction.equals("L")) {
                        isLeft = true;
                    } else if (direction.equals("R")) {
                        isLeft = false;
                    } else {
                        throw new ParseException(String.format("Expected L|R for firefly direction, was given %s. Actor index: ", direction, actorType), actorCount);
                    }
                    actor = new Firefly(isLeft, x, y, player);
                } else {
                    throw new ParseException(String.format("Failed to parse actor with ActorType %s. Actor index: %d", actorType, actorCount), actorCount);
                }
                actors.add(actor);
                if (grid.getTile(x, y).getOccupier() != null) {
                    throw new ParseException(String.format("Actor with index %d tried to occupy a tile already occupied."
                            , actorCount), actorCount);
                }
                grid.getTile(x, y).setOccupier(actor);
                actorCount++;
            }
        }
        return actors;
    }
}
