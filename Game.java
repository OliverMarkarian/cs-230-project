import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * A singleton class responsible for initialisation of controllers and the window.
 * @see MenuController
 * @see GameController
 */
public class Game extends Application {
    private static Stage stage;
    private MediaPlayer mediaPlayer;
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    /**
     * The entry point to the game.
     * @param args Any arguments are discarded.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The method to be called by JavaFX to initialise the game. It
     * is incorrect to call this yourself.
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception If the menu opening fails.
     */
    @Override
    public void start(Stage stage) throws Exception {
        Game.stage = stage;
        stage.setTitle("Boulder Dash");
        playMusic("ariaMath8Bit.mp3");

        runMenu();
    }

    /**
     * Plays background music during the game.
     *
     * @param filePath the path to the music file
     */
    private void playMusic(String filePath) {
        Media media = new Media(new File(filePath).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }

    /**
     * A method to get the window of the game.
     * @return The window.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * A method to load and show the menu.
     * @throws IOException If loading the menu fails.
     */
    public void runMenu() throws IOException {
        MenuController menuController = new MenuController(this);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(menuController);
        loader.setLocation(getClass().getResource("Menu.fxml"));
        Scene scene = new Scene(loader.load(), WIDTH, HEIGHT);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * A method to load a level from a file, based on its ID.
     * @param profile The profile of the player who is playing.
     * @param levelID The ID of the level to be loaded.
     * @throws IOException If the controller creation fails.
     * @throws ParseException If the level format is invalid.
     */
    public void runGame(Profile profile, int levelID) throws IOException, ParseException {
        GameController controller = new GameController(this);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getResource("GameUI.fxml"));
        Scene scene = new Scene(loader.load(), WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();

        controller.run(profile, scene, levelID);
    }

    /**
     * A method to load a save game from a file, based on its ID.
     * @param profile The profile of the player who is playing.
     * @param levelID The ID of the level to be loaded.
     * @throws IOException If the controller creation fails.
     * @throws ParseException If the level format is invalid.
     */
    public void loadSaveGame(Profile profile, int levelID) throws IOException, ParseException {
        GameController controller = new GameController(this);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getResource("GameUI.fxml"));
        Scene scene = new Scene(loader.load(), WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();

        controller.runFromSaveGame(profile, scene, levelID);
    }

    /**
     * A method to create and run a randomly generated level.
     * @throws IOException If the loading of the GameController fails.
     * @throws ParseException If the level text is in an invalid state.
     * @see MapGenerator
     * @see LevelFactory
     */
    public void runEndless() throws IOException, ParseException {
        GameController controller = new GameController(this);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getResource("GameUI.fxml"));
        Scene scene = new Scene(loader.load(), WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();

        controller.runEndless(scene);
    }
}
