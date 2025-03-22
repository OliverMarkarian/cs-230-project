import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

/**
 * This class represents the game level, including the player, actors, and the grid. It manages game state updates,
 * player input, level completion, and drawing of the game scene.
 *
 * @author Sam
 */
public class Level {
    private Player player; //probably best to address the Player as it's own thing.
    //this is a dummy null value until we actually have the player
    private ArrayList<Actor> npcs;
    private InputBuffer buf;
    private Grid grid;

    public static final int DRAW_ENTITY_SIZE = 32; //Entity being an actor or tile. We can vary this in game
    public static int LEVEL_DIAMOND_COUNT;
    private float amoebaRate;
    private int amoebaMax;
    private int levelTimer;
    private Queue<Actor> addList;
    private int tickCount = 0;
    private int ticksSinceDeath = 0;

    /**
     * Constructs a new Level with the given parameters, initializing the grid, player, actors, and other level attributes.
     *
     * @param player        The player object in this level
     * @param grid          The grid representing the level's layout
     * @param actors        A list of NPC actors in the level
     * @param amoebaRate    The rate at which amoebas spread in the level
     * @param amoebaMax     The maximum number of amoebas allowed in the level
     * @param levelTimer    The initial timer value for the level
     * @param diamondThreshold The threshold number of diamonds required in the level
     */
    public Level(Player player, Grid grid, ArrayList<Actor> actors, float amoebaRate, int amoebaMax, int levelTimer, int diamondThreshold) {
        this.player = player;
        grid.getTile(player.getX(), player.getY()).setOccupier(player);
        this.npcs = actors;
        this.npcs.add(player);
        this.grid = grid;
        buf = new InputBuffer(2);
        this.amoebaRate = amoebaRate;
        this.amoebaMax = amoebaMax;
        this.levelTimer = levelTimer;
        LEVEL_DIAMOND_COUNT = diamondThreshold;
        this.addList = new LinkedList<>();
        state = State.Updating;
    }

    /**
     * Receives a key event from the player and processes it through the input buffer.
     *
     * @param event The KeyEvent triggered by the player
     */
    public void receiveEvent(KeyEvent event) {
        buf.receiveEvent(event);
    }

    /**
     * Gets the current level timer value.
     *
     * @return The current value of the level timer
     */
    public int getLevelTimer() {
        return levelTimer;
    }

    private enum State {
        Updating,
        PlayerDied,
        Fading
    }

    private State state;

    /**
     * Updates the state of the level, including the player, actors, and grid. This method is called every game tick.
     *
     * @throws IOException   If an I/O error occurs during level update
     * @throws ParseException If a parsing error occurs
     */
    public void update() throws IOException, ParseException {

        if (state == State.Updating) {
            updateTimer();

            Optional<KeyEvent> playerInput = buf.tryRead();
            if (!playerInput.isEmpty()) {
                KeyEvent event = playerInput.get();
                this.player.takeInput(event);
            }

            for (Actor actor : npcs) {
                if (actor.shouldUpdate()) {
                    actor.update(grid);
                }
            }

            //Unoccupy any tiles with dead NPCs in them if they haven't been removed already.
            npcs.stream().filter(actor -> !actor.isAlive()
                            && grid.getTile(actor.getX(), actor.getY()).getOccupier() == actor)
                    .map(actor -> grid.getTile(actor.getX(), actor.getY()))
                    .forEach(tile -> tile.setOccupier(null));

            npcs.removeIf(actor -> !actor.isAlive());

            //Avoids iterator invalidation
            grid.pollNewActors().forEach(actor -> npcs.add(actor));

            ArrayList<Actor> toRemove = grid.pollRemovedActors();
            npcs.removeIf(actor -> toRemove.contains(actor)); //The time complexity here is evil.
        } else if (state == State.PlayerDied) {
            ticksSinceDeath++;
        }
    }

    /**
     * Updates the level timer based on the tick count.
     */
    private void updateTimer() {
        // every 1000 / 200 = 5 ticks, subtract a second from the counter
        if (tickCount == 10) {
            levelTimer -= 1;
            tickCount = 0;
        }

        tickCount++;
    }

    /**
     * A method to be used to determine if the level is finished running and should be closed.
     *
     * @return An exit reason, if the level is over.
     * @see ExitReason
     */
    public Optional<ExitReason> shouldExit() {
        if (!player.isAlive()) {
            if (ticksSinceDeath > 10) {
                return Optional.of(ExitReason.PLAYER_DEAD);
            } else {
                state = State.PlayerDied;
            }
        }

        if (levelTimer == 0) {
            return Optional.of(ExitReason.TIME_OUT);
        }

        if (grid.levelComplete()) {
            return Optional.of(ExitReason.LEVEL_COMPLETE);
        }

        //Otherwise, exited I guess.


        return Optional.empty();
    }

    private static final String LEVEL_TEXT = "Diamond count: %d/%d. Time left: %d.";

    public String getUIText() {
        return String.format(LEVEL_TEXT, player.getDiamondCount(), LEVEL_DIAMOND_COUNT, getLevelTimer());
    }

    /**
     * Gets the current score of the player, which is based on the number of diamonds collected.
     *
     * @return The player's current score (diamond count)
     */
    public int getScore() {
        return player.getDiamondCount();
    }

    /**
     * A method to save the entire levels state to the level file format specified in the design doc.
     *
     * @return A string containing this state in the format.
     */
    public String toText() {

        StringBuilder builder = new StringBuilder();

        builder.append("Level\n").append(amoebaRate).append("\n").append(amoebaMax).append("\n")
                .append(levelTimer).append("\n").append(LEVEL_DIAMOND_COUNT).append("\nGrid\n")
                .append(grid.toText()).append("\nPlayer\n").append(player.toText()).append("Actor\n");

        npcs.stream().filter(actor -> actor != player).forEach(actor -> builder.append(actor.toText()).append(","));

        return builder.toString();
    }

    /**
     * Draws the current level, including the grid and all actors (NPCs and player), to the specified GraphicsContext.
     *
     * @param gc The GraphicsContext to draw to
     */
    public void draw(GraphicsContext gc) {
        grid.draw(gc);

        npcs.forEach(actor -> actor.draw(gc));

        if (player.isAlive()) {
            player.draw(gc);
        }

    }

    /**
     * Binds the camera to the player, ensuring the camera follows the player's movements.
     *
     * @param camera The camera to bind to the player
     */
    public void bindCamera(Camera camera) {
        camera.setTarget(player);
    }
}
