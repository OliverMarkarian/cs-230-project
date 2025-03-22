import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Scanner;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Class that takes input from user to create a profile
 *
 * @author Sam
 */
public class NewProfile {

    private Stage stage;
    @FXML
    private TextField userInput;
    private Game game;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Method that is invoked when the "Create Profile" button is clicked.
     * It retrieves the text input from the user and prints it to the console.
     */
    @FXML
    public void loadProfile() throws IOException, ParseException {

        final String name = userInput.getText();
        File file = new File("profiles.txt");

        Scanner s = new Scanner(file);

        StringBuilder builder = new StringBuilder();
        boolean created = false;
        int maxLevel = -1;
        while (s.hasNext()) {
            final String next = s.next();

            if (!s.hasNextInt()) {

            }

            final int highestLevel = s.nextInt();

            builder.append(next).append(" ").append(highestLevel).append("\n");

            if (next.equals(name)) {
                created = true;
                maxLevel = highestLevel;
            }
        }

        s.close();

        if (!created) {
            PrintWriter writer = new PrintWriter(file);
            writer.write(builder.toString());
            writer.write(name + " 0\n");
            writer.close();

            Profile profile = new Profile(name, 0);
            Stage stage = game.getStage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LevelSelect.fxml"));
            Parent root = loader.load();

            LevelSelect controller = loader.getController();
            controller.setProfile(profile);
            controller.setGame(game);

            Scene scene = new Scene(root, 1280, 720);
            stage.setScene(scene);
            stage.show();
        } else {
            final String path = String.format("%s.txt", name);
            File saveFile = new File(path);

            Profile profile = new Profile(name, maxLevel);

            if (saveFile.exists()) {
                Scanner scanner = new Scanner(saveFile);
                final int levelID = scanner.nextInt();
                scanner.close();
                game.loadSaveGame(profile, levelID);
            } else {


                Stage stage = game.getStage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("LevelSelect.fxml"));
                Parent root = loader.load();

                LevelSelect controller = loader.getController();
                controller.setProfile(profile);
                controller.setGame(game);

                Scene scene = new Scene(root, 1280, 720);
                stage.setScene(scene);
                stage.show();
            }
        }
    }
}
