import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Optional;
import java.util.Scanner;

/**
 * Controller class for managing game interactions and UI updates.
 * This class is responsible for handling game controls and updating the user interface elements.
 *
 * @author Sam
 * @author Jess
 */
public class GameController {
    private Level level;
    private Profile profile;
    private static final int TICK_RATE = 100;

    @FXML
    private Canvas gameLayer;

    @FXML
    private Text UIText;

    private Group menuGroupNoSave;
    private Group menuGroup;

    private GraphicsContext gameGraphicsContext;

    private Camera levelCamera;
    @FXML
    private Rectangle fadeRectangle;
    private boolean paused = false;

    private Timeline tick;

    private Group currentGroup;

    private Game game;

    private Optional<Integer> levelID;

    public GameController(Game game) {
        this.game = game;
    }

    public void exit() throws IOException {
        game.runMenu();
    }

    public void saveExit() throws IOException {
        final String text = level.toText();

        File file = new File(profile.getName() + ".txt");

        PrintWriter writer = new PrintWriter(file);

        writer.write(String.format("%d\n",levelID.get()));

        writer.write(text);

        writer.close();

        exit();

    }

    public void continueGame() {
        paused = false;
        currentGroup.setVisible(false);
        tick.play();

    }

    public void pause() {
        if (this.tick == null) { //Fade transition, just wait
            return;
        }
        paused = true;
        tick.pause();
        currentGroup = (levelID.isEmpty()) ? menuGroupNoSave : menuGroup;
        currentGroup.setVisible(true);
    }

    /**
     * A method to create a randomly generated level.
     * @param scene The scene to render to.
     * @throws ParseException If the LevelFactory produces an incorrect level, this is thrown.
     * @see MapGenerator
     * @see LevelFactory
     */
    public void runEndless(Scene scene) throws ParseException, IOException {
        level = LevelFactory.createRandomMap(3840, 2160);

        levelID = Optional.empty();

        setup(null, scene, true); //No profiles for endless.
    }


    /**
     * A method to load a saved game and start the game.
     * @param profile The profile of the player who owns the save.
     * @param scene The scene of the controller in.
     * @param levelID The ID of the level being created.
     * @throws FileNotFoundException If the path does not exist.
     */
    public void runFromSaveGame(Profile profile, Scene scene, int levelID) throws IOException, ParseException {
        final String path = String.format("%s.txt", profile.getName());
        this.levelID = Optional.of(levelID);

        Scanner scanner = new Scanner(new File(path));
        scanner.nextLine();
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine()).append("\n");
        }
        scanner.close();

        try {
            level = LevelFactory.createFromText(builder.toString());
        } catch (ParseException exception) {
            throw new RuntimeException(String.format("Failed to parse savegame with path %s. Error: %s", path, exception));
        }

        setup(profile, scene, false);
    }

    /**
     * A method to load and play a level given by its ID. This method does not load save games.
     * @param profile The profile of the player who is loading the game.
     * @param scene The scene of the controller to load to.
     * @param levelID The ID of the level to be loaded.
     * @throws FileNotFoundException If the level file expected with the ID does not exist.
     */
    public void run(Profile profile, Scene scene, int levelID) throws IOException, ParseException {
        final String path = String.format("level%d.txt", levelID);
        this.levelID = Optional.of(levelID);
        File file = new File(path);
        StringBuilder builder = new StringBuilder();
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine()).append("\n");
        }
        scanner.close();

        try {
            level = LevelFactory.createFromText(builder.toString());
        } catch (ParseException exception) {
            throw new RuntimeException(String.format("Failed to parse level with path %s. Error: %s", path, exception));
        }

        setup(profile, scene, false);
    }


    private void setup(Profile profile, Scene scene, boolean isBig) throws IOException, ParseException {
        this.profile = profile;
        gameLayer = (Canvas) scene.lookup("#gameLayer"); //https://stackoverflow.com/questions/12201712/how-to-find-an-element-with-an-id-in-javafx
        UIText = (Text) scene.lookup("#UIText");
        gameGraphicsContext = gameLayer.getGraphicsContext2D();
        fadeRectangle = (Rectangle) scene.lookup("#fadeRectangle");
        levelCamera = new Camera(gameGraphicsContext);
        levelCamera.setIsBigLevel(isBig);
        level.bindCamera(levelCamera);

        fadeRectangle.setFill(Color.BLACK);

        menuGroup = (Group) scene.lookup("#menuGroup");
        menuGroupNoSave = (Group) scene.lookup("#menuGroupNoSave");

        menuGroup.setVisible(false);
        FadeTransition ft = new FadeTransition(Duration.millis(1000), fadeRectangle);

        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setCycleCount(0);
        ft.setOnFinished(any -> {
            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> level.receiveEvent(event));
            scene.lookup("#pauseButton").setVisible(true);
            tick = new Timeline(new KeyFrame(Duration.millis(TICK_RATE), event -> {
                try {
                    update();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }));
            tick.setCycleCount(Animation.INDEFINITE);
            tick.play();
            paused = false;

        });
        ft.play();
        update();
    }

    private void update() throws IOException, ParseException {
        level.update();
        gameGraphicsContext.setFill(Color.BLACK);
        gameGraphicsContext.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);
        levelCamera.update();
        gameGraphicsContext.clearRect(-Game.WIDTH, -Game.HEIGHT, Game.WIDTH, Game.HEIGHT);
        gameGraphicsContext.clearRect(0, -Game.HEIGHT, Game.WIDTH, Game.HEIGHT);
        gameGraphicsContext.clearRect(Game.WIDTH, -Game.HEIGHT, Game.WIDTH, Game.HEIGHT);
        gameGraphicsContext.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);
        gameGraphicsContext.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
        level.draw(gameGraphicsContext);
        UIText.setText(level.getUIText());

        handleLevelExit();

    }

    private void handleLevelExit() throws FileNotFoundException {
        if (!level.shouldExit().isEmpty()) {
            FadeTransition ft = new FadeTransition(Duration.millis(1000), fadeRectangle);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.setCycleCount(0);
            ft.setAutoReverse(true);
            tick.pause();
            ft.setOnFinished(any -> {
                try {
                    game.runMenu();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            ft.play();
            switch (level.shouldExit().get()) {
                case LEVEL_COMPLETE -> {
                    if (levelID.isPresent()) { //Playing an actual level that should be saved etc
                        if (this.profile.getMaxLevel() + 1 == levelID.get()) {
                            profile.setMaxLevel(profile.getMaxLevel() + 1);
                            Leaderboard.updateLeaderboard(this.profile, level.getScore(), levelID.get());
                        }
                    }

                    File file = new File("profiles.txt");

                    StringBuilder builder = new StringBuilder();
                    Scanner s = new Scanner(file);
                    while (s.hasNext()) {
                        final String next = s.next();

                        if (!s.hasNextInt()) {
                            //throw?
                        }

                        final int highestLevel = s.nextInt();

                        if (!next.equals(profile.getName())) {
                            builder.append(next).append(" ").append(highestLevel).append("\n");
                        }
                    }

                    s.close();

                    File save = new File(profile.getName() + ".txt");
                    if (save.exists()) {
                        save.delete();
                    }

                        PrintWriter writer = new PrintWriter(file);
                    writer.write(profile.getName() + " "
                            + profile.getMaxLevel() + "\n");
                    writer.write(builder.toString());
                    writer.close();


                }
                case TIME_OUT -> {
                    tick.stop();
                    break;
                }

                case PLAYER_DEAD -> {
                    break;
                }
                default -> {
                    break;
                }


            }
        }
    }
}
