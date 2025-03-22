import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Scanner;

/**
 * Represents the main menu of the game, allowing the player to start a new game, load a saved game,
 */
public class MenuController {
    private static Game game;

    /**
     * Constructs a MenuController with the given game instance.
     * @param game The game instance to be associated with this controller.
     */
    public MenuController(Game game) {
        this.game = game;
    }

    /**
     * Method that is invoked when the "Play" button is pressed. It starts the game at level 2.
     * @throws IOException If an I/O error occurs when starting the game.
     * @throws ParseException If an error occurs while parsing game data.
     */
    @FXML
    public void playPressed() throws IOException, ParseException {
        game.runGame(null,2);
    }

    /**
     * Method that is invoked when the "Endless Mode" button is pressed. It starts the game in endless mode.
     * @throws IOException If an I/O error occurs when starting the game.
     * @throws ParseException If an error occurs while parsing game data.
     */
    @FXML
    public void endlessPressed() throws IOException, ParseException {
        game.runEndless();
    }

    /**
     * Method that is invoked when the "Switch Level" button is pressed. It opens the level selection scene.
     * @throws IOException If an I/O error occurs while loading the level selection scene.
     */
    @FXML
    public void switchLevelPressed() throws IOException {
        Stage stage = game.getStage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LevelSelect.fxml"));
        Parent root = loader.load();

        LevelSelect controller = loader.getController();
        controller.setGame(game);

        Scene scene = new Scene(root, 1280, 720); // full screen babyyy
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Method that is invoked when the "New Game" button is pressed. It opens the new profile creation scene.
     * @throws IOException If an I/O error occurs while loading the new profile scene.
     */
    @FXML
    public void newGamePressed() throws IOException {
        Stage stage = game.getStage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("NewProfile.fxml"));
        Parent root = loader.load();

        NewProfile profileLoader = (NewProfile) loader.getController();
        profileLoader.setStage(stage);
        profileLoader.setGame(game);
        Scene scene = new Scene(root, 1280, 720); // full screen
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates a new player profile with the specified player name and saves it to the profiles file.
     * @param playerName The name of the player.
     * @throws FileNotFoundException If there is an error while saving the profile to the file.
     */
    public void createProfile(String playerName) throws FileNotFoundException {
        if (validProfileName(playerName)) {
            Profile newprofile = new Profile(playerName, 1);
            saveProfile(newprofile);
        } else {
            // display on screen
            System.out.println("Invalid");
        }
    }

    /**
     * Saves the provided player profile to the profiles file.
     * @param newProfile The profile to be saved.
     * @throws FileNotFoundException If there is an error while writing the profile to the file.
     */
    public static void saveProfile(Profile newProfile) throws FileNotFoundException {
        String profilesFile = "profiles.txt";
        PrintWriter out = new PrintWriter(profilesFile);
        out.println(newProfile.toString());
        out.close();
    }

    /**
     * Finds and returns the player profile for a given player name from the profiles file.
     * @param playerName The name of the player whose profile is to be found.
     * @return The player profile, or null if not found.
     * @throws FileNotFoundException If the profiles file cannot be read.
     */
    public static Profile findProfile(String playerName) throws FileNotFoundException {
        try {
            File profilesFile = new File("profiles.txt");
            Scanner in = new Scanner(profilesFile);
            in.useDelimiter("\\s");
            while (in.hasNextLine()) {
                String name = in.next();
                int maxLevel = in.nextInt();
                if (name == playerName) {
                    Profile user = new Profile(name,  maxLevel); //TODO
                    return user;
                } else {
                    System.out.println("Invalid");
                }
            }
            in.close();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        return null;
    }

    /**
     * Checks whether the given player name is unique in the profiles file.
     * @param playerName The player name to check.
     * @return True if the name is unique, false otherwise.
     * @throws FileNotFoundException If the profiles file cannot be read.
     */
    public static boolean isUnique(String playerName) throws FileNotFoundException {
        boolean isUnique = true;
        try {
            File profilesFile = new File("profiles.txt");
            Scanner in = new Scanner(profilesFile);
            in.useDelimiter("\\s");
            while (in.hasNextLine()) {
                String name = in.next();
                int maxLevel = in.nextInt();
                if (name == playerName) {
                    isUnique = false;
                } else {
                    isUnique = true;
                }
            }
            in.close();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        return isUnique;
    }

    /**
     * Logs in the user with the given player name, displaying their current level if the profile exists.
     * @param playerName The player name to log in with.
     * @throws FileNotFoundException If the profiles file cannot be read.
     */
    public void login(String playerName) throws FileNotFoundException {
        if (!isUnique(playerName)) {
            System.out.println("You can login and your level is" + findProfile(playerName).getMaxLevel());
        } else {
            System.out.println("Invalid");
        }
    }

    /**
     * Validates the player name to ensure it meets length and uniqueness criteria.
     * @param playerName The player name to validate.
     * @return True if the player name is valid, false otherwise.
     * @throws FileNotFoundException If the profiles file cannot be read.
     */
    public boolean validProfileName(String playerName) throws FileNotFoundException {
        if (playerName.length() > 0 && playerName.length() < 10 && isUnique(playerName)) {
            return true;
        } else {
            return false;
        }
    }
    @FXML
    public void exitGamePressed() throws IOException {
        System.exit(0);
    }

    /**
     * Deletes the player profile with the given player name from the profiles file.
     * @param playerName The player name whose profile is to be deleted.
     * @throws FileNotFoundException If there is an error while reading the profiles file.
     */
    public void deleteProfile(String playerName) throws FileNotFoundException {
        try {
            if (!isUnique(playerName)) {
                File profilesFile = new File("profiles.txt");
                Scanner in = new Scanner(profilesFile);
                in.useDelimiter("\\s");
                while (in.hasNextLine()) {
                    String name = in.next();
                    int maxLevel = in.nextInt();
                    if (name == playerName) {
                        // TO DO - DELETE PROFILE FROM TEXT FILE
                        System.out.println("DELETED");

                    }
                }
            } else {
                System.out.println("Player profile does not exist");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }


}
